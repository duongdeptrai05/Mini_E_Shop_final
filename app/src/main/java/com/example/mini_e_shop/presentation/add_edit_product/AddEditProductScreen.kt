package com.example.mini_e_shop.presentation.add_edit_product

import android.net.Uri
import android.os.Build
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    viewModel: AddEditProductViewModel = hiltViewModel(),
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    // 1. SỬA LẠI LOGIC: Lắng nghe sự kiện `saveEvent` từ ViewModel.
    //    Khi ViewModel lưu thành công và gửi sự kiện, hàm `onSave()` mới được gọi.
    LaunchedEffect(key1 = true) {
        viewModel.saveEvent.collectLatest {
            onSave() // Chỉ quay lại khi đã lưu xong.
        }
    }

    // thay dổi mới thêm ảnh thừ thư viện ảnh của thiết bị
    // --- THAY ĐỔI 1: Tạo launcher để chọn ảnh ---
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // Khi người dùng chọn ảnh, cập nhật ViewModel
        viewModel.onImageUriChange(uri)
    }

    // --- THAY ĐỔI 2: Tạo launcher để xin quyền truy cập bộ nhớ ---
    // Chỉ cần xin quyền cho Android phiên bản mới
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Nếu được cấp quyền, mở trình chọn ảnh
            imagePickerLauncher.launch("image/*")
        } else {
            // TODO: Hiển thị thông báo cho người dùng biết cần cấp quyền
        }
    }
    Scaffold(
        topBar = {
            // Sử dụng TopAppBar để có nút Quay lại và nút Lưu rõ ràng.
            TopAppBar(
                title = { Text("Thêm/Sửa Sản phẩm") },
                navigationIcon = {
                    IconButton(onClick = onBack) { // Gọi onBack khi nhấn nút quay lại.
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.saveProduct() }) {
                        Icon(Icons.Default.Done, contentDescription = "Save Product")
                    }
                }
            )
        }

    ) { padding ->
        // Lấy tất cả các state từ ViewModel.
        val name by viewModel.name.collectAsState()
        val brand by viewModel.brand.collectAsState()
        val category by viewModel.category.collectAsState()
        val origin by viewModel.origin.collectAsState()
        val price by viewModel.price.collectAsState()
        val stock by viewModel.stock.collectAsState()
//        val imageUrl by viewModel.imageUrl.collectAsState() // code cũ
        val imageUri by viewModel.imageUri.collectAsState() // code mới
        val description by viewModel.description.collectAsState()

        // 4. THÊM CÁC TEXTFIELD CÒN LẠI VÀ THÊM CUỘN DỌC.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- THAY ĐỔI 3: Khu vực hiển thị và chọn ảnh ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                    .clickable {
                        // Khi nhấn vào, kiểm tra và xin quyền nếu cần
                        val permission =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                Manifest.permission.READ_MEDIA_IMAGES
                            } else {
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            }
                        permissionLauncher.launch(permission)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    // Nếu đã có ảnh, hiển thị ảnh đó
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(imageUri)
                                .crossfade(true)
                                .build()
                        ),
                        contentDescription = "Product Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Nếu chưa có ảnh, hiển thị văn bản hướng dẫn
                    Text(
                        text = "Nhấn để chọn ảnh sản phẩm",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            OutlinedTextField(value = name, onValueChange = viewModel::onNameChange, label = { Text("Tên sản phẩm") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = brand, onValueChange = viewModel::onBrandChange, label = { Text("Thương hiệu") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = category, onValueChange = viewModel::onCategoryChange, label = { Text("Loại sản phẩm") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = origin, onValueChange = viewModel::onOriginChange, label = { Text("Xuất xứ") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = price, onValueChange = viewModel::onPriceChange, label = { Text("Giá") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = stock, onValueChange = viewModel::onStockChange, label = { Text("Số lượng tồn kho") }, modifier = Modifier.fillMaxWidth())
            //OutlinedTextField(value = imageUrl, onValueChange = viewModel::onImageUrlChange, label = { Text("URL Hình ảnh") }, modifier = Modifier.fillMaxWidth())
            // code cũ
            OutlinedTextField(value = description, onValueChange = viewModel::onDescriptionChange, label = { Text("Mô tả") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        }
    }
}
