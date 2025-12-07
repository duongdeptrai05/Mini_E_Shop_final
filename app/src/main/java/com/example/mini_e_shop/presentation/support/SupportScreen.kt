package com.example.mini_e_shop.presentation.support

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel

// Dummy data class for chat messages
data class Message(val text: String, val isFromUser: Boolean, val showAvatar: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    viewModel: SupportViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToContact: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            Message("Xin chào! Shop có thể giúp gì cho bạn?", isFromUser = false, showAvatar = true),
        )
    }
    val context = LocalContext.current

    // Launcher để chụp ảnh. Kết quả trả về là một Bitmap.
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            // TODO: Xử lý ảnh bitmap (ví dụ: hiển thị trong chat, tải lên server)
            Toast.makeText(context, "Đã chụp ảnh!", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher để yêu cầu quyền.
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Quyền đã được cấp, khởi chạy camera
            cameraLauncher.launch()
        } else {
            // Quyền bị từ chối
            Toast.makeText(context, "Quyền truy cập camera bị từ chối.", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chăm sóc khách hàng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToContact) {
                        Icon(Icons.Default.SupportAgent, contentDescription = "Contact")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF007AFF),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            MessageInputBar(
                messageText = messageText,
                onMessageChange = { messageText = it },
                onSendClick = {
                    if (messageText.isNotBlank()) {
                        messages.add(Message(messageText, isFromUser = true, showAvatar = false))
                        messageText = ""
                        // TODO: Add bot response logic here
                    }
                },
                onCameraClick = {
                    // Kiểm tra quyền trước khi khởi chạy camera
                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) -> {
                            // Quyền đã có, khởi chạy camera
                            cameraLauncher.launch()
                        }
                        else -> {
                            // Yêu cầu quyền
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE5F3FF))
                .padding(it)
                .padding(horizontal = 16.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) {
                MessageBubble(message = it)
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isFromUser && message.showAvatar) {
            Image(
                imageVector = Icons.Default.Person, // Using a vector asset
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .align(Alignment.Bottom)
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else if (!message.isFromUser) {
            Spacer(modifier = Modifier.width(48.dp)) // Spacer to align messages
        }

        Box(
            modifier = Modifier
                .background(
                    if (message.isFromUser) Color(0xFF007AFF) else Color.White,
                    RoundedCornerShape(20.dp)
                )
                .padding(12.dp)
                .weight(1f, fill = false)
        ) {
            Text(
                text = message.text,
                color = if (message.isFromUser) Color.White else Color.Black
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInputBar(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { Toast.makeText(context, "Chức năng gửi emoji sắp ra mắt!", Toast.LENGTH_SHORT).show() }) {
            Icon(Icons.Outlined.SentimentSatisfied, contentDescription = "Emoji", tint = Color.Gray)
        }
        IconButton(onClick = onCameraClick) {
            Icon(Icons.Default.CameraAlt, contentDescription = "Attach image", tint = Color.Gray)
        }

        TextField(
            value = messageText,
            onValueChange = onMessageChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Nhập tin nhắn...") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color(0xFF007AFF)
            )
        )

        IconButton(onClick = onSendClick) {
            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color(0xFF007AFF))
        }
    }
}
