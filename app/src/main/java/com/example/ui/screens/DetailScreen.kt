package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.BatikProduct
import com.example.ui.BatikViewModel
import com.example.ui.components.formatRupiah
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: BatikViewModel,
    onBack: () -> Unit,
    onBuyNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    val product by viewModel.selectedProduct.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    if (product == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Produk tidak ditemukan.", color = TextCharcoal)
        }
        return
    }

    val currentProduct = product!!
    val images = currentProduct.getImageList()
    var selectedImageIndex by remember { mutableStateOf(0) }

    val sizes = currentProduct.getSizeList()
    var selectedSize by remember { mutableStateOf(sizes.firstOrNull() ?: "M") }

    val colors = currentProduct.getColorList()
    var selectedColor by remember { mutableStateOf(colors.firstOrNull() ?: "Soga") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detail Produk", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BatikBrown)
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = PureCreamSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Outlined Add To Cart Button
                    OutlinedButton(
                        onClick = {
                            viewModel.addToCart(currentProduct, selectedSize, selectedColor) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Dimasukkan ke Keranjang ($selectedSize - $selectedColor)"
                                    )
                                }
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = BatikBrown
                        ),
                        border = BorderStroke(1.5.dp, BatikBrown),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Tambah Keranjang",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    // Direct Buy Now Button
                    Button(
                        onClick = {
                            // Add to cart and directly navigate to checkout
                            viewModel.addToCart(currentProduct, selectedSize, selectedColor) {
                                onBuyNow()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BatikBrown,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Beli Sekarang",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        },
        containerColor = WarmCreamBg,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Main Picture Slider Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .background(Color.Black.copy(alpha = 0.05f))
            ) {
                val activeImage = if (images.isNotEmpty()) images[selectedImageIndex] else ""

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(activeImage)
                        .crossfade(true)
                        .build(),
                    contentDescription = currentProduct.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Multiple Images Mini Preview Rows
                if (images.size > 1) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        images.forEachIndexed { index, imgUrl ->
                            val isSelected = index == selectedImageIndex
                            val strokeColor = if (isSelected) BatikGold else Color.Transparent

                            Box(
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(2.dp, strokeColor, RoundedCornerShape(6.dp))
                                    .clickable { selectedImageIndex = index }
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(imgUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            // Products information Card Sheet
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureCreamSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Category & Rating
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(LightGoldBg, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = currentProduct.category,
                                color = BatikBrown,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Rating layout
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Reviews",
                                tint = BatikGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${currentProduct.rating}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextCharcoal
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "(${currentProduct.reviewersCount} ulasan)",
                                fontSize = 12.sp,
                                color = GraySubtitle
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentProduct.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = TextCharcoal,
                        lineHeight = 26.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = formatRupiah(currentProduct.price),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BatikBrown
                    )
                }
            }

            // Size Selection Panel
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureCreamSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Pilih Ukuran (Size)",
                        fontWeight = FontWeight.Bold,
                        color = TextCharcoal,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sizes.forEach { size ->
                            val isSelected = size == selectedSize
                            val containerColor = if (isSelected) BatikBrown else LightGoldBg
                            val contentColor = if (isSelected) Color.White else BatikBrown

                            Box(
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(containerColor)
                                    .clickable { selectedSize = size }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = size,
                                    color = contentColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Color Selection Panel
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureCreamSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Pilihan Warna",
                        fontWeight = FontWeight.Bold,
                        color = TextCharcoal,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(colors) { colorName ->
                            val isSelected = colorName == selectedColor
                            val containerColor = if (isSelected) BatikBrown else Color.White
                            val textColor = if (isSelected) Color.White else TextCharcoal
                            val borderCol = if (isSelected) BatikBrown else SoftBorder

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(containerColor)
                                    .border(1.dp, borderCol, RoundedCornerShape(10.dp))
                                    .clickable { selectedColor = colorName }
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = colorName,
                                    color = textColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Detail Description Panel
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureCreamSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Deskripsi Produk",
                        fontWeight = FontWeight.Bold,
                        color = TextCharcoal,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentProduct.description,
                        fontSize = 13.sp,
                        color = TextCharcoal.copy(alpha = 0.85f),
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
