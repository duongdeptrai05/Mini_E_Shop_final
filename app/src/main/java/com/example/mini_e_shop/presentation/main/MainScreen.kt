package com.example.mini_e_shop.presentation.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mini_e_shop.data.local.entity.UserEntity
import com.example.mini_e_shop.presentation.auth.MainUiState
import com.example.mini_e_shop.presentation.main.components.BottomNavigationBar
import com.example.mini_e_shop.presentation.navigation.MainNavGraph
import com.example.mini_e_shop.presentation.navigation.Screen

@Composable
fun MainScreen(
    mainUiState: MainUiState.Success,
    currentUser: UserEntity?,
    mainNavController: NavHostController,
    onNavigateToOrders: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToAddEditProduct: (Int?) -> Unit,
    onProductClick: (Int) -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToCheckout: (String) -> Unit
) {
    // NavController này chỉ dành cho 4 tab bottom
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            // FIX: Xử lý lỗi icon bị che khuất bởi thanh điều hướng hệ thống
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White, // Đặt màu nền giống màu của BottomNavigationBar để tràn viền đẹp mắt
                shadowElevation = 8.dp // Tạo bóng nhẹ tách biệt nội dung
            ) {
                Column {
                    BottomNavigationBar(navController = bottomNavController)

                    // QUAN TRỌNG: Spacer này tự động lấy chiều cao của thanh điều hướng (Gesture bar)
                    // Nó đẩy BottomNavigationBar lên trên, giúp icon không bị che,
                    // nhưng vẫn giữ màu nền tràn xuống đáy.
                    Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                }
            }
        }
    ) { paddingValues ->
        MainNavGraph(
            // paddingValues ở đây sẽ tự động tính toán khoảng cách bao gồm cả bottomBar
            // nên nội dung bên trong sẽ không bị che mất phần cuối.
            modifier = Modifier.padding(paddingValues),
            mainNavController = mainNavController,
            bottomNavController = bottomNavController,
            isAdmin = mainUiState.isAdmin,
            currentUser = currentUser,
            onNavigateToOrders = onNavigateToOrders,
            onNavigateToSettings = { mainNavController.navigate(Screen.Settings.route) },
            onLogout = onLogout,
            onNavigateToAddEditProduct = onNavigateToAddEditProduct,
            onProductClick = onProductClick,
            onNavigateToSupport = onNavigateToSupport,
            onNavigateToCheckout = onNavigateToCheckout
        )
    }
}