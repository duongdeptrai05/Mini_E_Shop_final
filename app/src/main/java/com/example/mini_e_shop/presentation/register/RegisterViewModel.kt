package com.example.mini_e_shop.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.data.local.entity.UserEntity
import com.example.mini_e_shop.data.local.entity.UserRole
import com.example.mini_e_shop.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
//import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState = _registerState.asStateFlow()
    private val _registerEvent = Channel<Unit>()
    val registerEvent = _registerEvent.receiveAsFlow()

    fun onNameChange(newValue: String) {
        _name.value = newValue
    }

    fun onEmailChange(newValue: String) {
        _email.value = newValue
    }

    fun onPasswordChange(newValue: String) {
        _password.value = newValue
    }

    fun registerUser() {
        val email = _email.value.trim()
        val password = _password.value.trim()
        val username = _name.value.trim()

        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            _registerState.value = RegisterState.Error("Vui lòng điền đầy đủ thông tin.")
            return
        }
        if (password.length < 6) {
            _registerState.value = RegisterState.Error("Mật khẩu phải có ít nhất 6 ký tự.")
            return
        }

        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                // 1. Dùng Firebase Auth để tạo tài khoản
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Đăng ký thành công, lấy user từ Firebase
                            val firebaseUser = task.result?.user
                            if (firebaseUser != null) {
                                // Lấy ID String duy nhất (uid)
                                val userId = firebaseUser.uid


                                // 2. Tạo đối tượng UserEntity với cấu trúc mới
                                val newUserEntity = UserEntity(
                                    id = userId,
                                    name = username,
                                    email = email,
                                    isAdmin = false // Người dùng mới mặc định không phải admin
                                )

                                // 3. Lưu thông tin user vào cả Room và Firestore
                                viewModelScope.launch {
                                    // Lưu vào Room để dùng offline
                                    userRepository.registerUser(newUserEntity)

                                    // Lưu vào Firestore collection 'users' để quản lý trên server
                                    firestore.collection("users").document(userId).set(newUserEntity)
                                        .addOnSuccessListener {
                                            viewModelScope.launch {
                                                _registerState.value = RegisterState.Success
                                                _registerEvent.send(Unit) // Gửi sự kiện để điều hướng
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            _registerState.value = RegisterState.Error("Lỗi khi lưu thông tin người dùng: ${e.message}")
                                        }
                                }
                            } else {
                                _registerState.value = RegisterState.Error("Không thể tạo người dùng trên Firebase.")
                            }
                        } else {
                            // Đăng ký thất bại
                            _registerState.value = RegisterState.Error(task.exception?.message ?: "Email đã được sử dụng hoặc không hợp lệ.")
                        }
                    }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Đã có lỗi xảy ra: ${e.message}")
            }
        }
    }

    fun resetRegisterState() {
        _registerState.value = RegisterState.Idle
    }
}

sealed interface RegisterState {
    object Idle : RegisterState
    object Loading : RegisterState
    object Success : RegisterState
    data class Error(val message: String) : RegisterState
}
