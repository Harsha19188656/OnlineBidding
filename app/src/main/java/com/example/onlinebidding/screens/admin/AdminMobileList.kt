package com.example.onlinebidding.screens.admin

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

data class Mobile(
    val name: String,
    val specs: String,
    val rating: Double,
    val price: String,
    val imageRes: Int,
    val productId: Int? = null
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
fun AdminMobileList(
    navController: NavHostController,
    onBack: () -> Unit,
    userToken: String?
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var mobiles by remember { mutableStateOf<List<Mobile>>(fallbackMobiles) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isBackendConnected by remember { mutableStateOf(false) }
    var backendDataCount by remember { mutableStateOf(0) }

    // Fetch mobiles from backend
    LaunchedEffect(Unit) {
        isLoading = true
        scope.launch {
            try {
                android.util.Log.d("AdminMobileList", "🔌 Attempting to connect to backend API...")
                val response = RetrofitInstance.api.listAuctions(category = "mobile")
                android.util.Log.d("AdminMobileList", "📡 API Response Code: ${response.code()}")
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val items = response.body()?.items ?: emptyList()
                    backendDataCount = items.size
                    mobiles = items.map { it.toMobile() }
                    isBackendConnected = true
                    errorMessage = null
                    android.util.Log.d("AdminMobileList", "✅ Backend Connected! Received ${items.size} mobiles from API")
                } else {
                    // Use fallback data on API error
                    mobiles = fallbackMobiles
                    isBackendConnected = false
                    errorMessage = "Using offline data (API error)"
                    android.util.Log.w("AdminMobileList", "⚠️ API Error: ${response.code()} - Using fallback data")
                }
            } catch (e: Exception) {
                mobiles = fallbackMobiles
                isBackendConnected = false
                errorMessage = "Network error: ${e.message}"
                android.util.Log.e("AdminMobileList", "❌ Network Error: ${e.message}", e)
                android.util.Log.e("AdminMobileList", "📱 Using fallback data - Backend NOT connected")
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

            // Admin Add button
            if (userToken != null) {
                IconButton(onClick = {
                    navController.navigate("admin_product_form/mobile")
                }) {
                    Icon(Icons.Default.Add, "Add Mobile", tint = Color(0xFFFFC107))
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
                    text = "⚠️ $errorMessage",
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
                    text = "✅ Connected to backend - $backendDataCount mobiles loaded",
                    color = Color(0xFF4CAF50),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(12.dp)
                )
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

        if (!isLoading && mobiles.isNotEmpty()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            items(mobiles.size) { index ->
                AdminMobileCard(
                    mobile = mobiles[index],
                    navController = navController,
                    index = index,
                    userToken = userToken,
                    onDelete = { mobileIndex, productId ->
                        // If productId exists, reload from backend; otherwise remove from local list
                        if (productId != null) {
                            // Reload list after backend deletion
                            scope.launch {
                                try {
                                    val response = RetrofitInstance.api.listAuctions(category = "mobile")
                                    if (response.isSuccessful && response.body()?.success == true) {
                                        val items = response.body()?.items ?: emptyList()
                                        mobiles = items.map { it.toMobile() }
                                        backendDataCount = items.size
                                    }
                                } catch (e: Exception) {
                                    // Error reloading, ignore
                                }
                            }
                        } else {
                            // Remove from local list for fallback data
                            mobiles = mobiles.filterIndexed { i, _ -> i != mobileIndex }
                        }
                    }
                )
                }
            }
        } else if (!isLoading && mobiles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No mobiles found",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        }
    }
}

/* ---------------- CARD ---------------- */

@Composable
private fun AdminMobileCard(
    mobile: Mobile,
    navController: NavHostController,
    index: Int,
    userToken: String?,
    onDelete: (Int, Int?) -> Unit
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
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Admin Delete button - show for all mobiles
                        if (userToken != null) {
                            IconButton(onClick = {
                                if (mobile.productId != null) {
                                    // Delete from backend
                                    scope.launch {
                                        try {
                                            android.util.Log.d("AdminMobileList", "🗑️ Deleting mobile - productId: ${mobile.productId}, token: ${userToken?.take(20)}...")
                                            val response = RetrofitInstance.api.adminDeleteProduct(
                                                token = "Bearer $userToken",
                                                request = AdminDeleteProductRequest(product_id = mobile.productId)
                                            )
                                            android.util.Log.d("AdminMobileList", "📡 Delete response code: ${response.code()}")
                                            
                                            if (response.isSuccessful) {
                                                val body = response.body()
                                                android.util.Log.d("AdminMobileList", "📦 Delete response body: $body")
                                                
                                                if (body?.success == true) {
                                                    android.util.Log.d("AdminMobileList", "✅ Delete successful")
                                                    Toast.makeText(context, "Mobile deleted successfully", Toast.LENGTH_SHORT).show()
                                                    onDelete(index, mobile.productId)
                                                } else {
                                                    val errorMsg = body?.error ?: "Delete failed: ${response.code()}"
                                                    android.util.Log.e("AdminMobileList", "❌ Delete failed: $errorMsg")
                                                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                                }
                                            } else {
                                                val errorBody = response.errorBody()?.string()
                                                android.util.Log.e("AdminMobileList", "❌ HTTP Error ${response.code()}: $errorBody")
                                                val errorMsg = when (response.code()) {
                                                    500 -> "Server error. Check backend logs (C:\\xampp\\apache\\logs\\error.log)"
                                                    401 -> "Authentication failed. Please login again"
                                                    404 -> "Delete endpoint not found"
                                                    else -> "Delete failed: ${response.code()}"
                                                }
                                                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                            }
                                        } catch (e: Exception) {
                                            android.util.Log.e("AdminMobileList", "❌ Delete exception: ${e.message}", e)
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    // Delete from local list (fallback data)
                                    Toast.makeText(context, "Mobile removed from list", Toast.LENGTH_SHORT).show()
                                    onDelete(index, null)
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
                                navController.navigate("mobile_details/$index")
                            }
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    mobile.specs,
                    color = Color(0xFFB0B0B0),
                    fontSize = 11.sp
                )

                Spacer(Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        mobile.rating.toString(),
                        color = Color(0xFFFFC107),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        mobile.price,
                        color = Color(0xFFFFC107),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    navController.navigate("credits/mobile/$index/${mobile.name}")
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
            ) {
                Text("Credits", color = Color.Black)
            }

            Button(
                onClick = {
                    navController.navigate("mobile_auction_detail/$index")
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
            ) {
                Text("Bid", color = Color.Black)
            }
        }
    }
}
