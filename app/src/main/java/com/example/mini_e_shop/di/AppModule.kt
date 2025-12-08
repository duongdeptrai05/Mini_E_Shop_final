package com.example.mini_e_shop.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mini_e_shop.data.local.SampleData
import com.example.mini_e_shop.data.local.dao.CartDao
import com.example.mini_e_shop.data.local.dao.FavoriteDao
import com.example.mini_e_shop.data.local.dao.OrderDao
import com.example.mini_e_shop.data.local.dao.ProductDao
import com.example.mini_e_shop.data.local.dao.UserDao
import com.example.mini_e_shop.data.local.database.AppDatabase
import com.example.mini_e_shop.data.preferences.UserPreferencesManager
import com.example.mini_e_shop.data.repository.CartRepositoryImpl
import com.example.mini_e_shop.data.repository.FavoriteRepositoryImpl
import com.example.mini_e_shop.data.repository.OrderRepositoryImpl
import com.example.mini_e_shop.data.repository.ProductRepositoryImpl
import com.example.mini_e_shop.data.repository.UserRepositoryImpl
import com.example.mini_e_shop.domain.repository.CartRepository
import com.example.mini_e_shop.domain.repository.FavoriteRepository
import com.example.mini_e_shop.domain.repository.OrderRepository
import com.example.mini_e_shop.domain.repository.ProductRepository
import com.example.mini_e_shop.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

// Custom Callback class to break the circular dependency
class DatabaseCallback @Inject constructor(
//    private val userDao: Provider<UserDao>,
//    private val productDao: Provider<ProductDao>,
    private val firestore: Provider<FirebaseFirestore>,
    private val applicationScope: CoroutineScope
) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        // Use the injected scope to launch the coroutine
        applicationScope.launch {
//            createAdminAccount()
            seedProductsToFirestore()
        }
    }
    private fun seedProductsToFirestore() {
        val productCollection = firestore.get().collection("products")

        // Kiểm tra xem collection "products" đã có dữ liệu chưa để tránh ghi đè
        productCollection.limit(1).get().addOnCompleteListener { task ->
            if (task.isSuccessful && task.result?.isEmpty == true) {
                // Nếu collection rỗng, bắt đầu gieo dữ liệu
                println("Firestore: Seeding products...")
                val sampleProducts = SampleData.getSampleProducts()
                val batch = firestore.get().batch()

                sampleProducts.forEach { productEntity ->
                    // Chuyển đổi từ ProductEntity sang một Map để ghi lên Firestore
                    val productData = hashMapOf(
                        "name" to productEntity.name,
                        "brand" to productEntity.brand,
                        "category" to productEntity.category,
                        "origin" to productEntity.origin,
                        "price" to productEntity.price,
                        "stock" to productEntity.stock,
                        "imageUrl" to productEntity.imageUrl,
                        "description" to productEntity.description
                    )
                    // Dùng ID từ SampleData làm Document ID trên Firestore
                    val docRef = productCollection.document(productEntity.id)
                    batch.set(docRef, productData)
                }

                batch.commit().addOnSuccessListener {
                    println("Firestore: Successfully seeded ${sampleProducts.size} products.")
                }.addOnFailureListener { e ->
                    println("Firestore: Error seeding products: $e")
                }
            } else {
                println("Firestore: Products collection already contains data. No seeding needed.")
            }
        }
    }




}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        app: Application,
        callback: DatabaseCallback
    ): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "mini_e_shop.db"
        )
            .addCallback(callback)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides @Singleton
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    @Provides @Singleton
    fun provideProductDao(db: AppDatabase): ProductDao = db.productDao()
    @Provides @Singleton
    fun provideOrderDao(db: AppDatabase): OrderDao = db.orderDao()
    @Provides @Singleton
    fun provideCartDao(db: AppDatabase): CartDao = db.cartDao()
    @Provides @Singleton
    fun provideFavoriteDao(db: AppDatabase): FavoriteDao = db.favoriteDao()
}
// THÊM MODULE NÀY VÀO VỊ TRÍ BỊ THIẾU
//@Module
//@InstallIn(SingletonComponent::class)
//object FirebaseModule {
//
//    @Provides
//    @Singleton
//    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
//
//    @Provides
//    @Singleton
//    fun provideAuth(): FirebaseAuth = FirebaseAuth.getInstance()
//
//    @Provides
//    @Singleton
//    fun provideStorage(): FirebaseStorage = FirebaseStorage.getInstance()
//}


    @Module
    @InstallIn(SingletonComponent::class)
    abstract class RepositoryModule {
        @Binds @Singleton
        abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository
        @Binds @Singleton
        abstract fun bindProductRepository(productRepositoryImpl: ProductRepositoryImpl): ProductRepository
        @Binds @Singleton
        abstract fun bindOrderRepository(orderRepositoryImpl: OrderRepositoryImpl): OrderRepository
        @Binds @Singleton
        abstract fun bindCartRepository(cartRepositoryImpl: CartRepositoryImpl): CartRepository
        @Binds @Singleton
        abstract fun bindFavoriteRepository(favoriteRepositoryImpl: FavoriteRepositoryImpl): FavoriteRepository
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object PreferencesModule {
        @Provides
        @Singleton
        fun provideUserPreferencesManager(@ApplicationContext context: Context): UserPreferencesManager {
            return UserPreferencesManager(context)
        }
    }

    // Module to provide a Singleton CoroutineScope that lives as long as the application
    @Module
    @InstallIn(SingletonComponent::class)
    object CoroutineModule {
        @Provides
        @Singleton
        fun provideApplicationScope(): CoroutineScope {
            // SupervisorJob makes sure that if one child coroutine fails, the scope is not cancelled
            return CoroutineScope(SupervisorJob() + Dispatchers.IO)
        }
    }
