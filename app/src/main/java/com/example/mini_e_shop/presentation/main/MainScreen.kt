package com.example.mini_e_shop.presentation.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.mini_e_shop.data.local.entity.UserEntity
import com.example.mini_e_shop.presentation.auth.MainUiState
import com.example.mini_e_shop.presentation.main.components.BottomNavigationBar
import com.example.mini_e_shop.presentation.navigation.MainNavGraph

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    mainUiState: MainUiState.Success,
    onNavigateToOrders: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToAddEditProduct: (Int?) -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues: PaddingValues ->

        MainNavGraph(
            modifier = Modifier.padding(paddingValues),
            navController = navController,
            isAdmin = mainUiState.isAdmin,
            currentUser = mainUiState.currentUser, // Pass the user object down
            onNavigateToOrders = onNavigateToOrders,
            onLogout = onLogout,
            onNavigateToAddEditProduct = onNavigateToAddEditProduct
        )
    }
}
