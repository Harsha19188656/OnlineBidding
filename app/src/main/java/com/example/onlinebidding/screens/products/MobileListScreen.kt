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
import com.example.onlinebidding.screens.products.CreditsState
import kotlinx.coroutines.launch

/* ---------------- DATA ---------------- */

data class Mobile(
    val name: String,
    val specs: String,
    val rating: Double,
    val price: String,
    val imageRes: Int,
    val productId: Int? = null // Store product ID for admin operations
)

// Hardcoded fallback data (used if API fails)
private val fallbackMobiles = listOf(
    Mobile(
        "iPhone 15 Pro Max",
        "A17 Pro Chip · 512GB · Titanium",
        4.8,
        "₹1,28,000",
        R.drawable.ic_appleiphone15pro,
        productId = null
    ),
    Mobile(
        "Samsung Galaxy S24 Ultra",
        "Snapdragon 8 Gen 3 · 256GB · S Pen",
        4.6,
        "₹98,000",
        R.drawable.ic_samsunggalaxys24ultra,
        productId = null
    ),
    Mobile(
        "OnePlus 12 Pro",
        "Snapdragon 8 Gen 3 · 256GB · Fast Charging",
        4.9,
        "₹54,000",
        R.drawable.ic_oneplus12pro,
        productId = null
    )
)

// Extension function to map API response to Mobile
private fun com.example.onlinebidding.api.AuctionListItem.toMobile(): Mobile {
    // Use direct fields if available, otherwise use product data
    val name = this.name ?: this.product.title
    val specs = this.specs ?: this.product.specs ?: this.product.condition ?: "Premium Device"
    val rating = this.rating ?: 4.5
    val price = this.price ?: "₹${String.format("%.0f", this.auction.current_price)}"
    
    // For image, we'll use a default drawable based on product name
    val imageRes = when {
        name.contains("iPhone", ignoreCase = true) -> R.drawable.ic_appleiphone15pro
        name.contains("Samsung", ignoreCase = true) -> R.drawable.ic_samsunggalaxys24ultra
        name.contains("OnePlus", ignoreCase = true) -> R.drawable.ic_oneplus12pro
        else -> R.drawable.ic_appleiphone15pro // Default
    }
    
    return Mobile(
        name = name,
        specs = specs,
        rating = rating,
        price = price,
        imageRes = imageRes,
        productId = this.product.id
    )
}

/* ---------------- COMPOSABLE ---------------- */

@Composable
fun MobileList(
    navController: NavHostController,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var mobiles by remember { mutableStateOf<List<Mobile>>(fallbackMobiles) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isBackendConnected by remember { mutableStateOf(false) }
    var backendDataCount by remember { mutableStateOf(0) }

    // Fetch mobiles from backend - refresh when screen is displayed
    LaunchedEffect(navController.currentBackStackEntry?.id) {
        isLoading = true
        scope.launch {
            try {
                android.util.Log.d("MobileList", "🔌 Attempting to connect to backend API...")
                val response = RetrofitInstance.api.listAuctions(category = "mobile")
                android.util.Log.d("MobileList", "📡 API Response Code: ${response.code()}")
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val items = response.body()?.items ?: emptyList()
                    backendDataCount = items.size
                    if (items.isNotEmpty()) {
                        mobiles = items.map { it.toMobile() }
                        isBackendConnected = true
                        errorMessage = null
                        android.util.Log.d("MobileList", "✅ Backend Connected! Received ${items.size} mobiles from API")
                    } else {
                        // API connected but returned empty list - use fallback data
                        mobiles = fallbackMobiles
                        isBackendConnected = false
                        errorMessage = "No mobiles in database"
                        android.util.Log.w("MobileList", "⚠️ API connected but returned empty list - using fallback data")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.w("MobileList", "⚠️ API Error: ${response.code()} - ${response.message()}. Error body: $errorBody")
                    // Use fallback data on API error
                    mobiles = fallbackMobiles
                    isBackendConnected = false
                    errorMessage = "Using offline data (API error)"
                }
            } catch (e: Exception) {
                // Use fallback data on network error
                val errorMsg = e.message ?: "Unknown error"
                android.util.Log.e("MobileList", "❌ Network Error: $errorMsg", e)
                android.util.Log.e("MobileList", "📱 Using fallback data - Backend NOT connected")
                mobiles = fallbackMobiles
                isBackendConnected = false
                errorMessage = "Network error: $errorMsg"
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
                "Mobiles",
                color = Color(0xFFFFC107),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Column(horizontalAlignment = Alignment.End) {
                Text(mobiles.size.toString(), color = Color(0xFFFFC107))
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
                    text = "✅ Connected to backend - ${backendDataCount} mobiles loaded",
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
            Text("Search mobiles...", color = Color.Gray)
        }

        Spacer(Modifier.height(14.dp))

        /* ---------- LIST ---------- */

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            items(mobiles.size) { index ->
                MobileCard(
                    mobile = mobiles[index],
                    navController = navController,
                    index = index
                )
            }
        }
    }
}

/* ---------------- CARD ---------------- */

@Composable
private fun MobileCard(
    mobile: Mobile,
    navController: NavHostController,
    index: Int
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF3A2A00), RoundedCornerShape(22.dp))
            .background(Color(0xFF141414), RoundedCornerShape(22.dp))
            .padding(14.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Image(
                painter = painterResource(mobile.imageRes),
                contentDescription = mobile.name,
                modifier = Modifier
                    .size(74.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        mobile.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "View",
                        color = Color(0xFFFFC107),
                        fontSize = 12.sp,
                        modifier = Modifier.clickable {
                            navController.navigate("mobile_details/$index")
                        }
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    mobile.specs,
                    color = Color(0xFFB0B0B0),
                    fontSize = 12.sp,
                    maxLines = 2
                )

                Spacer(Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(mobile.rating.toString(), color = Color(0xFFFFC107), fontSize = 12.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(mobile.price, color = Color(0xFFFFC107), fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val itemId = "mobile_$index"
            val hasCredits = CreditsState.hasCreditsForItem(itemId)
            
            Button(
                onClick = {
                    navController.navigate("credits/mobile/$index/${mobile.name}")
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
            ) { 
                Text(if (hasCredits) "Credits Added" else "Credits", color = Color.Black) 
            }

            Button(
                onClick = {
                    if (hasCredits) {
                        navController.navigate("mobile_auction_detail/$index")
                    } else {
                        navController.navigate("credits/mobile/$index/${mobile.name}")
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
            ) { Text("Bid", color = Color.Black) }
        }
    }
}

