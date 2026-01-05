package com.example.onlinebidding.screens.admin

import androidx.compose.foundation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
data class ComputerItem(
    val name: String,
    val description: String,
    val rating: Double,
    val price: String,
    val image: Int,
    val productId: Int? = null
)

private val fallbackComputers = listOf(
    ComputerItem(
        "Custom Gaming PC RTX",
        "64GB DDR5 · 2TB Gen5 NVMe + 4TB HDD",
        4.9,
        "₹2,85,000",
        R.drawable.ic_pcgamming,
        productId = null
    ),
    ComputerItem(
        "Mac Studio M2 Ultra",
        "192GB Unified Memory · 4TB SSD",
        5.0,
        "₹3,10,000",
        R.drawable.ic_macstudio,
        productId = null
    ),
    ComputerItem(
        "HP Z8 G5 Workstation",
        "128GB ECC DDR5 · 4TB NVMe RAID",
        4.7,
        "₹1,95,000",
        R.drawable.ic_hp_z5,
        productId = null
    )
)

// Extension function to map API response to ComputerItem
private fun com.example.onlinebidding.api.AuctionListItem.toComputerItem(): ComputerItem {
    val name = this.name ?: this.product.title
    val description = this.specs ?: this.product.specs ?: this.product.condition ?: "Premium Device"
    val rating = this.rating ?: 4.5
    val price = this.price ?: "₹${String.format("%.0f", this.auction.current_price)}"
    
    val image = when {
        name.contains("Gaming", ignoreCase = true) || name.contains("RTX", ignoreCase = true) -> R.drawable.ic_pcgamming
        name.contains("Mac", ignoreCase = true) || name.contains("Studio", ignoreCase = true) -> R.drawable.ic_macstudio
        name.contains("HP", ignoreCase = true) || name.contains("Workstation", ignoreCase = true) -> R.drawable.ic_hp_z5
        else -> R.drawable.ic_pcgamming
    }
    
    return ComputerItem(
        name = name,
        description = description,
        rating = rating,
        price = price,
        image = image,
        productId = this.product.id
    )
}

/* ---------------- SCREEN ---------------- */
@Composable
fun AdminComputerList(
    navController: NavHostController,
    onBack: () -> Unit = {},
    userToken: String?
) {
    val scope = rememberCoroutineScope()
    var computers by remember { mutableStateOf<List<ComputerItem>>(fallbackComputers) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isBackendConnected by remember { mutableStateOf(false) }
    var backendDataCount by remember { mutableStateOf(0) }

    fun loadComputers() {
        isLoading = true
        scope.launch {
            try {
                android.util.Log.d("AdminComputerList", "🔌 Attempting to connect to backend API...")
                val response = RetrofitInstance.api.listAuctions(category = "computer")
                android.util.Log.d("AdminComputerList", "📡 API Response Code: ${response.code()}")
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val items = response.body()?.items ?: emptyList()
                    backendDataCount = items.size
                    if (items.isNotEmpty()) {
                        computers = items.map { it.toComputerItem() }
                        isBackendConnected = true
                        errorMessage = null
                        android.util.Log.d("AdminComputerList", "✅ Backend Connected! Received ${items.size} computers from API")
                    } else {
                        computers = fallbackComputers
                        isBackendConnected = false
                        errorMessage = "No computers in database"
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.w("AdminComputerList", "⚠️ API Error: ${response.code()} - ${response.message()}. Error body: $errorBody")
                    computers = fallbackComputers
                    isBackendConnected = false
                    errorMessage = "Using offline data (API error)"
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Unknown error"
                android.util.Log.e("AdminComputerList", "❌ Network Error: $errorMsg", e)
                computers = fallbackComputers
                isBackendConnected = false
                errorMessage = "Network error: $errorMsg"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadComputers()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0C0C0C), Color.Black)
                )
            )
    ) {

        /* ---------- TOP BAR ---------- */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFFFFC107),
                modifier = Modifier
                    .size(26.dp)
                    .clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = "Premium Computers",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f)
            )
            
            // Admin Add button
            if (userToken != null) {
                IconButton(onClick = {
                    navController.navigate("admin_product_form/computer")
                }) {
                    Icon(Icons.Default.Add, "Add Computer", tint = Color(0xFFFFC107))
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(computers.size.toString(), color = Color(0xFFFFC107))
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
                    .padding(horizontal = 18.dp, vertical = 8.dp),
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
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x334CAF50)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4CAF50))
            ) {
                Text(
                    text = "✅ Connected to backend - $backendDataCount computers loaded",
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
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = {
                Text("Search high-end systems…", color = Color.Gray)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFF2C2C2C),
                focusedBorderColor = Color(0xFFFFC107),
                unfocusedContainerColor = Color(0xFF111111),
                focusedContainerColor = Color(0xFF111111),
                cursorColor = Color(0xFFFFC107)
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        /* ---------- LIST ---------- */
        if (!isLoading && computers.isNotEmpty()) {
            LazyColumn(
                contentPadding = PaddingValues(18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                items(computers.size) { index ->
                    AdminComputerCard(
                        item = computers[index],
                        navController = navController,
                        index = index,
                        userToken = userToken,
                        onDelete = { computerIndex, productId ->
                            if (productId != null) {
                                scope.launch {
                                    try {
                                        val response = RetrofitInstance.api.listAuctions(category = "computer")
                                        if (response.isSuccessful && response.body()?.success == true) {
                                            val items = response.body()?.items ?: emptyList()
                                            computers = items.map { it.toComputerItem() }
                                            backendDataCount = items.size
                                        }
                                    } catch (e: Exception) {
                                        // Error reloading, ignore
                                    }
                                }
                            } else {
                                computers = computers.filterIndexed { i, _ -> i != computerIndex }
                            }
                        }
                    )
                }
            }
        } else if (!isLoading && computers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No computers found",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        }
    }
}

/* ---------------- CARD ---------------- */
@Composable
fun AdminComputerCard(
    item: ComputerItem,
    navController: NavHostController,
    index: Int,
    userToken: String?,
    onDelete: (Int, Int?) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val itemId = "computer_$index"
    val hasCredits = com.example.onlinebidding.screens.products.CreditsState.hasCreditsForItem(itemId)
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1A1A1A), Color(0xFF0E0E0E))
                )
            )
            .border(
                1.dp,
                Color(0xFF3A2A00),
                RoundedCornerShape(22.dp)
            )
            .padding(18.dp)
    ) {

        /* ---------- TOP ROW ---------- */
        Row(verticalAlignment = Alignment.CenterVertically) {

            Image(
                painter = painterResource(id = item.image),
                contentDescription = item.name,
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Admin Delete button
                if (userToken != null) {
                    IconButton(onClick = {
                        if (item.productId != null) {
                            scope.launch {
                                try {
                                    android.util.Log.d("AdminComputerList", "🗑️ Deleting computer - productId: ${item.productId}")
                                    val response = RetrofitInstance.api.adminDeleteProduct(
                                        token = "Bearer $userToken",
                                        request = AdminDeleteProductRequest(product_id = item.productId)
                                    )
                                    
                                    if (response.isSuccessful) {
                                        val body = response.body()
                                        if (body?.success == true) {
                                            android.util.Log.d("AdminComputerList", "✅ Delete successful")
                                            Toast.makeText(context, "Computer deleted successfully", Toast.LENGTH_SHORT).show()
                                            onDelete(index, item.productId)
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
                                    android.util.Log.e("AdminComputerList", "❌ Delete exception: ${e.message}", e)
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Computer removed from list", Toast.LENGTH_SHORT).show()
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
                com.example.onlinebidding.screens.products.PremiumViewChip {
                    navController.navigate("computer_details/$index")
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        /* ---------- PRICE & RATING ---------- */
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${item.rating}",
                    color = Color(0xFFFFC107),
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = item.price,
                color = Color(0xFFFFC107),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        /* ---------- ACTIONS ---------- */
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            com.example.onlinebidding.screens.products.GradientButton(
                text = if (hasCredits) "Credits Added" else "Credits",
                colors = listOf(Color(0xFF00E676), Color(0xFF00C853)),
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("credits/computer/$index/${item.name}")
                }
            )
            Spacer(modifier = Modifier.width(14.dp))
            com.example.onlinebidding.screens.products.GradientButton(
                text = "Bid Now",
                colors = listOf(Color(0xFFFFD54F), Color(0xFFFFA000)),
                modifier = Modifier.weight(1f),
                onClick = {
                    if (hasCredits) {
                        navController.navigate("computer_auction_detail/$index/${item.name}")
                    } else {
                        navController.navigate("credits/computer/$index/${item.name}")
                    }
                }
            )
        }
    }
}

