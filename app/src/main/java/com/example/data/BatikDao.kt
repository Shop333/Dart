package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM batik_products")
    fun getAllProducts(): Flow<List<BatikProduct>>

    @Query("SELECT * FROM batik_products WHERE category = :category")
    fun getProductsByCategory(category: String): Flow<List<BatikProduct>>

    @Query("SELECT * FROM batik_products WHERE name LIKE :query OR description LIKE :query OR category LIKE :query")
    fun searchProducts(query: String): Flow<List<BatikProduct>>

    @Query("SELECT * FROM batik_products WHERE id = :id")
    suspend fun getProductById(id: String): BatikProduct?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<BatikProduct>)

    @Query("SELECT COUNT(*) FROM batik_products")
    suspend fun getCount(): Int
}

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItemEntity)

    @Update
    suspend fun updateCartItem(cartItem: CartItemEntity)

    @Query("SELECT * FROM cart_items WHERE productId = :productId AND size = :size AND color = :color LIMIT 1")
    suspend fun getCartItemBySpec(productId: String, size: String, color: String): CartItemEntity?

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteCartItem(id: Int)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profiles WHERE uid = 'current_user' LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)
}
