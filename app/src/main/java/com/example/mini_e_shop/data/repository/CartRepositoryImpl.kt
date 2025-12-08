package com.example.mini_e_shop.data.repository

import com.example.mini_e_shop.data.local.dao.CartDao
import com.example.mini_e_shop.data.local.dao.CartItemWithProduct
import com.example.mini_e_shop.data.local.entity.CartItemEntity
import com.example.mini_e_shop.data.local.entity.ProductEntity
import com.example.mini_e_shop.data.mapper.toCartItemDetails
import com.example.mini_e_shop.domain.model.CartItem
import com.example.mini_e_shop.domain.model.Product
import com.example.mini_e_shop.domain.repository.CartRepository
import com.example.mini_e_shop.presentation.cart.CartItemDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao
) : CartRepository {

    override fun getCartItems(userId: String): Flow<List<CartItemDetails>> {
        return cartDao.getCartItemsWithProducts(userId).map {
            it.map { cartItemWithProduct ->
                cartItemWithProduct.toCartItemDetails()
            }
        }
    }

    override suspend fun addProductToCart(product: Product, userId: String) {
        // Kiểm tra xem sản phẩm đã có trong giỏ hàng của người dùng này chưa
        val existingItem = cartDao.getCartItem(userId, product.id)

        if (existingItem != null) {
            // Nếu đã có, chỉ tăng số lượng
            val updatedItem = existingItem.copy(quantity = existingItem.quantity + 1)
            cartDao.upsertCartItem(updatedItem)
        } else {
            // Nếu chưa có, tạo item mới
            val newItem = CartItemEntity(
                userId = userId,
                // Gán trực tiếp vì cả hai đều là String, không cần toInt()
                productId = product.id,
                quantity = 1
            )
            cartDao.upsertCartItem(newItem)
        }
        // TODO: Nâng cấp để đẩy thay đổi giỏ hàng này lên Firestore
    }
    override suspend fun getCartItemsByIds(cartItemIds: List<Int>): List<CartItemDetails> {
        return cartDao.getCartItemsByIds(cartItemIds).map{ cartItemWithProduct ->
            cartItemWithProduct.toCartItemDetails()
        }
    }
    override suspend fun updateQuantity(cartItemId: Int, newQuantity: Int) {
        cartDao.updateQuantity(cartItemId, newQuantity)
    }

    override suspend fun removeItem(cartItemId: Int) {
        cartDao.deleteCartItem(cartItemId)
    }

    override suspend fun clearCart(userId: String) {
        cartDao.clearCart(userId)
    }
}
// --- BƯỚC 1: THÊM CÁC HÀM MAPPER VÀO CUỐI FILE ---

private fun CartItemWithProduct.toCartItemDetails(): CartItemDetails {
    return CartItemDetails(
            cartItem = this.cartItem.toDomain(),
    product = this.product.toDomain()
    )
}

private fun CartItemEntity.toDomain(): CartItem {
    return CartItem(
        id = this.id,
        userId = this.userId,
        productId = this.productId,
        quantity = this.quantity
    )
}

private fun ProductEntity.toDomain(): Product {
    return Product(
        id = this.id,
        name = this.name,
        brand = this.brand,
        category = this.category,
        origin = this.origin,
        price = this.price,
        stock = this.stock,
        imageUrl = this.imageUrl,
        description = this.description
    )
}