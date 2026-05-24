package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "batik_products")
data class BatikProduct(
    @PrimaryKey val id: String,
    val name: String,
    val price: Double,
    val imageUrls: String, // Comma-separated image URLs (support multiple images)
    val description: String,
    val sizes: String,      // Comma-separated sizes (S, M, L, XL, XXL)
    val colors: String,     // Comma-separated colors
    val category: String,   // Solo, Jogja, Pekalongan, Cirebon
    val isBestseller: Boolean = false,
    val isRecent: Boolean = false,
    val rating: Float = 4.8f,
    val reviewersCount: Int = 120
) {
    fun getImageList(): List<String> = imageUrls.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    fun getSizeList(): List<String> = sizes.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    fun getColorList(): List<String> = colors.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: String,
    val name: String,
    val price: Double,
    val image: String,
    val size: String,
    val color: String,
    var quantity: Int
)

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val uid: String = "current_user",
    val name: String = "Suryo Atmojo",
    val email: String = "suryo.atmojo@gmail.com",
    val phone: String = "+62 812-3456-7890",
    val address: String = "Jl. Keraton No. 45, Kecamatan Pasar Kliwon, Surakarta, Jawa Tengah, 57118",
    val isLoggedIn: Boolean = true
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val orderId: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val itemsSummary: String, // Stringified checklist of products bought (e.g. "Kemeja Batik 1x S, Blouse Batik 2x M")
    val totalAmount: Double,
    val courier: String,      // JNE, J&T, SiCepat
    val shippingAddress: String,
    val courierCost: Double,
    val orderStatus: String = "Pesanan Diproses" // Diproses, Dikirim, Selesai
)
