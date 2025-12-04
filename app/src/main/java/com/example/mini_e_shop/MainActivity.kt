package com.example.mini_e_shop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mini_e_shop.presentation.add_edit_product.AddEditProductScreen
import com.example.mini_e_shop.presentation.login.LoginScreen
import com.example.mini_e_shop.presentation.main.MainScreen
import com.example.mini_e_shop.presentation.auth.MainUiState // Corrected import path
import com.example.mini_e_shop.presentation.navigation.Screen
import com.example.mini_e_shop.presentation.orders.OrdersScreen
import com.example.mini_e_shop.presentation.register.RegisterScreen
import com.example.mini_e_shop.ui.theme.Mini_E_ShopTheme
import dagger.hilt.android.AndroidEntryPoint
import com.example.mini_e_shop.presentation.auth.AuthViewModel
import com.example.mini_e_shop.presentation.auth.AuthState

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Mini_E_ShopTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val authViewModel = hiltViewModel<AuthViewModel>()
                    val authState by authViewModel.authState.collectAsState()
                    val navController = rememberNavController()

                    when (authState) {
                        AuthState.Loading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        AuthState.Authenticated -> {
                            val mainUiState by authViewModel.mainUiState.collectAsState()

                            when (mainUiState) {
                                is MainUiState.Loading -> {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator()
                                    }
                                }
                                is MainUiState.Success -> {
                                    NavHost(navController = navController, startDestination = Screen.Main.route) {
                                        composable(Screen.Main.route) {
                                            MainScreen(
                                                mainUiState = mainUiState as MainUiState.Success,
                                                onNavigateToOrders = {
                                                    navController.navigate(Screen.Orders.route)
                                                },
                                                onLogout = {
                                                    authViewModel.onLogout()
                                                },
                                                onNavigateToAddEditProduct = { productId ->
                                                    navController.navigate("${Screen.AddEditProduct.route}?productId=$productId")
                                                }
                                            )
                                        }
                                        composable(Screen.Orders.route) {
                                            OrdersScreen(
                                                viewModel = hiltViewModel(),
                                                onBack = { navController.popBackStack() }
                                            )
                                        }
                                        composable("${Screen.AddEditProduct.route}?productId={productId}") {
                                            AddEditProductScreen(
                                                viewModel = hiltViewModel(),
                                                onSave = { navController.popBackStack() },
                                                onBack = { navController.popBackStack() }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        AuthState.Unauthenticated -> {
                            NavHost(navController = navController, startDestination = Screen.Login.route) {
                                composable(Screen.Login.route) {
                                    LoginScreen(
                                        viewModel = hiltViewModel(),
                                        onLoginSuccess = {
                                            authViewModel.onLoginSuccess(it)
                                        },
                                        onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                                    )
                                }
                                composable(Screen.Register.route) {
                                    RegisterScreen(
                                        viewModel = hiltViewModel(),
                                        onRegisterSuccess = { navController.navigate(Screen.Login.route) { popUpTo(Screen.Register.route) { inclusive = true } } },
                                        onBackToLogin = { navController.popBackStack() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
