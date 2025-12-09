package com.example.mini_e_shop.presentation.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mini_e_shop.R
import com.example.mini_e_shop.domain.model.Order
import com.example.mini_e_shop.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(viewModel: OrderViewModel, onBack: () -> Unit) {
    val state by viewModel.orderState.collectAsState()

    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_orders), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                windowInsets = WindowInsets.statusBars // Sử dụng statusBars để tự động tính toán padding
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF3F4F6))
        ) {
            when (val currentState = state) {
                is OrderUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is OrderUiState.Empty -> {
                    Text(stringResource(R.string.no_orders), modifier = Modifier.align(Alignment.Center))
                }
                is OrderUiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(currentState.orders) { order ->
                            OrderRow(order = order)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderRow(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                // SỬA 1: Chuyển order.id thành String an toàn
                Text(
                    text = stringResource(R.string.order_number, order.id.toString()),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                // Kiểm tra null cho ngày tạo (nếu order.createdAt có thể null)
                Text(
                    text = order.createdAt ?: "Đang cập nhật", // Thêm giá trị mặc định
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // SỬA 2: Format tiền tệ thủ công thay vì dùng stringResource format số
            // Điều này tránh crash nếu order.totalAmount bị null hoặc sai kiểu
            val formattedTotal = try {
                // Giả sử totalAmount là Double
                "%,.0f đ".format(order.totalAmount)
            } catch (e: Exception) {
                "${order.totalAmount} đ"
            }

            Text(
                // Bạn có thể sửa string.xml dòng order_total thành: "Tổng tiền: %s"
                text = stringResource(R.string.order_total, formattedTotal),
                color = PrimaryBlue,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }
    }
}
