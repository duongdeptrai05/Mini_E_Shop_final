package com.example.mini_e_shop.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mini_e_shop.data.local.entity.UserEntity
import com.example.mini_e_shop.presentation.cart.CartScreen
import com.example.mini_e_shop.presentation.favorites.FavoritesScreen
import com.example.mini_e_shop.presentation.products_list.ProductListScreen
import com.example.mini_e_shop.presentation.profile.ProfileScreen

@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    mainNavController: NavHostController, // NavController chính
     // Để điều hướng ra các màn hình ngoài (Checkout, Detail...)
    bottomNavController: NavHostController, // Để điều hướng giữa các tab
    isAdmin: Boolean,
    currentUser: UserEntity?,
    onNavigateToOrders: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToAddEditProduct: (Int?) -> Unit,
    onProductClick: (Int) -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToCheckout: (String) -> Unit
) {
    NavHost(
        navController = bottomNavController, // NavHost này dùng NavController của bottom bar
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            ProductListScreen(
                viewModel = hiltViewModel(),
                isAdmin = isAdmin,
                onNavigateToAddEditProduct = onNavigateToAddEditProduct,
                onProductClick = onProductClick,
                onNavigateToSupport = onNavigateToSupport
            )
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onProductClick = onProductClick,
                onNavigateToAddEditProduct = onNavigateToAddEditProduct
            )
        }
        composable(Screen.Cart.route) {
            CartScreen(
                viewModel = hiltViewModel(),
                onNavigateToCheckout = onNavigateToCheckout // CartScreen dùng NavController chính để điều hướng ra ngoài
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                currentUser = currentUser,
                onNavigateToOrders = onNavigateToOrders,
                onLogout = onLogout
            )
        }
    }
}
