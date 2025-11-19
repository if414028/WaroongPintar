package com.nesher.waroongpintar.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "categories",
    indices = [Index(value = ["name"], unique = true)]
)
data class ProductCategory(
    @PrimaryKey val id: String,
    val name: String,
    val parentId: String? = null
)
