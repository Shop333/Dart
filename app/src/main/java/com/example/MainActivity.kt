package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.BatikDatabase
import com.example.data.BatikRepository
import com.example.ui.BatikViewModel
import com.example.ui.BatikViewModelFactory
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.DetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Room Database & repository locally
        val database = BatikDatabase.getDatabase(applicationContext)
        val repository = BatikRepository(database)
        val viewModelFactory = BatikViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[BatikViewModel::class.java]

        setContent {
            MyApplicationTheme {
                MainAppContainer(viewModel)
            }
        }
    }
}

@Composable
fun MainAppContainer(viewModel: BatikViewModel) {
    val navController = rememberNavController()

    // Monitor current back stack entry
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Tab routes where Bottom Bar should be persistently shown
    val rootTabRoutes = listOf("home_tab", "cart_tab", "profile_tab")
    val shouldShowBottomBar = currentRoute in rootTabRoutes

    val cartItems by viewModel.cartItems.collectAsState()
    val totalCartCount = cartItems.sumOf { it.quantity }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = shouldShowBottomBar,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                NavigationBar(
                    containerColor = PureCreamSurface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    val tabs = listOf(
                        BottomTabSpecs("home_tab", "Galeri", Icons.Filled.Home, Icons.Outlined.Home),
                        BottomTabSpecs("cart_tab", "Keranjang", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart, totalCartCount),
                        BottomTabSpecs("profile_tab", "Profil Saya", Icons.Filled.Person, Icons.Outlined.Person)
                    )

                    tabs.forEach { tab ->
                        val isSelected = currentRoute == tab.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != tab.route) {
                                    navController.navigate(tab.route) {
                                        // Pop up to the start destination of the graph to
                                        // avoid building up a large stack of destinations on the backstack
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                if (tab.badgeCount > 0) {
                                    BadgedBox(
                                        badge = {
                                            Badge(containerColor = BatikBrown, contentColor = Color.White) {
                                                Text("${tab.badgeCount}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = tab.title
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.title
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BatikBrown,
                                selectedTextColor = BatikBrown,
                                unselectedIconColor = GraySubtitle,
                                unselectedTextColor = GraySubtitle,
                                indicatorColor = LightGoldBg
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home_tab",
            modifier = Modifier.padding(innerPadding)
        ) {
            // Screen 1: Home Catalog Galleria Tab
            composable("home_tab") {
                HomeScreen(
                    viewModel = viewModel,
                    onProductClick = { product ->
                        viewModel.selectProduct(product)
                        navController.navigate("detail")
                    },
                    onCartClick = {
                        navController.navigate("cart_tab")
                    }
                )
            }

            // Screen 2: Shopping Cart Tab
            composable("cart_tab") {
                CartScreen(
                    viewModel = viewModel,
                    onCheckoutClick = {
                        navController.navigate("checkout")
                    }
                )
            }

            // Screen 3: Profile and Order Tracker Tab
            composable("profile_tab") {
                ProfileScreen(
                    viewModel = viewModel
                )
            }

            // Screen 4: Product details forward modal (Hides Bottom bar)
            composable("detail") {
                DetailScreen(
                    viewModel = viewModel,
                    onBack = {
                        navController.popBackStack()
                    },
                    onBuyNow = {
                        // Takes the user directly forward to checking out form
                        navController.navigate("checkout")
                    }
                )
            }

            // Screen 5: Checkout Form list (Hides Bottom Bar)
            composable("checkout") {
                CheckoutScreen(
                    viewModel = viewModel,
                    onBack = {
                        navController.popBackStack()
                    },
                    onOrderSuccess = {
                        // Navigate back with clear of stack so cart is cleared
                        navController.navigate("profile_tab") {
                            popUpTo("home_tab") { inclusive = false }
                        }
                    }
                )
            }
        }
    }
}

data class BottomTabSpecs(
    val route: String,
    val title: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val badgeCount: Int = 0
)
