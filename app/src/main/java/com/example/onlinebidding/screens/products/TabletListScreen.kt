package com.example.onlinebidding.screens.products

import androidx.compose.foundation.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onlinebidding.R
import com.example.onlinebidding.model.Product
import com.example.onlinebidding.model.Seller
import androidx.navigation.NavHostController
import com.example.onlinebidding.screens.products.CreditsState
import com.example.onlinebidding.api.RetrofitInstance
import kotlinx.coroutines.launch

/* ---------------- SAMPLE DATA ---------------- */
private val fallbackTablets = listOf(
    Product(
        id = 1,
        name = "iPad Pro 12.9\" M2",
        imageRes = R.drawable.ic_ipadtablet,
        currentBid = 85000,
        seller = Seller("Apple Store", verified = true, rating = 4.9),
        specs = mapOf("Display" to "12.9\" Retina XDR", "Storage" to "512GB")
    ),
    Product(
        id = 2,
        name = "Samsung Galaxy Tab S9",
        imageRes = R.drawable.ic_samsungtablet,
        currentBid = 72000,
        seller = Seller("Samsung Official", verified = true, rating = 4.8),
        specs = mapOf("Processor" to "Snapdragon 8 Gen 2", "Storage" to "256GB")
    )
)

// Extension function to map API response to Product
private fun com.example.onlinebidding.api.AuctionListItem.toTabletProduct(): Product {
    val name = this.name ?: this.product.title
    val specs = this.specs ?: this.product.specs ?: this.product.condition ?: "Premium Device"
    val rating = this.rating ?: 4.5
    val price = this.auction.current_price.toInt()
    
    val imageRes = when {
        name.contains("iPad", ignoreCase = true) -> R.drawable.ic_ipadtablet
        name.contains("Samsung", ignoreCase = true) -> R.drawable.ic_samsungtablet
        name.contains("Surface", ignoreCase = true) -> R.drawable.ic_surfacetablet
        else -> R.drawable.ic_ipadtablet
    }
    
    val specsMap = specs.split(" · ").associate { part ->
        val keyValue = part.split(":")
        if (keyValue.size == 2) {
            keyValue[0].trim() to keyValue[1].trim()
        } else {
            "Spec" to part.trim()
        }
    }
    
    return Product(
        id = this.product.id,
        name = name,
        imageRes = imageRes,
        currentBid = price,
        seller = Seller("Official Store", verified = true, rating = rating),
        specs = specsMap
    )
}

/* ---------------- SCREEN ---------------- */
@Composable
fun TabletListScreen(
    navController: NavHostController? = null,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var tablets by remember { mutableStateOf<List<Product>>(fallbackTablets) }
    var isLoading by remember { mutableStateOf(false) }

    // Fetch tablets from backend - refresh when screen is displayed
    LaunchedEffect(navController?.currentBackStackEntry?.id) {
        isLoading = true
        scope.launch {
            try {
                android.util.Log.d("TabletListScreen", "🔌 Attempting to connect to backend API...")
                val response = RetrofitInstance.api.listAuctions(category = "tablet")
                android.util.Log.d("TabletListScreen", "📡 API Response Code: ${response.code()}")
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val items = response.body()?.items ?: emptyList()
                    if (items.isNotEmpty()) {
                        tablets = items.map { it.toTabletProduct() }
                        android.util.Log.d("TabletListScreen", "✅ Backend Connected! Received ${items.size} tablets from API")
                    } else {
                        // API connected but returned empty list - use fallback data
                        tablets = fallbackTablets
                        android.util.Log.w("TabletListScreen", "⚠️ API connected but returned empty list - using fallback data")
                    }
                } else {
                    // Use fallback data on API error
                    tablets = fallbackTablets
                    android.util.Log.w("TabletListScreen", "⚠️ API Error: ${response.code()} - Using fallback data")
                }
            } catch (e: Exception) {
                // Use fallback data on network error
                android.util.Log.e("TabletListScreen", "❌ Network Error: ${e.message}", e)
                tablets = fallbackTablets
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
                    listOf(Color(0xFF0B0B0B), Color.Black)
                )
            )
    ) {

        /* ---------- HEADER ---------- */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color(0xFFFFC107)
                )
            }

            Text(
                text = "Premium Tablets",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = tablets.size.toString(),
                color = Color(0xFFFFC107),
                fontWeight = FontWeight.Bold
            )
        }

        /* ---------- SEARCH BAR ---------- */
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = {
                Text("Search tablets…", color = Color.Gray)
            },
            leadingIcon = {
                Icon(Icons.Default.Search, null, tint = Color(0xFFFFC107))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFFC107),
                unfocusedBorderColor = Color(0xFF2C2C2C),
                focusedContainerColor = Color(0xFF111111),
                unfocusedContainerColor = Color(0xFF111111),
                cursorColor = Color(0xFFFFC107)
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        /* ---------- LIST ---------- */
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFFFC107))
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                items(tablets.size) { index ->
                    TabletCard(
                        product = tablets[index],
                        navController = navController,
                        index = index
                    )
                }
            }
        }
    }
}

/* ---------------- CARD ---------------- */
@Composable
private fun TabletCard(
    product: Product,
    navController: NavHostController?,
    index: Int
) {
    val itemId = "tablet_$index"
    val hasCredits = CreditsState.hasCreditsForItem(itemId)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1A1A1A), Color(0xFF0E0E0E))
                )
            )
            .border(
                1.dp,
                Color(0xFF3A2A00),
                RoundedCornerShape(24.dp)
            )
            .padding(18.dp)
    ) {

        /* ---------- TOP ROW ---------- */
        Row(verticalAlignment = Alignment.CenterVertically) {

            Image(
                painter = painterResource(product.imageRes),
                contentDescription = product.name,
                modifier = Modifier
                    .size(78.dp)
                    .clip(RoundedCornerShape(18.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        product.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    PremiumViewChip(onClick = {
                        navController?.navigate("tablet_details/$index")
                    })
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    product.specs?.values?.joinToString(" · ") ?: "",
                    color = Color(0xFFB0B0B0),
                    fontSize = 12.sp,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        product.seller.rating.toString(),
                        color = Color(0xFFFFC107),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "₹${product.currentBid}",
                        color = Color(0xFFFFC107),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        /* ---------- ACTIONS ---------- */
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            GradientButton(
                text = if (hasCredits) "Credits Added" else "Credits",
                colors = listOf(Color(0xFF00E676), Color(0xFF00C853)),
                onClick = {
                    navController?.navigate("credits/tablet/$index/${product.name}")
                },
                modifier = Modifier.weight(1f)
            )
            GradientButton(
                text = "Bid Now",
                colors = listOf(Color(0xFFFFD54F), Color(0xFFFFA000)),
                onClick = {
                    if (hasCredits) {
                        navController?.navigate("tablet_auction_detail/$index/${product.name}")
                    } else {
                        navController?.navigate("credits/tablet/$index/${product.name}")
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/* ---------------- COMPONENTS ---------------- */
