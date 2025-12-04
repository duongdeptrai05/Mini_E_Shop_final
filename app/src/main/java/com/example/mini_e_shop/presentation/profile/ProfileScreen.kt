package com.example.mini_e_shop.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mini_e_shop.data.local.entity.UserEntity
import com.example.mini_e_shop.ui.theme.GradientEnd
import com.example.mini_e_shop.ui.theme.GradientStart
import com.example.mini_e_shop.ui.theme.PrimaryPurple

@Composable
fun ProfileScreen(
    currentUser: UserEntity?, // Receive the UserEntity directly
    onNavigateToOrders: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
    ) {
        Header(currentUser)
        Spacer(modifier = Modifier.height(20.dp))
        Menu(onNavigateToOrders)
        Spacer(modifier = Modifier.height(20.dp))
        LogoutButton(onClick = onLogout)
    }
}

@Composable
private fun Header(user: UserEntity?) {
    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    brush = Brush.linearGradient(colors = listOf(GradientStart, GradientEnd))
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Tài khoản", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(30.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(PrimaryPurple.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "User Avatar",
                            tint = PrimaryPurple,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(user?.name ?: "Loading...", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(user?.email ?: "", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun Menu(onNavigateToOrders: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MenuButton(text = "Đơn hàng của tôi", icon = Icons.Default.ReceiptLong, onClick = onNavigateToOrders)
        MenuButton(text = "Sản phẩm yêu thích", icon = Icons.Default.Favorite, onClick = { /* Navigate to favorites */ })
        MenuButton(text = "Cài đặt", icon = Icons.Default.Settings, onClick = { /* Navigate to settings */ })
    }
}

@Composable
private fun MenuButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = text, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text, fontWeight = FontWeight.SemiBold)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = "Arrow", tint = Color.Gray)
    }
}

@Composable
private fun LogoutButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Red
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout Icon")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Đăng xuất", fontWeight = FontWeight.Bold)
        }
    }
}
