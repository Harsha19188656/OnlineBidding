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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onlinebidding.R

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
    val currentBid: String,
    val maxPrice: String,
    val activeBidders: Int,
    val totalBids: Int,
    val conditionDetails: String,
    val latestBidder: String,
    val latestBidAmount: String,
    val alertMessage: String
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
    tabletIndex: Int = 0,
    onBack: () -> Unit = {},
    onSpecsClick: () -> Unit = {},
    onBidClick: () -> Unit = {}
) {
    val tablet = if (tabletIndex < tabletAuctions.size) tabletAuctions[tabletIndex] else tabletAuctions[0]
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
                        "Tablet Auction",
                        color = Color(0xFFFF9800),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        tablet.subtitle,
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
                    Icon(Icons.Filled.Info, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        tablet.alertMessage,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Product Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Image(
                    painter = painterResource(tablet.imageRes),
                    contentDescription = tablet.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
                // Premium/Pro Verified badge
                Card(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF9C27B0)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Premium", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                // Accessories button (for Samsung)
                if (tabletIndex == 1) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF9800)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "+ ACCESSORIES",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Product Name and Rating
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    tablet.name,
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
                            tablet.condition,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("by ${tablet.sellerName}", color = Color(0xFFB0B0B0), fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${tablet.sellerRating} (${tablet.sales} ${if (tabletIndex == 0) "builds" else "sales"})",
                        color = Color(0xFFB0B0B0),
                        fontSize = 12.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tablet Features Card
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
                            "TABLET FEATURES",
                            color = Color.Black,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    // Features in 2x2 grid
                    Row(modifier = Modifier.fillMaxWidth()) {
                        FeatureItem("DISPLAY", tablet.display, Color(0xFF2196F3))
                        Spacer(modifier = Modifier.width(10.dp))
                        FeatureItem("STYLUS", tablet.stylus, Color(0xFF9C27B0))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        FeatureItem("STORAGE", tablet.storage, Color(0xFFFF9800))
                        Spacer(modifier = Modifier.width(10.dp))
                        FeatureItem("CONNECTIVITY", tablet.connectivity, Color(0xFF4CAF50))
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
                        Text("Closing Soon", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                        Icon(Icons.Filled.Info, null, tint = Color(0xFFFF5722), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(tablet.alertMessage, color = Color.White, fontSize = 12.sp)
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
                BidInfoCard("Current Bid", tablet.currentBid, if (tabletIndex == 1) "Winning bid" else "Top offer", true, tabletIndex)
                Spacer(modifier = Modifier.width(10.dp))
                BidInfoCard("Max Price", tablet.maxPrice, if (tabletIndex == 1) "Price cap" else "Reserve", false, tabletIndex)
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
                                "${tablet.activeBidders} ${if (tabletIndex == 1) "Bidders" else "Active Bidders"}",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                if (tabletIndex == 1) "Active participants" else "Professional buyers",
                                color = Color(0xFFB0B0B0),
                                fontSize = 12.sp
                            )
                        }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF9C27B0)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "${tablet.totalBids} bids",
                                color = Color.White,
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
                        val circlesToShow = tablet.activeBidders.coerceAtMost(if (tabletIndex == 1) 5 else 6)
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
                        val moreCount = (tablet.activeBidders - circlesToShow).coerceAtLeast(0)
                        if (moreCount > 0) {
                            Text(
                                if (tabletIndex == 1) "+ $moreCount more" else "+ more bidding now",
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
                    if (tabletIndex == 1) "Specifications" else "Full Specs",
                    Icons.Filled.List,
                    if (tabletIndex == 1) "Full details" else "Detailed config",
                    onClick = { showSpecsDialog = true }
                )
                Spacer(modifier = Modifier.width(10.dp))
                ActionCard(
                    if (tabletIndex == 1) "Bid History" else "Bid Log",
                    Icons.Filled.List,
                    if (tabletIndex == 1) "Live activity" else "Activity feed",
                    badge = tablet.totalBids.toString(),
                    onClick = onBidClick
                )
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
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tablet Condition Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.List, null, tint = Color(0xFF9C27B0), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Tablet Condition",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        tablet.conditionDetails,
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
                    // Included accessories button (for Samsung)
                    if (tabletIndex == 1) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF9C27B0)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Included Included", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
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
                Text(
                    if (tabletIndex == 1) "Latest Bids" else "Bid Activity",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
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
                                .background(
                                    if (tabletIndex == 1) Color(0xFF9C27B0) else Color(0xFFFF9800),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (tabletIndex == 1) "#1" else "👑",
                                color = Color.White,
                                fontSize = if (tabletIndex == 1) 14.sp else 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                tablet.latestBidder,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFF9800)),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    if (tabletIndex == 1) "LEADING" else "LEADING BID",
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        tablet.latestBidAmount,
                        color = Color(0xFFFF9800),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // View All Bids Link
            Text(
                "View All ${tablet.totalBids} Bids →",
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
                containerColor = Color(0xFF9C27B0)
            ),
            shape = RoundedCornerShape(50)
        ) {
            Icon(Icons.Filled.List, null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Place Bid on Tablet", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/* ---------------- COMPONENTS ---------------- */

@Composable
private fun FeatureItem(label: String, value: String, iconColor: Color) {
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
private fun BidInfoCard(title: String, amount: String, subtitle: String, isCurrent: Boolean, tabletIndex: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(0.48f),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) {
                if (tabletIndex == 1) Color(0xFF2A1F00) else Color(0xFF2A1F00)
            } else Color(0xFF121212)
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

