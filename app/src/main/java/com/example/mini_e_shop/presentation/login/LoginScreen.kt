package com.example.mini_e_shop.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mini_e_shop.data.local.entity.UserEntity
import com.example.mini_e_shop.ui.theme.GradientEnd
import com.example.mini_e_shop.ui.theme.GradientStart
import com.example.mini_e_shop.ui.theme.LightGray
import com.example.mini_e_shop.ui.theme.PrimaryBlue
import com.example.mini_e_shop.ui.theme.TextGray

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: (UserEntity) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val usernameOrEmail by viewModel.usernameOrEmail.collectAsState()
    val password by viewModel.password.collectAsState()
    val rememberMe by viewModel.rememberMe.collectAsState()
    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is LoginViewModel.LoginState.Success -> {
                onLoginSuccess(state.user)
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderView()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp)
                    .offset(y = (-40).dp),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    AuthTabs(onRegisterClicked = onNavigateToRegister)
                    Spacer(modifier = Modifier.height(24.dp))

                    UsernameOrEmailField(value = usernameOrEmail, onValueChange = { viewModel.onUsernameOrEmailChange(it) })
                    Spacer(modifier = Modifier.height(16.dp))
                    PasswordField(value = password, onValueChange = { viewModel.onPasswordChange(it) })
                    Spacer(modifier = Modifier.height(16.dp)) // Reduced spacer

                    // Remember Me Checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { viewModel.onRememberMeChange(it) },
                            colors = CheckboxDefaults.colors(checkedColor = PrimaryBlue)
                        )
                        Text("Ghi nhớ tài khoản", color = TextGray, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(24.dp)) // Reduced spacer

                    Button(
                        onClick = { viewModel.loginUser() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = PrimaryBlue
                            ),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (loginState is LoginViewModel.LoginState.Loading) {
                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Đăng nhập", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    if (loginState is LoginViewModel.LoginState.Error) {
                        Text(
                            text = (loginState as LoginViewModel.LoginState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(
                brush = Brush.linearGradient(colors = listOf(GradientStart, GradientEnd))
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(12.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ShoppingBag, contentDescription = "Logo", tint = PrimaryBlue, modifier = Modifier.size(40.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Điện tử Văn Mạnh", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Mua sắm thông minh, tiện lợi", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
        }
    }
}

@Composable
private fun AuthTabs(onRegisterClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(LightGray)
            .padding(4.dp)
    ) {
        Button(
            onClick = { /* Đang ở tab Đăng nhập */ },
            modifier = Modifier
                .weight(1f)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Đăng nhập", fontWeight = FontWeight.SemiBold, color = Color.White)
        }
        TextButton(
            onClick = onRegisterClicked,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.textButtonColors(contentColor = TextGray)
        ) {
            Text("Đăng ký", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun UsernameOrEmailField(value: String, onValueChange: (String) -> Unit) {
    Column {
        Text("Tên tài khoản hoặc Email", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextGray)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("username or email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email icon", tint = TextGray) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color.LightGray,
                cursorColor = PrimaryBlue
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true
        )
    }
}

@Composable
private fun PasswordField(value: String, onValueChange: (String) -> Unit) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    Column {
        Text("Mật khẩu", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextGray)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("••••••••") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password icon", tint = TextGray) },
            trailingIcon = {
                val image = if (isPasswordVisible)
                // Use the "visibility off" icon when the password is visible
                    Icons.Filled.VisibilityOff
                else
                // Use the "visibility" icon when the password is hidden
                    Icons.Filled.Visibility

                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle password visibility")
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color.LightGray,
                cursorColor = PrimaryBlue
            ),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )
    }
}
