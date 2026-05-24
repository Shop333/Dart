package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class BatikRepository(private val database: BatikDatabase) {

    private val productDao = database.productDao()
    private val cartDao = database.cartDao()
    private val userDao = database.userDao()
    private val orderDao = database.orderDao()

    val allProducts: Flow<List<BatikProduct>> = productDao.getAllProducts()
    val cartItems: Flow<List<CartItemEntity>> = cartDao.getCartItems()
    val userProfile: Flow<UserProfile?> = userDao.getUserProfile()
    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()

    fun getProductsByCategory(category: String): Flow<List<BatikProduct>> {
        return if (category.lowercase() == "semua" || category.isEmpty()) {
            allProducts
        } else {
            productDao.getProductsByCategory(category)
        }
    }

    fun searchProducts(query: String): Flow<List<BatikProduct>> {
        return if (query.trim().isEmpty()) {
            allProducts
        } else {
            productDao.searchProducts("%$query%")
        }
    }

    suspend fun getProductById(id: String): BatikProduct? {
        return productDao.getProductById(id)
    }

    // Seeding products on demand
    suspend fun populateCatalog() {
        if (productDao.getCount() == 0) {
            val prepopulatedList = listOf(
                BatikProduct(
                    id = "p1",
                    name = "Kemeja Batik Solo Soga Premium",
                    price = 385000.0,
                    imageUrls = "https://images.unsplash.com/photo-1620799140408-edc6dcb6d633?q=80&w=600,https://images.unsplash.com/photo-1610189012906-40da39b9227c?q=80&w=600",
                    description = "Kemeja Pria Batik Solo Premium Tulis Halus dengan motif Sido Luhur klasik yang melambangkan martabat luhur. Dibuat dengan bahan katun primisima premium yang sejuk, nyaman dipakai seharian, serta memiliki pewarnaan soga alam asli Surakarta yang bernilai estetika tinggi.",
                    sizes = "S,M,L,XL,XXL",
                    colors = "Coklat Soga,Hitam Arang",
                    category = "Batik Solo",
                    isBestseller = true,
                    isRecent = false
                ),
                BatikProduct(
                    id = "p2",
                    name = "Daster Batik Pekalongan Flora Modern",
                    price = 125000.0,
                    imageUrls = "https://images.unsplash.com/photo-1544441893-675973e31985?q=80&w=600,https://images.unsplash.com/photo-1520004434532-66d45ee96aae?q=80&w=600",
                    description = "Daster / Homedress Batik Pekalongan motif flora pesisiran yang cerah, anggun, dan kekinian. Menggunakan 100% katun rayon super premium yang sangat dingin saat menyentuh kulit. Sempurna untuk aktivitas santai di rumah maupun jalan-jalan sore.",
                    sizes = "M,L,XL",
                    colors = "Merah Indigo,Biru Toska,Hijau Daun",
                    category = "Batik Pekalongan",
                    isBestseller = false,
                    isRecent = true
                ),
                BatikProduct(
                    id = "p3",
                    name = "Gamis Batik Jogja Sekar Jagad",
                    price = 420000.0,
                    imageUrls = "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?q=80&w=600,https://images.unsplash.com/photo-1574169208507-84376144848b?q=80&w=600",
                    description = "Gamis Eksklusif Keraton Yogyakarta motif Sekar Jagad dikombinasikan dengan kain polos katun Toyobo super premium. Menampilkan perpaduan kultur klasik dan modern yang sangat syar'i dan elegan. Dilengkapi resleting depan tersembunyi (busui friendly).",
                    sizes = "S,M,L,XL,XXL",
                    colors = "Mocca Gold,Hitam Klasik",
                    category = "Batik Jogja",
                    isBestseller = true,
                    isRecent = true
                ),
                BatikProduct(
                    id = "p4",
                    name = "Kemeja Batik Cirebon Mega Mendung",
                    price = 295000.0,
                    imageUrls = "https://images.unsplash.com/photo-1552346154-21d32810aba3?q=80&w=600,https://images.unsplash.com/photo-1598033129183-c4f50c736f10?q=80&w=600",
                    description = "Kemeja Batik pria motif legendaris Mega Mendung khas Cirebon dengan gradasi warna biru mendalam yang melambangkan ketenangan awan pembawa hujan. Proses pembatikan cap modern dengan tinta premium tahan pudar dan jahitan slim-fit nan rapi.",
                    sizes = "M,L,XL,XXL",
                    colors = "Biru Gradasi,Merah Mega,Hitam Gold",
                    category = "Batik Cirebon",
                    isBestseller = false,
                    isRecent = true
                ),
                BatikProduct(
                    id = "p5",
                    name = "Blouse Batik Pekalongan Canting Mas",
                    price = 195000.0,
                    imageUrls = "https://images.unsplash.com/photo-1520004434532-66d45ee96aae?q=80&w=600,https://images.unsplash.com/photo-1544441893-675973e31985?q=80&w=600",
                    description = "Blouse batik wanita modern dengan aksen hiasan kancing asimetris dan kerah shanghai. Menampilkan ceplokan bunga canting Pekalongan yang dinamis dan segar. Pas pilihan untuk busana kerja kantor harian maupun menghadiri perjamuan semi-formal.",
                    sizes = "S,M,L,XL",
                    colors = "Pastel Yellow,Teal Green,Terracotta",
                    category = "Batik Pekalongan",
                    isBestseller = true,
                    isRecent = false
                ),
                BatikProduct(
                    id = "p6",
                    name = "Selendang Sutra Batik Solo Sogan",
                    price = 650000.0,
                    imageUrls = "https://images.unsplash.com/photo-1574169208507-84376144848b?q=80&w=600,https://images.unsplash.com/photo-1594938298603-c8148c4dae35?q=80&w=600",
                    description = "Selendang sutra ATBM (Alat Tenun Bukan Mesin) Batik tulis eksklusif motif parang kencana kombinasi sidomukti. Mengkilap, sangat halus, jatuh anggun melingkari bahu. Sempurna untuk menyempurnakan kebaya adat ningrat Jawa Anda.",
                    sizes = "All Size",
                    colors = "Kuning Gold,Coklat Soga",
                    category = "Batik Solo",
                    isBestseller = false,
                    isRecent = false
                )
            )
            productDao.insertProducts(prepopulatedList)
        }
        
        // Also seed user profile
        userDao.insertUserProfile(UserProfile())
    }

    // Cart management
    suspend fun addToCart(product: BatikProduct, selectedSize: String, selectedColor: String) {
        val existing = cartDao.getCartItemBySpec(product.id, selectedSize, selectedColor)
        if (existing != null) {
            existing.quantity += 1
            cartDao.updateCartItem(existing)
        } else {
            val firstImage = product.getImageList().firstOrNull() ?: ""
            cartDao.insertCartItem(
                CartItemEntity(
                    productId = product.id,
                    name = product.name,
                    price = product.price,
                    image = firstImage,
                    size = selectedSize,
                    color = selectedColor,
                    quantity = 1
                )
            )
        }
    }

    suspend fun updateCartItemQuantity(item: CartItemEntity, newQty: Int) {
        if (newQty <= 0) {
            cartDao.deleteCartItem(item.id)
        } else {
            item.quantity = newQty
            cartDao.updateCartItem(item)
        }
    }

    suspend fun deleteCartItem(itemId: Int) {
        cartDao.deleteCartItem(itemId)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    // User operations
    suspend fun saveUserProfile(profile: UserProfile) {
        userDao.insertUserProfile(profile)
    }

    // Checkout operations
    suspend fun createOrder(items: List<CartItemEntity>, courier: String, shippingAddress: String, courierCost: Double, totalAmount: Double) {
        val summary = items.joinToString(", ") { "${it.name} (${it.quantity}x - ${it.size})" }
        val order = OrderEntity(
            itemsSummary = summary,
            totalAmount = totalAmount,
            courier = courier,
            shippingAddress = shippingAddress,
            courierCost = courierCost
        )
        // Insert order
        orderDao.insertOrder(order)
        // Clear active cart
        cartDao.clearCart()
    }
}
