package com.nesher.waroongpintar.productcatalogue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nesher.waroongpintar.AppDatabase
import com.nesher.waroongpintar.data.ProductRepository
import com.nesher.waroongpintar.data.model.ProductCategory
import com.nesher.waroongpintar.data.model.ProductList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductCatalogueViewModel @Inject constructor(
    private val repo: ProductRepository,
    private val db: AppDatabase
) : ViewModel() {

    private val _categories = MutableLiveData<List<ProductCategory>>()
    val categories: LiveData<List<ProductCategory>> = _categories

    private val _allProducts = MutableLiveData<List<ProductList>>()
    val allProducts: LiveData<List<ProductList>> = _allProducts

    init {
        loadCategories()
        loadProducts()
    }

    private fun loadCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            val cats =
                db.categoryDao().getAll()
            val list = listOf(ProductCategory(id = "", name = "Semua Kategori")) + cats
            _categories.postValue(list)
        }
    }

    private fun loadProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            val items = db.productDao()
                .listAllActive()
            _allProducts.postValue(items)
        }
    }

    fun refreshData() {
        loadCategories()
        loadProducts()
    }
}