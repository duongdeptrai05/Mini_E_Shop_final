package com.example.mini_e_shop.presentation.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mini_e_shop.presentation.products_list.ProductCard

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
    // Thêm các tham số điều hướng khi cần
    onProductClick: (Int) -> Unit,
    onNavigateToAddEditProduct: (Int?) -> Unit
) {
    // Lấy trạng thái UI từ ViewModel
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is FavoritesUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is FavoritesUiState.Success -> {
            if (state.favoriteProducts.isEmpty()) {
                // Hiển thị thông báo nếu danh sách yêu thích trống
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Bạn chưa có sản phẩm yêu thích nào.",
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Hiển thị danh sách sản phẩm yêu thích bằng LazyVerticalGrid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.favoriteProducts) { product ->
                        // Tái sử dụng ProductCard
                        ProductCard(
                            product = product,
                            isAdmin = false, // Màn hình yêu thích luôn dành cho user thường
                            onEdit = { /* No-op */ },
                            onDelete = { /* No-op */ },
                            onClick = { onProductClick(product.id) },
                            onAddToCart = { /* TODO: Implement add to cart from favorites */ },
                            onToggleFavorite = { viewModel.removeFromFavorites(product) },
                            isFavorite = true // Tất cả sản phẩm ở đây đều là yêu thích
                        )
                    }
                }
            }
        }
        is FavoritesUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = state.message)
            }
        }
    }
}
