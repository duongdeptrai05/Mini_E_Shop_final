package com.example.mini_e_shop.data.local.dao

import androidx.room.*
import com.example.mini_e_shop.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

     // KÍCH HOẠT LẠI HÀM NÀY VỚI KIỂU TRẢ VỀ ĐÚNG
    @Query("SELECT * FROM products WHERE category = :category ORDER BY price ASC")
    suspend fun getProductsByCategorySortedByPrice(category: String): List<ProductEntity>

    // SỬA LỖI CHÍNH Ở ĐÂY: HÀM UPDATE KHÔNG CẦN KIỂU TRẢ VỀ
    @Query("UPDATE products SET stock = stock - :quantity WHERE id = :productId")
    suspend fun decreaseStock(productId: Int, quantity: Int) // Bỏ kiểu trả về ở đây

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Upsert
    suspend fun upsertProduct(product: ProductEntity)

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Int): ProductEntity?

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int

    @Query("UPDATE products SET stock = :newStock WHERE id = :productId")
    suspend fun updateStock(productId: Int, newStock: Int)

}
