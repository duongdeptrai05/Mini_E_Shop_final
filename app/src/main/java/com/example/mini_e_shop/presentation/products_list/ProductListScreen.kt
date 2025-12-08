package com.example.mini_e_shop.presentation.products_list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mini_e_shop.R
import com.example.mini_e_shop.domain.model.Product
import com.example.mini_e_shop.ui.theme.*
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
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedSortType by viewModel.selectedSortType.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            CompactHeader(
                searchQuery = searchQuery,
                onSearchChange = { viewModel.onSearchQueryChange(it) },
                onSupportClick = onNavigateToSupport
            )

            when (val state = uiState) {
                is ProductListUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ProductListUiState.Success -> {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        CompactSortButton(
                            selectedSortType = selectedSortType,
                            onSortTypeSelected = { viewModel.onSortTypeSelected(it) }
                        )

                        CategoryTabs(
                            categories = state.categories,
                            selectedCategory = selectedCategory,
                            onCategorySelected = { viewModel.onCategorySelected(it) }
                        )
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp), // Tăng bottom padding để tránh bị menu che khuất nội dung cuối
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


@Composable
fun CompactHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSupportClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp), // chừa an toàn cho tai thỏ
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.dinh_manh_electronics),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(HomeColor.copy(alpha = 0.1f))
                    .clickable(onClick = onSupportClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ContactSupport,
                    contentDescription = stringResource(id = R.string.support),
                    tint = HomeColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp), 
            placeholder = {
                Text(stringResource(id = R.string.search_products), fontSize = 14.sp, color = TextLight)
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
            },
            shape = RoundedCornerShape(25.dp), 
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = BorderLight,
                focusedBorderColor = PrimaryIndigo,
                unfocusedContainerColor = BackgroundCard,
                focusedContainerColor = BackgroundCard,
                unfocusedTextColor = TextPrimary,
                focusedTextColor = TextPrimary
            ),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun CompactSortButton(
    selectedSortType: SortType,
    onSortTypeSelected: (SortType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.padding(start = 16.dp)) {
        Box(
            modifier = Modifier
                .size(40.dp) 
                .clip(RoundedCornerShape(12.dp))
                .background(if(selectedSortType != SortType.NONE) PrimaryIndigo else SurfaceLight)
                .border(1.dp, if(selectedSortType != SortType.NONE) PrimaryIndigo else BorderLight, RoundedCornerShape(12.dp))
                .clickable { expanded = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Sort,
                contentDescription = stringResource(id = R.string.sort),
                tint = if(selectedSortType != SortType.NONE) Color.White else TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            val options = mapOf(
                SortType.PRICE_ASC to stringResource(id = R.string.price_asc),
                SortType.PRICE_DESC to stringResource(id = R.string.price_desc),
                SortType.NAME_ASC to stringResource(id = R.string.name_asc),
                SortType.NAME_DESC to stringResource(id = R.string.name_desc)
            )

            options.forEach { (type, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onSortTypeSelected(type)
                        expanded = false
                    },
                    leadingIcon = {
                        if (selectedSortType == type) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryIndigo)
                        }
                    }
                )
            }
            Divider()
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.default_sort), color = TextSecondary) },
                onClick = {
                    onSortTypeSelected(SortType.NONE)
                    expanded = false
                }
            )
        }
    }
}

@Composable
private fun getCategoryStringResource(category: String): String {
    return when (category) {
        "Tất cả" -> stringResource(id = R.string.category_all)
        "Điện thoại" -> stringResource(id = R.string.category_phone)
        "Laptop" -> stringResource(id = R.string.category_laptop)
        "Tai nghe" -> stringResource(id = R.string.category_headphone)
        "Máy tính bảng" -> stringResource(id = R.string.category_tablet)
        "Đồng hồ" -> stringResource(id = R.string.category_watch)
        "Máy ảnh" -> stringResource(id = R.string.category_camera)
        "Gaming" -> stringResource(id = R.string.category_gaming)
        "Màn hình" -> stringResource(id = R.string.category_monitor)
        "Phụ kiện" -> stringResource(id = R.string.category_accessory)
        else -> category
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryTabs(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = getCategoryStringResource(category = category),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryIndigo,
                    selectedLabelColor = Color.White,
                    containerColor = SurfaceLight,
                    labelColor = TextSecondary
                ),
                border = BorderStroke(1.dp, if(isSelected) Color.Transparent else BorderLight),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.height(32.dp)
            )
        }
    }
}

// --- PRODUCT CARD MỚI (ĐÃ TỐI ƯU KHÔNG GIAN) ---
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
    // Mock data để demo giao diện đầy đủ
    val rating = 4.8
    val soldCount = 120

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(285.dp), // Tăng chiều cao để chứa đủ info
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard),
        onClick = { if (!isOutOfStock) onClick() }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- PHẦN 1: ẢNH ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Nút Favorite/Admin
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    if (isAdmin) {
                        Row(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(4.dp))
                                .padding(2.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = stringResource(id = R.string.edit), tint = PrimaryIndigo, modifier = Modifier.size(20.dp).clickable { onEdit() })
                            Icon(imageVector = Icons.Default.Delete, contentDescription = stringResource(id = R.string.delete), tint = ErrorRed, modifier = Modifier.size(20.dp).clickable { onDelete() })
                        }
                    } else {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = stringResource(id = R.string.favorites),
                            tint = if (isFavorite) Color.Red else Color.Gray,
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color.White.copy(alpha = 0.7f), CircleShape)
                                .padding(2.dp)
                                .clickable(onClick = onToggleFavorite)
                        )
                    }
                }

                if (isOutOfStock) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.out_of_stock).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            // --- PHẦN 2: THÔNG TIN CHI TIẾT ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Tên
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )

                // Tag Thương hiệu
                Surface(
                    color = SurfaceLight,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        text = product.brand.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        fontSize = 10.sp
                    )
                }

                // Đánh giá & Đã bán
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "$rating",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.sold_count, soldCount),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Giá & Nút Mua
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${String.format("%.0f", product.price)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryIndigo,
                        fontSize = 18.sp
                    )

                    if (!isAdmin && !isOutOfStock) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CartColor)
                                .clickable(onClick = onAddToCart),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.AddShoppingCart,
                                contentDescription = stringResource(id = R.string.add_to_cart),
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
