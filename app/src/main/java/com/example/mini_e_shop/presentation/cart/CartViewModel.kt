package com.example.mini_e_shop.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.domain.model.Product
import com.example.mini_e_shop.domain.repository.CartRepository
import com.example.mini_e_shop.domain.repository.OrderRepository
import com.example.mini_e_shop.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// LỚP WRAPPER ĐỂ QUẢN LÝ TRẠNG THÁI 'isSelected'
data class SelectableCartItem(
    val details: CartItemDetails,
    val isSelected: Boolean = true // Mặc định là được chọn khi vào giỏ hàng
)

// CẬP NHẬT LẠI UI STATE
sealed class CartUiState {
    object Loading : CartUiState()
    object Empty : CartUiState()
    data class Success(
        val selectableItems: List<SelectableCartItem> = emptyList(),
        val checkoutPrice: Double = 0.0,
        val isAllSelected: Boolean = true
    ) : CartUiState()
}
// --- THÊM SEALED CLASS CHO CÁC SỰ KIỆN VIEW ---
sealed class CartViewEvent {
    data class NavigateToCheckout(val cartItemIds: String) : CartViewEvent()
    data class ShowSnackbar(val message: String) : CartViewEvent()
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _cartItems = MutableStateFlow<List<SelectableCartItem>>(emptyList())
    private val _eventChannel = Channel<CartViewEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    // uiState là State cuối cùng được tính toán và đưa ra cho UI
    val uiState: StateFlow<CartUiState> = _cartItems
        .map { items ->
            if (items.isEmpty()) {
                CartUiState.Empty
            } else {
                // Tính toán tổng tiền chỉ dựa trên các item được chọn
                val checkoutPrice = items.filter { it.isSelected }
                    .sumOf { it.details.product.price * it.details.cartItem.quantity }

                // Kiểm tra xem có phải tất cả item đều được chọn không
                val isAllSelected = items.isNotEmpty() && items.all { it.isSelected }

                CartUiState.Success(
                    selectableItems = items,
                    checkoutPrice = checkoutPrice,
                    isAllSelected = isAllSelected
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartUiState.Loading
        )

    init {
        observeCartItems()
    }

    private fun observeCartItems() {
        viewModelScope.launch {
            // Lắng nghe user, sau đó chuyển sang lắng nghe giỏ hàng của user đó
            userRepository.getCurrentUser().flatMapLatest { user ->
                if (user != null) {
                    cartRepository.getCartItems(user.id)
                } else {
                    flowOf(emptyList()) // Nếu không có user, trả về list rỗng
                }
            }.collect { cartItemDetails ->
                // Mỗi khi dữ liệu từ DB thay đổi, cập nhật lại _cartItems
                // Giữ lại trạng thái isSelected của các item đã có
                val currentSelection = _cartItems.value.associateBy { it.details.cartItem.id }
                _cartItems.value = cartItemDetails.map { detail ->
                    SelectableCartItem(
                        details = detail,
                        isSelected = currentSelection[detail.cartItem.id]?.isSelected ?: true
                    )
                }
            }
        }
    }

    // CÁC HÀM MỚI ĐỂ XỬ LÝ SỰ KIỆN
    fun onItemCheckedChanged(cartItemId: Int, isChecked: Boolean) {
        _cartItems.value = _cartItems.value.map {
            if (it.details.cartItem.id == cartItemId) {
                it.copy(isSelected = isChecked)
            } else {
                it
            }
        }
    }

    fun onSelectAllChecked(isChecked: Boolean) {
        _cartItems.value = _cartItems.value.map { it.copy(isSelected = isChecked) }
    }

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
            val currentUserId = userRepository.getCurrentUser().firstOrNull()?.id ?: return@launch
            val itemsToCheckout = _cartItems.value.filter { it.isSelected }.map { it.details }

            if (itemsToCheckout.isNotEmpty()) {
                orderRepository.createOrderFromCart(currentUserId, itemsToCheckout)
                // Thay vì xóa toàn bộ giỏ hàng, chỉ xóa những item đã đặt
                itemsToCheckout.forEach {
                    cartRepository.removeItem(it.cartItem.id)
                }
            }
        }
    }
    // --- HÀM MỚI ĐỂ XỬ LÝ SỰ KIỆN CLICK NÚT MUA HÀNG ---
    fun onCheckoutClick() {
        viewModelScope.launch {
            val selectedIds = _cartItems.value
                .filter { it.isSelected }
                .map { it.details.cartItem.id }
                .joinToString(separator = ",")

            if (selectedIds.isNotEmpty()) {
                _eventChannel.send(CartViewEvent.NavigateToCheckout(selectedIds))
            }
        }
    }
}
