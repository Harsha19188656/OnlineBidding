package com.example.onlinebidding.screens.products

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.example.onlinebidding.R
import com.example.onlinebidding.screens.products.SpecificationsDialog
import com.example.onlinebidding.api.RetrofitInstance
import kotlinx.coroutines.launch
import android.widget.Toast

/* ---------------- DATA ---------------- */

data class MonitorAuctionData(
    val name: String,
    val subtitle: String,
    val condition: String,
    val sellerName: String,
    val sellerRating: Double,
    val sales: Int,
    val imageRes: Int,
    val processor: String,
    val graphics: String,
    val memory: String,
    val storage: String,
    val display: String = "", // Will use name or extract from specs
    val ram: String = "N/A", // Monitors don't have RAM
    val currentBid: String,
    val maxPrice: String,
    val activeBidders: Int,
    val totalBids: Int,
    val conditionDetails: String,
    val latestBidder: String,
    val latestBidAmount: String,
    val alertMessage: String,
    val timeRemaining: String = "0:30"
)

private val monitorAuctions = listOf(
    // Logitech Mouse (Index 0 - used when product name contains 'Logitech' or 'mouse')
    MonitorAuctionData(
        name = "Logitech mouse",
        subtitle = "Premium Wireless Mouse",
        condition = "Excellent",
        sellerName = "DisplayMasters",
        sellerRating = 4.5,
        sales = 120,
        imageRes = R.drawable.ic_mouse,
        processor = "DPI up to 16000",
        graphics = "Precision Optical Sensor",
        memory = "On-board Profiles",
        storage = "Wireless Receiver",
        currentBid = "₹4,000",
        maxPrice = "₹6,000",
        activeBidders = 10,
        totalBids = 5,
        conditionDetails = "Wireless, 6 buttons, Black. Smooth scroll and ergonomic design.",
        latestBidder = "Anita Verma",
        latestBidAmount = "₹4,000",
        alertMessage = "Bid now to grab this premium mouse!"
    ),
    // Samsung Odyssey G9 49" (Index 1)
    MonitorAuctionData(
        name = "Samsung Odyssey G9 49\"",
        subtitle = "Desktop & Workstations",
        condition = "Excellent",
        sellerName = "DisplayMasters",
        sellerRating = 4.8,
        sales = 178,
        imageRes = R.drawable.ic_monitor_samsung,
        processor = "49\" QHD",
        graphics = "240Hz",
        memory = "VA Panel",
        storage = "Curved",
        currentBid = "₹95,000",
        maxPrice = "₹1,35,000",
        activeBidders = 22,
        totalBids = 8,
        conditionDetails = "Ultra-wide gaming monitor. Perfect condition, no dead pixels.",
        latestBidder = "Rahul Singh",
        latestBidAmount = "₹95,000",
        alertMessage = "Secure this powerful system now!"
    ),
    // LG UltraFine 5K 27" (Index 2)
    MonitorAuctionData(
        name = "LG UltraFine 5K 27\"",
        subtitle = "Desktop & Workstations",
        condition = "Very Good",
        sellerName = "ProDisplay Hub",
        sellerRating = 4.9,
        sales = 234,
        imageRes = R.drawable.ic_monitor_lg,
        processor = "27\" 5K",
        graphics = "60Hz",
        memory = "IPS Panel",
        storage = "USB-C Hub",
        currentBid = "₹68,000",
        maxPrice = "₹95,000",
        activeBidders = 18,
        totalBids = 6,
        conditionDetails = "Professional display. Perfect for Mac users and creative work.",
        latestBidder = "Meera Patel",
        latestBidAmount = "₹68,000",
        alertMessage = "Secure this powerful system now!"
    ),
    // Dell UltraSharp U3423WE (Index 3)
    MonitorAuctionData(
        name = "Dell UltraSharp U3423WE",
        subtitle = "Desktop & Workstations",
        condition = "Excellent",
        sellerName = "OfficeGear Pro",
        sellerRating = 4.7,
        sales = 145,
        imageRes = R.drawable.ic_monitor_dell,
        processor = "34\" Ultrawide",
        graphics = "60Hz",
        memory = "IPS Panel",
        storage = "USB-C 90W",
        currentBid = "₹52,000",
        maxPrice = "₹75,000",
        activeBidders = 14,
        totalBids = 5,
        conditionDetails = "Business ultrawide monitor. USB-C hub with 90W charging.",
        latestBidder = "Suresh Kumar",
        latestBidAmount = "₹52,000",
        alertMessage = "Secure this powerful system now!"
    )
)

/* ---------------- SCREEN ---------------- */

@Composable
fun MonitorAuctionDetailScreen(
    auctionId: Int? = null,
    monitorIndex: Int = 0,
    monitorName: String = "",
    onBack: () -> Unit = {},
    onSpecsClick: () -> Unit = {},
    onBidClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var monitor by remember { mutableStateOf<MonitorAuctionData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showBidDialog by remember { mutableStateOf(false) }
    var showSpecsDialog by remember { mutableStateOf(false) }
    var currentAuctionId by remember { mutableStateOf<Int?>(auctionId) }
    val scrollState = rememberScrollState()
    
    fun loadAuctionDetails(id: Int) {
        isLoading = true
        scope.launch {
            try {
                val response = RetrofitInstance.api.auctionDetails(id)
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()!!
                    val product = data.product
                    val auction = data.auction
                    if (product != null && auction != null) {
                        // Determine image based on product name
                        val imageRes = when {
                            product.title.contains("Logitech", ignoreCase = true) || product.title.contains("mouse", ignoreCase = true) -> R.drawable.ic_mouse
                            product.title.contains("Samsung", ignoreCase = true) -> R.drawable.ic_monitor_samsung
                            product.title.contains("LG", ignoreCase = true) -> R.drawable.ic_monitor_lg
                            product.title.contains("Dell", ignoreCase = true) -> R.drawable.ic_monitor_dell
                            else -> R.drawable.ic_monitor_samsung // Default
                        }
                        
                        monitor = MonitorAuctionData(
                            name = product.title,
                            subtitle = product.category ?: "Desktop & Workstations",
                            condition = product.condition ?: "Excellent",
                            sellerName = "Monitor Store",
                            sellerRating = 4.8,
                            sales = 200,
                            imageRes = imageRes,
                            processor = "High Resolution",
                            graphics = "High Refresh Rate",
                            memory = "IPS Panel",
                            storage = "USB-C Hub",
                            currentBid = "₹${String.format("%.0f", auction.current_price)}",
                            maxPrice = "₹${String.format("%.0f", product.base_price)}",
                            activeBidders = data.bids.distinctBy { it.user_id }.size,
                            totalBids = data.bids.size,
                            conditionDetails = product.description ?: "Premium monitor display",
                            latestBidder = data.bids.firstOrNull()?.user_name ?: "User",
                            latestBidAmount = if (data.bids.isNotEmpty()) "₹${String.format("%.0f", data.bids[0].amount)}" else "₹0",
                            alertMessage = "Secure this powerful system now!"
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("MonitorAuction", "Error: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
    
    LaunchedEffect(auctionId, monitorIndex, monitorName) {
        if (auctionId != null && auctionId > 0) {
            currentAuctionId = auctionId
            loadAuctionDetails(auctionId)
        } else {
            // Try to match by name first, then fall back to index
            monitor = if (monitorName.isNotBlank()) {
                val normalizedSearchName = monitorName.trim().lowercase()
                monitorAuctions.find { auction ->
                    val normalizedAuctionName = auction.name.trim().lowercase()
                    normalizedAuctionName.contains(normalizedSearchName) || 
                    normalizedSearchName.contains(normalizedAuctionName) ||
                    (normalizedAuctionName.contains("samsung") && normalizedSearchName.contains("samsung")) ||
                    (normalizedAuctionName.contains("lg") && normalizedSearchName.contains("lg")) ||
                    (normalizedAuctionName.contains("dell") && normalizedSearchName.contains("dell")) ||
                    (normalizedAuctionName.contains("mouse") && normalizedSearchName.contains("mouse")) ||
                    (normalizedAuctionName.contains("logitech") && normalizedSearchName.contains("logitech"))
                } ?: if (monitorIndex < monitorAuctions.size) monitorAuctions[monitorIndex] else monitorAuctions[0]
            } else {
                if (monitorIndex < monitorAuctions.size) monitorAuctions[monitorIndex] else monitorAuctions[0]
            }
            isLoading = false
        }
    }
    
    if (isLoading || monitor == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFFFFC107))
        }
        return
    }
    
    val currentMonitor = monitor!!
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 90.dp)
        ) {
            /* ---------- HEADER ---------- */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFFFFC107)
                    )
                }
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(
                        text = "Monitor Auction",
                        color = Color(0xFFFFC107),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentMonitor.subtitle,
                        color = Color(0x99FFC107),
                        fontSize = 12.sp
                    )
                }
            }

            /* ---------- PRODUCT IMAGE SECTION ---------- */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF7F00FF), Color(0xFF1565C0))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(currentMonitor.imageRes),
                        contentDescription = currentMonitor.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.FillBounds
                    )
                }

                // Verified Seller Badge
                Card(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFC107)),
                    shape = RoundedCornerShape(50)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Verified Seller",
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            /* ---------- PRODUCT INFO ---------- */
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = currentMonitor.name,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFC107)),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            currentMonitor.condition,
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "by ${currentMonitor.sellerName}",
                        color = Color(0xFFFFECB3),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${currentMonitor.sellerRating} (${currentMonitor.sales} sales)",
                        color = Color(0xFFFFC107),
                        fontSize = 12.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))

            /* ---------- SPECIFICATIONS CARDS (2x2 Grid) ---------- */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    SpecCard("Processor", currentMonitor.processor, Color(0xFFFFC107), Icons.Default.Info)
                }
                Box(modifier = Modifier.weight(1f)) {
                    SpecCard("Storage", currentMonitor.storage, Color(0xFFFFC107), Icons.Default.Info)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    SpecCard("Display", currentMonitor.graphics, Color(0xFF2196F3), Icons.Default.Info)
                }
                Box(modifier = Modifier.weight(1f)) {
                    SpecCard("RAM", currentMonitor.memory, Color(0xFF4CAF50), Icons.Default.Info)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            /* ---------- AUCTION TIMER ---------- */
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2B0F0F)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            "Auction Ends In",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        currentMonitor.timeRemaining,
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Bid fast to secure this monitor!",
                        color = Color(0x99FFFFFF),
                        fontSize = 12.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))

            /* ---------- BID INFO CARDS ---------- */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    BidInfoCard("Current Bid", currentMonitor.currentBid, "Highest offer", true)
                }
                Box(modifier = Modifier.weight(1f)) {
                    BidInfoCard("Max Price", currentMonitor.maxPrice, "Reserve limit", false)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            /* ---------- ACTIVE BIDDERS ---------- */
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "${currentMonitor.activeBidders} Active Bidders",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFC107)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "${currentMonitor.totalBids} bids",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(minOf(4, currentMonitor.activeBidders)) { index ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFFFFC107), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${index + 1}",
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (index < 3 && currentMonitor.activeBidders > 4) {
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                        }
                        if (currentMonitor.activeBidders > 4) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "+ ${currentMonitor.activeBidders - 4} more bidding",
                                color = Color(0x99FFFFFF),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            /* ---------- FULL SPECS & BID HISTORY CARDS ---------- */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    ActionCard(
                        "Full Specs", 
                        Icons.Default.Info, 
                        "View details",
                        onClick = { showSpecsDialog = true }
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    ActionCard(
                        "Bid History", 
                        Icons.Default.List, 
                        "Live updates", 
                        badge = currentMonitor.totalBids.toString(),
                        onClick = onBidClick
                    )
                }
            }
            
            // Specifications Dialog
            if (showSpecsDialog) {
                SpecificationsDialog(
                    laptopIndex = 0,
                    deviceType = "monitor",
                    deviceIndex = monitorIndex,
                    productName = currentMonitor.name,
                    onDismiss = { showSpecsDialog = false }
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))

            /* ---------- CONDITION DETAILS ---------- */
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Monitor Condition Details",
                            color = Color(0xFFFFC107),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        currentMonitor.conditionDetails,
                        color = Color(0xB3FFFFFF),
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LinearProgressIndicator(
                            progress = 0.95f,
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFFFFC107),
                            trackColor = Color(0x33FFFFFF)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "95% Quality",
                            color = Color(0xFFFFC107),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            /* ---------- LATEST BIDS ---------- */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Latest Bids",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Live",
                    color = Color(0x99FFFFFF),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Latest Bid Row
            LatestBidRow(
                BidInfo(
                    rank = 1,
                    name = currentMonitor.latestBidder,
                    amount = currentMonitor.latestBidAmount,
                    isTopBid = true
                )
            )

            TextButton(
                onClick = onBidClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    "View All ${currentMonitor.totalBids} Bids →",
                    color = Color(0xFFFFC107),
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
        
        /* ---------- PLACE BID BUTTON ---------- */
        Button(
            onClick = {
                if (currentAuctionId != null) {
                    showBidDialog = true
                } else {
                    onBidClick()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp)
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
            shape = RoundedCornerShape(50)
        ) {
            Text(
                "₹ Place Bid on Monitor",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        if (showBidDialog && currentAuctionId != null) {
            BidEntryDialog(
                currentBid = currentMonitor.currentBid.replace("₹", "").replace(",", "").toDoubleOrNull() ?: 0.0,
                onDismiss = { showBidDialog = false },
                onConfirm = { amount ->
                    scope.launch {
                        try {
                            val bidAmount = amount.replace("₹", "").replace(",", "").toDoubleOrNull() ?: 0.0
                            val response = RetrofitInstance.api.placeBid(
                                com.example.onlinebidding.api.PlaceBidRequest(
                                    auction_id = currentAuctionId!!,
                                    amount = bidAmount,
                                    user_id = 25
                                )
                            )
                            if (response.isSuccessful && response.body()?.success == true) {
                                Toast.makeText(context, "Bid placed successfully!", Toast.LENGTH_SHORT).show()
                                loadAuctionDetails(currentAuctionId!!)
                                showBidDialog = false
                            } else {
                                val error = response.body()?.error ?: "Failed to place bid"
                                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Network error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun BidEntryDialog(
    currentBid: Double,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var bidAmount by remember { mutableStateOf("") }
    val minBid = currentBid + 1000
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Place Your Bid", color = Color(0xFFFFC107), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close", tint = Color(0xFFFFC107))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Current Bid: ₹${String.format("%.0f", currentBid)}", color = Color(0x99FFFFFF), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Minimum Bid: ₹${String.format("%.0f", minBid)}", color = Color(0xFFFFC107), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(20.dp))
                Text("Bid Amount (₹)", color = Color(0xFFFFC107), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = bidAmount,
                    onValueChange = { bidAmount = it.filter { char -> char.isDigit() } },
                    placeholder = { Text("Enter amount", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFC107),
                        unfocusedBorderColor = Color(0xFF666666),
                        focusedContainerColor = Color(0xFF0F0F0F),
                        unfocusedContainerColor = Color(0xFF0F0F0F),
                        cursorColor = Color(0xFFFFC107),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    leadingIcon = { Text("₹", color = Color(0xFFFFC107), modifier = Modifier.padding(start = 12.dp)) }
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        if (bidAmount.isNotBlank()) {
                            val amount = bidAmount.toDoubleOrNull() ?: 0.0
                            if (amount >= minBid) {
                                onConfirm("₹$bidAmount")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text("Place Bid", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/* ---------------- COMPONENTS ---------------- */

@Composable
private fun SpecCard(title: String, value: String, iconColor: Color, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                title,
                color = Color(0x99FFFFFF),
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun BidInfoCard(title: String, amount: String, subtitle: String, isCurrent: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) Color(0xFF2A1F00) else Color(0xFF121212)
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                color = Color(0x99FFFFFF),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                amount,
                color = if (isCurrent) Color(0xFFFFC107) else Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                subtitle,
                color = Color(0x99FFFFFF),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun ActionCard(
    title: String, 
    icon: ImageVector, 
    subtitle: String, 
    badge: String? = null,
    onClick: () -> Unit = {}
) {
    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    subtitle,
                    color = Color(0x99FFFFFF),
                    fontSize = 11.sp
                )
            }
        }
        if (badge != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset((-6).dp, 6.dp)
                    .background(Color.Red, CircleShape)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    badge,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun LatestBidRow(bid: BidInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(
                            if (bid.isTopBid) Color(0xFFFFC107) else Color(0x33FFFFFF),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "#${bid.rank}",
                        color = if (bid.isTopBid) Color.Black else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    bid.name,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                if (bid.isTopBid) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFC107)),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            "TOP BID",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Text(
                bid.amount,
                color = Color(0xFFFFC107),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

