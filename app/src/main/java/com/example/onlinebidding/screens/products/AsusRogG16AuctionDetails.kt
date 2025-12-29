package com.example.onlinebidding.screens.products

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun AsusRogG16AuctionDetails(
    onBack: () -> Unit = {}
) {
    val background = Brush.verticalGradient(
        listOf(Color(0xFF0D0D0D), Color.Black)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 90.dp)
        ) {

            /* ---------------- TOP BAR ---------------- */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFFFFC107)
                    )
                }
                Text(
                    text = "Auction Details",
                    color = Color(0xFFFFC107),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            /* ---------------- VERIFIED BADGE ---------------- */
            Row(modifier = Modifier.padding(start = 16.dp)) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFFF9800), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Verified",
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            /* ---------------- IMAGE ---------------- */
            Image(
                painter = painterResource(id = R.drawable.ic_asus_rog),
                contentDescription = "ASUS ROG Zephyrus G16",
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .height(220.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(16.dp))

            /* ---------------- TITLE ---------------- */
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "ASUS ROG Zephyrus G16",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Excellent • by GamerZone • Laptop",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            /* ---------------- TIMER ---------------- */
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2B0F0F)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Time Remaining", color = Color(0xFFFF8A80))
                    Text(
                        "0:27",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------------- BID INFO ---------------- */
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoBox(
                    modifier = Modifier.weight(1f),
                    label = "Current Bid",
                    value = "₹1,42,000"
                )
                InfoBox(
                    modifier = Modifier.weight(1f),
                    label = "Max Price",
                    value = "₹1,90,000"
                )
            }

            Spacer(Modifier.height(16.dp))

            /* ---------------- ACTIVE BIDDERS ---------------- */
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("15 Active Bidders", color = Color.White)
                        Text(
                            "Open to unlimited participants",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    Text("6 bids", color = Color.Gray)
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------------- ACTION BUTTONS ---------------- */
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton("Specs", Icons.Default.Info)
                ActionButton("Bids", Icons.Default.List, badge = "6")
            }

            Spacer(Modifier.height(16.dp))

            /* ---------------- CONDITION ---------------- */
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Condition Details",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Used for 6 months. Minor scratches on base. Perfect working condition.",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------------- LATEST BIDS ---------------- */
            Text(
                "Latest Bids",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            BidRow("Neha Verma", "₹1,42,000", true)
            BidRow("Vikram Shah", "₹1,40,000")
            BidRow("Amit Patel", "₹1,38,000")

            TextButton(
                onClick = {},
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("View All Bids (6)", color = Color(0xFFFFC107))
            }
        }

        /* ---------------- PLACE BID ---------------- */
        Button(
            onClick = {},
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                "₹  Place Bid",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/* ================= REUSABLE COMPONENTS ================= */

@Composable
private fun InfoBox(
    modifier: Modifier,
    label: String,
    value: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(label, color = Color.Gray, fontSize = 12.sp)
            Text(value, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badge: String? = null
) {
    Box {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .width(80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(icon, null, tint = Color(0xFFFFC107))
                Text(label, color = Color.White, fontSize = 12.sp)
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
                Text(badge, color = Color.White, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun BidRow(
    name: String,
    amount: String,
    top: Boolean = false
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                if (top) "$name • Top Bid" else name,
                color = Color.White
            )
            Text(amount, color = Color(0xFFFFC107))
        }
    }
}
