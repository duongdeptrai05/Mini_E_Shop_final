package com.example.mini_e_shop.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String? = null,
    val icon: ImageVector? = null
) {
    // Luồng xác thực
    object Login : Screen(route = "login")
    object Register : Screen(route = "register")

    // Màn hình chính chứa Bottom Nav
    object Main : Screen(route = "main")

    // Các màn hình trong Bottom Nav
    object Home : Screen(
        route = "home",
        title = "Trang chủ",
        icon = Icons.Default.Home
    )
    object Favorites : Screen(
        route = "favorites",
        title = "Yêu thích",
        icon = Icons.Default.Favorite
    )
    object Cart : Screen(
        route = "cart",
        title = "Giỏ hàng",
        icon = Icons.Default.ShoppingCart
    )
    object Profile : Screen(
        route = "profile",
        title = "Cá nhân",
        icon = Icons.Default.Person
    )

    // Các màn hình khác
    object Orders : Screen(route = "orders")
    object AddEditProduct : Screen(route = "add_edit_product")
    object ProductDetail : Screen("product_detail")
}
