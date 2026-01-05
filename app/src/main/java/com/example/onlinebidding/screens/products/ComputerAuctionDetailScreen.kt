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

data class ComputerAuctionData(
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
    val display: String = "N/A", // For monitors/computers
    val ram: String = "", // Will use memory field
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

private val computerAuctions = listOf(
    // Custom Gaming PC RTX (Index 0)
    ComputerAuctionData(
        name = "Custom Gaming PC RTX 4090",
        subtitle = "Desktop & Workstations",
        condition = "Excellent",
        sellerName = "PCBuilders Pro",
        sellerRating = 4.9,
        sales = 167,
        imageRes = R.drawable.ic_pcgamming,
        processor = "Intel Core i9-14900K",
        graphics = "NVIDIA RTX 4090 24GB",
        memory = "64GB DDR5 6000MHz",
        storage = "2TB NVMe Gen 5",
        currentBid = "₹2,85,000",
        maxPrice = "₹3,80,000",
        activeBidders = 24,
        totalBids = 9,
        conditionDetails = "Custom built. RGB lighting, liquid cooling. All components under warranty.",
        latestBidder = "Rohit Sharma",
        latestBidAmount = "₹2,85,000",
        alertMessage = "Secure this powerful system now!"
    ),
    // Mac Studio M2 Ultra (Index 1)
    ComputerAuctionData(
        name = "Mac Studio M2 Ultra",
        subtitle = "Desktop & Workstations",
        condition = "Excellent",
        sellerName = "Apple Premium",
        sellerRating = 5.0,
        sales = 298,
        imageRes = R.drawable.ic_macstudio,
        processor = "Apple M2 Ultra (24-c)",
        graphics = "76-core GPU",
        memory = "192GB Unified Memory",
        storage = "4TB SSD",
        currentBid = "₹3,10,000",
        maxPrice = "₹4,25,000",
        activeBidders = 18,
        totalBids = 6,
        conditionDetails = "Professional workstation. Perfect for video editing and 3D work.",
        latestBidder = "Rohit Sharma",
        latestBidAmount = "₹3,10,000",
        alertMessage = "Secure this powerful system now!"
    ),
    // HP Z8 G5 Workstation (Index 2)
    ComputerAuctionData(
        name = "Workstation HP Z8 G5",
        subtitle = "Desktop & Workstations",
        condition = "Very Good",
        sellerName = "Enterprise Tech",
        sellerRating = 4.7,
        sales = 134,
        imageRes = R.drawable.ic_hp_z5,
        processor = "Intel Xeon W9-3495X",
        graphics = "NVIDIA RTX A6000 48G",
        memory = "128GB ECC DDR5",
        storage = "4TB NVMe RAID",
        currentBid = "₹1,95,000",
        maxPrice = "₹2,80,000",
        activeBidders = 14,
        totalBids = 4,
        conditionDetails = "Enterprise workstation. CAD, rendering, data science ready.",
        latestBidder = "Aditya Gupta",
        latestBidAmount = "₹1,95,000",
        alertMessage = "Secure this powerful system now!"
    )
)

/* ---------------- SCREEN ---------------- */

@Composable
fun ComputerAuctionDetailScreen(
    auctionId: Int? = null,
    computerIndex: Int = 0,
    computerName: String = "",
    onBack: () -> Unit = {},
    onSpecsClick: () -> Unit = {},
    onBidClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var computer by remember { mutableStateOf<ComputerAuctionData?>(null) }
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
                            product.title.contains("Gaming", ignoreCase = true) || product.title.contains("RTX", ignoreCase = true) -> R.drawable.ic_pcgamming
                            product.title.contains("Mac", ignoreCase = true) || product.title.contains("Studio", ignoreCase = true) -> R.drawable.ic_macstudio
                            product.title.contains("HP", ignoreCase = true) || product.title.contains("Workstation", ignoreCase = true) -> R.drawable.ic_hp_z5
                            else -> R.drawable.ic_pcgamming // Default
                        }
                        
                        computer = ComputerAuctionData(
                            name = product.title,
                            subtitle = product.category ?: "Desktop & Workstations",
                            condition = product.condition ?: "Excellent",
                            sellerName = "Computer Store",
                            sellerRating = 4.8,
                            sales = 200,
                            imageRes = imageRes,
                            processor = "High Performance",
                            graphics = "Dedicated GPU",
                            memory = "32GB RAM",
                            storage = "1TB SSD",
                            currentBid = "₹${String.format("%.0f", auction.current_price)}",
                            maxPrice = "₹${String.format("%.0f", product.base_price)}",
                            activeBidders = data.bids.distinctBy { it.user_id }.size,
                            totalBids = data.bids.size,
                            conditionDetails = product.description ?: "Premium computer system",
                            latestBidder = data.bids.firstOrNull()?.user_name ?: "User",
                            latestBidAmount = if (data.bids.isNotEmpty()) "₹${String.format("%.0f", data.bids[0].amount)}" else "₹0",
                            alertMessage = "Secure this powerful system now!"
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("ComputerAuction", "Error: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
    
    LaunchedEffect(auctionId, computerIndex, computerName) {
        if (auctionId != null && auctionId > 0) {
            currentAuctionId = auctionId
            loadAuctionDetails(auctionId)
        } else {
            // Try to match by name first, then fall back to index
            computer = if (computerName.isNotBlank()) {
                // Normalize names for comparison (remove extra spaces, convert to lowercase)
                val normalizedSearchName = computerName.trim().lowercase()
                computerAuctions.find { auction ->
                    val normalizedAuctionName = auction.name.trim().lowercase()
                    // Check if either name contains the other, or if they share key identifiers
                    normalizedAuctionName.contains(normalizedSearchName) || 
                    normalizedSearchName.contains(normalizedAuctionName) ||
                    (normalizedAuctionName.contains("gaming") && normalizedSearchName.contains("gaming")) ||
                    (normalizedAuctionName.contains("rtx") && normalizedSearchName.contains("rtx")) ||
                    (normalizedAuctionName.contains("mac") && normalizedSearchName.contains("mac")) ||
                    (normalizedAuctionName.contains("studio") && normalizedSearchName.contains("studio")) ||
                    (normalizedAuctionName.contains("hp") && normalizedSearchName.contains("hp")) ||
                    (normalizedAuctionName.contains("workstation") && normalizedSearchName.contains("workstation"))
                } ?: if (computerIndex < computerAuctions.size) computerAuctions[computerIndex] else computerAuctions[0]
            } else {
                if (computerIndex < computerAuctions.size) computerAuctions[computerIndex] else computerAuctions[0]
            }
            isLoading = false
        }
    }
    
    if (isLoading || computer == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFFFFC107))
        }
        return
    }
    
    val currentComputer = computer!!
    
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
                        text = "Computer Auction",
                        color = Color(0xFFFFC107),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentComputer.subtitle,
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
                        painter = painterResource(currentComputer.imageRes),
                        contentDescription = currentComputer.name,
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
                    text = currentComputer.name,
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
                            currentComputer.condition,
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "by ${currentComputer.sellerName}",
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
                        "${currentComputer.sellerRating} (${currentComputer.sales} sales)",
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
                    SpecCard("Processor", currentComputer.processor, Color(0xFFFFC107), Icons.Default.Info)
                }
                Box(modifier = Modifier.weight(1f)) {
                    SpecCard("Storage", currentComputer.storage, Color(0xFFFFC107), Icons.Default.Info)
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
                    SpecCard("Graphics", currentComputer.graphics, Color(0xFF2196F3), Icons.Default.Info)
                }
                Box(modifier = Modifier.weight(1f)) {
                    SpecCard("RAM", currentComputer.memory, Color(0xFF4CAF50), Icons.Default.Info)
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
                        currentComputer.timeRemaining,
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Bid fast to secure this computer!",
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
                    BidInfoCard("Current Bid", currentComputer.currentBid, "Highest offer", true)
                }
                Box(modifier = Modifier.weight(1f)) {
                    BidInfoCard("Max Price", currentComputer.maxPrice, "Reserve limit", false)
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
                                "${currentComputer.activeBidders} Active Bidders",
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
                                "${currentComputer.totalBids} bids",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(minOf(4, currentComputer.activeBidders)) { index ->
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
                            if (index < 3 && currentComputer.activeBidders > 4) {
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                        }
                        if (currentComputer.activeBidders > 4) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "+ ${currentComputer.activeBidders - 4} more bidding",
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
                        badge = currentComputer.totalBids.toString(),
                        onClick = onBidClick
                    )
                }
            }
            
            // Specifications Dialog
            if (showSpecsDialog) {
                SpecificationsDialog(
                    laptopIndex = 0,
                    deviceType = "computer",
                    deviceIndex = computerIndex,
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
                            "Computer Condition Details",
                            color = Color(0xFFFFC107),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        currentComputer.conditionDetails,
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
                    name = currentComputer.latestBidder,
                    amount = currentComputer.latestBidAmount,
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
                    "View All ${currentComputer.totalBids} Bids →",
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
                "₹ Place Bid on Computer",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        if (showBidDialog && currentAuctionId != null) {
            BidEntryDialog(
                currentBid = currentComputer.currentBid.replace("₹", "").replace(",", "").toDoubleOrNull() ?: 0.0,
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

