package com.example.mini_e_shop.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mini_e_shop.data.local.entity.UserEntity
import com.example.mini_e_shop.presentation.cart.CartScreen
import com.example.mini_e_shop.presentation.cart.CartViewModel
import com.example.mini_e_shop.presentation.products_list.ProductListScreen
import com.example.mini_e_shop.presentation.profile.ProfileScreen

@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    isAdmin: Boolean,
    currentUser: UserEntity?, // Receive the current user
    onNavigateToOrders: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToAddEditProduct: (Int?) -> Unit,
    onProductClick: (Int) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            ProductListScreen(
                viewModel = hiltViewModel(),
                isAdmin = isAdmin,
                onNavigateToAddEditProduct = onNavigateToAddEditProduct,
                onProductClick = onProductClick
            )
        }
        composable(Screen.Favorites.route) {
            // Favorites screen will be created here later
        }
        composable(Screen.Cart.route) {
            val cartViewModel = hiltViewModel<CartViewModel>()
            CartScreen(viewModel = cartViewModel)
        }
        composable(Screen.Profile.route) {
            // No longer needs UserViewModel, pass the user object directly
            ProfileScreen(
                currentUser = currentUser,
                onNavigateToOrders = onNavigateToOrders,
                onLogout = onLogout
            )
        }
    }
}
