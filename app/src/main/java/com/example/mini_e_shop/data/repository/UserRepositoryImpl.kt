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
import kotlinx.coroutines.tasks.await

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
                                        val isAdminValue = data["isAdmin"]
                                        val isAdmin = when {
                                            isAdminValue is Boolean -> isAdminValue
                                            isAdminValue is Number -> isAdminValue.toInt() != 0
                                            else -> false
                                        }
                                        
                                        android.util.Log.d("UserRepositoryImpl", "Firestore data - isAdmin raw: $isAdminValue, converted: $isAdmin")
                                        
                                        val userEntity = UserEntity(
                                            id = snapshot.id,
                                            email = data["email"] as? String ?: "",
                                            name = data["name"] as? String ?: "",
                                            isAdmin = isAdmin
                                        )
                                        
                                        // Cập nhật vào Room (Flow sẽ tự động emit giá trị mới)
                                        repositoryScope.launch {
                                            userDao.insertUser(userEntity)
                                            android.util.Log.d("UserRepositoryImpl", "User ${userEntity.id} synced from Firestore to Room. Email: ${userEntity.email}, isAdmin: ${userEntity.isAdmin}")
                                            println("User ${userEntity.id} synced from Firestore to Room. isAdmin: ${userEntity.isAdmin}")
                                        }
                                    }
                                } catch (e: Exception) {
                                    android.util.Log.e("UserRepositoryImpl", "Error converting Firestore user to UserEntity: $e")
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
        // Force refresh từ Firestore trước khi observe từ Room
        repositoryScope.launch {
            try {
                android.util.Log.d("UserRepositoryImpl", "observeUserById - Starting force refresh for userId: $userId")
                
                // Lấy từ Room trước để so sánh
                val roomUser = userDao.getUserById(userId)
                android.util.Log.d("UserRepositoryImpl", "observeUserById - Room user: ${roomUser?.email}, isAdmin: ${roomUser?.isAdmin}")
                
                // Lấy từ Firestore
                val snapshot = firestore.collection("users").document(userId).get().await()
                if (snapshot.exists()) {
                    val data = snapshot.data
                    if (data != null) {
                        val isAdminValue = data["isAdmin"]
                        val isAdmin = when {
                            isAdminValue is Boolean -> isAdminValue
                            isAdminValue is Number -> isAdminValue.toInt() != 0
                            else -> false
                        }
                        
                        android.util.Log.d("UserRepositoryImpl", "observeUserById - Firestore data - isAdmin raw: $isAdminValue, converted: $isAdmin")
                        
                        val userEntity = UserEntity(
                            id = snapshot.id,
                            email = data["email"] as? String ?: "",
                            name = data["name"] as? String ?: "",
                            isAdmin = isAdmin
                        )
                        
                        // Nếu Firestore có isAdmin = true nhưng Room có isAdmin = false, hoặc ngược lại, cập nhật Room
                        if (roomUser == null || roomUser.isAdmin != userEntity.isAdmin) {
                            android.util.Log.d("UserRepositoryImpl", "observeUserById - Updating Room with Firestore data. Room isAdmin: ${roomUser?.isAdmin}, Firestore isAdmin: ${userEntity.isAdmin}")
                            userDao.insertUser(userEntity)
                            android.util.Log.d("UserRepositoryImpl", "observeUserById - User synced to Room. Email: ${userEntity.email}, isAdmin: ${userEntity.isAdmin}")
                        } else {
                            android.util.Log.d("UserRepositoryImpl", "observeUserById - Room and Firestore are in sync. isAdmin: ${userEntity.isAdmin}")
                        }
                    } else {
                        android.util.Log.w("UserRepositoryImpl", "observeUserById - Firestore snapshot exists but data is null")
                    }
                } else {
                    android.util.Log.w("UserRepositoryImpl", "observeUserById - Firestore document does not exist for userId: $userId")
                    // Nếu Firestore không có nhưng Room có, sync lên Firestore
                    if (roomUser != null) {
                        android.util.Log.d("UserRepositoryImpl", "observeUserById - Syncing Room user to Firestore. isAdmin: ${roomUser.isAdmin}")
                        val userMap = hashMapOf(
                            "email" to roomUser.email,
                            "name" to roomUser.name,
                            "isAdmin" to roomUser.isAdmin
                        )
                        firestore.collection("users").document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                android.util.Log.d("UserRepositoryImpl", "observeUserById - Room user synced to Firestore. isAdmin: ${roomUser.isAdmin}")
                            }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("UserRepositoryImpl", "observeUserById - Error refreshing from Firestore: $e", e)
            }
        }
        
        return userDao.observeUserById(userId)
    }

    override suspend fun registerUser(user: UserEntity) {
        android.util.Log.d("UserRepositoryImpl", "registerUser - User: ${user.email}, isAdmin: ${user.isAdmin}")
        userDao.insertUser(user)
        // Đồng bộ lên Firestore - sử dụng Map thay vì Entity để đảm bảo isAdmin được lưu đúng
        val userMap = hashMapOf(
            "email" to user.email,
            "name" to user.name,
            "isAdmin" to user.isAdmin
        )
        firestore.collection("users").document(user.id)
            .set(userMap)
            .addOnSuccessListener {
                android.util.Log.d("UserRepositoryImpl", "User ${user.id} synced to Firestore. isAdmin: ${user.isAdmin}")
            }
            .addOnFailureListener { e ->
                android.util.Log.e("UserRepositoryImpl", "Error syncing user to Firestore: $e")
                println("Error syncing user to Firestore: $e")
            }
    }
    
    override suspend fun updateUser(user: UserEntity) {
        android.util.Log.d("UserRepositoryImpl", "updateUser - User: ${user.email}, isAdmin: ${user.isAdmin}")
        // Cập nhật vào Room trước (Flow sẽ tự động emit giá trị mới)
        userDao.insertUser(user) // Sử dụng insertUser với REPLACE để update
        
        // Sau đó cập nhật lên Firestore - sử dụng Map thay vì Entity
        val userMap = hashMapOf(
            "email" to user.email,
            "name" to user.name,
            "isAdmin" to user.isAdmin
        )
        firestore.collection("users").document(user.id)
            .set(userMap)
            .addOnSuccessListener {
                android.util.Log.d("UserRepositoryImpl", "User ${user.id} updated successfully in Firestore. isAdmin: ${user.isAdmin}")
                println("User ${user.id} updated successfully in Firestore.")
            }
            .addOnFailureListener { e ->
                android.util.Log.e("UserRepositoryImpl", "Error updating user in Firestore: $e")
                println("Error updating user in Firestore: $e")
            }
    }
    
    override fun getCurrentUser(): Flow<UserEntity?> {
        return preferencesManager.authPreferencesFlow.flatMapLatest { prefs ->
            if (prefs.isLoggedIn && prefs.loggedInUserId.isNotEmpty()){
                val userId = prefs.loggedInUserId
                android.util.Log.d("UserRepositoryImpl", "getCurrentUser - userId: $userId")
                
                // Force sync từ Room lên Firestore nếu Room có isAdmin = true
                repositoryScope.launch {
                    val roomUser = userDao.getUserById(userId)
                    if (roomUser != null && roomUser.isAdmin) {
                        android.util.Log.d("UserRepositoryImpl", "getCurrentUser - Room user has isAdmin = true, syncing to Firestore")
                        val userMap = hashMapOf(
                            "email" to roomUser.email,
                            "name" to roomUser.name,
                            "isAdmin" to roomUser.isAdmin
                        )
                        firestore.collection("users").document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                android.util.Log.d("UserRepositoryImpl", "getCurrentUser - Room user synced to Firestore. isAdmin: ${roomUser.isAdmin}")
                            }
                            .addOnFailureListener { e ->
                                android.util.Log.e("UserRepositoryImpl", "getCurrentUser - Error syncing to Firestore: $e")
                            }
                    }
                }
                
                userDao.observeUserById(userId)
            } else {
                flowOf(null)
            }
        }
    }
}
