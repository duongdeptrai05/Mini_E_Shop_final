package com.example.mini_e_shop.presentation.products_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.domain.model.Product
import com.example.mini_e_shop.domain.repository.CartRepository
import com.example.mini_e_shop.domain.repository.FavoriteRepository // BƯỚC 1: Import FavoriteRepository
import com.example.mini_e_shop.domain.repository.ProductRepository
import com.example.mini_e_shop.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository,
    private val favoriteRepository: FavoriteRepository // BƯỚC 2: Inject FavoriteRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // SỬA: _uiState sẽ kết hợp cả sản phẩm và danh sách yêu thích
    val uiState: StateFlow<ProductListUiState> = combine(
        productRepository.getAllProducts(),
        searchQuery.debounce(300),
        // Lấy danh sách ID sản phẩm yêu thích của người dùng hiện tại
        userRepository.getCurrentUser().flatMapLatest { user ->
            if (user != null) {
                favoriteRepository.getFavoriteProductIds(user.id)
            } else {
                flowOf(emptySet())
            }
        }
    ) { allProducts, query, favoriteIds ->
        val filteredProducts = if (query.isBlank()) {
            allProducts
        } else {
            allProducts.filter {
                it.name.contains(query, ignoreCase = true) || it.brand.contains(query, ignoreCase = true)
            }
        }
        // Tạo một map để kiểm tra sản phẩm có trong danh sách yêu thích không
        val favoriteStatusMap = filteredProducts.associate { it.id to (it.id in favoriteIds) }
        ProductListUiState.Success(filteredProducts, favoriteStatusMap)
    }
        .catch<ProductListUiState> { e -> emit(ProductListUiState.Error(e.message ?: "Lỗi không xác định")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProductListUiState.Loading)


    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            productRepository.deleteProduct(product)
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            userRepository.getCurrentUser().firstOrNull()?.let { user ->
                cartRepository.addProductToCart(product, user.id)
            }
        }
    }

    // BƯỚC 3: Viết logic hoàn chỉnh cho toggleFavorite
    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            userRepository.getCurrentUser().firstOrNull()?.let { user ->
                favoriteRepository.toggleFavorite(productId = product.id, userId = user.id)
            }
        }
    }
}


// SỬA: Thêm favoriteStatusMap vào trạng thái Success
sealed class ProductListUiState {
    object Loading : ProductListUiState()
    data class Success(
        val products: List<Product>,
        val favoriteStatusMap: Map<Int, Boolean> = emptyMap()
    ) : ProductListUiState()
    data class Error(val message: String) : ProductListUiState()
}
