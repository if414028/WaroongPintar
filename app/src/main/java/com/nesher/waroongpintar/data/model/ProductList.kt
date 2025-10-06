package com.nesher.waroongpintar.data.model

import androidx.room.DatabaseView

@DatabaseView(
    viewName = "product_list_item",
    value = """
    SELECT p.id, p.sku, p.barcode, p.name, 
           p.categoryId, c.name AS categoryName,
           p.brandId, b.name AS brandName,
           p.unit, p.stock, p.price, p.imageUrl,
           p.isActive
    FROM products p
    LEFT JOIN categories c ON c.id = p.categoryId
    LEFT JOIN brands b ON b.id = p.brandId
    WHERE p.deletedAt IS NULL
    """
)
data class ProductList(
    val id: String,
    val sku: String,
    val barcode: String?,
    val name: String,
    val categoryId: String?,
    val categoryName: String?,
    val brandId: String?,
    val brandName: String?,
    val unit: String,
    val stock: Int,
    val price: Long,
    val imageUrl: String?,
    val isActive: Boolean
)
