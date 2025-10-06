package com.nesher.waroongpintar.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nesher.waroongpintar.AppDatabase
import com.nesher.waroongpintar.data.model.Product
import com.nesher.waroongpintar.data.model.ProductList
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val db: AppDatabase) {
    private val productDao = db.productDao()

    fun pagedProducts(query: String?, categoryId: String?): Flow<PagingData<ProductList>> {
        return Pager(
            config = PagingConfig(pageSize = 30, prefetchDistance = 2, enablePlaceholders = false),
            pagingSourceFactory = { productDao.paging(query, categoryId) }
        ).flow
    }

    suspend fun upsertProducts(items: List<Product>) =
        productDao.upsertAll(items)

    suspend fun adjustStock(productId: String, delta: Int) =
        productDao.adjustStock(productId, delta)

    suspend fun deleteSoft(productId: String) =
        productDao.softDelete(productId)
}