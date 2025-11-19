package com.nesher.waroongpintar.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    indices = [
        Index(value = ["sku"], unique = true),
        Index(value = ["barcode"], unique = true),
        Index("name"),
        Index("categoryId"),
        Index("brandId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = ProductCategory::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = ProductBrand::class,
            parentColumns = ["id"],
            childColumns = ["brandId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class Product(
    @PrimaryKey val id: String,
    val sku: String,
    val barcode: String? = null,
    val name: String,
    val categoryId: String? = null,
    val brandId: String? = null,
    val unit: String = "pcs",
    val stock: Int = 0,
    val minStock: Int = 0,
    val price: Long,
    val cost: Long? = null,
    val imageUrl: String? = null,
    val isActive: Boolean = true,
    val isDirty: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deletedAt: Long? = null
)
