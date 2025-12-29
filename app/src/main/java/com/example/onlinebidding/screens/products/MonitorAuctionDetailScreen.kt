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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onlinebidding.R
import com.example.onlinebidding.screens.products.SpecificationsDialog

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
    val currentBid: String,
    val maxPrice: String,
    val activeBidders: Int,
    val totalBids: Int,
    val conditionDetails: String,
    val latestBidder: String,
    val latestBidAmount: String,
    val alertMessage: String
)

private val monitorAuctions = listOf(
    // Samsung Odyssey G9 49" (Index 0)
    MonitorAuctionData(
        name = "Samsung Odyssey G9 49\"",
        subtitle = "Desktop & Workstations",
        condition = "Excellent",
        sellerName = "DisplayMasters",
        sellerRating = 4.8,
        sales = 178,
        imageRes = R.drawable.ic_monitor_samsung,
        processor = "N/A",
        graphics = "Integrated",
        memory = "N/A",
        storage = "N/A",
        currentBid = "₹95,000",
        maxPrice = "₹1,35,000",
        activeBidders = 22,
        totalBids = 8,
        conditionDetails = "Ultra-wide gaming monitor. Perfect condition, no dead pixels.",
        latestBidder = "Rahul Singh",
        latestBidAmount = "₹95,000",
        alertMessage = "Secure this powerful system now!"
    ),
    // LG UltraFine 5K 27" (Index 1)
    MonitorAuctionData(
        name = "LG UltraFine 5K 27\"",
        subtitle = "Desktop & Workstations",
        condition = "Very Good",
        sellerName = "ProDisplay Hub",
        sellerRating = 4.9,
        sales = 234,
        imageRes = R.drawable.ic_monitor_lg,
        processor = "N/A",
        graphics = "Integrated",
        memory = "N/A",
        storage = "N/A",
        currentBid = "₹68,000",
        maxPrice = "₹95,000",
        activeBidders = 18,
        totalBids = 6,
        conditionDetails = "Professional display. Perfect for Mac users and creative work.",
        latestBidder = "Meera Patel",
        latestBidAmount = "₹68,000",
        alertMessage = "Secure this powerful system now!"
    ),
    // Dell UltraSharp U3423WE (Index 2)
    MonitorAuctionData(
        name = "Dell UltraSharp U3423WE",
        subtitle = "Desktop & Workstations",
        condition = "Excellent",
        sellerName = "OfficeGear Pro",
        sellerRating = 4.7,
        sales = 145,
        imageRes = R.drawable.ic_monitor_dell,
        processor = "N/A",
        graphics = "Integrated",
        memory = "N/A",
        storage = "N/A",
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
    monitorIndex: Int = 0,
    onBack: () -> Unit = {},
    onSpecsClick: () -> Unit = {},
    onBidClick: () -> Unit = {}
) {
    val monitor = if (monitorIndex < monitorAuctions.size) monitorAuctions[monitorIndex] else monitorAuctions[0]
    val scrollState = rememberScrollState()
    var showSpecsDialog by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
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
                        "Monitors Auction",
                        color = Color(0xFFFF9800),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        monitor.subtitle,
                        color = Color(0xFFB0B0B0),
                        fontSize = 12.sp
                    )
                }
            }
            
            // Product Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Image(
                    painter = painterResource(monitor.imageRes),
                    contentDescription = monitor.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
                // Pro Verified badge
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
                
                // HIGH PERFORMANCE badge
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF8B0000)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Star, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("HIGH PERFORMANCE", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Product Name and Rating
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    monitor.name,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2196F3)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            monitor.condition,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("by ${monitor.sellerName}", color = Color(0xFFB0B0B0), fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${monitor.sellerRating} (${monitor.sales} builds)",
                        color = Color(0xFFB0B0B0),
                        fontSize = 12.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // System Configuration Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Orange bar header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFF9800), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.List, null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "SYSTEM CONFIGURATION",
                            color = Color.Black,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    // Specifications in 2x2 grid
                    Row(modifier = Modifier.fillMaxWidth()) {
                        SpecItem("PROCESSOR", monitor.processor, Color(0xFFFF5722))
                        Spacer(modifier = Modifier.width(10.dp))
                        SpecItem("GRAPHICS", monitor.graphics, Color(0xFF4CAF50))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        SpecItem("MEMORY", monitor.memory, Color(0xFF2196F3))
                        Spacer(modifier = Modifier.width(10.dp))
                        SpecItem("STORAGE", monitor.storage, Color(0xFF9C27B0))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Auction Countdown
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF8B0000)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, null, tint = Color(0xFFFF5722), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AUCTION COUNTDOWN", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "0:30",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, null, tint = Color(0xFFFF5722), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(monitor.alertMessage, color = Color.White, fontSize = 12.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Bid Info Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                BidInfoCard("CURRENT BID", monitor.currentBid, "Top offer", true)
                Spacer(modifier = Modifier.width(10.dp))
                BidInfoCard("MAX PRICE", monitor.maxPrice, "Reserve", false)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Active Bidders Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "${monitor.activeBidders} Active Bidders",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Professional buyers",
                                color = Color(0xFFB0B0B0),
                                fontSize = 12.sp
                            )
                        }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFF9800)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "${monitor.totalBids} bids",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(-8.dp)
                    ) {
                        // Overlapping circles for bidders
                        val circlesToShow = monitor.activeBidders.coerceAtMost(6)
                        for (i in 1..circlesToShow) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF9C27B0), Color(0xFFE91E63))
                                        ),
                                        CircleShape
                                    )
                                    .border(2.dp, Color.Black, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    i.toString(),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        val moreCount = (monitor.activeBidders - 6).coerceAtLeast(0)
                        if (moreCount > 0) {
                            Text(
                                "+ more bidding now",
                                color = Color(0xFFB0B0B0),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                ActionCard(
                    "Full Specs",
                    Icons.Filled.List,
                    "Detailed config",
                    onClick = { showSpecsDialog = true }
                )
                Spacer(modifier = Modifier.width(10.dp))
                ActionCard(
                    "Bid Log",
                    Icons.Filled.List,
                    "Activity feed",
                    badge = monitor.totalBids.toString(),
                    onClick = onBidClick
                )
            }
            
            // Specifications Dialog
            if (showSpecsDialog) {
                SpecificationsDialog(
                    laptopIndex = 0,
                    deviceType = "monitor",
                    deviceIndex = monitorIndex,
                    onDismiss = { showSpecsDialog = false }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // System Condition Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.List, null, tint = Color(0xFFFF9800), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "System Condition",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        monitor.conditionDetails,
                        color = Color(0xFFB0B0B0),
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    // Status badges
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusBadge("Tested", Color(0xFF4CAF50))
                        StatusBadge("Warranty", Color(0xFF2196F3))
                        StatusBadge("Certified", Color(0xFF9C27B0))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Bid Activity Section (Second Screen Content)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Bid Activity", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .background(Color(0xFF4CAF50), CircleShape)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("LIVE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Latest Bid Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFFF9800), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Star, null, tint = Color.Black, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                monitor.latestBidder,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFF9800)),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    "LEADING BID",
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        monitor.latestBidAmount,
                        color = Color(0xFFFF9800),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // View All Bids Link
            Text(
                "View All ${monitor.totalBids} Bids →",
                color = Color(0xFFB0B0B0),
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable { onBidClick() }
            )
            
            Spacer(modifier = Modifier.height(100.dp))
        }
        
        // Place Bid Button (Fixed at bottom)
        Button(
            onClick = onBidClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF9800)
            ),
            shape = RoundedCornerShape(50)
        ) {
            Icon(Icons.Filled.List, null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("BID ON COMPUTER", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/* ---------------- COMPONENTS ---------------- */

@Composable
private fun SpecItem(label: String, value: String, iconColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(0.48f),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(iconColor, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Star, null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(label, color = Color(0xFFB0B0B0), fontSize = 9.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(2.dp))
                Text(value, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun BidInfoCard(title: String, amount: String, subtitle: String, isCurrent: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(0.48f),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) Color(0xFF2A1F00) else Color(0xFF121212)
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, color = if (isCurrent) Color(0xFFFFC107) else Color(0xFFB0B0B0), fontSize = 10.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                amount,
                color = if (isCurrent) Color(0xFFFF9800) else Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, color = Color(0xFFB0B0B0), fontSize = 10.sp)
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
    Box(modifier = Modifier.fillMaxWidth(0.48f)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    Icon(icon, null, tint = Color(0xFFFF9800), modifier = Modifier.size(28.dp))
                    if (badge != null) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset((-6).dp, 6.dp)
                                .background(Color.Red, CircleShape)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(badge, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(subtitle, color = Color(0xFFB0B0B0), fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun StatusBadge(text: String, color: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

