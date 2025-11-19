package com.nesher.waroongpintar.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nesher.waroongpintar.data.model.ProductCategory

@Dao
interface ProductCategoryDao {
    @Query("SELECT * FROM categories ORDER BY name")
    suspend fun getAll(): List<ProductCategory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ProductCategory>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(cat: ProductCategory)

    @Query("SELECT * FROM categories WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    suspend fun findByNameLower(name: String): ProductCategory?
}