package com.example.mini_e_shop.presentation.add_edit_product

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.domain.model.Product
import com.example.mini_e_shop.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.UUID

@HiltViewModel
class AddEditProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // --- State cho các trường của sản phẩm ---
    // Các StateFlow này sẽ liên kết với các TextField trên giao diện
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _brand = MutableStateFlow("")
    val brand = _brand.asStateFlow()

    private val _category = MutableStateFlow("")
    val category = _category.asStateFlow()

    private val _origin = MutableStateFlow("")
    val origin = _origin.asStateFlow()

    private val _price = MutableStateFlow("")
    val price = _price.asStateFlow()

    private val _stock = MutableStateFlow("")
    val stock = _stock.asStateFlow()
//---------------------------------------------------
// thay thế việc nhập url bằng tải ảnh ln từ thiết bị
// nhập URL (cũ)
//    private val _imageUrl = MutableStateFlow("")
//    val imageUrl = _imageUrl.asStateFlow()
// tải ảnh từ thiết bị (new)
    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri = _imageUri.asStateFlow()
//--------------------------------------------------
    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _saveEvent = Channel<Unit>()
    val saveEvent = _saveEvent.receiveAsFlow()
//---------------------------------------------------------------

    // Lấy productId trực tiếp từ savedStateHandle. Nếu không có, mặc định là -1.
    private var currentProductId: String? = savedStateHandle.get<String>("productId")
    init {
        // --- Sửa đổi 3: Viết lại toàn bộ khối init ---
        // Chỉ chạy logic lấy sản phẩm nếu productId không phải là -1 (tức là đang sửa)
        if (currentProductId != null) {
            viewModelScope.launch {
                // Lấy sản phẩm từ repository
                productRepository.getProductById(currentProductId!!)?.let { product ->
                    // Điền thông tin sản phẩm vào các StateFlow
                    _name.value = product.name
                    _brand.value = product.brand
                    _category.value = product.category
                    _origin.value = product.origin
                    _price.value = product.price.toString()
                    _stock.value = product.stock.toString()
                    //_imageUrl.value = product.imageUrl // từ code cũ
                    if (product.imageUrl.isNotEmpty()) {
                        _imageUri.value = Uri.parse(product.imageUrl)
                    } // từ code mới

                    _description.value = product.description
                }
            }
        }
    }
    // --- Các hàm xử lý sự kiện thay đổi từ UI ---
    fun onNameChange(newName: String) {
        _name.value = newName
    }
    fun onBrandChange(newBrand: String) {
        _brand.value = newBrand
    }
    fun onCategoryChange(newCategory: String) {
        _category.value = newCategory
    }
    fun onOriginChange(newOrigin: String) {
        _origin.value = newOrigin
    }
    fun onPriceChange(newPrice: String) {
        _price.value = newPrice
    }
    fun onStockChange(newStock: String) {
        _stock.value = newStock
    }
//    fun onImageUrlChange(newUrl: String) {
//        _imageUrl.value = newUrl
//    }
    // code cũ
    fun onImageUriChange(newUri: Uri?) {
        _imageUri.value = newUri
    }
    // code mới
    fun onDescriptionChange(newDescription: String) {
        _description.value = newDescription
    }
//---------------------------------------------------------------
fun saveProduct() {
    viewModelScope.launch {
        // --- Sửa đổi 4: Sửa lại logic tạo productToSave ---
        val imageUrlString = _imageUri.value?.toString() ?: ""
        val productId = currentProductId ?: UUID.randomUUID().toString() // Nếu đang sửa, dùng ID cũ. Nếu tạo mới, tạo ID ngẫu nhiên mới.

        val productToSave = Product(
            id = productId,
            name = name.value.trim(),
            brand = brand.value.trim(),
            category = category.value.trim(),
            origin = origin.value.trim(),
            price = price.value.toDoubleOrNull() ?: 0.0,
            stock = stock.value.toIntOrNull() ?: 0,
//            imageUrl = imageUrl.value.trim(), //code cũ
            imageUrl = imageUrlString, // code mới
            description = description.value.trim()
        )
        // Kiểm tra các trường bắt buộc không được để trống
        if (productToSave.name.isBlank() || productToSave.category.isBlank()) {
            // TODO: Gửi sự kiện để hiển thị lỗi cho người dùng (ví dụ: qua một StateFlow khác)
            // Ví dụ: _errorEvent.send("Tên và Loại sản phẩm không được để trống")
            return@launch // Dừng hàm nếu có lỗi
        }
        productRepository.upsertProduct(productToSave)
        _saveEvent.send(Unit)
    }
}
}