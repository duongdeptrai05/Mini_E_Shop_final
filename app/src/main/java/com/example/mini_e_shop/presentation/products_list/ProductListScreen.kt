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
import com.example.mini_e_shop.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel,
    isAdmin: Boolean,
    onNavigateToAddEditProduct: (String?) -> Unit,
    onProductClick: (String) -> Unit,
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
                title = { 
                    Text(
                        "Điện tử Văn Mạnh", 
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(HomeColor.copy(alpha = 0.1f))
                            .clickable(onClick = onNavigateToSupport)
                            .padding(8.dp)
                    ) {
                        Icon(
                            Icons.Default.ContactSupport, 
                            contentDescription = "Customer Support",
                            tint = HomeColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HomeColor.copy(alpha = 0.05f),
                    titleContentColor = HomeColor,
                    actionIconContentColor = HomeColor
                ),
//                elevation = TopAppBarDefaults.topAppBarElevation(defaultElevation = 0.dp)
            )
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = { onNavigateToAddEditProduct(null) },
                    containerColor = PrimaryIndigo,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Product")
                }
            }
        },
        containerColor = BackgroundLight
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
// Composable mới cho thanh lọc category - Improved design
@Composable
private fun CategoryTabs(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundCard),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryIndigo,
                    selectedLabelColor = Color.White,
                    containerColor = SurfaceLight,
                    labelColor = TextSecondary
                ),
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortOptions(
    selectedSortType: SortType,
    onSortTypeSelected: (SortType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    val sortTypes = mapOf(
        SortType.PRICE_ASC to "Giá tăng dần",
        SortType.PRICE_DESC to "Giá giảm dần",
        SortType.NAME_ASC to "Tên A-Z"
    )
    
    val selectedText = sortTypes[selectedSortType] ?: "Sắp xếp"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        FilterChip(
            selected = selectedSortType != SortType.NONE,
            onClick = { expanded = true },
            label = { 
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Sort,
                        contentDescription = "Sort",
                        modifier = Modifier.size(18.dp)
                    )
                    Text(selectedText)
                }
            },
            trailingIcon = {
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Thu gọn" else "Mở rộng",
                    modifier = Modifier.size(18.dp)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = HomeColor.copy(alpha = 0.15f),
                selectedLabelColor = HomeColor,
                containerColor = SurfaceLight,
                labelColor = TextSecondary
            ),
            shape = RoundedCornerShape(12.dp)
        )
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            DropdownMenuItem(
                text = { Text("Giá tăng dần") },
                onClick = {
                    onSortTypeSelected(SortType.PRICE_ASC)
                    expanded = false
                },
                leadingIcon = {
                    if (selectedSortType == SortType.PRICE_ASC) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = HomeColor)
                    }
                }
            )
            DropdownMenuItem(
                text = { Text("Giá giảm dần") },
                onClick = {
                    onSortTypeSelected(SortType.PRICE_DESC)
                    expanded = false
                },
                leadingIcon = {
                    if (selectedSortType == SortType.PRICE_DESC) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = HomeColor)
                    }
                }
            )
            DropdownMenuItem(
                text = { Text("Tên A-Z") },
                onClick = {
                    onSortTypeSelected(SortType.NAME_ASC)
                    expanded = false
                },
                leadingIcon = {
                    if (selectedSortType == SortType.NAME_ASC) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = HomeColor)
                    }
                }
            )
            if (selectedSortType != SortType.NONE) {
                Divider()
                DropdownMenuItem(
                    text = { Text("Bỏ sắp xếp", color = TextSecondary) },
                    onClick = {
                        onSortTypeSelected(SortType.NONE)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SearchBar(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        placeholder = { 
            Text(
                "Tìm kiếm sản phẩm...",
                color = TextLight
            ) 
        },
        leadingIcon = { 
            Icon(
                Icons.Default.Search, 
                contentDescription = "Search Icon",
                tint = TextSecondary
            ) 
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = BorderLight,
            focusedBorderColor = PrimaryIndigo,
            unfocusedContainerColor = BackgroundCard,
            focusedContainerColor = BackgroundCard,
            cursorColor = PrimaryIndigo
        ),
        singleLine = true
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
    val isOutOfStock = product.stock == 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp,
            hoveredElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard),
        onClick = { if (!isOutOfStock) onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Image section with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // Favorite/Admin buttons overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    if (isAdmin) {
                        Row(
                            modifier = Modifier
                                .background(
                                    Color.White.copy(alpha = 0.9f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = onEdit,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = PrimaryIndigo,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(
                                onClick = onDelete,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = ErrorRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(
                                    if (isFavorite) FavoritesColor.copy(alpha = 0.2f) 
                                    else Color.White.copy(alpha = 0.95f)
                                )
                                .clickable(onClick = onToggleFavorite),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) FavoritesColor else TextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
                
                // Out of stock overlay
                if (isOutOfStock) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f))
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ErrorRed),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Hết hàng",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
            
            // Content section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = product.brand,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${String.format("%.2f", product.price)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryIndigo
                    )
                    if (!isAdmin) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isOutOfStock) SurfaceLight 
                                    else CartColor.copy(alpha = 0.15f)
                                )
                                .clickable(
                                    enabled = !isOutOfStock,
                                    onClick = onAddToCart
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.AddShoppingCart,
                                contentDescription = "Add to Cart",
                                tint = if (isOutOfStock) TextLight else CartColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        Text(
                            text = "Kho: ${product.stock}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isOutOfStock) ErrorRed else TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
