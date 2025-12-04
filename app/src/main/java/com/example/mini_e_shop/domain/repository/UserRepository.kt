package com.example.mini_e_shop.domain.repository

import com.example.mini_e_shop.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserByEmail(email: String):  UserEntity?
    suspend fun getUserById(userId: Int): UserEntity?
    fun observeUserById(userId: Int): Flow<UserEntity?>
    suspend fun registerUser(user: UserEntity)
}
