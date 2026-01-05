package com.example.onlinebidding.screens.products

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.onlinebidding.R
import com.example.onlinebidding.api.RetrofitInstance
import kotlinx.coroutines.launch

/* ---------------- DATA ---------------- */
data class ComputerItem(
    val name: String,
    val description: String,
    val rating: Double,
    val price: String,
    val image: Int
)

// Hardcoded fallback data (used if API fails)
val fallbackComputers = listOf(
    ComputerItem(
        "Custom Gaming PC RTX",
        "64GB DDR5 · 2TB Gen5 NVMe + 4TB HDD",
        4.9,
        "₹2,85,000",
        R.drawable.ic_pcgamming
    ),
    ComputerItem(
        "Mac Studio M2 Ultra",
        "192GB Unified Memory · 4TB SSD",
        5.0,
        "₹3,10,000",
        R.drawable.ic_macstudio
    ),
    ComputerItem(
        "HP Z8 G5 Workstation",
        "128GB ECC DDR5 · 4TB NVMe RAID",
        4.7,
        "₹1,95,000",
        R.drawable.ic_hp_z5
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
        image = image
    )
}

/* ---------------- SCREEN ---------------- */
@Composable
fun ComputerList(
    navController: NavHostController,
    onBack: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var computers by remember { mutableStateOf<List<ComputerItem>>(fallbackComputers) }
    var isLoading by remember { mutableStateOf(false) }

    // Fetch computers from backend - refresh when screen is displayed
    LaunchedEffect(navController.currentBackStackEntry?.id) {
        isLoading = true
        scope.launch {
            try {
                android.util.Log.d("ComputerList", "🔌 Attempting to connect to backend API...")
                val response = RetrofitInstance.api.listAuctions(category = "computer")
                android.util.Log.d("ComputerList", "📡 API Response Code: ${response.code()}")
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val items = response.body()?.items ?: emptyList()
                    if (items.isNotEmpty()) {
                        computers = items.map { it.toComputerItem() }
                        android.util.Log.d("ComputerList", "✅ Backend Connected! Received ${items.size} computers from API")
                    } else {
                        // API connected but returned empty list - use fallback data
                        computers = fallbackComputers
                        android.util.Log.w("ComputerList", "⚠️ API connected but returned empty list - using fallback data")
                    }
                } else {
                    // Use fallback data on API error
                    computers = fallbackComputers
                    android.util.Log.w("ComputerList", "⚠️ API Error: ${response.code()} - Using fallback data")
                }
            } catch (e: Exception) {
                // Use fallback data on network error
                android.util.Log.e("ComputerList", "❌ Network Error: ${e.message}", e)
                computers = fallbackComputers
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
                items(computers.size) { index ->
                    ComputerCard(
                        item = computers[index],
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
fun ComputerCard(
    item: ComputerItem,
    navController: NavHostController,
    index: Int
) {
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
                    .clip(RoundedCornerShape(16.dp))
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

            com.example.onlinebidding.screens.products.PremiumViewChip {
                navController.navigate("computer_details/$index")
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
            GradientButton(
                text = if (hasCredits) "Credits Added" else "Credits",
                colors = listOf(Color(0xFF00E676), Color(0xFF00C853)),
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("credits/computer/$index/${item.name}")
                }
            )
            Spacer(modifier = Modifier.width(14.dp))
            GradientButton(
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

/* ---------------- COMPONENTS ---------------- */
@Composable
fun PremiumViewChip(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFFFFD54F), Color(0xFFFFA000))
                )
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "VIEW",
            color = Color.Black,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun GradientButton(
    text: String,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .height(46.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.horizontalGradient(colors))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}