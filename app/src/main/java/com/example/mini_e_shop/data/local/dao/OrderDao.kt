package com.example.mini_e_shop.data.local.dao

import androidx.room.*
import com.example.mini_e_shop.data.local.entity.OrderEntity
import com.example.mini_e_shop.data.local.entity.OrderItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun insertOrder(order: OrderEntity)

    // 2. Thêm hàm này để chèn nhiều item cùng lúc
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    // 3. Thêm hàm này để lấy tất cả đơn hàng của một người dùng
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    fun getOrdersByUser(userId: String): Flow<List<OrderEntity>>

    // Hàm tùy chọn: Lấy tất cả item của một đơn hàng cụ thể
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getItemsForOrder(orderId: String): Flow<List<OrderItemEntity>>
}
