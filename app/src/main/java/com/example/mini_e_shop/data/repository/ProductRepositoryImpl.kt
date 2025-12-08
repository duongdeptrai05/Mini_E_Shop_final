package com.example.mini_e_shop.data.repository

import com.example.mini_e_shop.data.local.dao.ProductDao
import com.example.mini_e_shop.data.local.entity.ProductEntity
import com.example.mini_e_shop.domain.model.Product
import com.example.mini_e_shop.domain.repository.ProductRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val firestore: FirebaseFirestore
) : ProductRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    init {
        // Khởi tạo việc lắng nghe dữ liệu từ Firestore ngay khi Repository được tạo
        subscribeToRemoteProductChanges()
    }
    // BƯỚC 1: Thêm một khối init
//    init {
//        // Sử dụng CoroutineScope để chạy một tác vụ bất đồng bộ không chặn
//        // mà không cần đến viewModelScope (vì đây là Repository)
//        CoroutineScope(Dispatchers.IO).launch {
//            seedSampleProducts()
//        }
//    }

    // Hàm này sẽ chạy ngầm để lắng nghe Firestore và cập nhật vào Room
    private fun subscribeToRemoteProductChanges() {
        // Sử dụng scope đã tạo, không tạo scope mới.
        repositoryScope.launch {
            firestore.collection("products")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        // Xử lý lỗi, ví dụ: log lỗi
                        println("Firestore listener error: ${error.message}")
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val remoteProducts = snapshot.toObjects(Product::class.java)
                        val productEntities = remoteProducts.map { product ->
                            product.toEntity()
                        }

                        // Cập nhật dữ liệu mới vào Room trong cùng scope
                        repositoryScope.launch {
                            productDao.upsertProducts(productEntities)
                        }
                    }
                }
        }
    }
//    // BƯỚC 2: Tạo hàm kiểm tra và chèn dữ liệu
//    private suspend fun seedSampleProducts() {
//        if (productDao.getProductCount() == 0) {
//            productDao.insertProducts(SampleData.getSampleProducts())
//        }
//    }

    override fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts().map { listOfEntities ->
            listOfEntities.map { entity ->
                entity.toDomain()
            }
        }
    }

    override suspend fun getProductById(id: String): Product? {
        return productDao.getProductById(id)?.toDomain()
    }

    override suspend fun upsertProduct(product: Product) {
        productDao.upsertProduct(product.toEntity())
    }

    override suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product.toEntity())
    }

    override suspend fun updateProductStock(productId: String, newStock: Int) {
        productDao.updateStock(productId, newStock)
    }
}

// --- Các hàm Mapper (Chuyển đổi) ---

private fun ProductEntity.toDomain(): Product {
    return Product(
        id = this.id,
        name = this.name,
        brand = this.brand,
        category = this.category,
        origin = this.origin,
        price = this.price,
        stock = this.stock,
        imageUrl = this.imageUrl,
        description = this.description
    )
}

private fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = this.id,
        name = this.name,
        brand = this.brand,
        category = this.category,
        origin = this.origin,
        price = this.price,
        stock = this.stock,
        imageUrl = this.imageUrl,
        description = this.description
    )
}
