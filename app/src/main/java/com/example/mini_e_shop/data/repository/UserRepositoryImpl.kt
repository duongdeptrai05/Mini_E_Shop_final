package com.example.mini_e_shop.data.repository

import com.example.mini_e_shop.data.local.dao.UserDao
import com.example.mini_e_shop.data.local.entity.UserEntity
import com.example.mini_e_shop.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import com.example.mini_e_shop.data.preferences.UserPreferencesManager
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val preferencesManager: UserPreferencesManager,
    private val firestore: FirebaseFirestore
) : UserRepository {
    
    private val repositoryScope = kotlinx.coroutines.CoroutineScope(
        kotlinx.coroutines.Dispatchers.IO + kotlinx.coroutines.SupervisorJob()
    )
    
    private var currentListener: com.google.firebase.firestore.ListenerRegistration? = null
    
    init {
        // Khởi tạo việc lắng nghe thay đổi user từ Firestore và cập nhật vào Room
        subscribeToRemoteUserChanges()
    }
    
    // Hàm này sẽ chạy ngầm để lắng nghe Firestore và cập nhật vào Room
    private fun subscribeToRemoteUserChanges() {
        repositoryScope.launch {
            // Lắng nghe thay đổi của user hiện tại đang đăng nhập
            preferencesManager.authPreferencesFlow.collect { prefs ->
                // Hủy listener cũ nếu có
                currentListener?.remove()
                currentListener = null
                
                if (prefs.isLoggedIn && prefs.loggedInUserId.isNotEmpty()) {
                    val userId = prefs.loggedInUserId
                    
                    // Lắng nghe thay đổi của user này trên Firestore
                    currentListener = firestore.collection("users").document(userId)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                println("Firestore user listener error: ${error.message}")
                                return@addSnapshotListener
                            }
                            
                            if (snapshot != null && snapshot.exists()) {
                                try {
                                    // Lấy dữ liệu từ Firestore và convert sang UserEntity
                                    val data = snapshot.data
                                    if (data != null) {
                                        val userEntity = UserEntity(
                                            id = snapshot.id,
                                            email = data["email"] as? String ?: "",
                                            name = data["name"] as? String ?: "",
                                            isAdmin = (data["isAdmin"] as? Boolean) ?: false
                                        )
                                        
                                        // Cập nhật vào Room (Flow sẽ tự động emit giá trị mới)
                                        repositoryScope.launch {
                                            userDao.insertUser(userEntity)
                                            println("User ${userEntity.id} synced from Firestore to Room. isAdmin: ${userEntity.isAdmin}")
                                        }
                                    }
                                } catch (e: Exception) {
                                    println("Error converting Firestore user to UserEntity: $e")
                                    e.printStackTrace()
                                }
                            }
                        }
                }
            }
        }
    }
    
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
        // Đồng bộ lên Firestore
        firestore.collection("users").document(user.id)
            .set(user)
            .addOnFailureListener { e ->
                println("Error syncing user to Firestore: $e")
            }
    }
    
    override suspend fun updateUser(user: UserEntity) {
        // Cập nhật vào Room trước (Flow sẽ tự động emit giá trị mới)
        userDao.insertUser(user) // Sử dụng insertUser với REPLACE để update
        
        // Sau đó cập nhật lên Firestore
        firestore.collection("users").document(user.id)
            .set(user)
            .addOnSuccessListener {
                println("User ${user.id} updated successfully in Firestore.")
            }
            .addOnFailureListener { e ->
                println("Error updating user in Firestore: $e")
            }
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
