package com.example.mini_e_shop.data.repository

import com.example.mini_e_shop.data.local.dao.UserDao
import com.example.mini_e_shop.data.local.entity.UserEntity
import com.example.mini_e_shop.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {
    override suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }

    override suspend fun getUserById(userId: Int): UserEntity? {
        return userDao.getUserById(userId)
    }

    override fun observeUserById(userId: Int): Flow<UserEntity?> {
        return userDao.observeUserById(userId)
    }

    override suspend fun registerUser(user: UserEntity) {
        userDao.insertUser(user)
    }
}
