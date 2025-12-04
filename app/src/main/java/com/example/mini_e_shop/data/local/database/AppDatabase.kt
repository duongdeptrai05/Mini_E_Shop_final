package com.example.mini_e_shop.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mini_e_shop.data.local.dao.CartDao
import com.example.mini_e_shop.data.local.dao.OrderDao
import com.example.mini_e_shop.data.local.dao.ProductDao
import com.example.mini_e_shop.data.local.dao.UserDao
import com.example.mini_e_shop.data.local.entity.CartItemEntity
import com.example.mini_e_shop.data.local.entity.OrderEntity
import com.example.mini_e_shop.data.local.entity.OrderItemEntity
import com.example.mini_e_shop.data.local.entity.ProductEntity
import com.example.mini_e_shop.data.local.entity.UserEntity
import com.example.mini_e_shop.data.local.Converters

@Database(
    entities = [
        UserEntity::class,
        ProductEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class
    ],
    version = 7, // Force database recreation
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun userDao(): UserDao
}