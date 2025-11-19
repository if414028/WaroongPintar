package com.nesher.waroongpintar.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "brands",
    indices = [Index(value = ["name"], unique = true)]
)
data class ProductBrand(
    @PrimaryKey val id: String,
    val name: String
)
