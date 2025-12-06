package com.example.mini_e_shop.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.domain.model.Product
import com.example.mini_e_shop.domain.repository.FavoriteRepository
import com.example.mini_e_shop.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// Trạng thái của giao diện màn hình yêu thích
sealed class FavoritesUiState {
    data object Loading : FavoritesUiState()
    data class Success(val favoriteProducts: List<Product>) : FavoritesUiState()
    data class Error(val message: String) : FavoritesUiState()
}

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // uiState sẽ tự động cập nhật khi danh sách yêu thích của người dùng thay đổi
    val uiState: StateFlow<FavoritesUiState> = userRepository.getCurrentUser()
        .flatMapLatest { user ->
            if (user != null) {
                // Nếu có user, bắt đầu lắng nghe danh sách sản phẩm yêu thích của họ
                favoriteRepository.getFavoriteProducts(user.id)
                    .map<List<Product>, FavoritesUiState> { products ->
                        FavoritesUiState.Success(products)
                    }
            } else {
                // Nếu không có user, trả về danh sách rỗng
                flowOf(FavoritesUiState.Success(emptyList()))
            }
        }
        .catch { e ->
            // Bắt lỗi nếu có
            emit(FavoritesUiState.Error(e.message ?: "Lỗi không xác định"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FavoritesUiState.Loading
        )

    // Hàm để xử lý khi người dùng bỏ yêu thích ngay trên màn hình này
    fun removeFromFavorites(product: Product) {
        viewModelScope.launch {
            userRepository.getCurrentUser().firstOrNull()?.let { user ->
                // Chúng ta chỉ cần gọi toggleFavorite, nó sẽ tự động xóa
                favoriteRepository.toggleFavorite(productId = product.id, userId = user.id)
            }
        }
    }
}
