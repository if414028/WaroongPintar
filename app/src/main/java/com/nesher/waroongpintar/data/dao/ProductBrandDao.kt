package com.nesher.waroongpintar.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nesher.waroongpintar.data.model.ProductBrand

@Dao
interface ProductBrandDao {
    @Query("SELECT * FROM brands ORDER BY name")
    suspend fun getAll(): List<ProductBrand>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ProductBrand>)
}