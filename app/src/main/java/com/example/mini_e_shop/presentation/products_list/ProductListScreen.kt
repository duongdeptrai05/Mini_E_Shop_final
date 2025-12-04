package com.example.mini_e_shop.presentation.products_list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mini_e_shop.domain.model.Product
import com.example.mini_e_shop.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel,
    isAdmin: Boolean,
    onNavigateToAddEditProduct: (Int?) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ShopMini", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                actions = {
                    IconButton(onClick = { /* Navigate to cart */ }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(onClick = { onNavigateToAddEditProduct(null) }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Product")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            SearchBar(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) }
            )
            when (val state = uiState) {
                is ProductListUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ProductListUiState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.products) { product ->
                            ProductCard(
                                product = product,
                                isAdmin = isAdmin,
                                onEdit = { onNavigateToAddEditProduct(product.id) },
                                onDelete = { viewModel.deleteProduct(product) },
                                onClick = { /* TODO: Navigate to Product Detail Screen */ },
                                onAddToCart = { viewModel.addToCart(product) },
                                onToggleFavorite = { viewModel.toggleFavorite(product) },
                                isFavorite = false // TODO: Get real favorite state from ViewModel
                            )
                        }
                    }
                }
                is ProductListUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message)
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("Tìm kiếm sản phẩm...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
        shape = RoundedCornerShape(50),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.LightGray,
            focusedBorderColor = PrimaryBlue
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(
    product: Product,
    isAdmin: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    onToggleFavorite: () -> Unit,
    isFavorite: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClick
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(product.imageUrl).crossfade(true).build(),
                contentDescription = product.name,
                modifier = Modifier.fillMaxWidth().height(140.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(product.name, fontWeight = FontWeight.Bold, maxLines = 1, fontSize = 14.sp)
                Text(product.brand, color = Color.Gray, fontSize = 12.sp)
                Text("$${product.price}", fontWeight = FontWeight.Bold, color = PrimaryBlue, fontSize = 16.sp)
            }
            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                if (isAdmin) {
                    Row {
                        IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray, modifier = Modifier.size(20.dp))
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(20.dp))
                        }
                    }
                } else {
                    Row {
                        IconButton(onClick = onToggleFavorite, modifier = Modifier.size(36.dp)) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) Color.Red else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(onClick = onAddToCart, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Outlined.AddShoppingCart, contentDescription = "Add to Cart", tint = Color.Gray, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}
