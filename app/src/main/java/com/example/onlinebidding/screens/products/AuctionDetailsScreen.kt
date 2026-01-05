package com.example.onlinebidding.screens.products

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.clickable
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.onlinebidding.R
import com.example.onlinebidding.api.RetrofitInstance
import com.example.onlinebidding.screens.products.SpecificationsDialog
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/* ---------------- DATA MODEL ---------------- */

data class LaptopAuctionData(
    val name: String,
    val subtitle: String,
    val condition: String,
    val sellerName: String,
    val sellerRating: Double,
    val sales: Int,
    val imageRes: Int,
    val processor: String,
    val storage: String,
    val display: String,
    val ram: String,
    val timeRemaining: String,
    val currentBid: String,
    val maxPrice: String,
    val activeBidders: Int,
    val totalBids: Int,
    val conditionDetails: String,
    val qualityPercent: Int,
    val latestBids: List<BidInfo>,
    val imageCount: Int
)

data class BidInfo(
    val rank: Int,
    val name: String,
    val amount: String,
    val isTopBid: Boolean = false
)

private val laptopAuctions = listOf(
    // MacBook Pro 16" M3 Max (Index 0)
    LaptopAuctionData(
        name = "MacBook Pro 16\" M3 Max",
        subtitle = "Premium Computing",
        condition = "Excellent",
        sellerName = "TechMaster Pro",
        sellerRating = 4.9,
        sales = 248,
        imageRes = R.drawable.ic_macbook,
        processor = "Apple M3 Max",
        storage = "1TB SSD",
        display = "16.2\" Liquid",
        ram = "48GB Unified Memory",
        timeRemaining = "0:30",
        currentBid = "₹1,85,000",
        maxPrice = "₹2,50,000",
        activeBidders = 28,
        totalBids = 12,
        conditionDetails = "Brand new, sealed box. All accessories included. Apple warranty valid.",
        qualityPercent = 95,
        latestBids = listOf(
            BidInfo(1, "Raj Kumar", "₹1,85,000", true),
            BidInfo(2, "Priya Singh", "₹1,80,000"),
            BidInfo(3, "Amit Patel", "₹1,75,000")
        ),
        imageCount = 3
    ),
    // Dell XPS 15 OLED (Index 1)
    LaptopAuctionData(
        name = "Dell XPS 15 OLED",
        subtitle = "Premium Computing",
        condition = "Very Good",
        sellerName = "EliteGadgets",
        sellerRating = 4.7,
        sales = 156,
        imageRes = R.drawable.ic_dell_xps,
        processor = "Intel Core i7-13700H",
        storage = "1TB NVMe SSD",
        display = "15.6\" 4K",
        ram = "32GB DDR5",
        timeRemaining = "0:25",
        currentBid = "₹95,000",
        maxPrice = "₹1,40,000",
        activeBidders = 19,
        totalBids = 8,
        conditionDetails = "Used for 6 months. Minor scratches on base. Perfect working condition.",
        qualityPercent = 95,
        latestBids = listOf(
            BidInfo(1, "Neha Verma", "₹95,000", true),
            BidInfo(2, "Vikram Shah", "₹92,000")
        ),
        imageCount = 2
    ),
    // ASUS ROG Zephyrus G16 (Index 2)
    LaptopAuctionData(
        name = "ASUS ROG Zephyrus G16",
        subtitle = "Premium Computing",
        condition = "Excellent",
        sellerName = "GamerZone",
        sellerRating = 4.8,
        sales = 203,
        imageRes = R.drawable.ic_asus_rog,
        processor = "AMD Ryzen 9 7940HS",
        storage = "2TB SSD",
        display = "16\" QHD",
        ram = "32GB DDR5",
        timeRemaining = "0:28",
        currentBid = "₹1,42,000",
        maxPrice = "₹1,90,000",
        activeBidders = 15,
        totalBids = 6,
        conditionDetails = "Gaming powerhouse. RGB keyboard, high refresh display. Like new.",
        qualityPercent = 95,
        latestBids = listOf(
            BidInfo(1, "Arjun Mehta", "₹1,42,000", true)
        ),
        imageCount = 2
    )
)

/* ---------------- SCREEN ---------------- */

@Composable
fun AuctionDetailsScreen(
    auctionId: Int? = null,
    laptopIndex: Int = 0, // Fallback for backward compatibility
    laptopName: String = "",
    onBack: () -> Unit = {},
    onPlaceBid: () -> Unit = {},
    navController: NavHostController? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var laptop by remember { mutableStateOf<LaptopAuctionData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showBidDialog by remember { mutableStateOf(false) }
    var showSpecsDialog by remember { mutableStateOf(false) }
    var currentAuctionId by remember { mutableStateOf<Int?>(auctionId) }
    
    // Function to load auction details
    fun loadAuctionDetails(id: Int) {
        isLoading = true
        errorMessage = null
        scope.launch {
            try {
                android.util.Log.d("AuctionDetails", "🔌 Fetching auction details for ID: $id")
                val response = RetrofitInstance.api.auctionDetails(id)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()!!
                    val product = data.product
                    val auction = data.auction
                    val bids = data.bids
                    
                    if (product != null && auction != null) {
                        // Parse specs to extract processor, storage, display, ram
                        val specsMap = parseSpecs(product.specs ?: "")
                        
                        // Convert bids to BidInfo format
                        val latestBids = bids.take(3).mapIndexed { index, bid ->
                            BidInfo(
                                rank = index + 1,
                                name = bid.user_name ?: "User ${bid.user_id}",
                                amount = "₹${String.format("%.0f", bid.amount)}",
                                isTopBid = index == 0
                            )
                        }
                        
                        laptop = LaptopAuctionData(
                            name = product.title,
                            subtitle = "Premium Computing",
                            condition = product.condition ?: "Excellent",
                            sellerName = "TechMaster Pro", // Default, can be added to backend
                            sellerRating = 4.9, // Default, can be added to backend
                            sales = 248, // Default, can be added to backend
                            imageRes = when {
                                product.title.contains("MacBook", ignoreCase = true) -> R.drawable.ic_macbook
                                product.title.contains("Dell", ignoreCase = true) -> R.drawable.ic_dell_xps
                                product.title.contains("ASUS", ignoreCase = true) || product.title.contains("ROG", ignoreCase = true) -> R.drawable.ic_asus_rog
                                else -> R.drawable.ic_macbook // Default
                            },
                            processor = specsMap["Processor"] ?: specsMap["processor"] ?: "Apple M3 Max",
                            storage = specsMap["Storage"] ?: specsMap["storage"] ?: "1TB SSD",
                            display = specsMap["Display"] ?: specsMap["display"] ?: "16.2\" Liquid",
                            ram = specsMap["RAM"] ?: specsMap["ram"] ?: "48GB Unified Memory",
                            timeRemaining = calculateTimeRemaining(auction.end_at),
                            currentBid = "₹${String.format("%.0f", auction.current_price)}",
                            maxPrice = "₹${String.format("%.0f", product.base_price)}",
                            activeBidders = bids.distinctBy { it.user_id }.size,
                            totalBids = bids.size,
                            conditionDetails = product.description ?: "Brand new, sealed box. All accessories included.",
                            qualityPercent = 95,
                            latestBids = latestBids,
                            imageCount = 3
                        )
                        android.util.Log.d("AuctionDetails", "✅ Successfully loaded auction details")
                    } else {
                        errorMessage = "Auction data incomplete"
                    }
                } else {
                    errorMessage = response.body()?.error ?: "Failed to load auction details"
                    android.util.Log.w("AuctionDetails", "⚠️ API Error: ${response.code()}")
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.message}"
                android.util.Log.e("AuctionDetails", "❌ Network Error: ${e.message}", e)
            } finally {
                isLoading = false
            }
        }
    }
    
    // Fetch auction details from backend
    LaunchedEffect(auctionId, laptopIndex, laptopName) {
        if (auctionId != null && auctionId > 0) {
            currentAuctionId = auctionId
            loadAuctionDetails(auctionId)
        } else {
            // Try to match by name first, then fall back to index
            laptop = if (laptopName.isNotBlank()) {
                val normalizedSearchName = laptopName.trim().lowercase()
                laptopAuctions.find { auction ->
                    val normalizedAuctionName = auction.name.trim().lowercase()
                    normalizedAuctionName.contains(normalizedSearchName) || 
                    normalizedSearchName.contains(normalizedAuctionName) ||
                    (normalizedAuctionName.contains("macbook") && normalizedSearchName.contains("macbook")) ||
                    (normalizedAuctionName.contains("dell") && normalizedSearchName.contains("dell")) ||
                    (normalizedAuctionName.contains("asus") && normalizedSearchName.contains("asus")) ||
                    (normalizedAuctionName.contains("rog") && normalizedSearchName.contains("rog"))
                } ?: laptopAuctions.getOrElse(laptopIndex) { laptopAuctions[0] }
            } else {
                laptopAuctions.getOrElse(laptopIndex) { laptopAuctions[0] }
            }
            isLoading = false
        }
    }
    
    // Show loading or error state
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFFFFC107))
        }
        return
    }
    
    if (errorMessage != null || laptop == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = errorMessage ?: "Failed to load auction",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBack) {
                    Text("Go Back")
                }
            }
        }
        return
    }
    
    val laptopData = laptop!!
    val background = Brush.verticalGradient(
        colors = listOf(Color(0xFF0D0D0D), Color.Black)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
                        text = "Laptop Auction",
                        color = Color(0xFFFFC107),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = laptopData.subtitle,
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
                    // Product image - clear and high quality
                    Image(
                        painter = painterResource(laptopData.imageRes),
                        contentDescription = laptopData.name,
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

                // Image count badge
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xAA000000)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            laptopData.imageCount.toString(),
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            /* ---------- PRODUCT INFO ---------- */
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = laptopData.name,
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
                            laptopData.condition,
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "by ${laptopData.sellerName}",
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
                        "${laptopData.sellerRating} (${laptopData.sales} sales)",
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
                    SpecCard("Processor", laptopData.processor, Color(0xFFFFC107), Icons.Default.Info)
                }
                Box(modifier = Modifier.weight(1f)) {
                    SpecCard("Storage", laptopData.storage, Color(0xFFFFC107), Icons.Default.Info)
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
                    SpecCard("Display", laptopData.display, Color(0xFF2196F3), Icons.Default.Info)
                }
                Box(modifier = Modifier.weight(1f)) {
                    SpecCard("RAM", laptopData.ram, Color(0xFF4CAF50), Icons.Default.Info)
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
                            Icons.Default.Info, // Clock icon representation
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
                        laptopData.timeRemaining,
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Bid fast to secure this laptop!",
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
                BidInfoCard("Current Bid", laptopData.currentBid, "Highest offer", true)
                }
                Box(modifier = Modifier.weight(1f)) {
                BidInfoCard("Max Price", laptopData.maxPrice, "Reserve limit", false)
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
                                "${laptopData.activeBidders} Active Bidders",
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
                                "${laptopData.totalBids} bids",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(minOf(4, laptopData.activeBidders)) { index ->
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
                            if (index < 3 && laptopData.activeBidders > 4) {
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                        }
                        if (laptopData.activeBidders > 4) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "+ ${laptopData.activeBidders - 4} more bidding",
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
                    badge = laptopData.totalBids.toString(),
                    onClick = {
                        // Navigate with auction_id if available, otherwise use index
                        val route = if (currentAuctionId != null && currentAuctionId!! > 0) {
                            "bid_comments/laptop/$laptopIndex/${currentAuctionId!!}"
                        } else {
                            "bid_comments/laptop/$laptopIndex"
                        }
                        navController?.navigate(route)
                    }
                )
                }
            }
            
            // Specifications Dialog
            if (showSpecsDialog) {
                SpecificationsDialog(
                    laptopIndex = laptopIndex,
                    deviceType = "laptop",
                    deviceIndex = laptopIndex,
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
                            "Laptop Condition Details",
                            color = Color(0xFFFFC107),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        laptopData.conditionDetails,
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
                            progress = laptopData.qualityPercent / 100f,
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFFFFC107),
                            trackColor = Color(0x33FFFFFF)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "${laptopData.qualityPercent}% Quality",
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

            laptopData.latestBids.forEach { bid ->
                LatestBidRow(bid)
                Spacer(modifier = Modifier.height(8.dp))
            }

            TextButton(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    "View All ${laptopData.totalBids} Bids →",
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
                    onPlaceBid()
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
                "₹ Place Bid on Laptop",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Bid Entry Dialog
        if (showBidDialog && currentAuctionId != null) {
            BidEntryDialog(
                currentBid = laptopData.currentBid.replace("₹", "").replace(",", "").toDoubleOrNull() ?: 0.0,
                onDismiss = { showBidDialog = false },
                onConfirm = { amount ->
                    scope.launch {
                        try {
                            val bidAmount = amount.replace("₹", "").replace(",", "").toDoubleOrNull() ?: 0.0
                            android.util.Log.d("AuctionDetails", "💰 Placing bid: ₹$bidAmount for auction $currentAuctionId")
                            
                            val response = RetrofitInstance.api.placeBid(
                                com.example.onlinebidding.api.PlaceBidRequest(
                                    auction_id = currentAuctionId!!,
                                    amount = bidAmount,
                                    user_id = 25 // TODO: Get from logged in user session - using 25 for now (Admin User)
                                )
                            )
                            
                            if (response.isSuccessful) {
                                val responseBody = response.body()
                                if (responseBody?.success == true) {
                                    android.util.Log.d("AuctionDetails", "✅ Bid placed successfully!")
                                    // Refresh auction details
                                    loadAuctionDetails(currentAuctionId!!)
                                    showBidDialog = false
                                    
                                    // Show success toast
                                    android.widget.Toast.makeText(
                                        context,
                                        "Bid placed successfully!",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    // API returned success=false
                                    val error = responseBody?.error ?: "Failed to place bid"
                                    android.util.Log.e("AuctionDetails", "❌ Bid rejected by API: $error")
                                    android.util.Log.e("AuctionDetails", "   Response code: ${response.code()}")
                                    android.widget.Toast.makeText(
                                        context,
                                        "Bid rejected: $error",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                }
                            } else {
                                // HTTP error (4xx, 5xx)
                                val errorBody = response.errorBody()?.string()
                                val error = try {
                                    response.body()?.error ?: errorBody ?: "HTTP ${response.code()}: Failed to place bid"
                                } catch (e: Exception) {
                                    "HTTP ${response.code()}: Failed to place bid"
                                }
                                android.util.Log.e("AuctionDetails", "❌ HTTP Error ${response.code()}: $error")
                                android.widget.Toast.makeText(
                                    context,
                                    "Error: $error",
                                    android.widget.Toast.LENGTH_LONG
                                ).show()
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("AuctionDetails", "❌ Network error: ${e.message}", e)
                            android.widget.Toast.makeText(
                                context,
                                "Network error: ${e.message}",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            )
        }
    }
}

/* ================= REUSABLE COMPONENTS ================= */

@Composable
private fun SpecCard(title: String, value: String, iconColor: Color, icon: androidx.compose.ui.graphics.vector.ImageVector) {
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
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
    icon: androidx.compose.ui.graphics.vector.ImageVector, 
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

/* ---------------- HELPER FUNCTIONS ---------------- */

private fun parseSpecs(specsString: String): Map<String, String> {
    val specsMap = mutableMapOf<String, String>()
    
    if (specsString.isEmpty()) return specsMap
    
    // Try to parse as "key: value" format
    val parts = specsString.split(" · ", " | ", ", ")
    for (part in parts) {
        val colonIndex = part.indexOf(':')
        if (colonIndex > 0) {
            val key = part.substring(0, colonIndex).trim()
            val value = part.substring(colonIndex + 1).trim()
            specsMap[key] = value
        }
    }
    
    return specsMap
}

private fun calculateTimeRemaining(endAt: String?): String {
    if (endAt == null) return "0:30"
    
    try {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val endTime = format.parse(endAt)
        val now = Date()
        
        if (endTime != null && endTime.after(now)) {
            val diff = endTime.time - now.time
            val minutes = (diff / 60000).toInt()
            val seconds = ((diff % 60000) / 1000).toInt()
            return "$minutes:${String.format("%02d", seconds)}"
        }
    } catch (e: Exception) {
        android.util.Log.e("AuctionDetails", "Error parsing end_at: ${e.message}")
    }
    
    return "0:30"
}

/* ---------------- BID ENTRY DIALOG ---------------- */

@Composable
private fun BidEntryDialog(
    currentBid: Double,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var bidAmount by remember { mutableStateOf("") }
    val minBid = currentBid + 1000 // Minimum bid is current + 1000
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Place Your Bid",
                        color = Color(0xFFFFC107),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            "Close",
                            tint = Color(0xFFFFC107)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "Current Bid: ₹${String.format("%.0f", currentBid)}",
                    color = Color(0x99FFFFFF),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Minimum Bid: ₹${String.format("%.0f", minBid)}",
                    color = Color(0xFFFFC107),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Bid Amount Field
                Text("Bid Amount (₹)", color = Color(0xFFFFC107), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = bidAmount,
                    onValueChange = { 
                        // Allow only numbers
                        bidAmount = it.filter { char -> char.isDigit() }
                    },
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
                    leadingIcon = {
                        Text("₹", color = Color(0xFFFFC107), modifier = Modifier.padding(start = 12.dp))
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Confirm Button
                Button(
                    onClick = {
                        if (bidAmount.isNotBlank()) {
                            val amount = bidAmount.toDoubleOrNull() ?: 0.0
                            if (amount >= minBid) {
                                onConfirm("₹$bidAmount")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    shape = RoundedCornerShape(25.dp),
                    enabled = bidAmount.isNotBlank() && (bidAmount.toDoubleOrNull() ?: 0.0) >= minBid
                ) {
                    Text("Submit Bid", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
