package com.example.onlinebidding.screens.products

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
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
import androidx.compose.ui.window.Dialog
import com.example.onlinebidding.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.onlinebidding.api.RetrofitInstance
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

// Data classes - using existing ones from AuctionDetailsScreen
// Import the data classes from AuctionDetailsScreen

// State management for credits
object CreditsState {
    private val paidCredits = mutableStateMapOf<String, Boolean>()
    
    fun hasCreditsForItem(itemId: String): Boolean {
        return paidCredits[itemId] == true
    }
    
    fun setCreditsPaid(itemId: String) {
        paidCredits[itemId] = true
    }
}

// Auction Detail Screen (Scrollable with product + bid info)
@Composable
fun AuctionDetailScreen(
    laptopIndex: Int = 0,
    deviceType: String = "laptop",
    onBack: () -> Unit,
    onSpecsClick: () -> Unit,
    onBidClick: () -> Unit
) {
    var showSpecsDialog by remember { mutableStateOf(false) }
    val laptops = listOf(
        com.example.onlinebidding.screens.products.LaptopAuctionData(
            name = "MacBook Pro 16\" M3 Max",
            subtitle = "Premium Computing",
            condition = "Excellent",
            sellerName = "TechMaster Pro",
            sellerRating = 4.9,
            sales = 248,
            imageRes = R.drawable.ic_macbook,
            processor = "Apple M3 Max",
            storage = "1TB SSD",
            display = "16.2\" Liquid Retina XDR",
            ram = "48GB Unified Memory",
            timeRemaining = "0:30",
            currentBid = "₹1,85,000",
            maxPrice = "₹2,50,000",
            activeBidders = 28,
            totalBids = 12,
            conditionDetails = "Brand new, sealed box. All accessories included. Apple warranty valid.",
            qualityPercent = 95,
            latestBids = listOf(
                com.example.onlinebidding.screens.products.BidInfo(1, "Raj Kumar", "₹1,85,000", true),
                com.example.onlinebidding.screens.products.BidInfo(2, "Priya Singh", "₹1,80,000"),
                com.example.onlinebidding.screens.products.BidInfo(3, "Amit Patel", "₹1,75,000")
            ),
            imageCount = 3
        ),
        com.example.onlinebidding.screens.products.LaptopAuctionData(
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
                com.example.onlinebidding.screens.products.BidInfo(1, "Neha Verma", "₹95,000", true),
                com.example.onlinebidding.screens.products.BidInfo(2, "Vikram Shah", "₹92,000")
            ),
            imageCount = 2
        ),
        com.example.onlinebidding.screens.products.LaptopAuctionData(
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
                com.example.onlinebidding.screens.products.BidInfo(1, "Arjun Mehta", "₹1,42,000", true)
            ),
            imageCount = 2
        )
    )
    
    val laptop = laptops.getOrElse(laptopIndex) { laptops[0] }
    val scrollState = rememberScrollState()
    
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
                Icon(Icons.Default.ArrowBack, "Back", tint = Color(0xFFFFC107))
            }
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    "Laptop Auction",
                    color = Color(0xFFFFC107),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    laptop.subtitle,
                    color = Color(0x99FFC107),
                    fontSize = 12.sp
                )
            }
        }
        
            // Image Carousel (No Play Button)
            val imageList = remember { listOf(laptop.imageRes, laptop.imageRes, laptop.imageRes) }
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
                            contentDescription = "${laptop.name} - Image ${index + 1}",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop // Crop maintains aspect ratio while filling
                        )
                        if (index == 0) {
                            // Verified Seller Badge on first image
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
                                    Icon(Icons.Default.CheckCircle, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Verified Seller", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                                    Icon(Icons.Default.Info, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(laptop.imageCount.toString(), color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Product Info
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    laptop.name,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFC107)),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            laptop.condition,
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("by ${laptop.sellerName}", color = Color.White, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${laptop.sellerRating} (${laptop.sales} sales)",
                        color = Color(0xFFFFC107),
                        fontSize = 12.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Specifications Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                SpecCard("Processor", laptop.processor, Color(0xFFFFC107), Icons.Default.Info)
                Spacer(modifier = Modifier.width(10.dp))
                SpecCard("Storage", laptop.storage, Color(0xFFFFC107), Icons.Default.Info)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                SpecCard("Display", laptop.display, Color(0xFF2196F3), Icons.Default.Info)
                Spacer(modifier = Modifier.width(10.dp))
                SpecCard("RAM", laptop.ram, Color(0xFF4CAF50), Icons.Default.Info)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Auction Timer - Always shows 0:30
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF8B0000)), // Dark red
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Auction Ends In", color = Color.White, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "0:30",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Bid fast to secure this laptop!", color = Color.White, fontSize = 12.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Bid Info Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                BidInfoCardFlow("Current Bid", laptop.currentBid, "Highest offer", true)
                Spacer(modifier = Modifier.width(10.dp))
                BidInfoCardFlow("Max Price", laptop.maxPrice, "Reserve limit", false)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Active Bidders with circles
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "${laptop.activeBidders} Active Bidders",
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
                                "${laptop.totalBids} bids",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    // Bidding circles
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Show 4 circles with numbers 1-4
                        for (i in 1..4) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFFFFC107), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    i.toString(),
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        val moreCount = (laptop.activeBidders - 4).coerceAtLeast(0)
                        Text(
                            "+ $moreCount more bidding",
                            color = Color(0xFFB0B0B0),
                            fontSize = 12.sp
                        )
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
                ActionCardFlow("Full Specs", Icons.Default.Info, "View details", onClick = { showSpecsDialog = true })
                Spacer(modifier = Modifier.width(10.dp))
                ActionCardFlow("Bid History", Icons.Default.List, "Live updates", badge = laptop.totalBids.toString(), onClick = onBidClick)
            }
            
            // Specifications Dialog
            if (showSpecsDialog) {
                SpecificationsDialog(
                    laptopIndex = laptopIndex,
                    deviceType = deviceType,
                    deviceIndex = laptopIndex,
                    onDismiss = { showSpecsDialog = false }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Condition Details with Quality Progress Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFFFFC107), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Info, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        }
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
                        laptop.conditionDetails,
                        color = Color(0xB3FFFFFF),
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    // Quality Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LinearProgressIndicator(
                            progress = laptop.qualityPercent / 100f,
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFFFFC107),
                            trackColor = Color(0xFF333333)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "${laptop.qualityPercent}% Quality",
                            color = Color(0xFFFFC107),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Latest Bids Section
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Latest Bids", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Live", color = Color(0xFFFFC107), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            laptop.latestBids.forEach { bid ->
                LatestBidRowFlow(bid)
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
        
        // Place Bid Button (Fixed at bottom)
        Button(
            onClick = onBidClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
            shape = RoundedCornerShape(50)
        ) {
            Text("₹ Place Bid on Laptop", color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SpecCard(title: String, value: String, iconColor: Color, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(0.48f),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, color = Color(0x99FFFFFF), fontSize = 11.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun BidInfoCardFlow(title: String, amount: String, subtitle: String, isCurrent: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(0.48f),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) Color(0xFF2A1F00) else Color(0xFF121212)
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = Color(0x99FFFFFF), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                amount,
                color = if (isCurrent) Color(0xFFFFC107) else Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, color = Color(0x99FFFFFF), fontSize = 11.sp)
        }
    }
}

@Composable
fun ActionCardFlow(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    subtitle: String,
    badge: String? = null,
    onClick: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxWidth(0.48f)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { onClick() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(icon, null, tint = Color(0xFFFFC107), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(subtitle, color = Color(0x99FFFFFF), fontSize = 11.sp)
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
                Text(badge, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun LatestBidRowFlow(bid: com.example.onlinebidding.screens.products.BidInfo) {
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
                Text(bid.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
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
            Text("Bidding ${bid.amount}", color = Color(0xFFFFC107), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// Specifications Dialog
@Composable
fun SpecificationsDialog(
    laptopIndex: Int = 0,
    deviceType: String = "laptop",
    deviceIndex: Int = 0,
    productName: String = "",
    onDismiss: () -> Unit
) {
    // Define specs based on device type and index, with special handling for mouse products
    val specsMap = when (deviceType) {
        "laptop" -> when (laptopIndex) {
            0 -> mapOf( // MacBook Pro 16" M3 Max
                "Processor" to "Apple M3 Max (16-core CPU)",
                "RAM" to "48GB Unified Memory",
                "Storage" to "1TB SSD",
                "Display" to "16.2\" Liquid Retina XDR",
                "Graphics" to "40-core GPU",
                "Battery" to "Up to 22 hours"
            )
            1 -> mapOf( // Dell XPS 15 OLED
                "Processor" to "Intel Core i7-13700H",
                "RAM" to "32GB DDR5",
                "Storage" to "1TB NVMe SSD",
                "Display" to "15.6\" 4K OLED Touch",
                "Graphics" to "NVIDIA RTX 4060",
                "Battery" to "Up to 10 hours"
            )
            2 -> mapOf( // ASUS ROG Zephyrus G16
                "Processor" to "AMD Ryzen 9 7940HS",
                "RAM" to "32GB DDR5",
                "Storage" to "2TB SSD",
                "Display" to "16\" QHD 240Hz",
                "Graphics" to "NVIDIA RTX 4080",
                "Battery" to "Up to 8 hours"
            )
            else -> mapOf()
        }
        "mobile" -> when (deviceIndex) {
            0 -> mapOf( // iPhone 15 Pro Max
                "Display" to "6.7\" Super Retina XDR",
                "Processor" to "A17 Pro Chip",
                "Camera" to "48MP Main + 12MP Ultra Wide",
                "Storage" to "512GB",
                "Battery" to "Up to 29 hours video",
                "5G" to "Yes"
            )
            1 -> mapOf( // Samsung Galaxy S24 Ultra
                "Display" to "6.8\" Dynamic AMOLED 2X",
                "Processor" to "Snapdragon 8 Gen 3",
                "Camera" to "200MP Main + AI Zoom",
                "Storage" to "256GB",
                "Battery" to "5000mAh",
                "S Pen" to "Included"
            )
            2 -> mapOf( // OnePlus 12 Pro
                "Display" to "6.82\" LTPO AMOLED",
                "Processor" to "Snapdragon 8 Gen 3",
                "Camera" to "Hasselblad 50MP Triple",
                "Storage" to "256GB",
                "Battery" to "5400mAh 100W Fast",
                "RAM" to "16GB"
            )
            else -> mapOf()
        }
        "computer" -> when (deviceIndex) {
            0 -> mapOf( // Custom Gaming PC RTX
                "CPU" to "Intel Core i9-14900K",
                "GPU" to "NVIDIA RTX 4090 24GB",
                "RAM" to "64GB DDR5 6000MHz",
                "Storage" to "2TB NVMe Gen 5 + 4TB HDD",
                "Cooling" to "Liquid Cooling AIO",
                "PSU" to "1000W 80+ Gold"
            )
            1 -> mapOf( // Mac Studio M2 Ultra
                "Chip" to "Apple M2 Ultra (24-core CPU)",
                "GPU" to "76-core GPU",
                "RAM" to "192GB Unified Memory",
                "Storage" to "4TB SSD",
                "Ports" to "Thunderbolt 4 x 6",
                "Display" to "Studio Display included"
            )
            2 -> mapOf( // HP Z8 G5 Workstation
                "CPU" to "Intel Xeon W9-3495X",
                "GPU" to "NVIDIA RTX A6000 48GB",
                "RAM" to "128GB ECC DDR5",
                "Storage" to "4TB NVMe RAID",
                "Certification" to "ISV Certified",
                "Warranty" to "3 Years"
            )
            else -> mapOf()
        }
        "monitor" -> {
            // Check if product is a mouse/Logitech product
            val normalizedName = productName.lowercase()
            if (normalizedName.contains("mouse", ignoreCase = true) || normalizedName.contains("logitech", ignoreCase = true)) {
                mapOf( // Logitech Mouse
                    "Type" to "Wireless Mouse",
                    "Buttons" to "6 Buttons",
                    "Connectivity" to "Wireless",
                    "DPI" to "Up to 16000 DPI",
                    "Battery" to "Rechargeable",
                    "Color" to "Black"
                )
            } else {
                when (deviceIndex) {
                    0 -> mapOf( // Samsung Odyssey G9
                        "Size" to "49 inch Curved",
                        "Resolution" to "5120x1440 DQHD",
                        "Refresh Rate" to "240Hz",
                        "Panel" to "Quantum Dot VA",
                        "HDR" to "HDR1000",
                        "Response" to "1ms GtG"
                    )
                    1 -> mapOf( // LG UltraFine 5K
                        "Size" to "27 inch",
                        "Resolution" to "5120x2880 5K",
                        "Color Gamut" to "P3 Wide Color",
                        "Brightness" to "500 nits",
                        "Ports" to "Thunderbolt 3",
                        "Built" to "Webcam + Speakers"
                    )
                    2 -> mapOf( // Dell UltraSharp U3423WE
                        "Size" to "34 inch Ultrawide",
                        "Resolution" to "3440x1440 WQHD",
                        "Color Gamut" to "99% sRGB",
                        "Brightness" to "400 nits",
                        "Ports" to "USB-C Hub 90W",
                        "Built" to "KVM Switch"
                    )
                    else -> mapOf()
                }
            }
        }
        "tablet" -> when (deviceIndex) {
            0 -> mapOf( // iPad Pro 12.9" M2
                "Display" to "12.9\" Liquid Retina XDR",
                "Chip" to "Apple M2",
                "Storage" to "512GB",
                "Camera" to "12MP Wide + 10MP Ultra",
                "Connectivity" to "5G + WiFi 6E",
                "Accessories" to "Keyboard + Pencil"
            )
            1 -> mapOf( // Samsung Galaxy Tab S9
                "Display" to "14.6\" Dynamic AMOLED 2X",
                "Processor" to "Snapdragon 8 Gen 2",
                "RAM" to "12GB",
                "Storage" to "256GB",
                "S Pen" to "Included",
                "Battery" to "11200mAh 45W Fast"
            )
            2 -> mapOf( // Microsoft Surface Pro 9
                "Display" to "13\" PixelSense",
                "Processor" to "Intel Core i7",
                "RAM" to "16GB",
                "Storage" to "512GB SSD",
                "Battery" to "Up to 15.5 hours",
                "Accessories" to "Surface Pen + Keyboard"
            )
            else -> mapOf()
        }
        else -> mapOf()
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Specifications",
                        color = Color(0xFFFFC107),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close", tint = Color(0xFFFFC107))
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                specsMap.forEach { (key, value) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0F0F)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(key, color = Color(0xFFB0B0B0), fontSize = 14.sp)
                            Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

// Bid Comments Screen with Countdown
@Composable
fun BidCommentsScreen(
    itemName: String,
    laptopIndex: Int = 0,
    deviceType: String = "laptop",
    initialTime: Int = 30,
    auctionId: Int? = null, // Backend auction ID
    onBack: () -> Unit,
    onAddBid: () -> Unit,
    onTimeUp: (winnerName: String, winnerBid: String) -> Unit
) {
    // Define bid data based on device type and index
    val initialBidsData = when (deviceType) {
        "tablet" -> when (laptopIndex) {
            0 -> listOf( // iPad Pro 12.9" M2
                BidEntry("Anjali Sharma", "₹85,000", true, false),
                BidEntry("Rahul Mehta", "₹82,000", false, false),
                BidEntry("Priya Patel", "₹80,000", false, false),
                BidEntry("Vikram Singh", "₹78,000", false, false)
            )
            1 -> listOf( // Samsung Galaxy Tab S9
                BidEntry("Vikram Reddy", "₹72,000", true, false),
                BidEntry("Sneha Iyer", "₹70,000", false, false),
                BidEntry("Arjun Kumar", "₹68,000", false, false),
                BidEntry("Meera Joshi", "₹65,000", false, false)
            )
            2 -> listOf( // Microsoft Surface Pro 9
                BidEntry("Neha Patel", "₹58,000", true, false),
                BidEntry("Karan Shah", "₹56,000", false, false),
                BidEntry("Anita Desai", "₹54,000", false, false)
            )
            else -> listOf(
                BidEntry("Anjali Sharma", "₹85,000", true, false),
                BidEntry("Rahul Mehta", "₹82,000", false, false)
            )
        }
        "mobile" -> when (laptopIndex) {
            0 -> listOf( // iPhone 15 Pro Max
                BidEntry("Raj Kumar", "₹1,28,000", true, false),
                BidEntry("Priya Singh", "₹1,25,000", false, false),
                BidEntry("Amit Patel", "₹1,22,000", false, false)
            )
            1 -> listOf( // Samsung Galaxy S24 Ultra
                BidEntry("Karan Joshi", "₹98,000", true, false),
                BidEntry("Sneha Iyer", "₹95,000", false, false),
                BidEntry("Vikram Shah", "₹92,000", false, false)
            )
            2 -> listOf( // OnePlus 12 Pro
                BidEntry("Pooja Iyer", "₹54,000", true, false),
                BidEntry("Ravi Singh", "₹52,000", false, false),
                BidEntry("Kiran Patel", "₹50,000", false, false)
            )
            else -> listOf(
                BidEntry("Raj Kumar", "₹1,28,000", true, false),
                BidEntry("Priya Singh", "₹1,25,000", false, false)
            )
        }
        "computer" -> when (laptopIndex) {
            0 -> listOf( // Custom Gaming PC RTX
                BidEntry("Rahul Singh", "₹2,85,000", true, false),
                BidEntry("Anjali Mehta", "₹2,80,000", false, false),
                BidEntry("Vikram Patel", "₹2,75,000", false, false)
            )
            1 -> listOf( // Mac Studio M2 Ultra
                BidEntry("Priya Sharma", "₹3,10,000", true, false),
                BidEntry("Arjun Kumar", "₹3,05,000", false, false),
                BidEntry("Neha Joshi", "₹3,00,000", false, false)
            )
            2 -> listOf( // HP Z8 G5 Workstation
                BidEntry("Suresh Kumar", "₹1,95,000", true, false),
                BidEntry("Meera Patel", "₹1,90,000", false, false),
                BidEntry("Kiran Shah", "₹1,85,000", false, false)
            )
            else -> listOf(
                BidEntry("Rahul Singh", "₹2,85,000", true, false),
                BidEntry("Anjali Mehta", "₹2,80,000", false, false)
            )
        }
        "monitor" -> when (laptopIndex) {
            0 -> listOf( // Samsung Odyssey G9
                BidEntry("Rahul Singh", "₹95,000", true, false),
                BidEntry("Anjali Mehta", "₹92,000", false, false),
                BidEntry("Vikram Patel", "₹90,000", false, false)
            )
            1 -> listOf( // LG UltraFine 5K
                BidEntry("Meera Patel", "₹68,000", true, false),
                BidEntry("Suresh Kumar", "₹65,000", false, false),
                BidEntry("Priya Sharma", "₹62,000", false, false)
            )
            2 -> listOf( // Dell UltraSharp
                BidEntry("Suresh Kumar", "₹52,000", true, false),
                BidEntry("Neha Joshi", "₹50,000", false, false),
                BidEntry("Arjun Kumar", "₹48,000", false, false)
            )
            else -> listOf(
                BidEntry("Rahul Singh", "₹95,000", true, false),
                BidEntry("Anjali Mehta", "₹92,000", false, false)
            )
        }
        else -> when (laptopIndex) { // Laptop
            0 -> listOf( // MacBook Pro 16" M3 Max
                BidEntry("Amit Patel", "₹2,00,000", true, false),
                BidEntry("Priya Singh", "₹1,95,000", false, false),
                BidEntry("Raj Kumar", "₹1,90,000", false, false)
            )
            1 -> listOf( // Dell XPS 15 OLED
                BidEntry("Neha Verma", "₹95,000", true, false),
                BidEntry("Vikram Shah", "₹92,000", false, false),
                BidEntry("Suresh Kumar", "₹90,000", false, false)
            )
            2 -> listOf( // ASUS ROG Zephyrus G16
                BidEntry("Arjun Mehta", "₹1,50,000", true, false),
                BidEntry("Ravi Singh", "₹1,48,000", false, false),
                BidEntry("Kiran Patel", "₹1,45,000", false, false)
            )
            else -> listOf(
                BidEntry("Amit Patel", "₹2,00,000", true, false),
                BidEntry("Priya Singh", "₹1,95,000", false, false),
                BidEntry("Raj Kumar", "₹1,90,000", false, false)
            )
        }
    }
    
    // Helper function to parse amount string to numeric value
    fun parseAmount(amountStr: String): Long {
        return amountStr.replace("₹", "").replace(",", "").replace(" ", "").toLongOrNull() ?: 0L
    }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Timer always starts at 30 seconds
    var timeLeft by remember { mutableStateOf(30) }
    
    // Sort initial bids by amount (highest first) and mark only first as top bid
    val sortedInitialBids = initialBidsData.sortedByDescending { parseAmount(it.amount) }
        .mapIndexed { index, bid -> bid.copy(isTopBid = index == 0) }
    
    var bids by remember { mutableStateOf(sortedInitialBids) }
    var isLoading by remember { mutableStateOf(auctionId != null) }
    var showBidDialog by remember { mutableStateOf(false) }
    var timerPaused by remember { mutableStateOf(false) }
    var timerResetKey by remember { mutableStateOf(0) } // Key to restart timer
    var currentAuctionId by remember { mutableStateOf(auctionId) }
    var currentBidPrice by remember { mutableStateOf(0.0) }
    
    // Load bids from API if auctionId is provided
    LaunchedEffect(auctionId) {
        android.util.Log.d("BidComments", "🔍 LaunchedEffect triggered - auctionId: $auctionId, currentAuctionId: $currentAuctionId")
        if (auctionId != null && auctionId > 0) {
            currentAuctionId = auctionId
            isLoading = true
            try {
                android.util.Log.d("BidComments", "🔌 Loading bids for auction ID: $auctionId")
                val response = RetrofitInstance.api.auctionDetails(auctionId)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val auctionData = response.body()!!
                    val apiBids = auctionData.bids
                    currentBidPrice = auctionData.auction?.current_price ?: 0.0
                    
                    // Convert API bids to BidEntry format
                    val convertedBids = apiBids.map { bidDto ->
                        val amountStr = "₹${String.format("%,.0f", bidDto.amount)}"
                        BidEntry(
                            name = bidDto.user_name ?: "Unknown",
                            amount = amountStr,
                            isTopBid = false, // Will be sorted below
                            isYou = bidDto.user_id == 25 // TODO: Get from logged in user
                        )
                    }
                    
                    // Sort by amount (highest first) and mark top bid
                    val sortedBids = convertedBids.sortedByDescending { parseAmount(it.amount) }
                        .mapIndexed { index, bid -> bid.copy(isTopBid = index == 0) }
                    
                    bids = sortedBids
                    android.util.Log.d("BidComments", "✅ Loaded ${sortedBids.size} bids from API")
                } else {
                    android.util.Log.w("BidComments", "⚠️ API error, using fallback data")
                    // Keep using fallback data
                }
            } catch (e: Exception) {
                android.util.Log.e("BidComments", "❌ Error loading bids: ${e.message}", e)
                // Keep using fallback data
            } finally {
                isLoading = false
            }
        }
    }
    
    LaunchedEffect(timerResetKey) {
        timeLeft = 30
        while (timeLeft > 0) {
            if (!timerPaused) {
                delay(1000)
                timeLeft--
            } else {
                delay(100)
            }
        }
        if (timeLeft == 0) {
            // Get the winner from current bids
            val topBid = bids.firstOrNull() ?: initialBidsData.firstOrNull()
            val winnerName = topBid?.name ?: initialBidsData.firstOrNull()?.name ?: "Unknown"
            val winnerBid = topBid?.amount ?: initialBidsData.firstOrNull()?.amount ?: "₹0"
            onTimeUp(winnerName, winnerBid)
        }
    }
    
    fun addUserBid(name: String, amount: String) {
        // Format the amount with currency symbol and commas
        val amountNum = amount.toLongOrNull() ?: 0L
        
        android.util.Log.d("BidComments", "🔍 addUserBid called - name: $name, amount: $amount, currentAuctionId: $currentAuctionId, auctionId: $auctionId")
        
        // If we have an auction ID, save bid to backend
        if (currentAuctionId != null && currentAuctionId!! > 0) {
            scope.launch {
                try {
                    val bidAmount = amountNum.toDouble()
                    android.util.Log.d("BidComments", "💰 Placing bid: ₹$bidAmount for auction $currentAuctionId")
                    
                    val response = RetrofitInstance.api.placeBid(
                        com.example.onlinebidding.api.PlaceBidRequest(
                            auction_id = currentAuctionId!!,
                            amount = bidAmount,
                            user_id = 25 // TODO: Get from logged in user session
                        )
                    )
                    
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody?.success == true) {
                            android.util.Log.d("BidComments", "✅ Bid placed successfully! Bid ID: ${responseBody.current_price}")
                            
                            // Reload bids from API to get updated list
                            val apiResponse = RetrofitInstance.api.auctionDetails(currentAuctionId!!)
                            if (apiResponse.isSuccessful && apiResponse.body()?.success == true) {
                                val apiBids = apiResponse.body()!!.bids
                                currentBidPrice = apiResponse.body()!!.auction?.current_price ?: 0.0
                                
                                // Convert API bids to BidEntry format
                                val convertedBids = apiBids.map { bidDto ->
                                    val amountStr = "₹${String.format("%,.0f", bidDto.amount)}"
                                    BidEntry(
                                        name = bidDto.user_name ?: "Unknown",
                                        amount = amountStr,
                                        isTopBid = false,
                                        isYou = bidDto.user_id == 25
                                    )
                                }
                                
                                // Sort and mark top bid
                                val sortedBids = convertedBids.sortedByDescending { parseAmount(it.amount) }
                                    .mapIndexed { index, bid -> bid.copy(isTopBid = index == 0) }
                                
                                bids = sortedBids
                                
                                Toast.makeText(context, "Bid placed successfully!", Toast.LENGTH_SHORT).show()
                            } else {
                                android.util.Log.w("BidComments", "⚠️ Bid placed but failed to reload bids")
                                Toast.makeText(context, "Bid placed! Refreshing...", Toast.LENGTH_SHORT).show()
                            }
                            
                            showBidDialog = false
                            timerPaused = false
                            timerResetKey++
                        } else {
                            // API returned success=false
                            val error = responseBody?.error ?: "Failed to place bid"
                            android.util.Log.e("BidComments", "❌ Bid rejected by API: $error")
                            android.util.Log.e("BidComments", "   Response code: ${response.code()}")
                            Toast.makeText(context, "Bid rejected: $error", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        // HTTP error (4xx, 5xx)
                        val errorBody = response.errorBody()?.string()
                        val error = try {
                            response.body()?.error ?: errorBody ?: "HTTP ${response.code()}: Failed to place bid"
                        } catch (e: Exception) {
                            "HTTP ${response.code()}: Failed to place bid"
                        }
                        android.util.Log.e("BidComments", "❌ HTTP Error ${response.code()}: $error")
                        Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    android.util.Log.e("BidComments", "❌ Network error: ${e.message}", e)
                    Toast.makeText(context, "Network error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            // Fallback: Add bid locally (for testing without auction ID)
            val formattedAmount = "₹${String.format("%,d", amountNum)}"
            val newUserBid = BidEntry(name, formattedAmount, false, true)
            val allBids = bids + newUserBid
            
            // Sort bids by amount in descending order (highest first)
            val sortedBids = allBids.sortedByDescending { parseAmount(it.amount) }
            
            // Mark only the first (highest) bid as top bid
            bids = sortedBids.mapIndexed { index, bid ->
                bid.copy(
                    isTopBid = index == 0,
                    isYou = bid.name.equals(name, ignoreCase = true)
                )
            }
            
            showBidDialog = false
            timerPaused = false
            // Reset timer to 30 seconds when a new bid is added
            timerResetKey++
        }
    }
    
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back", tint = Color(0xFFFFC107))
            }
            Text(
                "Bid Comments",
                color = Color(0xFFFFC107),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        // Timer
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1F00)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Time Left", color = Color(0xFFFFC107), fontSize = 14.sp)
                Text(
                    "0:${String.format("%02d", timeLeft)}",
                    color = Color(0xFFFFC107),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        // Bids List
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            bids.forEach { bid ->
                BidEntryCard(bid)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        
        // Add Bid Button
        Button(
            onClick = {
                timerPaused = true
                showBidDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Add Bid", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        
        // Bid Entry Dialog
        if (showBidDialog) {
            BidEntryDialog(
                onDismiss = { 
                    showBidDialog = false
                    timerPaused = false
                },
                onConfirm = { name, amount ->
                    addUserBid(name, amount)
                    onAddBid()
                }
            )
        }
    }
}

// Bid Entry Dialog
@Composable
fun BidEntryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var bidderName by remember { mutableStateOf("") }
    var bidAmount by remember { mutableStateOf("") }
    
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
                        "Enter Bid Details",
                        color = Color(0xFFFFC107),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close", tint = Color(0xFFFFC107))
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Bidder Name Field
                Text("Bidder Name", color = Color(0xFFFFC107), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = bidderName,
                    onValueChange = { bidderName = it },
                    placeholder = { Text("Enter your name", color = Color.Gray) },
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
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
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
                    prefix = { Text("₹", color = Color(0xFFFFC107)) }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Confirm Button
                Button(
                    onClick = {
                        if (bidderName.isNotBlank() && bidAmount.isNotBlank()) {
                            onConfirm(bidderName, bidAmount)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    shape = RoundedCornerShape(25.dp),
                    enabled = bidderName.isNotBlank() && bidAmount.isNotBlank()
                ) {
                    Text("Submit Bid", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

data class BidEntry(
    val name: String,
    val amount: String,
    val isTopBid: Boolean,
    val isYou: Boolean
)

@Composable
private fun BidEntryCard(bid: BidEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (bid.isTopBid) Color(0xFF2A1F00) else Color(0xFF121212)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (bid.isTopBid) Color(0xFFFFC107) else Color(0xFF3A2A00),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    bid.name.first().toString(),
                    color = if (bid.isTopBid) Color.Black else Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        bid.name,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (bid.isTopBid) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFF9800)),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                "Top Bid",
                                color = Color.Black,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (bid.isYou) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                "You",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Bidding ${bid.amount}",
                    color = Color(0xFFB0B0B0),
                    fontSize = 14.sp
                )
            }
        }
    }
}

// UPI Entry Screen
@Composable
fun UPIEntryScreen(
    paymentMethod: String,
    amount: Int,
    onBack: () -> Unit,
    onProceed: (String) -> Unit,
    auctionId: Int? = null,
    userId: Int = 25, // TODO: Get from logged in user session
    saveToDatabase: Boolean = false // Option to save payment to database
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var upiId by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    
    // UPI ID validation function
    fun isValidUPI(upi: String): Boolean {
        val upiPattern = Regex("^[a-zA-Z0-9._-]+@[a-zA-Z0-9]+$")
        return upiPattern.matches(upi.trim())
    }
    
    // Save payment to database
    fun savePayment(upi: String) {
        if (!saveToDatabase) {
            onProceed(upi)
            return
        }
        
        isProcessing = true
        scope.launch {
            var paymentSuccess = false
            var paymentError: String? = null
            
            try {
                val response = RetrofitInstance.api.createPayment(
                    com.example.onlinebidding.api.CreatePaymentRequest(
                        user_id = userId,
                        auction_id = auctionId,
                        amount = amount.toDouble(),
                        payment_method = paymentMethod,
                        upi_id = upi
                    )
                )
                
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.success == true) {
                        android.util.Log.d("UPIEntry", "✅ Payment saved: ${responseBody.transaction_id}")
                        paymentSuccess = true
                        errorMessage = null
                    } else {
                        val error = responseBody?.error ?: "Failed to process payment"
                        android.util.Log.e("UPIEntry", "❌ Payment failed: $error")
                        android.util.Log.e("UPIEntry", "   Response code: ${response.code()}")
                        paymentError = error
                        errorMessage = error
                    }
                } else {
                    // HTTP error (4xx, 5xx)
                    val errorBody = response.errorBody()?.string()
                    val error = try {
                        response.body()?.error ?: errorBody ?: "HTTP ${response.code()}: Failed to process payment"
                    } catch (e: Exception) {
                        "HTTP ${response.code()}: Failed to process payment"
                    }
                    android.util.Log.e("UPIEntry", "❌ HTTP Error ${response.code()}: $error")
                    paymentError = error
                    errorMessage = error
                }
            } catch (e: Exception) {
                android.util.Log.e("UPIEntry", "❌ Network error: ${e.message}", e)
                paymentError = "Network error: ${e.message}"
                errorMessage = paymentError
            } finally {
                isProcessing = false
                
                // Navigate to success screen regardless of payment result
                // (Payment will be logged, but user can proceed)
                if (paymentSuccess) {
                    android.widget.Toast.makeText(
                        context,
                        "Payment processed successfully!",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Show warning but still proceed
                    android.util.Log.w("UPIEntry", "⚠️ Payment API failed, but proceeding to success screen")
                    if (paymentError != null) {
                        android.widget.Toast.makeText(
                            context,
                            "Payment note: $paymentError (proceeding anyway)",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                
                // Always navigate to success screen
                onProceed(upi)
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp)
    ) {
        // Circular back button with border
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .border(1.dp, Color(0xFFFFC107), CircleShape)
        ) {
            Icon(
                Icons.Default.ArrowBack,
                "Back",
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            "Enter UPI ID",
            color = Color(0xFFFFC107),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            "Pay via ${if (paymentMethod == "phonepe") "PhonePe" else if (paymentMethod == "gpay") "Google Pay" else paymentMethod.replaceFirstChar { it.uppercaseChar() }}",
            color = Color(0xFFB0B0B0),
            fontSize = 14.sp
        )
        
        if (amount > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Amount: ₹${String.format("%,d", amount)}",
                color = Color(0xFFFFC107),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text("UPI ID", color = Color(0xFFFFC107), fontSize = 14.sp, fontWeight = FontWeight.Medium)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = upiId,
            onValueChange = { 
                upiId = it
                errorMessage = null // Clear error when user types
            },
            placeholder = { Text("yourname@upi", color = Color.Gray) },
            leadingIcon = {
                Icon(Icons.Default.AccountCircle, null, tint = Color(0xFFFFC107))
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (errorMessage != null) Color.Red else Color(0xFFFFC107),
                unfocusedBorderColor = if (errorMessage != null) Color.Red else Color(0x33FFC107),
                focusedContainerColor = Color(0xFF1A1A1A),
                unfocusedContainerColor = Color(0xFF1A1A1A),
                cursorColor = Color(0xFFFFC107),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            isError = errorMessage != null
        )
        
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                errorMessage!!,
                color = Color.Red,
                fontSize = 12.sp
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // UPI ID format hint
        Text(
            "Format: yourname@upi (e.g., john@paytm, jane@ybl)",
            color = Color(0xFF666666),
            fontSize = 12.sp
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = { 
                val trimmedUpi = upiId.trim()
                if (trimmedUpi.isBlank()) {
                    errorMessage = "Please enter your UPI ID"
                } else if (!isValidUPI(trimmedUpi)) {
                    errorMessage = "Invalid UPI ID format. Use: yourname@upi"
                } else {
                    errorMessage = null
                    savePayment(trimmedUpi)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = upiId.isNotBlank() && !isProcessing,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
            shape = RoundedCornerShape(40.dp)
        ) {
            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text("Proceed to Pay", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Auction Winner Screen
@Composable
fun AuctionWinnerScreen(
    itemName: String,
    winnerName: String,
    winningBid: String,
    onProceedToPayment: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // Trophy Icon (using emoji for trophy cup)
            Text(
                "🏆",
                fontSize = 80.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "You Won!",
                color = Color(0xFFFF9800),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                "Congratulations! You won the auction for",
                color = Color.White,
                fontSize = 16.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                itemName,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Winning Bid Card with orange outline
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(2.dp, Color(0xFFFF9800))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Winning Bid",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        winningBid,
                        color = Color(0xFFFF9800),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Button(
                onClick = onProceedToPayment,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(40.dp)
            ) {
                Text(
                    "Proceed to Payment",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Payment Success with Logout
@Composable
fun PaymentSuccessLogoutScreen(
    onLogout: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    listOf(Color(0x33FFC107), Color.Black)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0x33FFC107), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(60.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "Payment Successful!",
                color = Color(0xFFFFC107),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                "Congratulations! Your payment has been confirmed.",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Logout", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, null, tint = Color.White)
                }
            }
        }
    }
}
