package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BatikViewModel
import com.example.ui.components.formatRupiah
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: BatikViewModel,
    onBack: () -> Unit,
    onOrderSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val cartSubtotal by viewModel.cartSubtotal.collectAsState()
    val courierCost by viewModel.courierOptionCost.collectAsState()
    val checkoutTotal by viewModel.checkoutTotal.collectAsState()
    val selectedCourier by viewModel.selectedCourier.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var showSuccessDialog by remember { mutableStateOf(false) }

    // Shipping address form fields (linked local states pre-populated from VM)
    var name by remember(userProfile.name) { mutableStateOf(userProfile.name) }
    var phone by remember(userProfile.phone) { mutableStateOf(userProfile.phone) }
    var address by remember(userProfile.address) { mutableStateOf(userProfile.address) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout Pesanan", fontWeight = FontWeight.Bold, color = Color.White) },
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
            if (cartItems.isNotEmpty()) {
                Surface(
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    color = PureCreamSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Total Pembayaran",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextCharcoal
                            )
                            Text(
                                formatRupiah(checkoutTotal),
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                color = BatikBrown
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                // Save profile in database first
                                viewModel.updateProfile(name, userProfile.email, phone, address)
                                // Trigger order creation
                                viewModel.confirmOrder {
                                    showSuccessDialog = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BatikBrown,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = name.isNotBlank() && phone.isNotBlank() && address.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Text(
                                "Konfirmasi Order",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        },
        containerColor = WarmCreamBg,
        modifier = modifier
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Address Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PureCreamSurface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "📍 Alamat Pengiriman",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = TextCharcoal
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Nama Penerima", color = GraySubtitle) },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BatikBrown,
                                    unfocusedBorderColor = SoftBorder,
                                    focusedLabelColor = BatikBrown
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("Nomor Hubungi (Telepon)", color = GraySubtitle) },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BatikBrown,
                                    unfocusedBorderColor = SoftBorder,
                                    focusedLabelColor = BatikBrown
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = address,
                                onValueChange = { address = it },
                                label = { Text("Alamat Lengkap", color = GraySubtitle) },
                                minLines = 2,
                                maxLines = 4,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BatikBrown,
                                    unfocusedBorderColor = SoftBorder,
                                    focusedLabelColor = BatikBrown
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Courier Logistics的选择
                item {
                    val couriers = listOf(
                        CourierOption("JNE", "Reguler (2-3 Hari)", 15000.0),
                        CourierOption("J&T", "Ekspres (1-2 Hari)", 12000.0),
                        CourierOption("SiCepat", "Hemat (3-5 Hari)", 10000.0)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PureCreamSurface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🚚 Pilihan Pengiriman (Kurir)",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = TextCharcoal
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            couriers.forEach { option ->
                                val isSelected = option.name == selectedCourier
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) LightGoldBg else Color.Transparent)
                                        .border(
                                            1.dp,
                                            if (isSelected) BatikGold else SoftBorder,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { viewModel.selectedCourier.value = option.name }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalShipping,
                                        contentDescription = option.name,
                                        tint = BatikBrown,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = option.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = TextCharcoal
                                        )
                                        Text(
                                            text = option.service,
                                            fontSize = 11.sp,
                                            color = GraySubtitle
                                        )
                                    }
                                    Text(
                                        text = formatRupiah(option.cost),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp,
                                        color = BatikBrown
                                    )
                                }
                            }
                        }
                    }
                }

                // Billing Recap List Items Summary
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PureCreamSurface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "📋 Ringkasan Pesanan",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = TextCharcoal
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            cartItems.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${item.name} (${item.size} - ${item.color})",
                                        fontSize = 13.sp,
                                        color = TextCharcoal,
                                        modifier = Modifier.weight(0.7f),
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${item.quantity}x",
                                        fontSize = 13.sp,
                                        color = GraySubtitle,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                    Text(
                                        text = formatRupiah(item.price * item.quantity),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextCharcoal,
                                        modifier = Modifier.weight(0.3f),
                                        textAlign = TextAlign.End
                                    )
                                }
                            }

                            HorizontalDivider(
                                color = SoftBorder,
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )

                            // Price breakdown calculations
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Subtotal Belanja", fontSize = 13.sp, color = GraySubtitle)
                                Text(formatRupiah(cartSubtotal), fontSize = 13.sp, color = TextCharcoal)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Ongkos Kirim ($selectedCourier)", fontSize = 13.sp, color = GraySubtitle)
                                Text(formatRupiah(courierCost), fontSize = 13.sp, color = TextCharcoal)
                            }
                        }
                    }
                }
            }

            // High aesthetic order success overlay
            if (showSuccessDialog) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = PureCreamSurface)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Berhasil",
                                tint = SuccessLeaf,
                                modifier = Modifier.size(72.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Pesanan Berhasil!",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = TextCharcoal,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Terima kasih telah berbelanja di BatikStore. Sineas pembatik kami segera memproses pesanan luhur Anda.",
                                fontSize = 13.sp,
                                color = GraySubtitle,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    showSuccessDialog = false
                                    onOrderSuccess()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BatikBrown),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("Kembali Berbelanja", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class CourierOption(
    val name: String,
    val service: String,
    val cost: Double
)
