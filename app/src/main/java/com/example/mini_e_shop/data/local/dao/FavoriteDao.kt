package com.example.mini_e_shop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mini_e_shop.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE userId = :userId AND productId = :productId")
    suspend fun removeFavorite(userId: Int, productId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND productId = :productId LIMIT 1)")
    suspend fun isFavorite(userId: Int, productId: Int): Boolean

    @Query("SELECT productId FROM favorites WHERE userId = :userId")
    fun getFavoriteProductIds(userId: Int): Flow<List<Int>>
}
    