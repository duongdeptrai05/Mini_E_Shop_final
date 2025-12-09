package com.example.mini_e_shop.presentation.main.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mini_e_shop.presentation.navigation.Screen
import com.example.mini_e_shop.ui.theme.*

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.Favorites,
        Screen.Cart,
        Screen.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets(0), // tránh bị khuyết do inset mặc định
        modifier = Modifier
            .height(72.dp) // cao hơn 1 chút để đủ không gian icon + nhãn
            .fillMaxWidth()
    ) {
        items.forEach { screen ->
            val isSelected = currentRoute == screen.route
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.15f else 1f,
                animationSpec = tween(durationMillis = 200),
                label = "scale"
            )

            // Màu sắc riêng cho từng trang
            val pageColor = when (screen.route) {
                Screen.Home.route -> HomeColor
                Screen.Favorites.route -> FavoritesColor
                Screen.Cart.route -> CartColor
                Screen.Profile.route -> ProfileColor
                else -> PrimaryIndigo
            }

            val pageColorLight = when (screen.route) {
                Screen.Home.route -> HomeColorLight
                Screen.Favorites.route -> FavoritesColorLight
                Screen.Cart.route -> CartColorLight
                Screen.Profile.route -> ProfileColorLight
                else -> PrimaryLight
            }

            NavigationBarItem(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) pageColor.copy(alpha = 0.15f) else Color.Transparent
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = screen.icon!!,
                            contentDescription = screen.title?.let { stringResource(it) },
                            modifier = Modifier
                                .scale(scale)
                                .size(22.dp),
                            tint = if (isSelected) pageColor else TextSecondary
                        )
                    }
                },
                label = {
                    Text(
                        text = stringResource(screen.title!!),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) pageColor else TextSecondary
                    )
                },
                selected = isSelected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = pageColor,
                    selectedTextColor = pageColor,
                    indicatorColor = Color.Transparent, // Bỏ indicator mặc định, dùng background riêng
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )
        }
    }
}
