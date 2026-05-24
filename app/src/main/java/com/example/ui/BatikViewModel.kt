package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BatikViewModel(private val repository: BatikRepository) : ViewModel() {

    // Initializer to seed database empty state
    init {
        viewModelScope.launch {
            repository.populateCatalog()
        }
    }

    // Catalog State Flows
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("Semua")

    // Categories list pre-defined
    val categories = listOf("Semua", "Batik Solo", "Batik Jogja", "Batik Pekalongan", "Batik Cirebon")

    // Reactively filtered catalog list
    val filteredProducts: StateFlow<List<BatikProduct>> = combine(
        repository.allProducts,
        selectedCategory,
        searchQuery
    ) { products, category, query ->
        products.filter { product ->
            val matchesCategory = category == "Semua" || product.category.equals(category, ignoreCase = true)
            val matchesQuery = query.isBlank() || product.name.contains(query, ignoreCase = true) || product.description.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Selection details state
    private val _selectedProduct = MutableStateFlow<BatikProduct?>(null)
    val selectedProduct = _selectedProduct.asStateFlow()

    fun selectProduct(product: BatikProduct?) {
        _selectedProduct.value = product
    }

    // Cart Items flow
    val cartItems: StateFlow<List<CartItemEntity>> = repository.cartItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Reactive calculations form states
    val cartSubtotal: StateFlow<Double> = cartItems.map { items ->
        items.sumOf { it.price * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun addToCart(product: BatikProduct, size: String, color: String, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.addToCart(product, size, color)
            onDone()
        }
    }

    fun updateCartQuantity(item: CartItemEntity, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartItemQuantity(item, quantity)
        }
    }

    fun removeCartItem(itemId: Int) {
        viewModelScope.launch {
            repository.deleteCartItem(itemId)
        }
    }

    // Profile section state
    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .map { it ?: UserProfile() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfile()
        )

    fun updateProfile(name: String, email: String, phone: String, address: String) {
        viewModelScope.launch {
            repository.saveUserProfile(
                UserProfile(name = name, email = email, phone = phone, address = address)
            )
        }
    }

    // Checkout section states
    val selectedCourier = MutableStateFlow("JNE")
    val courierOptionCost: StateFlow<Double> = selectedCourier.map { courier ->
        when (courier) {
            "JNE" -> 15000.0
            "J&T" -> 12000.0
            "SiCepat" -> 10000.0
            else -> 15000.0
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 15000.0)

    val checkoutTotal: StateFlow<Double> = combine(cartSubtotal, courierOptionCost) { subtotal, courier ->
        subtotal + courier
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Purchase history flow
    val ordersList: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun confirmOrder(onDone: () -> Unit) {
        val items = cartItems.value
        val courier = selectedCourier.value
        val cost = courierOptionCost.value
        val addressState = userProfile.value.address
        val total = checkoutTotal.value

        if (items.isNotEmpty()) {
            viewModelScope.launch {
                repository.createOrder(
                    items = items,
                    courier = courier,
                    shippingAddress = addressState,
                    courierCost = cost,
                    totalAmount = total
                )
                onDone()
            }
        }
    }
}

class BatikViewModelFactory(private val repository: BatikRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BatikViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BatikViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
