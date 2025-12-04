package com.example.mini_e_shop.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.data.preferences.UserPreferencesManager
import com.example.mini_e_shop.domain.repository.CartRepository
import com.example.mini_e_shop.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _userId = MutableStateFlow<Int?>(null)

    val cartState: StateFlow<CartUiState> = userPreferencesManager.authPreferencesFlow
        .flatMapLatest { prefs ->
            _userId.value = if (prefs.isLoggedIn) prefs.loggedInUserId else null
            if (prefs.isLoggedIn) {
                cartRepository.getCartItems(prefs.loggedInUserId)
            } else {
                kotlinx.coroutines.flow.flowOf(emptyList())
            }
        }
        .map { items ->
            if (items.isEmpty()) {
                CartUiState.Empty
            } else {
                val totalPrice = items.sumOf { it.product.price * it.cartItem.quantity }
                CartUiState.Success(items, totalPrice)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartUiState.Loading
        )

    fun onQuantityChange(cartItemId: Int, newQuantity: Int) {
        viewModelScope.launch {
            if (newQuantity > 0) {
                cartRepository.updateQuantity(cartItemId, newQuantity)
            } else {
                cartRepository.removeItem(cartItemId)
            }
        }
    }

    fun placeOrder() {
        viewModelScope.launch {
            val currentUserId = _userId.value ?: return@launch
            val currentState = cartState.value
            if (currentState is CartUiState.Success) {
                orderRepository.createOrderFromCart(currentUserId, currentState.items)
                cartRepository.clearCart(currentUserId)
            }
        }
    }
}

sealed class CartUiState {
    object Loading : CartUiState()
    object Empty : CartUiState()
    data class Success(
        val items: List<CartItemDetails>,
        val totalPrice: Double
    ) : CartUiState()
}
