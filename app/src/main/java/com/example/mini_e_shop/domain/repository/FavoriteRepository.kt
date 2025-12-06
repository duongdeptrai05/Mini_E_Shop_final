package com.example.mini_e_shop.domain.repository

import com.example.mini_e_shop.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getFavoriteProductIds(userId: Int): Flow<Set<Int>>
    suspend fun toggleFavorite(productId: Int, userId: Int)
    fun getFavoriteProducts(userId: Int): Flow<List<Product>>
}