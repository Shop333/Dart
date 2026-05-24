package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OrderEntity
import com.example.ui.BatikViewModel
import com.example.ui.components.formatRupiah
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: BatikViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val ordersList by viewModel.ordersList.collectAsState()

    var isEditing by remember { mutableStateOf(false) }

    // Forms fields linked local properties
    var nameField by remember(userProfile.name) { mutableStateOf(userProfile.name) }
    var emailField by remember(userProfile.email) { mutableStateOf(userProfile.email) }
    var phoneField by remember(userProfile.phone) { mutableStateOf(userProfile.phone) }
    var addressField by remember(userProfile.address) { mutableStateOf(userProfile.address) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profil Pengguna", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BatikBrown)
            )
        },
        containerColor = WarmCreamBg,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card Details with interactive Edit Toggle Mode
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PureCreamSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(BatikBrown),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Avatar",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = userProfile.name,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = TextCharcoal
                                    )
                                    Text(
                                        text = "Member Premium BatikStore",
                                        fontSize = 11.sp,
                                        color = BatikGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    if (isEditing) {
                                        // Save edit fields
                                        viewModel.updateProfile(nameField, emailField, phoneField, addressField)
                                    }
                                    isEditing = !isEditing
                                }
                            ) {
                                Icon(
                                    imageVector = if (isEditing) Icons.Default.Save else Icons.Default.Edit,
                                    contentDescription = "Edit Profile",
                                    tint = BatikBrown
                                )
                            }
                        }

                        HorizontalDivider(
                            color = SoftBorder,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        if (isEditing) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = nameField,
                                    onValueChange = { nameField = it },
                                    label = { Text("Nama Lengkap", color = GraySubtitle) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BatikBrown, focusedLabelColor = BatikBrown),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = emailField,
                                    onValueChange = { emailField = it },
                                    label = { Text("Email", color = GraySubtitle) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BatikBrown, focusedLabelColor = BatikBrown),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = phoneField,
                                    onValueChange = { phoneField = it },
                                    label = { Text("Nomor Telepon", color = GraySubtitle) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BatikBrown, focusedLabelColor = BatikBrown),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = addressField,
                                    onValueChange = { addressField = it },
                                    label = { Text("Alamat Pengiriman Default", color = GraySubtitle) },
                                    minLines = 2,
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BatikBrown, focusedLabelColor = BatikBrown),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                ProfileDataItem("Email", userProfile.email)
                                ProfileDataItem("Telepon", userProfile.phone)
                                ProfileDataItem("Alamat Kirim", userProfile.address)
                            }
                        }
                    }
                }
            }

            // Order History Header Area
            item {
                Text(
                    text = "📦 Riwayat Pesanan Saya",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = TextCharcoal
                )
            }

            // Check Order historic lists
            if (ordersList.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PureCreamSurface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Riwayat Kosong",
                                tint = GraySubtitle.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Belum Ada Pesanan",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextCharcoal
                            )
                            Text(
                                text = "Lakukan pemesanan baju batik pertama Anda dan lacak histori pengiriman di panel ini.",
                                fontSize = 12.sp,
                                color = GraySubtitle,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            } else {
                items(ordersList) { order ->
                    OrderHistoryItemCard(order = order)
                }
            }
        }
    }
}

@Composable
fun ProfileDataItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            color = GraySubtitle,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            color = TextCharcoal,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 18.sp
        )
    }
}

@Composable
fun OrderHistoryItemCard(order: OrderEntity) {
    val dateString = remember(order.timestamp) {
        try {
            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("in", "ID"))
            sdf.format(Date(order.timestamp))
        } catch (e: Exception) {
            val s = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            s.format(Date(order.timestamp))
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PureCreamSurface),
        border = BorderStroke(1.dp, SoftBorder.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Invoice No & Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "INV/BTK/${order.orderId}/${1000 + order.orderId}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BatikBrown
                    )
                    Text(
                        text = dateString,
                        fontSize = 11.sp,
                        color = GraySubtitle
                    )
                }

                // Delivery Status Tag
                Box(
                    modifier = Modifier
                        .background(LightGoldBg, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = order.orderStatus,
                        color = BatikBrown,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(
                color = SoftBorder.copy(alpha = 0.5f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            // Body: Items list summary
            Text(
                text = "Rincian Barang:",
                fontSize = 11.sp,
                color = GraySubtitle,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = order.itemsSummary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextCharcoal,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Courier info & Total Amount paid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Kurir: ${order.courier}",
                        fontSize = 11.sp,
                        color = GraySubtitle
                    )
                    Text(
                        text = "Biaya Kirim: ${formatRupiah(order.courierCost)}",
                        fontSize = 11.sp,
                        color = GraySubtitle
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total Bayar",
                        fontSize = 11.sp,
                        color = GraySubtitle
                    )
                    Text(
                        text = formatRupiah(order.totalAmount),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = BatikBrown
                    )
                }
            }
        }
    }
}
