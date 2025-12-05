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

    /**
     * Lấy một sản phẩm duy nhất bằng ID.
     * Đây là một suspend function vì nó là một thao tác chỉ diễn ra một lần.
     */
    suspend fun getProductById(id: Int): Product?

    /**
     * Thêm mới hoặc cập nhật một sản phẩm.
     * Dùng cho cả chức năng "Thêm" và "Sửa".
     */
    suspend fun upsertProduct(product: Product)

    /**
     * Xóa một sản phẩm.
     */
    suspend fun deleteProduct(product: Product)
}
