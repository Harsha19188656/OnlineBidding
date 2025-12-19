package com.example.onlinebidding.screens.trending

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ---------------- DATA MODEL ---------------- */

data class TrendingItem(
    val rank: Int,
    val title: String,
    val category: String,
    val currentBid: String,
    val bidders: Int,
    val bids: Int,
    val condition: String,
    val gradient: Brush
)

/* ---------------- MAIN SCREEN ---------------- */

@Composable
fun TrendingAuctions(
    onBack: () -> Unit
) {

    // ✅ MORE DEVICES ADDED
    val items = listOf(
        TrendingItem(1, "iPhone 15 Pro Max 512GB", "Mobile", "₹1,28,000", 32, 14, "Excellent",
            Brush.linearGradient(listOf(Color(0xFF3A1C71), Color.Black))
        ),
        TrendingItem(2, "MacBook Pro 16\" M3 Max", "Laptop", "₹1,85,000", 28, 12, "Excellent",
            Brush.linearGradient(listOf(Color(0xFF0F2027), Color.Black))
        ),
        TrendingItem(3, "iPad Pro 12.9\" M2", "Tablet", "₹85,000", 26, 11, "Excellent",
            Brush.linearGradient(listOf(Color(0xFF42275A), Color.Black))
        ),
        TrendingItem(4, "Samsung Galaxy S24 Ultra", "Mobile", "₹1,10,000", 41, 19, "Very Good",
            Brush.linearGradient(listOf(Color(0xFF232526), Color.Black))
        ),
        TrendingItem(5, "Dell XPS 15 OLED", "Laptop", "₹1,45,000", 22, 9, "Excellent",
            Brush.linearGradient(listOf(Color(0xFF141E30), Color.Black))
        ),
        TrendingItem(6, "iMac 24\" M1", "Computer", "₹1,20,000", 18, 7, "Good",
            Brush.linearGradient(listOf(Color(0xFF000428), Color.Black))
        ),
        TrendingItem(7, "Lenovo Legion 7", "Laptop", "₹1,60,000", 35, 16, "Excellent",
            Brush.linearGradient(listOf(Color(0xFF1F1C2C), Color.Black))
        ),
        TrendingItem(8, "OnePlus Open Fold", "Mobile", "₹1,05,000", 29, 13, "Very Good",
            Brush.linearGradient(listOf(Color(0xFF232526), Color.Black))
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        /* ---------------- HEADER ---------------- */

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFFFFC107)
                )
            }

            Spacer(Modifier.width(8.dp))

            Column {
                Text(
                    "Trending Auctions",
                    color = Color(0xFFFFD700),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Most popular auctions right now",
                    color = Color(0x99FFD700),
                    fontSize = 12.sp
                )
            }
        }

        /* ---------------- HOT STATS ---------------- */

        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF3A2600), Color(0xFF120C00))
                    ),
                    RoundedCornerShape(18.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Stat("12", "Trending")
                Stat("103", "Total Bids")
                Stat("259", "Bidders")
            }
        }

        Spacer(Modifier.height(16.dp))

        /* ---------------- SCROLLABLE LIST ---------------- */

        LazyColumn(
            modifier = Modifier.fillMaxSize(), // ✅ ensures scrolling
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { item ->
                TrendingCard(item)
            }
        }
    }
}

/* ---------------- CARD ---------------- */

@Composable
private fun TrendingCard(item: TrendingItem) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(item.gradient, RoundedCornerShape(22.dp))
            .padding(16.dp)
    ) {
        Column {

            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .background(Color(0xFFFFC107), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("#${item.rank}", color = Color.Black, fontSize = 12.sp)
            }

            Spacer(Modifier.height(8.dp))

            Text(item.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(item.category, color = Color(0x99FFFFFF), fontSize = 12.sp)

            Spacer(Modifier.height(12.dp))

            Text("Current Bid", color = Color(0x99FFD700), fontSize = 12.sp)
            Text(item.currentBid, color = Color(0xFFFFD700), fontSize = 18.sp, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Info("${item.bidders}", "👥")
                Info("${item.bids} bids", "📈")
                Info("0m left", "⏱")
            }

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(0x3322C55E), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22C55E))
                )
                Spacer(Modifier.width(6.dp))
                Text(item.condition, color = Color(0xFF22C55E), fontSize = 12.sp)
            }
        }
    }
}

/* ---------------- SMALL UI ---------------- */

@Composable
private fun Stat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color(0xFFFFC107), fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Color(0x99FFD700), fontSize = 12.sp)
    }
}

@Composable
private fun Info(text: String, icon: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(icon, fontSize = 12.sp)
        Spacer(Modifier.width(4.dp))
        Text(text, color = Color(0x99FFD700), fontSize = 12.sp)
    }
}
