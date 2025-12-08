package com.example.mini_e_shop.domain.repository

import com.example.mini_e_shop.domain.model.Product
import kotlinx.coroutines.flow.Flow

/**
 * Interface cho Product Repository, định nghĩa các phương thức để tương tác với dữ liệu sản phẩm.
 */
interface ProductRepository {

    /**
     * Lấy tất cả sản phẩm dưới dạng một Flow để theo dõi thay đổi.
     */
    fun getAllProducts(): Flow<List<Product>>
    suspend fun getProductById(id: String): Product?

    suspend fun upsertProduct(product: Product)

    /**
     * Xóa một sản phẩm.
     */
    suspend fun deleteProduct(product: Product)

    /**
     * Cập nhật số lượng tồn kho của sản phẩm.
     */
    suspend fun updateProductStock(productId: String, newStock: Int)
}
