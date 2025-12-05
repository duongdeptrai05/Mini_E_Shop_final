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

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val preferencesManager: UserPreferencesManager
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
    override fun getCurrentUser(): Flow<UserEntity?> {
        return preferencesManager.authPreferencesFlow.flatMapLatest { prefs ->
            if (prefs.isLoggedIn && prefs.loggedInUserId != -1){
                userDao.observeUserById(prefs.loggedInUserId)
            } else {
                // Nếu người dùng chưa đăng nhập, trả về một Flow chứa giá trị null.
                kotlinx.coroutines.flow.flowOf(null)
            }
        }
    }
}
