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

data class MobileAuctionData(
    val name: String,
    val subtitle: String,
    val condition: String,
    val sellerName: String,
    val sellerRating: Double,
    val sales: Int,
    val imageRes: Int,
    val display: String,
    val camera: String,
    val processor: String,
    val storage: String,
    val ram: String = "8GB", // Default RAM for mobile
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

private val mobileAuctions = listOf(
    // iPhone 15 Pro Max (Index 0)
    MobileAuctionData(
        name = "iPhone 15 Pro Max 512GB",
        subtitle = "Smartphones & Devices",
        condition = "Excellent",
        sellerName = "iStore Premium",
        sellerRating = 5.0,
        sales = 412,
        imageRes = R.drawable.ic_appleiphone15pro,
        display = "6.7\" Super Retina XDR",
        camera = "48MP Main",
        processor = "A17 Pro Chip",
        storage = "512GB",
        ram = "8GB",
        currentBid = "₹1,28,000",
        maxPrice = "₹1,55,000",
        activeBidders = 32,
        totalBids = 14,
        conditionDetails = "Titanium Blue. Complete box with all accessories. AppleCare+ included.",
        latestBidder = "Raj Kumar",
        latestBidAmount = "₹1,28,000",
        alertMessage = "Secure this powerful system now!"
    ),
    // Samsung Galaxy S24 Ultra (Index 1)
    MobileAuctionData(
        name = "Samsung Galaxy S24 Ultra",
        subtitle = "Smartphones & Devices",
        condition = "Very Good",
        sellerName = "GalaxyHub",
        sellerRating = 4.6,
        sales = 189,
        imageRes = R.drawable.ic_samsunggalaxys24ultra,
        display = "6.8\" Dynamic",
        camera = "200MP Main",
        processor = "Snapdragon 8",
        storage = "256GB",
        ram = "12GB",
        currentBid = "₹98,000",
        maxPrice = "₹1,35,000",
        activeBidders = 21,
        totalBids = 7,
        conditionDetails = "Titanium Gray. Minor back glass scratches. S Pen included.",
        latestBidder = "Karan Joshi",
        latestBidAmount = "₹98,000",
        alertMessage = "Don't miss this premium mobile!"
    ),
    // OnePlus 12 Pro (Index 2)
    MobileAuctionData(
        name = "OnePlus 12 Pro",
        subtitle = "Smartphones & Devices",
        condition = "Good",
        sellerName = "OnePlus Official",
        sellerRating = 4.9,
        sales = 321,
        imageRes = R.drawable.ic_oneplus12pro,
        display = "6.82\" LTPO",
        camera = "Hasselblad 50MP",
        processor = "Snapdragon 8",
        storage = "256GB",
        ram = "12GB",
        currentBid = "₹54,000",
        maxPrice = "₹75,000",
        activeBidders = 12,
        totalBids = 5,
        conditionDetails = "Flawless Black. Screen protector and case included.",
        latestBidder = "Pooja Iyer",
        latestBidAmount = "₹54,000",
        alertMessage = "Don't miss this premium mobile!"
    )
)

/* ---------------- SCREEN ---------------- */

@Composable
fun MobileAuctionDetailScreen(
    auctionId: Int? = null,
    mobileIndex: Int = 0,
    mobileName: String = "",
    onBack: () -> Unit = {},
    onSpecsClick: () -> Unit = {},
    onBidClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var mobile by remember { mutableStateOf<MobileAuctionData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showBidDialog by remember { mutableStateOf(false) }
    var showSpecsDialog by remember { mutableStateOf(false) }
    var currentAuctionId by remember { mutableStateOf<Int?>(auctionId) }
    val scrollState = rememberScrollState()
    
    // Load auction from API
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
                            product.title.contains("iPhone", ignoreCase = true) -> R.drawable.ic_appleiphone15pro
                            product.title.contains("Samsung", ignoreCase = true) -> R.drawable.ic_samsunggalaxys24ultra
                            product.title.contains("OnePlus", ignoreCase = true) -> R.drawable.ic_oneplus12pro
                            else -> R.drawable.ic_appleiphone15pro // Default
                        }
                        
                        mobile = MobileAuctionData(
                            name = product.title,
                            subtitle = product.category ?: "Smartphones & Devices",
                            condition = product.condition ?: "Excellent",
                            sellerName = "Mobile Store",
                            sellerRating = 4.8,
                            sales = 200,
                            imageRes = imageRes,
                            display = "6.7\" Display",
                            camera = "48MP",
                            processor = "Latest Chip",
                            storage = "256GB",
                            currentBid = "₹${String.format("%.0f", auction.current_price)}",
                            maxPrice = "₹${String.format("%.0f", product.base_price)}",
                            activeBidders = data.bids.distinctBy { it.user_id }.size,
                            totalBids = data.bids.size,
                            conditionDetails = product.description ?: "Premium mobile device",
                            latestBidder = data.bids.firstOrNull()?.user_name ?: "User",
                            latestBidAmount = if (data.bids.isNotEmpty()) "₹${String.format("%.0f", data.bids[0].amount)}" else "₹0",
                            alertMessage = "Don't miss this premium mobile!"
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("MobileAuction", "Error: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
    
    LaunchedEffect(auctionId, mobileIndex, mobileName) {
        if (auctionId != null && auctionId > 0) {
            currentAuctionId = auctionId
            loadAuctionDetails(auctionId)
        } else {
            // Try to match by name first, then fall back to index
            mobile = if (mobileName.isNotBlank()) {
                // Normalize names for comparison (remove extra spaces, convert to lowercase)
                val normalizedSearchName = mobileName.trim().lowercase()
                mobileAuctions.find { auction ->
                    val normalizedAuctionName = auction.name.trim().lowercase()
                    // Check if either name contains the other, or if they share key brand identifiers
                    normalizedAuctionName.contains(normalizedSearchName) || 
                    normalizedSearchName.contains(normalizedAuctionName) ||
                    (normalizedAuctionName.contains("oneplus") && normalizedSearchName.contains("oneplus")) ||
                    (normalizedAuctionName.contains("iphone") && normalizedSearchName.contains("iphone")) ||
                    (normalizedAuctionName.contains("samsung") && normalizedSearchName.contains("samsung"))
                } ?: if (mobileIndex < mobileAuctions.size) mobileAuctions[mobileIndex] else mobileAuctions[0]
            } else {
                if (mobileIndex < mobileAuctions.size) mobileAuctions[mobileIndex] else mobileAuctions[0]
            }
            isLoading = false
        }
    }
    
    if (isLoading || mobile == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFFFFC107))
        }
        return
    }
    
    val currentMobile = mobile!!
    
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
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, null, tint = Color(0xFFFF9800))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Mobile Auction",
                        color = Color(0xFFFF9800),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        currentMobile.subtitle,
                        color = Color(0xFFB0B0B0),
                        fontSize = 12.sp
                    )
                }
            }
            
            // Alert Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF8B0000)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                        Icon(Icons.Filled.Star, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        currentMobile.alertMessage,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Image Carousel
            val imageList = remember { listOf(currentMobile.imageRes, currentMobile.imageRes, currentMobile.imageRes) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                imageList.forEachIndexed { index, imageRes ->
                    Box(
                        modifier = Modifier
                            .width(280.dp)
                            .height(200.dp)
                    ) {
                        Image(
                            painter = painterResource(imageRes),
                            contentDescription = "${currentMobile.name} - Image ${index + 1}",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.FillBounds
                        )
                        if (index == 0) {
                            // Verified badge on first image
                            Card(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF2196F3)),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Pro Verified", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Product Name and Rating
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    currentMobile.name,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2196F3)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            currentMobile.condition,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("by ${currentMobile.sellerName}", color = Color.White, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${currentMobile.sellerRating} (${currentMobile.sales} builds)",
                        color = Color.White,
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
                    SpecCard("Processor", currentMobile.processor, Color(0xFFFFC107), Icons.Default.Info)
                }
                Box(modifier = Modifier.weight(1f)) {
                    SpecCard("Storage", currentMobile.storage, Color(0xFFFFC107), Icons.Default.Info)
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
                    SpecCard("Display", currentMobile.display, Color(0xFF2196F3), Icons.Default.Info)
                }
                Box(modifier = Modifier.weight(1f)) {
                    SpecCard("RAM", currentMobile.ram, Color(0xFF4CAF50), Icons.Default.Info)
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
                        currentMobile.timeRemaining,
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Bid fast to secure this mobile!",
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
                    BidInfoCard("Current Bid", currentMobile.currentBid, "Highest offer", true)
                }
                Box(modifier = Modifier.weight(1f)) {
                    BidInfoCard("Max Price", currentMobile.maxPrice, "Reserve limit", false)
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
                                "${currentMobile.activeBidders} Active Bidders",
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
                                "${currentMobile.totalBids} bids",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(minOf(4, currentMobile.activeBidders)) { index ->
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
                            if (index < 3 && currentMobile.activeBidders > 4) {
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                        }
                        if (currentMobile.activeBidders > 4) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "+ ${currentMobile.activeBidders - 4} more bidding",
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
                        badge = currentMobile.totalBids.toString(),
                        onClick = onBidClick
                    )
                }
            }
            
            // Specifications Dialog
            if (showSpecsDialog) {
                SpecificationsDialog(
                    laptopIndex = 0,
                    deviceType = "mobile",
                    deviceIndex = mobileIndex,
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
                            "Mobile Condition Details",
                            color = Color(0xFFFFC107),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        currentMobile.conditionDetails,
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
                    name = currentMobile.latestBidder,
                    amount = currentMobile.latestBidAmount,
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
                    "View All ${currentMobile.totalBids} Bids →",
                    color = Color(0xFFFFC107),
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            
            Spacer(modifier = Modifier.height(80.dp))
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
                "₹ Place Bid on Mobile",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Bid Entry Dialog
        if (showBidDialog && currentAuctionId != null) {
            BidEntryDialog(
                currentBid = currentMobile.currentBid.replace("₹", "").replace(",", "").toDoubleOrNull() ?: 0.0,
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


