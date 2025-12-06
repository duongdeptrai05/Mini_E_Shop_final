package com.example.mini_e_shop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mini_e_shop.presentation.add_edit_product.AddEditProductScreen
import com.example.mini_e_shop.presentation.auth.AuthState
import com.example.mini_e_shop.presentation.auth.AuthViewModel
import com.example.mini_e_shop.presentation.auth.MainUiState
import com.example.mini_e_shop.presentation.checkout.CheckoutScreen
import com.example.mini_e_shop.presentation.contact.ContactScreen
import com.example.mini_e_shop.presentation.login.LoginScreen
import com.example.mini_e_shop.presentation.main.MainScreen
import com.example.mini_e_shop.presentation.navigation.Screen
import com.example.mini_e_shop.presentation.orders.OrdersScreen
import com.example.mini_e_shop.presentation.product_detail.ProductDetailScreen
import com.example.mini_e_shop.presentation.register.RegisterScreen
import com.example.mini_e_shop.presentation.support.SupportScreen
import com.example.mini_e_shop.ui.theme.Mini_E_ShopTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Mini_E_ShopTheme {
                val authViewModel = hiltViewModel<AuthViewModel>()
                val authState by authViewModel.authState.collectAsState()
                val navController = rememberNavController() // CHỈ DÙNG MỘT NAVCONTROLLER DUY NHẤT
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()


                // SỬA: BỌC TẤT CẢ TRONG MỘT SCAFFOLD ĐỂ CUNG CẤP SNACKBARHOST
                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { padding -> // padding này là bắt buộc nhưng chúng ta không dùng
                    Surface(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        when (authState) {
                            AuthState.Loading -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                            AuthState.Authenticated -> {
                                val mainUiState by authViewModel.mainUiState.collectAsState()

                                // Đổi tên biến để tránh nhầm lẫn với authState
                                when (val currentState = mainUiState) {
                                    is MainUiState.Loading -> {
                                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                    is MainUiState.Success -> {
                                        NavHost(navController = navController, startDestination = Screen.Main.route) {
                                            composable(Screen.Main.route) {
                                                MainScreen(
                                                    mainUiState = currentState,
                                                    // SỬA LỖI: Dùng thuộc tính đúng là 'currentUser'
                                                    currentUser = currentState.currentUser,
                                                    mainNavController = navController,
                                                    onNavigateToOrders = { navController.navigate(Screen.Orders.route) },
                                                    onLogout = { authViewModel.onLogout() },
                                                    onNavigateToAddEditProduct = { productId ->
                                                        navController.navigate("${Screen.AddEditProduct.route}?productId=$productId")
                                                    },
                                                    onProductClick = { productId ->
                                                        navController.navigate("${Screen.ProductDetail.route}/$productId")
                                                    },
                                                    onNavigateToSupport = { navController.navigate(Screen.Support.route) },
                                                    onNavigateToCheckout = { cartItemIds ->
                                                        navController.navigate("${Screen.Checkout.route}/$cartItemIds")
                                                    }

                                                )
                                            }
                                            composable(Screen.Orders.route) {
                                                OrdersScreen(
                                                    viewModel = hiltViewModel(),
                                                    onBack = { navController.popBackStack() }
                                                )
                                            }
                                            composable(
                                                route = "${Screen.AddEditProduct.route}?productId={productId}",
                                                arguments = listOf(
                                                    navArgument("productId") {
                                                        type = NavType.IntType
                                                        defaultValue = -1
                                                    }
                                                )
                                            ) {
                                                AddEditProductScreen(
                                                    viewModel = hiltViewModel(),
                                                    onSave = { navController.popBackStack() },
                                                    onBack = { navController.popBackStack() }
                                                )
                                            }
                                            composable(
                                                route = "${Screen.ProductDetail.route}/{productId}",
                                                arguments = listOf(navArgument("productId") { type = NavType.IntType })
                                            ) {
                                                ProductDetailScreen(
                                                    onBack = { navController.popBackStack() }
                                                )
                                            }
                                            composable(Screen.Support.route) {
                                                SupportScreen(
                                                    onBack = { navController.popBackStack() },
                                                    onNavigateToContact = { navController.navigate(Screen.Contact.route) }
                                                )
                                            }
                                            composable(Screen.Contact.route) {
                                                ContactScreen(onBack = { navController.popBackStack() })
                                            }
                                            // SỬA: COMPOSABLE CHO CHECKOUT GIỜ ĐÃ HOÀN TOÀN HỢP LỆ
                                            composable(
                                                route = "${Screen.Checkout.route}/{cartItemIds}",
                                                arguments = listOf(navArgument("cartItemIds") { type = NavType.StringType })
                                            ) {
                                                CheckoutScreen(
                                                    // Không cần truyền cartViewModel nữa
                                                    onNavigateBack = { navController.popBackStack() },
                                                    onShowSnackbar = { message ->
                                                        // Giờ đây scope và snackbarHostState đã tồn tại và hợp lệ
                                                        scope.launch {
                                                            snackbarHostState.showSnackbar(message)
                                                        }
                                                    }
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
}
