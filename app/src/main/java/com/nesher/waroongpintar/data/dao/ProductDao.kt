package com.nesher.waroongpintar.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nesher.waroongpintar.data.model.Product
import com.nesher.waroongpintar.data.model.ProductBrand
import com.nesher.waroongpintar.data.model.ProductCategory
import com.nesher.waroongpintar.data.model.ProductList

@Dao
interface ProductDao {
    // ---- Insert / Update ----
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(item: Product)

    @Update
    suspend fun update(item: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<Product>)

    @Query("UPDATE products SET stock = stock + :delta, isDirty = 1, updatedAt = :ts WHERE id = :productId")
    suspend fun adjustStock(productId: String, delta: Int, ts: Long = System.currentTimeMillis())

    @Query("UPDATE products SET deletedAt = :ts, isDirty = 1 WHERE id = :productId")
    suspend fun softDelete(productId: String, ts: Long = System.currentTimeMillis())

    // ---- Detail ----
    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): Product?

    @Query("SELECT * FROM product_list_item WHERE isActive = 1 ORDER BY name COLLATE NOCASE")
    suspend fun listAllActive(): List<ProductList>

    // ---- Paging untuk katalog ----
    @Query("""
        SELECT * FROM product_list_item
        WHERE (:categoryId IS NULL OR categoryId = :categoryId)
          AND (
            :q IS NULL OR :q = '' OR
            name LIKE '%' || :q || '%' OR
            sku  LIKE '%' || :q || '%' OR
            IFNULL(barcode,'') LIKE '%' || :q || '%'
          )
          AND isActive = 1
        ORDER BY name COLLATE NOCASE ASC
    """)
    fun paging(q: String?, categoryId: String?): PagingSource<Int, ProductList>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(products: List<Product>)

    @Query("SELECT id, name FROM categories")
    suspend fun getAllCategories(): List<IdName>

    @Query("SELECT id, name FROM brands")
    suspend fun getAllBrands(): List<IdName>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(entity: ProductCategory): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBrand(entity: ProductBrand): Long

    @Query("SELECT 1 FROM products WHERE sku = :sku LIMIT 1")
    suspend fun existsBySku(sku: String): Int

    @Query("SELECT 1 FROM products WHERE barcode = :barcode LIMIT 1")
    suspend fun existsByBarcode(barcode: String): Int

    @Query("SELECT LOWER(sku) FROM products WHERE LOWER(sku) IN (:skus)")
    suspend fun findSkuInLower(skus: List<String>): List<String>

    @Query("SELECT LOWER(barcode) FROM products WHERE barcode IS NOT NULL AND LOWER(barcode) IN (:barcodes)")
    suspend fun findBarcodeInLower(barcodes: List<String>): List<String>
}