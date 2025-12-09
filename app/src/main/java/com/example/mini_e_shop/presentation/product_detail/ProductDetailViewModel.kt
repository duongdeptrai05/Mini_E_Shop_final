package com.example.mini_e_shop.presentation.product_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.domain.model.Product
import com.example.mini_e_shop.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.mini_e_shop.domain.repository.CartRepository
import com.example.mini_e_shop.domain.repository.UserRepository
import kotlinx.coroutines.flow.firstOrNull

// Trạng thái của giao diện màn hình chi tiết
sealed class ProductDetailUiState {
    data object Loading : ProductDetailUiState()
    data class Success(val product: Product) : ProductDetailUiState()
    data class Added(val product: Product, val message: String) : ProductDetailUiState()
    data class Error(val message: String) : ProductDetailUiState()
}

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        // Lấy productId từ arguments
        val productId: String? = savedStateHandle.get("productId")
        if (productId != null) {
            fetchProductDetails(productId)
        } else {
            _uiState.value = ProductDetailUiState.Error("Không tìm thấy ID sản phẩm.")
        }
    }

    private fun fetchProductDetails(productId: String) {
        viewModelScope.launch {
            try {
                // Gọi repository để lấy sản phẩm
                val product = productRepository.getProductById(productId)
                if (product != null) {
                    _uiState.value = ProductDetailUiState.Success(product)
                } else {
                    _uiState.value = ProductDetailUiState.Error("Không tìm thấy sản phẩm.")
                }
            } catch (e: Exception) {
                _uiState.value = ProductDetailUiState.Error("Lỗi khi tải dữ liệu: ${e.message}")
            }
        }
    }

    fun onAddToCart(product: Product) {
        viewModelScope.launch {
            try {
                // Lấy người dùng hiện tại đang đăng nhập
                val currentUser = userRepository.getCurrentUser().firstOrNull()

                if (currentUser != null) {
                    // Gọi hàm với đầy đủ 2 tham số: product và userId
                    cartRepository.addProductToCart(product, currentUser.id)

                    _uiState.value = ProductDetailUiState.Added(
                        product = product,
                        message = "Đã thêm ${product.name} vào giỏ hàng"
                    )
                } else {
                    _uiState.value = ProductDetailUiState.Error("Vui lòng đăng nhập để thêm giỏ hàng")
                }
            } catch (e: Exception){
                _uiState.value = ProductDetailUiState.Error("Lỗi khi thêm giỏ hàng: ${e.message}")
            }
        }
    }

    fun resetToSuccess(product: Product) {
        _uiState.value = ProductDetailUiState.Success(product)
    }
}
