package com.example.mini_e_shop.presentation.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mini_e_shop.data.local.entity.UserEntity
import com.example.mini_e_shop.presentation.auth.MainUiState
import com.example.mini_e_shop.presentation.main.components.BottomNavigationBar
import com.example.mini_e_shop.presentation.navigation.MainNavGraph

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    mainUiState: MainUiState.Success,
    currentUser: UserEntity?,
    mainNavController: NavHostController, // Để điều hướng ra các màn hình ngoài
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
            BottomNavigationBar(navController = bottomNavController)
        }
    ) { paddingValues ->
        MainNavGraph(
            modifier = Modifier.padding(paddingValues),
            mainNavController = mainNavController, // Truyền NavController chính xuống
            bottomNavController = bottomNavController, // Truyền NavController của bottom bar xuống
            isAdmin = mainUiState.isAdmin,
            currentUser = currentUser,
            onNavigateToOrders = onNavigateToOrders,
            onLogout = onLogout,
            onNavigateToAddEditProduct = onNavigateToAddEditProduct,
            onProductClick = onProductClick,
            onNavigateToSupport = onNavigateToSupport,
            onNavigateToCheckout = onNavigateToCheckout
        )
    }
}
