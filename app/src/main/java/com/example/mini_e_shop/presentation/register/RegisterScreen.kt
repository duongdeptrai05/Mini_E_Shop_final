package com.example.mini_e_shop.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import com.example.mini_e_shop.ui.theme.GradientEnd
import com.example.mini_e_shop.ui.theme.GradientStart
import com.example.mini_e_shop.ui.theme.LightGray
import com.example.mini_e_shop.ui.theme.PrimaryBlue
import com.example.mini_e_shop.ui.theme.TextGray

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val name by viewModel.name.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val registerState by viewModel.registerState.collectAsState()

    LaunchedEffect(registerState) {
        if (registerState is RegisterState.Success) {
            onRegisterSuccess()
            viewModel.resetRegisterState() // Reset state after success
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
                    AuthTabs(onLoginClicked = onBackToLogin)
                    Spacer(modifier = Modifier.height(24.dp))

                    NameField(value = name, onValueChange = { viewModel.onNameChange(it) })
                    Spacer(modifier = Modifier.height(16.dp))
                    EmailField(value = email, onValueChange = { viewModel.onEmailChange(it) })
                    Spacer(modifier = Modifier.height(16.dp))
                    PasswordField(value = password, onValueChange = { viewModel.onPasswordChange(it) })
                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { viewModel.registerUser() },
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
                        if (registerState is RegisterState.Loading) {
                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Đăng ký", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    if (registerState is RegisterState.Error) {
                        Text(
                            text = (registerState as RegisterState.Error).message,
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
            Text("Tạo tài khoản mới", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
        }
    }
}

@Composable
private fun AuthTabs(onLoginClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(LightGray)
            .padding(4.dp)
    ) {
        TextButton(
            onClick = onLoginClicked,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.textButtonColors(contentColor = TextGray)
        ) {
            Text("Đăng nhập", fontWeight = FontWeight.SemiBold)
        }
        Button(
            onClick = { /* Đang ở tab Đăng ký */ },
            modifier = Modifier
                .weight(1f)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Đăng ký", fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    }
}

@Composable
private fun NameField(value: String, onValueChange: (String) -> Unit) {
    Column {
        Text("Tên của bạn", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextGray)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Nguyễn Văn A") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name icon", tint = TextGray) },
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
private fun EmailField(value: String, onValueChange: (String) -> Unit) {
    Column {
        Text("Tên tài khoản hoặc Email", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextGray)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("name or email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email icon", tint = TextGray) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color.LightGray,
                cursorColor = PrimaryBlue
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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
                // Icon mắt đóng khi mật khẩu đang hiển thị
                    Icons.Filled.VisibilityOff
                else
                // Icon mắt mở khi mật khẩu đang bị ẩn
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
