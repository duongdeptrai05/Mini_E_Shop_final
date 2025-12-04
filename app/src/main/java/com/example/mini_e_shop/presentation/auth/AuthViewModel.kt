package com.example.mini_e_shop.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.data.local.entity.UserEntity
import com.example.mini_e_shop.data.local.entity.UserRole
import com.example.mini_e_shop.data.preferences.UserPreferencesManager
import com.example.mini_e_shop.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- STATE DEFINITIONS ---

sealed interface AuthState {
    object Loading : AuthState
    object Authenticated : AuthState
    object Unauthenticated : AuthState
}

// Define MainUiState here, as it's now owned and produced by AuthViewModel.
sealed interface MainUiState {
    object Loading : MainUiState
    data class Success(val currentUser: UserEntity?, val isAdmin: Boolean) : MainUiState
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userPreferencesManager: UserPreferencesManager,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _sessionUser = MutableStateFlow<UserEntity?>(null)

    val authState: StateFlow<AuthState> = combine(
        userPreferencesManager.authPreferencesFlow,
        _sessionUser
    ) { prefs, sessionUser ->
        if (prefs.isLoggedIn || sessionUser != null) {
            AuthState.Authenticated
        } else {
            AuthState.Unauthenticated
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthState.Loading)

    val mainUiState: StateFlow<MainUiState> = combine(
        userPreferencesManager.authPreferencesFlow,
        _sessionUser
    ) { prefs, sessionUser ->
        sessionUser?.id ?: if (prefs.isLoggedIn) prefs.loggedInUserId else null
    }.flatMapLatest { userId ->
        if (userId != null) {
            userRepository.observeUserById(userId)
        } else {
            flowOf(null)
        }
    }.map { userEntity ->
        if (userEntity != null) {
            MainUiState.Success(userEntity, userEntity.role == UserRole.ADMIN)
        } else {
            MainUiState.Success(null, false)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MainUiState.Loading)


    fun onLoginSuccess(user: UserEntity) {
        _sessionUser.value = user
    }

    fun onLogout() {
        _sessionUser.value = null
        viewModelScope.launch {
            userPreferencesManager.clearLoginState()
        }
    }
}
