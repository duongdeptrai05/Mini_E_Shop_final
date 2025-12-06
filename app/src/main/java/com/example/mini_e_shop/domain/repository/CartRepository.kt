package com.example.mini_e_shop.domain.repository

import com.example.mini_e_shop.domain.model.Product
import com.example.mini_e_shop.presentation.cart.CartItemDetails
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItems(userId: Int): Flow<List<CartItemDetails>>
    suspend fun updateQuantity(cartItemId: Int, newQuantity: Int)
    suspend fun removeItem(cartItemId: Int)
    suspend fun clearCart(userId: Int)
    suspend fun addProductToCart(product: Product, userId: Int)

    suspend fun getCartItemsByIds(cartItemIds: List<Int>): List<CartItemDetails>
}

