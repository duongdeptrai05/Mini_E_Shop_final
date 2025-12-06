package com.example.mini_e_shop.presentation.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mini_e_shop.domain.model.CartItem
import com.example.mini_e_shop.domain.model.Product
import com.example.mini_e_shop.ui.theme.PrimaryBlue
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

// Data class này đã có trong ViewModel, nhưng để ở đây để file không báo lỗi
// và để các composable có thể tham chiếu
data class CartItemDetails(
    val cartItem: CartItem,
    val product: Product
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel(),
    // SỬA: Nhận một lambda function để điều hướng
    onNavigateToCheckout: (String) -> Unit)
{
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect {event ->
            when (event) {
            is CartViewEvent.NavigateToCheckout -> {
                onNavigateToCheckout(event.cartItemIds)
            }
            is CartViewEvent.ShowSnackbar -> {
                // (Logic này sẽ dùng cho thông báo sau này)
            }
        }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Giỏ hàng của bạn", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        // SỬA: BottomBar giờ đây phức tạp hơn
        bottomBar = {
            if (state is CartUiState.Success) {
                val successState = state as CartUiState.Success
                CheckoutBar(
                    checkoutPrice = successState.checkoutPrice,
                    isAllSelected = successState.isAllSelected,
                    onSelectAll = viewModel::onSelectAllChecked,
                    onCheckout = viewModel::onCheckoutClick,
                    // Nút Mua hàng chỉ bật khi có ít nhất 1 sản phẩm được chọn
                    isCheckoutEnabled = successState.checkoutPrice > 0,
                    // Đếm số lượng item được chọn để hiển thị trên nút
                    selectedItemsCount = successState.selectableItems.count { it.isSelected }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF3F4F6))
        ) {
            when (val currentState = state) {
                is CartUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is CartUiState.Empty -> {
                    Text("Giỏ hàng của bạn đang trống.", modifier = Modifier.align(Alignment.Center))
                }
                is CartUiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(currentState.selectableItems, key = { it.details.cartItem.id }) { item ->
                            CartItemRow(
                                item = item.details,
                                isSelected = item.isSelected,
                                onCheckedChange = { isChecked ->
                                    viewModel.onItemCheckedChanged(item.details.cartItem.id, isChecked)
                                },
                                onQuantityChange = viewModel::onQuantityChange
                            )
                        }
                    }
                }
            }
        }
    }
}

// SỬA: CartItemRow giờ có thêm Checkbox và ảnh thật
@Composable
fun CartItemRow(
    item: CartItemDetails,
    isSelected: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onQuantityChange: (Int, Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = onCheckedChange
            )
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.product.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = item.product.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("$${item.product.price}", color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.width(16.dp))
            QuantitySelector(
                item = item.cartItem,
                onQuantityChange = { newQuantity -> onQuantityChange(item.cartItem.id, newQuantity) }
            )
        }
    }
}

// QuantitySelector giữ nguyên
@Composable
fun QuantitySelector(item: CartItem, onQuantityChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onQuantityChange(item.quantity - 1) }) {
            Icon(if (item.quantity == 1) Icons.Default.Delete else Icons.Default.Remove, contentDescription = "Remove")
        }
        Text("${item.quantity}", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
        IconButton(onClick = { onQuantityChange(item.quantity + 1) }) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}

// SỬA: CheckoutBar được viết lại hoàn toàn
@Composable
private fun CheckoutBar(
    checkoutPrice: Double,
    isAllSelected: Boolean,
    onSelectAll: (Boolean) -> Unit,
    isCheckoutEnabled: Boolean,
    selectedItemsCount: Int,
    onCheckout: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp), // Để thanh bar phẳng
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isAllSelected,
                        onCheckedChange = onSelectAll
                    )
                    Text("Tất cả")
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Tổng thanh toán", color = Color.Gray, fontSize = 14.sp)
                    Text(
                        "$${String.format("%.2f", checkoutPrice)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = PrimaryBlue
                    )
                }
            }
            Button(
                onClick = onCheckout,
                enabled = isCheckoutEnabled, // Bật/tắt nút dựa trên trạng thái
                shape = RoundedCornerShape(0.dp), // Nút hình chữ nhật
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    disabledContainerColor = Color.LightGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Default.ShoppingCartCheckout, contentDescription = "Checkout")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mua hàng ($selectedItemsCount)", fontWeight = FontWeight.Bold)
            }
        }
    }
}
