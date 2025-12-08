package com.example.mini_e_shop.data.datafirebase

import com.google.firebase.firestore.DocumentId

data class Product(
    @DocumentId // Chú thích này sẽ tự động gán ID của document vào trường `id`
    val id: String = "",

    val name: String = "",
    val brand: String = "",
    val category: String = "",
    val origin: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val imageUrl: String = "", // Đổi tên từ imageIn1 thành imageUrl cho thống nhất
    val description: String = ""
)
