package com.nesher.waroongpintar.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nesher.waroongpintar.data.model.Product
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
}