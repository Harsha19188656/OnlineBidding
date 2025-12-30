package com.example.onlinebidding.screens.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
data class MonitorProduct(
    val name: String,
    val specs: String,
    val rating: Double,
    val price: String,
    val image: Int,
    val productId: Int? = null
)

private val fallbackMonitors = listOf(
    MonitorProduct("Samsung Odyssey G9 49\"", "Premium Device", 4.8, "₹95,000", R.drawable.ic_monitor_samsung, productId = null),
    MonitorProduct("LG UltraFine 5K 27\"", "Premium Device", 4.9, "₹68,000", R.drawable.ic_monitor_lg, productId = null),
    MonitorProduct("Dell UltraSharp U3423WE", "Premium Device", 4.7, "₹52,000", R.drawable.ic_monitor_dell, productId = null)
)

// Extension function to map API response to MonitorProduct
private fun com.example.onlinebidding.api.AuctionListItem.toMonitorProduct(): MonitorProduct {
    val name = this.name ?: this.product.title
    val specs = this.specs ?: this.product.specs ?: this.product.condition ?: "Premium Device"
    val rating = this.rating ?: 4.5
    val price = this.price ?: "₹${String.format("%.0f", this.auction.current_price)}"
    
    val image = when {
        name.contains("Samsung", ignoreCase = true) -> R.drawable.ic_monitor_samsung
        name.contains("LG", ignoreCase = true) -> R.drawable.ic_monitor_lg
        name.contains("Dell", ignoreCase = true) -> R.drawable.ic_monitor_dell
        else -> R.drawable.ic_monitor_samsung
    }
    
    return MonitorProduct(
        name = name,
        specs = specs,
        rating = rating,
        price = price,
        image = image,
        productId = this.product.id
    )
}

/* ---------------- SCREEN ---------------- */
@Composable
fun AdminMonitorList(
    navController: NavHostController,
    onBack: () -> Unit = {},
    userToken: String?
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var monitors by remember { mutableStateOf<List<MonitorProduct>>(fallbackMonitors) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isBackendConnected by remember { mutableStateOf(false) }
    var backendDataCount by remember { mutableStateOf(0) }

    fun loadMonitors() {
        isLoading = true
        scope.launch {
            try {
                android.util.Log.d("AdminMonitorList", "🔌 Attempting to connect to backend API...")
                val response = RetrofitInstance.api.listAuctions(category = "monitor")
                android.util.Log.d("AdminMonitorList", "📡 API Response Code: ${response.code()}")
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val items = response.body()?.items ?: emptyList()
                    backendDataCount = items.size
                    if (items.isNotEmpty()) {
                        monitors = items.map { it.toMonitorProduct() }
                        isBackendConnected = true
                        errorMessage = null
                        android.util.Log.d("AdminMonitorList", "✅ Backend Connected! Received ${items.size} monitors from API")
                    } else {
                        monitors = fallbackMonitors
                        isBackendConnected = false
                        errorMessage = "No monitors in database"
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.w("AdminMonitorList", "⚠️ API Error: ${response.code()} - ${response.message()}. Error body: $errorBody")
                    monitors = fallbackMonitors
                    isBackendConnected = false
                    errorMessage = "Using offline data (API error)"
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Unknown error"
                android.util.Log.e("AdminMonitorList", "❌ Network Error: $errorMsg", e)
                monitors = fallbackMonitors
                isBackendConnected = false
                errorMessage = "Network error: $errorMsg"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadMonitors()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color.Black, Color(0xFF0B0B0B))
                )
            )
            .padding(16.dp)
    ) {

        /* ---------- HEADER ---------- */
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFFFFC107),
                modifier = Modifier
                    .size(26.dp)
                    .clickable { onBack() }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Monitors",
                color = Color(0xFFFFC107),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            // Admin Add button
            if (userToken != null) {
                IconButton(onClick = {
                    navController.navigate("admin_product_form/monitor")
                }) {
                    Icon(Icons.Default.Add, "Add Monitor", tint = Color(0xFFFFC107))
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(monitors.size.toString(), color = Color(0xFFFFC107))
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
                    text = "✅ Connected to backend - $backendDataCount monitors loaded",
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

        Spacer(modifier = Modifier.height(14.dp))

        /* ---------- SEARCH (UI ONLY) ---------- */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(
                    1.dp,
                    Color(0xFF3A2A00),
                    RoundedCornerShape(14.dp)
                )
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, null, tint = Color(0xFFFFC107))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Search monitors...", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(18.dp))

        /* ---------- LIST ---------- */
        if (!isLoading && monitors.isNotEmpty()) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                items(monitors.size) { index ->
                    AdminMonitorCard(
                        monitor = monitors[index],
                        navController = navController,
                        index = index,
                        userToken = userToken,
                        onDelete = { monitorIndex, productId ->
                            if (productId != null) {
                                scope.launch {
                                    try {
                                        val response = RetrofitInstance.api.listAuctions(category = "monitor")
                                        if (response.isSuccessful && response.body()?.success == true) {
                                            val items = response.body()?.items ?: emptyList()
                                            monitors = items.map { it.toMonitorProduct() }
                                            backendDataCount = items.size
                                        }
                                    } catch (e: Exception) {
                                        // Error reloading, ignore
                                    }
                                }
                            } else {
                                monitors = monitors.filterIndexed { i, _ -> i != monitorIndex }
                            }
                        }
                    )
                }
            }
        } else if (!isLoading && monitors.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No monitors found",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        }
    }
}

/* ---------------- CARD ---------------- */
@Composable
fun AdminMonitorCard(
    monitor: MonitorProduct,
    navController: NavHostController,
    index: Int,
    userToken: String?,
    onDelete: (Int, Int?) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val itemId = "monitor_$index"
    val hasCredits = com.example.onlinebidding.screens.products.CreditsState.hasCreditsForItem(itemId)
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1A1A1A), Color(0xFF0D0D0D))
                ),
                RoundedCornerShape(22.dp)
            )
            .border(
                1.dp,
                Color(0xFF3A2A00),
                RoundedCornerShape(22.dp)
            )
            .padding(16.dp)
    ) {

        /* ---------- TOP ---------- */
        Row(verticalAlignment = Alignment.CenterVertically) {

            Image(
                painter = painterResource(id = monitor.image),
                contentDescription = monitor.name,
                modifier = Modifier
                    .size(78.dp)
                    .clip(RoundedCornerShape(14.dp))
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = monitor.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Admin Delete button
                        if (userToken != null) {
                            IconButton(onClick = {
                                if (monitor.productId != null) {
                                    scope.launch {
                                        try {
                                            android.util.Log.d("AdminMonitorList", "🗑️ Deleting monitor - productId: ${monitor.productId}")
                                            val response = RetrofitInstance.api.adminDeleteProduct(
                                                token = "Bearer $userToken",
                                                request = AdminDeleteProductRequest(product_id = monitor.productId)
                                            )
                                            
                                            if (response.isSuccessful) {
                                                val body = response.body()
                                                if (body?.success == true) {
                                                    android.util.Log.d("AdminMonitorList", "✅ Delete successful")
                                                    Toast.makeText(context, "Monitor deleted successfully", Toast.LENGTH_SHORT).show()
                                                    onDelete(index, monitor.productId)
                                                } else {
                                                    val errorMsg = body?.error ?: "Delete failed: ${response.code()}"
                                                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                                }
                                            } else {
                                                val errorMsg = when (response.code()) {
                                                    500 -> "Server error. Check backend logs"
                                                    401 -> "Authentication failed. Please login again"
                                                    404 -> "Delete endpoint not found"
                                                    else -> "Delete failed: ${response.code()}"
                                                }
                                                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                            }
                                        } catch (e: Exception) {
                                            android.util.Log.e("AdminMonitorList", "❌ Delete exception: ${e.message}", e)
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Monitor removed from list", Toast.LENGTH_SHORT).show()
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
                            text = "View",
                            color = Color(0xFFFFC107),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                navController.navigate("monitor_details/$index")
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = monitor.specs,
                    color = Color(0xFFB0B0B0),
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = monitor.rating.toString(),
                        color = Color(0xFFFFC107),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = monitor.price,
                        color = Color(0xFFFFC107),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        /* ---------- ACTIONS ---------- */
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Button(
                onClick = {
                    navController.navigate("credits/monitor/$index/${monitor.name}")
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    if (hasCredits) "Credits Added" else "Credits",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    if (hasCredits) {
                        navController.navigate("monitor_auction_detail/$index")
                    } else {
                        navController.navigate("credits/monitor/$index/${monitor.name}")
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Bid", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
