package com.example.onlinebidding.screens.products

import androidx.compose.foundation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.onlinebidding.R
import com.example.onlinebidding.api.RetrofitInstance
import com.example.onlinebidding.api.AdminDeleteProductRequest
import android.widget.Toast
import kotlinx.coroutines.launch

/* ---------------- DATA ---------------- */

data class Laptop(
    val name: String,
    val specs: String,
    val rating: Double,
    val price: String,
    val imageRes: Int,
    val productId: Int? = null, // Store product ID for admin operations
    val auctionId: Int? = null // Store auction ID for backend connectivity
)

// Hardcoded fallback data (used if API fails)
private val fallbackLaptops = listOf(
    Laptop(
        "MacBook Pro 16\" M3",
        "Apple M3 Max · 48GB · 1TB SSD",
        4.9,
        "₹1,85,000",
        R.drawable.ic_macbook,
        productId = null
    ),
    Laptop(
        "Dell XPS 15 OLED",
        "Intel i7-13700H · 32GB · 1TB SSD",
        4.7,
        "₹95,000",
        R.drawable.ic_dell_xps,
        productId = null
    ),
    Laptop(
        "ASUS ROG Zephyrus G16",
        "Ryzen 9 7940HS · 32GB · 2TB SSD",
        4.8,
        "₹1,42,000",
        R.drawable.ic_asus_rog,
        productId = null
    )
)

// Extension function to map API response to Laptop
private fun com.example.onlinebidding.api.AuctionListItem.toLaptop(): Laptop {
    // Use direct fields if available, otherwise use product data
    val name = this.name ?: this.product.title
    val specs = this.specs ?: this.product.specs ?: this.product.condition ?: "Premium Device"
    val rating = this.rating ?: 4.5
    val price = this.price ?: "₹${String.format("%.0f", this.auction.current_price)}"
    val productId = this.product.id
    
    // For image, we'll use a default drawable for now (can be enhanced later with image URLs)
    val imageRes = when {
        name.contains("MacBook", ignoreCase = true) -> R.drawable.ic_macbook
        name.contains("Dell", ignoreCase = true) -> R.drawable.ic_dell_xps
        name.contains("ASUS", ignoreCase = true) -> R.drawable.ic_asus_rog
        else -> R.drawable.ic_macbook // Default
    }
    
    return Laptop(
        name = name,
        specs = specs,
        rating = rating,
        price = price,
        imageRes = imageRes,
        productId = productId,
        auctionId = this.auction.id // Include auction ID for bid placement
    )
}

/* ---------------- COMPOSABLE ---------------- */

@Composable
fun LaptopList(
    navController: NavHostController,
    onBack: () -> Unit,
    userToken: String? = null,
    userRole: String? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var laptops by remember { mutableStateOf<List<Laptop>>(fallbackLaptops) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isBackendConnected by remember { mutableStateOf(false) }
    var backendDataCount by remember { mutableStateOf(0) }
    val isAdmin = userRole == "admin"

    // Fetch laptops from backend - refresh when screen is displayed
    LaunchedEffect(navController.currentBackStackEntry?.id) {
        isLoading = true
        scope.launch {
            try {
                android.util.Log.d("LaptopList", "🔌 Attempting to connect to backend API...")
                val response = RetrofitInstance.api.listAuctions(category = "laptop")
                android.util.Log.d("LaptopList", "📡 API Response Code: ${response.code()}")
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val items = response.body()?.items ?: emptyList()
                    backendDataCount = items.size
                    laptops = items.map { it.toLaptop() }
                    isBackendConnected = true
                    errorMessage = null
                    android.util.Log.d("LaptopList", "✅ Backend Connected! Received ${items.size} laptops from API")
                } else {
                    // Use fallback data on API error
                    laptops = fallbackLaptops
                    isBackendConnected = false
                    errorMessage = "Using offline data (API error)"
                    android.util.Log.w("LaptopList", "⚠️ API Error: ${response.code()} - Using fallback data")
                }
            } catch (e: Exception) {
                // Use fallback data on network error
                laptops = fallbackLaptops
                isBackendConnected = false
                errorMessage = "Network error: ${e.message}"
                android.util.Log.e("LaptopList", "❌ Network Error: ${e.message}", e)
                android.util.Log.e("LaptopList", "📱 Using fallback data - Backend NOT connected")
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color.Black, Color(0xFF0B0B0B))
                )
            )
    ) {

        /* ---------- HEADER ---------- */

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, null, tint = Color(0xFFFFC107))
            }

            Text(
                "Laptops",
                color = Color(0xFFFFC107),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(laptops.size.toString(), color = Color(0xFFFFC107))
                    // Backend connection indicator
                    if (!isLoading) {
                        Text(
                            text = if (isBackendConnected) "✅ Online" else "⚠️ Offline",
                            color = if (isBackendConnected) Color(0xFF4CAF50) else Color(0xFFFF9800),
                            fontSize = 10.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
        
        // Show connection status banner
        if (!isLoading && errorMessage != null && !isBackendConnected) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x33FF9800)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF9800))
            ) {
                Text(
                    text = "⚠️ Using offline data - Backend not connected",
                    color = Color(0xFFFF9800),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        
        // Show success banner
        if (!isLoading && isBackendConnected && backendDataCount > 0) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x334CAF50)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4CAF50))
            ) {
                Text(
                    text = "✅ Connected to backend - ${backendDataCount} laptops loaded",
                    color = Color(0xFF4CAF50),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        
        // Show loading indicator
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFFFC107))
            }
        }

        /* ---------- SEARCH ---------- */

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .height(48.dp)
                .border(1.dp, Color(0xFFFFC107), RoundedCornerShape(14.dp))
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, null, tint = Color(0xFFFFC107))
            Spacer(Modifier.width(8.dp))
            Text("Search laptops...", color = Color.Gray)
        }

        Spacer(Modifier.height(14.dp))

        /* ---------- LIST ---------- */

        if (!isLoading && laptops.isNotEmpty()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            items(laptops.size) { index ->
                LaptopCard(
                    laptop = laptops[index],
                    navController = navController,
                    index = index,
                    isAdmin = isAdmin,
                    userToken = userToken,
                    onDelete = { productId ->
                        // Reload list after deletion
                        scope.launch {
                            try {
                                val response = RetrofitInstance.api.listAuctions(category = "laptop")
                                if (response.isSuccessful && response.body()?.success == true) {
                                    val items = response.body()?.items ?: emptyList()
                                    laptops = items.map { it.toLaptop() }
                                    backendDataCount = items.size
                                }
                            } catch (e: Exception) {
                                // Error reloading, ignore
                            }
                        }
                    }
                )
                }
            }
        } else if (!isLoading && laptops.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No laptops found",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        }
    }
}

/* ---------------- CARD ---------------- */

@Composable
private fun LaptopCard(
    laptop: Laptop,
    navController: NavHostController,
    index: Int,
    isAdmin: Boolean = false,
    userToken: String? = null,
    onDelete: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF3A2A00), RoundedCornerShape(22.dp))
            .background(Color(0xFF141414), RoundedCornerShape(22.dp))
            .padding(14.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Image(
                painter = painterResource(laptop.imageRes),
                contentDescription = laptop.name,
                modifier = Modifier
                    .size(74.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        laptop.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Admin-only Delete button
                        if (isAdmin && userToken != null && laptop.productId != null) {
                            IconButton(onClick = {
                                scope.launch {
                                    try {
                                        val response = RetrofitInstance.api.adminDeleteProduct(
                                            token = "Bearer $userToken",
                                            request = AdminDeleteProductRequest(laptop.productId)
                                        )
                                        if (response.isSuccessful && response.body()?.success == true) {
                                            Toast.makeText(context, "Laptop deleted successfully", Toast.LENGTH_SHORT).show()
                                            onDelete(laptop.productId)
                                        } else {
                                            Toast.makeText(context, response.body()?.error ?: "Delete failed", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }) {
                                Icon(
                                    Icons.Default.Delete,
                                    "Delete",
                                    tint = Color.Red,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Text(
                            "View",
                            color = Color(0xFFFFC107),
                            fontSize = 12.sp,
                            modifier = Modifier.clickable {
                                navController.navigate("laptop_details/$index")
                            }
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    laptop.specs,
                    color = Color(0xFFB0B0B0),
                    fontSize = 12.sp,
                    maxLines = 2
                )

                Spacer(Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(laptop.rating.toString(), color = Color(0xFFFFC107), fontSize = 12.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(laptop.price, color = Color(0xFFFFC107), fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    navController.navigate("credits/laptop/$index/${laptop.name}")
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
            ) { Text("Credits", color = Color.Black) }

            Button(
                onClick = {
                    val itemId = "laptop_$index"
                    if (com.example.onlinebidding.screens.products.CreditsState.hasCreditsForItem(itemId)) {
                        // Pass auction_id if available, otherwise use index
                        val auctionId = laptop.auctionId
                        if (auctionId != null) {
                            navController.navigate("auction_detail/$index/$auctionId")
                        } else {
                            navController.navigate("auction_detail/$index")
                        }
                    } else {
                        navController.navigate("credits/laptop/$index/${laptop.name}")
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
            ) { Text("Bid", color = Color.Black) }
        }
    }
}
