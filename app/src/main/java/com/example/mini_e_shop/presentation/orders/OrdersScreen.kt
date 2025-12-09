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
import java.text.SimpleDateFormat // Thêm import này vào đầu file
import java.util.Locale
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
    // Hàm định dạng tiền tệ (Giữ lại hàm này)
    fun formatCurrency(amount: Double?): String {
        return try {
            val formatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("vi", "VN"))
            formatter.format(amount ?: 0.0)
        } catch (e: Exception) {
            "${amount} đ"
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Dòng 1: Mã đơn & Ngày
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Mã: ...${order.id.takeLast(8)}", // Rút gọn ID
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = order.createdAt ?: "", // Đã format đẹp từ Repository rồi
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            // Dòng 2: Trạng thái
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Trạng thái: ", fontSize = 14.sp, color = Color.Gray)
                Text(
                    text = order.status ?: "Đã thanh toán",
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF228B22)
                )
            }

            // Dòng 3: Tổng tiền
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = formatCurrency(order.totalAmount),
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}
