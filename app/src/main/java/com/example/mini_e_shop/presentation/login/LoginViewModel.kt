package com.example.mini_e_shop.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.data.local.entity.UserEntity
import com.example.mini_e_shop.data.preferences.UserPreferencesManager
import com.example.mini_e_shop.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferencesManager: UserPreferencesManager,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    companion object {
        // Email admin mặc định đã seed trong AppModule
        private const val DEFAULT_ADMIN_EMAIL = "admin@eshop.com"
    }

    private val _usernameOrEmail = MutableStateFlow("")
    val usernameOrEmail = _usernameOrEmail.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _rememberMe = MutableStateFlow(false)
    val rememberMe = _rememberMe.asStateFlow()

    private val _loginEvent = Channel<LoginEvent>()
    val loginEvent = _loginEvent.receiveAsFlow()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    init {
        loadRememberMeCredentials()
    }

    private fun loadRememberMeCredentials() {
        viewModelScope.launch {
            val prefs = userPreferencesManager.authPreferencesFlow.first()
            _usernameOrEmail.value = prefs.rememberMeEmail // Assuming this stores the last login identifier
            if (prefs.rememberMeEmail.isNotEmpty()) {
                _rememberMe.value = true
            }
        }
    }

    fun onUsernameOrEmailChange(newValue: String) {
        _usernameOrEmail.value = newValue
    }

    fun onPasswordChange(newValue: String) {
        _password.value = newValue
    }

    fun onRememberMeChange(newValue: Boolean) {
        _rememberMe.value = newValue
    }

    fun loginUser() {
        val email = _usernameOrEmail.value.trim()
        val password = _password.value.trim()
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Vui lòng nhập email và mật khẩu")
            return
        }
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                // 1. Dùng Firebase để đăng nhập
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // 2. Đăng nhập thành công, lấy user từ Firebase Auth
                            val firebaseUser = task.result?.user
                            if (firebaseUser != null) {
                                // Lấy User ID kiểu String
                                val userId = firebaseUser.uid

                                // Dùng viewModelScope.launch ở đây vì onCompleteListener không phải coroutine
                                viewModelScope.launch {
                                    // 3. Lưu trạng thái đăng nhập vào DataStore
                                    userPreferencesManager.saveLoginState(
                                        userId = userId,
                                        email = email,
                                        rememberMe = _rememberMe.value
                                    )
                                    
                                    // 4. Luôn sync từ Firestore để đảm bảo isAdmin được cập nhật đúng
                                    val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                    var userEntity: UserEntity? = null
                                    
                                    try {
                                        val snapshot = firestore.collection("users").document(userId).get().await()
                                        if (snapshot.exists()) {
                                            val data = snapshot.data
                                            if (data != null) {
                                                val isAdminValue = data["isAdmin"]
                                                var isAdmin = when {
                                                    isAdminValue is Boolean -> isAdminValue
                                                    isAdminValue is Number -> isAdminValue.toInt() != 0
                                                    else -> false
                                                }

                                                // Fallback: nếu là tài khoản admin seed nhưng thiếu cờ isAdmin trên Firestore
                                                if (!isAdmin && email.equals(DEFAULT_ADMIN_EMAIL, ignoreCase = true)) {
                                                    isAdmin = true
                                                }
                                                
                                                android.util.Log.d("LoginViewModel", "Firestore data - isAdmin raw: $isAdminValue, converted: $isAdmin")
                                                
                                                userEntity = com.example.mini_e_shop.data.local.entity.UserEntity(
                                                    id = snapshot.id,
                                                    email = data["email"] as? String ?: email,
                                                    name = data["name"] as? String ?: "",
                                                    isAdmin = isAdmin
                                                )
                                                // Lưu vào Room để đảm bảo đồng bộ
                                                userRepository.registerUser(userEntity)
                                                // Đồng bộ lại cờ isAdmin lên Firestore nếu cần
                                                if (isAdmin) {
                                                    firestore.collection("users").document(userId)
                                                        .update("isAdmin", true)
                                                }
                                                android.util.Log.d("LoginViewModel", "User synced from Firestore to Room. Email: ${userEntity.email}, isAdmin: ${userEntity.isAdmin}")
                                                println("LoginViewModel: User synced from Firestore to Room. Email: ${userEntity.email}, isAdmin: ${userEntity.isAdmin}")
                                            }
                                        } else {
                                            // Nếu không có trong Firestore, lấy từ Room hoặc tạo mới
                                            userEntity = userRepository.getUserById(userId)
                                            if (userEntity == null) {
                                                // Tạo user mới, gán admin nếu trùng email seed
                                                val isAdmin = email.equals(DEFAULT_ADMIN_EMAIL, ignoreCase = true)
                                                userEntity = com.example.mini_e_shop.data.local.entity.UserEntity(
                                                    id = userId,
                                                    email = email,
                                                    name = email.split("@")[0], // Dùng phần trước @ làm tên mặc định
                                                    isAdmin = isAdmin
                                                )
                                                userRepository.registerUser(userEntity)
                                                if (isAdmin) {
                                                    firestore.collection("users").document(userId)
                                                        .set(userEntity)
                                                }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        println("LoginViewModel: Error fetching user from Firestore: $e")
                                        // Nếu lỗi, lấy từ Room
                                        userEntity = userRepository.getUserById(userId)
                                        if (userEntity == null) {
                                            // Tạo user mới nếu không có trong Room
                                            val isAdmin = email.equals(DEFAULT_ADMIN_EMAIL, ignoreCase = true)
                                            userEntity = com.example.mini_e_shop.data.local.entity.UserEntity(
                                                id = userId,
                                                email = email,
                                                name = email.split("@")[0],
                                                isAdmin = isAdmin
                                            )
                                            userRepository.registerUser(userEntity)
                                            if (isAdmin) {
                                                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                                    .collection("users").document(userId)
                                                    .set(userEntity)
                                            }
                                        }
                                    }

                                    _loginState.value = LoginState.Success(userEntity)
                                    // Gửi sự kiện để điều hướng đến màn hình chính
                                    _loginEvent.send(LoginEvent.NavigateToHome)
                                }
                            } else {
                                _loginState.value = LoginState.Error("Không thể lấy thông tin người dùng")
                            }
                        } else {
                            // Đăng nhập thất bại
                            _loginState.value = LoginState.Error(
                                task.exception?.message ?: "Email hoặc mật khẩu không đúng"
                            )
                        }
                    }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Đã có lỗi xảy ra: ${e.message}")
            }
        }
    }

    sealed interface LoginState {
        object Idle : LoginState
        object Loading : LoginState
        data class Success(val user: UserEntity?) : LoginState
        data class Error(val message: String) : LoginState
    }
    sealed interface LoginEvent {
        object NavigateToHome : LoginEvent
    }
}
