package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.BatikProduct
import com.example.ui.BatikViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: BatikViewModel,
    onProductClick: (BatikProduct) -> Unit,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()

    val totalCartItemsCount = cartItems.sumOf { it.quantity }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(BatikGold)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "B",
                                color = Color.Black,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BatikStore",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onCartClick) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Keranjang Belanja",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                            if (totalCartItemsCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(17.dp)
                                        .clip(CircleShape)
                                        .background(BatikGold)
                                        .align(Alignment.TopEnd),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$totalCartItemsCount",
                                        color = Color.Black,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BatikBrown
                )
            )
        },
        containerColor = WarmCreamBg,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Search Bar area with solid dark gradient accent behind
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BatikBrown)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    SearchBarWidget(
                        query = searchQuery,
                        onQueryChange = { viewModel.searchQuery.value = it }
                    )
                }
            }

            // Slider & Categories appear ONLY when search query is empty to avoid catalog distraction
            if (searchQuery.isBlank()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    PromoBannerSlider(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // Categories Header selector & Tabs
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Jelajahi Kategori",
                        color = TextCharcoal,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    CategoryTabs(
                        categories = viewModel.categories,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { viewModel.selectedCategory.value = it }
                    )
                }

                // Horizontal list of Bestsellers
                val bestsellers = filteredProducts.filter { it.isBestseller }
                if (bestsellers.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Koleksi Terlaris 🔥",
                                color = TextCharcoal,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(bestsellers) { product ->
                                ProductCard(
                                    product = product,
                                    onClick = { onProductClick(product) },
                                    modifier = Modifier.width(160.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Main Catalog Feed Title
            item {
                Spacer(modifier = Modifier.height(24.dp))
                val feedTitle = when {
                    searchQuery.isNotBlank() -> "Hasil Pencarian \"$searchQuery\""
                    selectedCategory != "Semua" -> "Koleksi $selectedCategory"
                    else -> "Semua Produk Batik"
                }

                Text(
                    text = feedTitle,
                    color = TextCharcoal,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (filteredProducts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "🧶",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Batik tidak ditemukan",
                                color = TextCharcoal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Cobalah kata kunci lain atau pilih kategori Semua.",
                                color = GraySubtitle,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 32.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                // Chunk products list into pairs of two to form a responsive scrolling grid
                val pairs = filteredProducts.chunked(2)
                items(pairs) { rowProducts ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProductCard(
                            product = rowProducts[0],
                            onClick = { onProductClick(rowProducts[0]) },
                            modifier = Modifier.weight(1f)
                        )
                        if (rowProducts.size > 1) {
                            ProductCard(
                                product = rowProducts[1],
                                onClick = { onProductClick(rowProducts[1]) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            // Empty box placeholder to maintain grids sizing layout alignment
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
