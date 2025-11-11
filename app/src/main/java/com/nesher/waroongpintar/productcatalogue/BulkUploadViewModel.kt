package com.nesher.waroongpintar.productcatalogue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.nesher.waroongpintar.AppDatabase
import com.nesher.waroongpintar.data.model.Product
import com.nesher.waroongpintar.data.model.ProductBrand
import com.nesher.waroongpintar.data.model.ProductCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BulkUploadViewModel @Inject constructor (private val db : AppDatabase) : ViewModel() {

    data class PreviewRow(
        val lineNo: Int,
        val raw: Map<String, String?>,
        val errors: List<String>
    ) {
        val isValid get() = errors.isEmpty()
    }

    data class ImportResult(
        val total: Int,
        val valid: Int,
        val inserted: Int,
        val skipped: Int,
        val failed: Int,
        val message: String
    )

    private val _preview = MutableLiveData<List<PreviewRow>>(emptyList())
    val preview: LiveData<List<PreviewRow>> = _preview

    private val _busy = MutableLiveData(false)
    val busy: LiveData<Boolean> = _busy

    private val _statusText = MutableLiveData<String>()
    val statusText: LiveData<String> = _statusText

    private val _importResult = MutableLiveData<ImportResult?>()
    val importResult: LiveData<ImportResult?> = _importResult

    fun parseCsvText(csvText: String) {
        viewModelScope.launch(Dispatchers.Default) {
            _busy.postValue(true)
            val reader = csvReader {
                quoteChar = '"'
                delimiter = ','
                skipEmptyLine = true
            }

            val rowsRaw = reader.readAllWithHeader(csvText)

            val rows = rowsRaw.map { row ->
                row.mapValues { it.value?.trim() }
            }

            val seenSku = mutableSetOf<String>()
            val seenBarcode = mutableSetOf<String>()
            val previews = rows.mapIndexed { idx, row ->
                val line = idx + 2
                val errs = mutableListOf<String>()

                fun get(k: String) = row[k]?.trim().orEmpty()
                fun nz(s: String?) = s?.trim().orEmpty()

                val sku = nz(row["sku"])
                if (sku.isEmpty()) errs += "sku wajib"
                else if (!seenSku.add(sku.lowercase())) errs += "sku duplikat di file"

                val name = nz(row["name"])
                if (name.isEmpty()) errs += "name wajib"

                val priceStr = nz(row["price"])
                val price = priceStr.toLongOrNull()
                if (price == null || price < 0) errs += "price tidak valid"

                val barcode = nz(row["barcode"]).ifEmpty { null }
                if (!barcode.isNullOrEmpty()) {
                    val lc = barcode.lowercase()
                    if (!seenBarcode.add(lc)) errs += "barcode duplikat di file"
                }

                PreviewRow(
                    lineNo = line,
                    raw = row,
                    errors = errs
                )
            }

            _preview.postValue(previews)
            val total = previews.size
            val valid = previews.count { it.isValid }
            _statusText.postValue("Pratinjau: $valid/$total baris valid. Siap diimpor jika tidak ada error.")
            _busy.postValue(false)
        }
    }

    fun importValidRows() {
        viewModelScope.launch(Dispatchers.IO) {
            val previews = _preview.value.orEmpty()
            if (previews.isEmpty()) {
                _statusText.postValue("Tidak ada data untuk diimpor.")
                return@launch
            }
            _busy.postValue(true)

            val validRows = previews.filter { it.isValid }

            val catNames = validRows.mapNotNull { it.raw["category_name"]?.trim() }
                .filter { it.isNotEmpty() }
                .map { it to it.lowercase() }
                .distinctBy { it.second }

            val brandNames = validRows.mapNotNull { it.raw["brand_name"]?.trim() }
                .filter { it.isNotEmpty() }
                .map { it to it.lowercase() }
                .distinctBy { it.second }

            val categoryDao = db.categoryDao()
            val brandDao = db.brandDao()
            val productDao = db.productDao()

            var inserted = 0
            var skipped = 0
            var failed = 0

            db.withTransaction {
                val catIdByLower = mutableMapOf<String, String>()
                for ((orig, lower) in catNames) {
                    val existing = categoryDao.findByNameLower(lower)
                    val id = if (existing != null) {
                        existing.id
                    } else {
                        val nid = UUID.randomUUID().toString()
                        categoryDao.insert(ProductCategory(id = nid, name = orig))
                        nid
                    }
                    catIdByLower[lower] = id
                }

                val brandIdByLower = mutableMapOf<String, String>()
                for ((orig, lower) in brandNames) {
                    val existing = brandDao.findByNameLower(lower)
                    val id = if (existing != null) {
                        existing.id
                    } else {
                        val nid = UUID.randomUUID().toString()
                        brandDao.insert(ProductBrand(id = nid, name = orig))
                        nid
                    }
                    brandIdByLower[lower] = id
                }

                val skus = validRows.mapNotNull { it.raw["sku"]?.trim()?.lowercase() }.distinct()
                val barcodes = validRows.mapNotNull { it.raw["barcode"]?.trim()?.lowercase() }.distinct()

                val existingSkuSet = productDao.findSkuInLower(skus).toSet()
                val existingBarcodeSet = productDao.findBarcodeInLower(barcodes).toSet()

                validRows.forEach { row ->
                    try {
                        val r = row.raw
                        val sku = r["sku"]!!.trim()
                        val name = r["name"]!!.trim()
                        val price = r["price"]!!.trim().toLong()

                        val unit = r["unit"]?.trim().takeUnless { it.isNullOrEmpty() } ?: "pcs"
                        val stock = r["stock"]?.trim()?.toIntOrNull() ?: 0
                        val minStock = r["min_stock"]?.trim()?.toIntOrNull() ?: 0
                        val cost = r["cost"]?.trim()?.toLongOrNull()
                        val barcode = r["barcode"]?.trim().takeUnless { it.isNullOrEmpty() }
                        val isActive = (r["is_active"]?.trim()?.lowercase()).let {
                            when (it) {
                                "0", "false", "no", "n" -> false
                                else -> true
                            }
                        }

                        if (existingSkuSet.contains(sku.lowercase())) {
                            skipped++
                            return@forEach
                        }
                        if (!barcode.isNullOrEmpty() && existingBarcodeSet.contains(barcode.lowercase())) {
                            skipped++
                            return@forEach
                        }

                        val categoryId = r["category_name"]?.trim()
                            ?.takeIf { it.isNotEmpty() }
                            ?.let { catIdByLower[it.lowercase()] }

                        val brandId = r["brand_name"]?.trim()
                            ?.takeIf { it.isNotEmpty() }
                            ?.let { brandIdByLower[it.lowercase()] }

                        val p = Product(
                            id = UUID.randomUUID().toString(),
                            sku = sku,
                            barcode = barcode,
                            name = name,
                            categoryId = categoryId,
                            brandId = brandId,
                            unit = unit,
                            stock = stock,
                            minStock = minStock,
                            price = price,
                            cost = cost,
                            imageUrl = r["image_url"]?.trim().takeUnless { it.isNullOrEmpty() },
                            isActive = isActive,
                            isDirty = false,
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis(),
                            deletedAt = null
                        )
                        productDao.insert(p)
                        inserted++
                    } catch (_: Throwable) {
                        failed++
                    }
                }
            }

            val total = previews.size
            val valid = validRows.size
            val res = ImportResult(
                total = total,
                valid = valid,
                inserted = inserted,
                skipped = skipped,
                failed = failed,
                message = "Impor selesai: $inserted insert, $skipped skip, $failed gagal dari $valid valid ($total total)."
            )
            _importResult.postValue(res)
            _statusText.postValue(res.message)
            _busy.postValue(false)
        }
    }

}