package com.example.mini_e_shop.data.repository

import com.example.mini_e_shop.data.local.dao.UserDao
import com.example.mini_e_shop.data.local.entity.UserEntity
import com.example.mini_e_shop.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import com.example.mini_e_shop.data.preferences.UserPreferencesManager
import kotlinx.coroutines.flow.flowOf

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val preferencesManager: UserPreferencesManager
) : UserRepository {
    override suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }

    override suspend fun getUserByName(name: String): UserEntity? {
        return userDao.getUserByName(name)
    }

    override suspend fun getUserById(userId: String): UserEntity? {
        return userDao.getUserById(userId)
    }

    override fun observeUserById(userId: String): Flow<UserEntity?> {
        return userDao.observeUserById(userId)
    }

    override suspend fun registerUser(user: UserEntity) {
        userDao.insertUser(user)
    }
    override fun getCurrentUser(): Flow<UserEntity?> {
        return preferencesManager.authPreferencesFlow.flatMapLatest { prefs ->
            if (prefs.isLoggedIn && prefs.loggedInUserId.isNotEmpty()){
                userDao.observeUserById(prefs.loggedInUserId)
            } else {
                flowOf(null)
            }
        }
    }
}
