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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// BƯỚC 1: Tạo enum class để định nghĩa các kiểu sắp xếp
enum class SortType {
    NONE, // Mặc định
    PRICE_ASC, // Giá tăng dần
    PRICE_DESC, // Giá giảm dần
    NAME_ASC, // Tên A-Z
    NAME_DESC // Tên Z-A
}

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

    private val _selectedCategory = MutableStateFlow<String?>("All") // Mặc định là "All"
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedSortType = MutableStateFlow(SortType.NONE)
    val selectedSortType = _selectedSortType.asStateFlow()

    // Tạo Channel và Flow cho sự kiện
    private val _eventChannel = Channel<String>()
    val eventFlow = _eventChannel.receiveAsFlow()

    // SỬA: _uiState sẽ kết hợp cả sản phẩm và danh sách yêu thích
    val uiState: StateFlow<ProductListUiState> = combine(
        productRepository.getAllProducts(),
        searchQuery.debounce(300),
        _selectedCategory,
        _selectedSortType,
        // Lấy danh sách ID sản phẩm yêu thích của người dùng hiện tại
        userRepository.getCurrentUser().flatMapLatest { user ->
            if (user != null) {
                favoriteRepository.getFavoriteProductIds(user.id)
            } else {
                flowOf(emptySet())
            }
        }
    ) { allProducts, query, category, sortType, favoriteIds ->
        // Lấy ra danh sách các category duy nhất từ tất cả sản phẩm
        val categories = listOf("All") + allProducts.map { it.category }.distinct()

        // Lọc theo search query trước
        val searchedProducts = if (query.isBlank()) {
            allProducts
        } else {
            allProducts.filter {
                it.name.contains(query, ignoreCase = true) || it.brand.contains(query, ignoreCase = true)
            }
        }
        //--------------
        // Lọc tiếp theo category
        val categorizedProducts = if (category == "All") {
            searchedProducts
        } else {
            searchedProducts.filter { it.category == category }
        }

        // BƯỚC 4: Sắp xếp danh sách ở bước cuối cùng
        val sortedProducts = when (sortType) {
            SortType.PRICE_ASC -> categorizedProducts.sortedBy { it.price }
            SortType.PRICE_DESC -> categorizedProducts.sortedByDescending { it.price }
            SortType.NAME_ASC -> categorizedProducts.sortedBy { it.name }
            SortType.NAME_DESC -> categorizedProducts.sortedByDescending { it.name }
            SortType.NONE -> categorizedProducts // Không sắp xếp
        }

        val favoriteStatusMap = sortedProducts.associate { it.id to (it.id in favoriteIds) }
        // Trả về cả danh sách sản phẩm, danh sách category và trạng thái yêu thích
        ProductListUiState.Success(sortedProducts, categories, favoriteStatusMap)

    }
        .catch<ProductListUiState> { e -> emit(ProductListUiState.Error(e.message ?: "Lỗi không xác định")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProductListUiState.Loading)


    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            // Kiểm tra quyền admin trước khi xóa
            val currentUser = userRepository.getCurrentUser().firstOrNull()
            if (currentUser?.isAdmin == true) {
                productRepository.deleteProduct(product)
            } else {
                // Gửi thông báo lỗi nếu không phải admin
                _eventChannel.send("PERMISSION_DENIED")
            }
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            userRepository.getCurrentUser().firstOrNull()?.let { user ->
                try {
                    // Check if the product is in stock
                    if (product.stock > 0) {
                        cartRepository.addProductToCart(product, user.id)
                        // Gửi thông báo thành công qua Channel với format có thể dịch được
                        _eventChannel.send("ADD_TO_CART_SUCCESS:${product.name}")
                    } else {
                        _eventChannel.send("PRODUCT_OUT_OF_STOCK")
                    }
                } catch (e: Exception) {
                    _eventChannel.send("ADD_TO_CART_ERROR")
                }
            } ?: _eventChannel.send("PLEASE_LOGIN")
        }
    }
    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            userRepository.getCurrentUser().firstOrNull()?.let { user ->
                try {
                    // isFavorite bây giờ cần được tính toán trước khi toggle
                    val isCurrentlyFavorite = favoriteRepository.getFavoriteProductIds(user.id).first().contains(product.id)
                    favoriteRepository.toggleFavorite(productId = product.id, userId = user.id)

                    // Gửi thông báo tương ứng với format có thể dịch được
                    if (isCurrentlyFavorite) {
                        _eventChannel.send("REMOVE_FROM_FAVORITES_SUCCESS:${product.name}")
                    } else {
                        _eventChannel.send("ADD_TO_FAVORITES_SUCCESS:${product.name}")
                    }
                } catch (e: Exception) {
                    _eventChannel.send("FAVORITE_ACTION_ERROR")
                }
            } ?: _eventChannel.send("PLEASE_LOGIN")
        }
    }
    fun onSortTypeSelected(sortType: SortType) {
        _selectedSortType.value = sortType
    }
}


// SỬA: Thêm favoriteStatusMap vào trạng thái Success
sealed class ProductListUiState {
    object Loading : ProductListUiState()
    data class Success(
        val products: List<Product>,
        val categories: List<String> = emptyList(),
        val favoriteStatusMap: Map<String, Boolean> = emptyMap()
    ) : ProductListUiState()
    data class Error(val message: String) : ProductListUiState()
}
