package com.example.mini_e_shop.data.repository

import com.example.mini_e_shop.data.local.dao.FavoriteDao
import com.example.mini_e_shop.data.local.entity.FavoriteEntity
import com.example.mini_e_shop.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {
    override fun getFavoriteProductIds(userId: Int): Flow<Set<Int>> {
        return favoriteDao.getFavoriteProductIds(userId).map { it.toSet() }
    }

    override suspend fun toggleFavorite(productId: Int, userId: Int) {
        val isFavorite = favoriteDao.isFavorite(userId, productId)
        if (isFavorite) {
            favoriteDao.removeFavorite(userId, productId)
        } else {
            favoriteDao.addFavorite(FavoriteEntity(userId = userId, productId = productId))
        }
    }
}