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
import androidx.compose.foundation.horizontalScroll
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

data class TabletAuctionData(
    val name: String,
    val subtitle: String,
    val condition: String,
    val sellerName: String,
    val sellerRating: Double,
    val sales: Int,
    val imageRes: Int,
    val display: String,
    val stylus: String,
    val storage: String,
    val connectivity: String,
    val processor: String = "M2 Chip", // Default processor
    val ram: String = "8GB", // Default RAM
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

private val tabletAuctions = listOf(
    // iPad Pro 12.9" M2 (Index 0)
    TabletAuctionData(
        name = "iPad Pro 12.9\" M2",
        subtitle = "Tablets & Accessories",
        condition = "Excellent",
        sellerName = "Apple Store Elite",
        sellerRating = 5.0,
        sales = 389,
        imageRes = R.drawable.ic_ipadtablet,
        display = "12.9\" Liquid Retina XDR",
        stylus = "Apple Pencil 2",
        storage = "512GB",
        connectivity = "WiFi + 5G",
        processor = "M2 Chip",
        ram = "8GB",
        currentBid = "₹85,000",
        maxPrice = "₹1,25,000",
        activeBidders = 26,
        totalBids = 11,
        conditionDetails = "Space Gray 512GB with Magic Keyboard and Apple Pencil 2.",
        latestBidder = "Anjali Sharma",
        latestBidAmount = "₹85,000",
        alertMessage = "Perfect for work & creativity!"
    ),
    // Samsung Galaxy Tab S9 (Index 1)
    TabletAuctionData(
        name = "Samsung Galaxy Tab S9 Ultra",
        subtitle = "Tablets & Accessories",
        condition = "Very Good",
        sellerName = "TabletZone",
        sellerRating = 4.8,
        sales = 267,
        imageRes = R.drawable.ic_samsungtablet,
        display = "14.6\" Dynamic AMOLED 2X",
        stylus = "S Pen Included",
        storage = "256GB",
        connectivity = "WiFi",
        processor = "Snapdragon 8 Gen 2",
        ram = "12GB",
        currentBid = "₹72,000",
        maxPrice = "₹1,05,000",
        activeBidders = 20,
        totalBids = 9,
        conditionDetails = "Graphite with S Pen. Book Cover Keyboard included.",
        latestBidder = "Vikram Reddy",
        latestBidAmount = "₹72,000",
        alertMessage = "Perfect for work & creativity!"
    ),
    // Microsoft Surface Pro 9 (Index 2)
    TabletAuctionData(
        name = "Microsoft Surface Pro 9",
        subtitle = "Tablets & Accessories",
        condition = "Excellent",
        sellerName = "Surface Store",
        sellerRating = 4.7,
        sales = 198,
        imageRes = R.drawable.ic_surfacetablet,
        display = "13\" PixelSense",
        stylus = "Surface Pen",
        storage = "512GB SSD",
        connectivity = "WiFi + LTE",
        processor = "Intel Core i7",
        ram = "16GB",
        currentBid = "₹58,000",
        maxPrice = "₹85,000",
        activeBidders = 16,
        totalBids = 7,
        conditionDetails = "Platinum with Surface Pen and Type Cover included.",
        latestBidder = "Neha Patel",
        latestBidAmount = "₹58,000",
        alertMessage = "Perfect for work & creativity!"
    )
)

/* ---------------- SCREEN ---------------- */

@Composable
fun TabletAuctionDetailScreen(
    auctionId: Int? = null,
    tabletIndex: Int = 0,
    tabletName: String = "",
    onBack: () -> Unit = {},
    onSpecsClick: () -> Unit = {},
    onBidClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var tablet by remember { mutableStateOf<TabletAuctionData?>(null) }
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
                            product.title.contains("iPad", ignoreCase = true) -> R.drawable.ic_ipadtablet
                            product.title.contains("Samsung", ignoreCase = true) -> R.drawable.ic_samsungtablet
                            product.title.contains("Surface", ignoreCase = true) -> R.drawable.ic_surfacetablet
                            else -> R.drawable.ic_ipadtablet // Default
                        }
                        
                        tablet = TabletAuctionData(
                            name = product.title,
                            subtitle = product.category ?: "Tablets & Accessories",
                            condition = product.condition ?: "Excellent",
                            sellerName = "Tablet Store",
                            sellerRating = 4.8,
                            sales = 200,
                            imageRes = imageRes,
                            display = "12.9\" Display",
                            stylus = "Stylus Included",
                            storage = "256GB",
                            connectivity = "WiFi + 5G",
                            currentBid = "₹${String.format("%.0f", auction.current_price)}",
                            maxPrice = "₹${String.format("%.0f", product.base_price)}",
                            activeBidders = data.bids.distinctBy { it.user_id }.size,
                            totalBids = data.bids.size,
                            conditionDetails = product.description ?: "Premium tablet device",
                            latestBidder = data.bids.firstOrNull()?.user_name ?: "User",
                            latestBidAmount = if (data.bids.isNotEmpty()) "₹${String.format("%.0f", data.bids[0].amount)}" else "₹0",
                            alertMessage = "Perfect for work & creativity!"
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("TabletAuction", "Error: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
    
    LaunchedEffect(auctionId) {
        if (auctionId != null && auctionId > 0) {
            currentAuctionId = auctionId
            loadAuctionDetails(auctionId)
        } else {
            tablet = if (tabletIndex < tabletAuctions.size) tabletAuctions[tabletIndex] else tabletAuctions[0]
            isLoading = false
        }
    }
    
    if (isLoading || tablet == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFFFFC107))
        }
        return
    }
    
    val currentTablet = tablet!!
    
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
                        text = "Tablet Auction",
                        color = Color(0xFFFFC107),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentTablet.subtitle,
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
                        painter = painterResource(currentTablet.imageRes),
                        contentDescription = currentTablet.name,
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
                    text = currentTablet.name,
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
                            currentTablet.condition,
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "by ${currentTablet.sellerName}",
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
                        "${currentTablet.sellerRating} (${currentTablet.sales} sales)",
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
                    SpecCard("Processor", currentTablet.processor, Color(0xFFFFC107), Icons.Default.Info)
                }
                Box(modifier = Modifier.weight(1f)) {
                    SpecCard("Storage", currentTablet.storage, Color(0xFFFFC107), Icons.Default.Info)
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
                    SpecCard("Display", currentTablet.display, Color(0xFF2196F3), Icons.Default.Info)
                }
                Box(modifier = Modifier.weight(1f)) {
                    SpecCard("RAM", currentTablet.ram, Color(0xFF4CAF50), Icons.Default.Info)
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
                        currentTablet.timeRemaining,
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Bid fast to secure this tablet!",
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
                    BidInfoCard("Current Bid", currentTablet.currentBid, "Highest offer", true)
                }
                Box(modifier = Modifier.weight(1f)) {
                    BidInfoCard("Max Price", currentTablet.maxPrice, "Reserve limit", false)
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
                                "${currentTablet.activeBidders} Active Bidders",
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
                                "${currentTablet.totalBids} bids",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(minOf(4, currentTablet.activeBidders)) { index ->
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
                            if (index < 3 && currentTablet.activeBidders > 4) {
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                        }
                        if (currentTablet.activeBidders > 4) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "+ ${currentTablet.activeBidders - 4} more bidding",
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
                        badge = currentTablet.totalBids.toString(),
                        onClick = onBidClick
                    )
                }
            }
            
            // Specifications Dialog
            if (showSpecsDialog) {
                SpecificationsDialog(
                    laptopIndex = 0,
                    deviceType = "tablet",
                    deviceIndex = tabletIndex,
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
                            "Tablet Condition Details",
                            color = Color(0xFFFFC107),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        currentTablet.conditionDetails,
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
                    name = currentTablet.latestBidder,
                    amount = currentTablet.latestBidAmount,
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
                    "View All ${currentTablet.totalBids} Bids →",
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
                "₹ Place Bid on Tablet",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        if (showBidDialog && currentAuctionId != null) {
            BidEntryDialog(
                currentBid = currentTablet.currentBid.replace("₹", "").replace(",", "").toDoubleOrNull() ?: 0.0,
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

