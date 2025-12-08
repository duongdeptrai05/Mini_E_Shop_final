package com.example.mini_e_shop.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "cart_items",
    // Định nghĩa các khóa ngoại để đảm bảo toàn vẹn dữ liệu
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"], // id của UserEntity giờ là String
            childColumns = ["userId"], // userId của CartItemEntity giờ là String
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"], // id của ProductEntity giờ là String
            childColumns = ["productId"], // productId của CartItemEntity giờ là String
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // ID của dòng này vẫn có thể là Int tự tăng

    val userId: String, // Đổi từ Int thành String
    val productId: String, // Giữ nguyên là String
    val quantity: Int
)
    