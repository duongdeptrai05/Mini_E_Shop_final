package com.example.mini_e_shop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(    // SỬA ĐỔI CHÍNH NẰM Ở ĐÂY
    @PrimaryKey
    val id: String, // Thêm giá trị mặc định là 0

    val name: String,
    val brand: String,
    val category: String,
    val origin: String,
    val price: Double,
    val stock: Int,
    val imageUrl: String,
    val description: String
)
