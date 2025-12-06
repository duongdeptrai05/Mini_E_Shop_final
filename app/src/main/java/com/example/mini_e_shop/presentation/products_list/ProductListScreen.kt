package com.example.mini_e_shop.presentation.products_list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel,
    isAdmin: Boolean,
    onNavigateToAddEditProduct: (Int?) -> Unit,
    onProductClick: (Int) -> Unit,
    onNavigateToSupport: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    // Lấy category đang được chọn từ ViewModel
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedSortType by viewModel.selectedSortType.collectAsState()
    // EDIT: Add SnackbarHostState and CoroutineScope
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // EDIT: Add LaunchedEffect to listen for events from the ViewModel
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = event,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    Scaffold(
        // EDIT: Add the SnackbarHost to the Scaffold
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Điện tử Văn Mạnh", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                actions = {
                    IconButton(onClick = onNavigateToSupport) {
                        Icon(Icons.Default.ContactSupport, contentDescription = "Customer Support")
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
                    // Thanh lọc category
                    CategoryTabs(
                        categories = state.categories,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { viewModel.onCategorySelected(it) }
                    )

                    // THÊM THANH SẮP XẾP VÀO ĐÂY
                    SortOptions(
                        selectedSortType = selectedSortType,
                        onSortTypeSelected = { viewModel.onSortTypeSelected(it) }
                    )
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
                                onClick = { onProductClick(product.id) },
                                onAddToCart = { viewModel.addToCart(product) },
                                onToggleFavorite = { viewModel.toggleFavorite(product) },
                                isFavorite = state.favoriteStatusMap[product.id] ?: false
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
// Composable mới cho thanh lọc category
@Composable
private fun CategoryTabs(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (isSelected) PrimaryBlue else Color.LightGray.copy(alpha = 0.5f))
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = category,
                    color = if (isSelected) Color.White else Color.Black,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortOptions(
    selectedSortType: SortType,
    onSortTypeSelected: (SortType) -> Unit
) {
    // Dùng Map để lưu trữ tên hiển thị cho mỗi kiểu sắp xếp
    val sortTypes = mapOf(
        SortType.PRICE_ASC to "Giá tăng dần",
        SortType.PRICE_DESC to "Giá giảm dần",
        SortType.NAME_ASC to "Tên A-Z"
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sortTypes.keys.toList()) { sortType ->
            val isSelected = selectedSortType == sortType
            FilterChip(
                selected = isSelected,
                onClick = {
                    // Nếu đang chọn rồi mà bấm lại thì bỏ chọn (quay về mặc định)
                    if (isSelected) {
                        onSortTypeSelected(SortType.NONE)
                    } else {
                        onSortTypeSelected(sortType)
                    }
                },
                label = { Text(sortTypes[sortType] ?: "") },
                leadingIcon = if (isSelected) {
                    { Icon(Icons.Filled.Check, contentDescription = "Selected") }
                } else {
                    null
                }
            )
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
    // Thêm dòng này: Kiểm tra xem sản phẩm có hết hàng không
    val isOutOfStock = product.stock == 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        // Sửa dòng này: Nếu hết hàng thì không cho phép nhấn vào Card
        onClick = { if (!isOutOfStock) onClick() }
    ) {
        Column {
            Box(contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentScale = ContentScale.Crop
                )
                // Thêm phần này: Nếu hết hàng, hiển thị một lớp nền mờ và chữ "Hết hàng"
                if (isOutOfStock) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                    )
                    Text(
                        text = "Hết hàng",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(product.name, fontWeight = FontWeight.Bold, maxLines = 1, fontSize = 14.sp)
                Text(product.brand, color = Color.Gray, fontSize = 12.sp)

                // Sửa phần này: Tạo một Row để chứa Giá và Tồn kho
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Giá sản phẩm
                    Text(
                        text = "$${product.price}",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        fontSize = 16.sp
                    )
                    // Thêm Spacer này để đẩy số lượng tồn kho sang bên phải
                    Spacer(modifier = Modifier.weight(1f))
                    // Thêm Text này để hiển thị số lượng tồn kho
                    Text(
                        text = "Kho: ${product.stock}",
                        color = if (isOutOfStock) Color.Red else Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
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
                        IconButton(
                            onClick = onAddToCart,
                            modifier = Modifier.size(36.dp),
                            enabled = !isOutOfStock // Thêm thuộc tính enabled
                        ) {
                            Icon(
                                Icons.Outlined.AddShoppingCart,
                                contentDescription = "Add to Cart",
                                // Đổi màu icon nếu bị vô hiệu hóa
                                tint = if (isOutOfStock) Color.LightGray else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
