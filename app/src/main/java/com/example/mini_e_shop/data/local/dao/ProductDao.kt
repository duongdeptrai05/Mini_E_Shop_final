package com.example.mini_e_shop.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.mini_e_shop.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) cho Product.
 * Tất cả các hàm liên quan đến ID đã được chuyển sang dùng kiểu String để đồng bộ với Firebase.
 */
@Dao
interface ProductDao {

    /**
     * Lấy tất cả sản phẩm từ cơ sở dữ liệu và trả về dưới dạng một Flow.
     * UI sẽ tự động cập nhật khi dữ liệu thay đổi.
     */
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    /**
     * Lấy một sản phẩm duy nhất bằng ID của nó.
     * ID giờ là kiểu String.
     */
    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: String): ProductEntity?

    /**
     * Lấy danh sách sản phẩm theo danh mục và sắp xếp theo giá tăng dần.
     */
    @Query("SELECT * FROM products WHERE category = :category ORDER BY price ASC")
    suspend fun getProductsByCategorySortedByPrice(category: String): List<ProductEntity>

    /**
     * Chèn hoặc cập nhật (nếu đã tồn tại) một sản phẩm duy nhất.
     */
    @Upsert
    suspend fun upsertProduct(product: ProductEntity)

    /**
     * Chèn hoặc cập nhật (nếu đã tồn tại) một danh sách sản phẩm.
     * Hàm này rất quan trọng cho việc đồng bộ dữ liệu từ Firebase.
     */
    @Upsert
    suspend fun upsertProducts(products: List<ProductEntity>)

    /**
     * Cập nhật số lượng tồn kho cho một sản phẩm cụ thể.
     * ID của sản phẩm giờ là kiểu String.
     */
    @Query("UPDATE products SET stock = :newStock WHERE id = :productId")
    suspend fun updateStock(productId: String, newStock: Int)

    /**
     * Giảm số lượng tồn kho sau khi người dùng đặt hàng.
     * ID của sản phẩm giờ là kiểu String.
     */
    @Query("UPDATE products SET stock = stock - :quantity WHERE id = :productId")
    suspend fun decreaseStock(productId: String, quantity: Int)

    /**
     * Xóa một sản phẩm khỏi cơ sở dữ liệu.
     */
    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    /**
     * Hàm này không còn cần thiết khi đã có dữ liệu từ Firebase.
     * Bạn có thể xóa nó nếu muốn.
     */
    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int
}
