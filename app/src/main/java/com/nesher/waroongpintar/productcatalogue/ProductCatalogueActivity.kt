package com.nesher.waroongpintar.productcatalogue

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nesher.waroongpintar.R
import com.nesher.waroongpintar.data.model.ProductCategory
import com.nesher.waroongpintar.data.model.ProductList
import com.nesher.waroongpintar.databinding.ActivityProductCatalogueBinding
import com.nesher.waroongpintar.databinding.ItemCategoryBinding
import com.nesher.waroongpintar.databinding.ViewProductItemBinding
import com.nesher.waroongpintar.utils.SimpleFilterRecyclerAdapter
import com.nesher.waroongpintar.utils.SimpleRecyclerAdapter
import com.nesher.waroongpintar.utils.toRupiah
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductCatalogueActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductCatalogueBinding
    private val viewModel: ProductCatalogueViewModel by viewModels()

    private lateinit var categoryAdapter: SimpleRecyclerAdapter<ProductCategory>
    private lateinit var productAdapter: SimpleFilterRecyclerAdapter<ProductList>

    private var selectedCategoryId: String? = null

    private val bulkUploadLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.refreshData()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_catalogue)
        binding.lifecycleOwner = this

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = sb.bottom)
            insets
        }

        setupLayout()
        observeVm()
    }

    private fun setupLayout() {
        binding.icBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        setupCategoryAdapter()
        setupProductAdapter()

        binding.etSearch.doAfterTextChanged { text ->
            productAdapter.filter(text?.toString())
        }

        binding.btnBulkUpload.setOnClickListener { goToBulkUpload() }
    }

    private fun goToBulkUpload() {
        val intent = Intent(this, BulkUploadActivity::class.java)
        bulkUploadLauncher.launch(intent)
    }

    private fun observeVm() {
        viewModel.categories.observe(this) { list ->
            categoryAdapter.updateMainData(list.toMutableList())
            selectedCategoryId = null
            categoryAdapter.notifyDataSetChanged()
        }

        viewModel.allProducts.observe(this) { list ->
            productAdapter.updateMainData(list.toMutableList())
            applyCategoryFilterAndSearch()
        }
    }

    private fun setupCategoryAdapter() {
        categoryAdapter =
            SimpleRecyclerAdapter(arrayListOf(), R.layout.item_category, { holder, item ->
                val itemBinding: ItemCategoryBinding = holder.layoutBinding as ItemCategoryBinding
                itemBinding.tvCategory.text = item.name

                val isSelected =
                    (selectedCategoryId == null && item.id.isEmpty()) || selectedCategoryId == item.id
                itemBinding.isSelected = isSelected

                itemBinding.root.setOnClickListener {
                    selectedCategoryId = item.id.ifEmpty { null }
                    applyCategoryFilterAndSearch()
                    categoryAdapter.notifyDataSetChanged()
                }
            })
        binding.rvCategory.apply {
            layoutManager =
                LinearLayoutManager(this@ProductCatalogueActivity, RecyclerView.HORIZONTAL, false)
            adapter = categoryAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupProductAdapter() {
        productAdapter =
            SimpleFilterRecyclerAdapter(arrayListOf(), R.layout.view_product_item, { holder, item ->
                val itemBinding: ViewProductItemBinding =
                    holder.layoutBinding as ViewProductItemBinding
                itemBinding.tvName.text = item.name
                itemBinding.tvSku.text = buildString {
                    append("SKU: ")
                    append(item.sku)
                }
                itemBinding.tvBrand.text = buildString {
                    append("Brand: ")
                    append(item.brandName)
                }
                itemBinding.tvCategory.text = buildString {
                    append("Kategori: ")
                    append(item.categoryName)
                }
                itemBinding.tvStock.text = buildString {
                    append("Stock: ")
                    append(item.stock)
                }
                itemBinding.tvPrice.text = buildString {
                    append("Harga: ")
                    append(item.price.toRupiah())
                }


            }, { model, searchedText ->
                val match = model.name.contains(searchedText, true)
                        || model.sku.contains(searchedText, true)
                        || (model.barcode?.contains(searchedText, true) == true)
                if (match) model else null
            })

        binding.rvProducts.apply {
            layoutManager =
                GridLayoutManager(this@ProductCatalogueActivity, 5)
            adapter = productAdapter
            setHasFixedSize(true)
        }
    }

    private fun applyCategoryFilterAndSearch() {
        val full = productAdapter.savedMainData.toList()
        val base = if (selectedCategoryId == null) {
            viewModel.allProducts.value.orEmpty()
        } else {
            viewModel.allProducts.value.orEmpty().filter { it.categoryId == selectedCategoryId }
        }

        productAdapter.updateMainData(base.toMutableList())
        val currentText = binding.etSearch.text?.toString()
        productAdapter.filter(currentText)
    }
}