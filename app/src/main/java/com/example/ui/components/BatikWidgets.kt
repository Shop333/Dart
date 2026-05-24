package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.BatikProduct
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

// Rupiah currency formatter using solid, crash-safe locale formatting
fun formatRupiah(price: Double): String {
    return try {
        val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
        "Rp ${formatter.format(price.toLong())}"
    } catch (e: Exception) {
        val backupFormatter = NumberFormat.getNumberInstance(Locale.GERMAN)
        "Rp ${backupFormatter.format(price.toLong())}"
    }
}

@Composable
fun SearchBarWidget(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Cari batik impian Anda...", color = GraySubtitle) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Cari",
                tint = BatikBrown
            )
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BatikBrown,
            unfocusedBorderColor = SoftBorder,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = BatikBrown
        ),
        shape = RoundedCornerShape(24.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            keyboardController?.hide()
        }),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
    )
}

@Composable
fun PromoBannerSlider(
    modifier: Modifier = Modifier
) {
    val banners = listOf(
        PromoBanner(
            "https://images.unsplash.com/photo-1620799140408-edc6dcb6d633?q=80&w=600",
            "Maha Karya Tradisi",
            "Diskon hingga 30% untuk Batik Solo tulis asli"
        ),
        PromoBanner(
            "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?q=80&w=600",
            "Yogyakarta Klasik",
            "Koleksi Sekar Jagad eksklusif Keraton"
        ),
        PromoBanner(
            "https://images.unsplash.com/photo-1552346154-21d32810aba3?q=80&w=600",
            "Pekalongan Coastal Wave",
            "Sentuhan warna modern, gratis pengiriman seluruh Jawa"
        )
    )

    var currentBannerIndex by remember { mutableStateOf(0) }

    // Auto-scroll loop
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            currentBannerIndex = (currentBannerIndex + 1) % banners.size
        }
    }

    val banner = banners[currentBannerIndex]

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkBrownPrimary)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(banner.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Promo Banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay for visual aesthetics and text contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.82f))
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(BatikGold, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "PROMO SPESIAL",
                    color = Color.Black,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = banner.title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = banner.subtitle,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Dot indicators
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            banners.indices.forEach { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == currentBannerIndex) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(if (index == currentBannerIndex) BatikGold else Color.White.copy(alpha = 0.5f))
                )
            }
        }
    }
}

data class PromoBanner(
    val imageUrl: String,
    val title: String,
    val subtitle: String
)

@Composable
fun CategoryTabs(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            val containerColor = if (isSelected) BatikBrown else LightGoldBg
            val contentColor = if (isSelected) Color.White else BatikBrown
            val borderModifier = if (isSelected) Modifier else Modifier.background(SoftBorder.copy(alpha = 0.4f))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(containerColor)
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category,
                    color = contentColor,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun ProductCard(
    product: BatikProduct,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val firstImage = product.getImageList().firstOrNull() ?: ""

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PureCreamSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(firstImage)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Label tagging
                if (product.isBestseller) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .background(BatikGold, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "Laris",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (product.isRecent) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .background(BatikBrown, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "Baru",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Text(
                    text = product.category,
                    color = GraySubtitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = product.name,
                    color = TextCharcoal,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp,
                    modifier = Modifier.heightIn(min = 36.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatRupiah(product.price),
                    color = BatikBrown,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = BatikGold,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${product.rating}",
                        color = TextCharcoal,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${product.reviewersCount})",
                        color = GraySubtitle,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
