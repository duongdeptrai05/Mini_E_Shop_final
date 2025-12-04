package com.example.mini_e_shop.presentation.products_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.data.preferences.UserPreferencesManager
import com.example.mini_e_shop.domain.model.Product
import com.example.mini_e_shop.domain.repository.CartRepository
import com.example.mini_e_shop.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _userId = MutableStateFlow<Int?> (null)

    init {
        getProducts()
        loadUserId()
    }

    private fun getProducts() {
        // Combine the products flow with the search query flow
        productRepository.getAllProducts()
            .combine(searchQuery) { products, query ->
                if (query.isBlank()) {
                    products // Return all products if query is empty
                } else {
                    products.filter { it.name.contains(query, ignoreCase = true) } // Filter by name
                }
            }
            .onEach { filteredProducts ->
                _uiState.value = ProductListUiState.Success(filteredProducts)
            }
            .catch { e ->
                _uiState.value = ProductListUiState.Error(e.message ?: "Đã có lỗi không xác định xảy ra")
            }
            .launchIn(viewModelScope)
    }

    private fun loadUserId() {
        viewModelScope.launch {
            val prefs = userPreferencesManager.authPreferencesFlow.first()
            if (prefs.isLoggedIn) {
                _userId.value = prefs.loggedInUserId
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            try {
                productRepository.deleteProduct(product)
            } catch (e: Exception) {
                _uiState.value = ProductListUiState.Error("Xóa sản phẩm thất bại: ${e.message}")
            }
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            val currentUserId = _userId.value
            if (currentUserId != null) {
                cartRepository.addProductToCart(product, currentUserId)
                // TODO: Hiển thị thông báo "Đã thêm vào giỏ hàng"
            } else {
                // TODO: Xử lý trường hợp không tìm thấy user (ví dụ: hiển thị thông báo yêu cầu đăng nhập)
            }
        }
    }

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            // TODO: Triển khai logic cho chức năng Yêu thích
            println("Toggled favorite for ${product.name}")
        }
    }
}

sealed class ProductListUiState {
    object Loading : ProductListUiState()
    data class Success(val products: List<Product>) : ProductListUiState()
    data class Error(val message: String) : ProductListUiState()
}
