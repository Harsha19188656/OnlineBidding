package com.example.onlinebidding.screens.flash

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

data class FlashItem(
    val title: String,
    val category: String,
    val price: String,
    val bidders: Int,
    val bids: Int,
    val timeLeft: String,
    val condition: String
)

/* ---------------- SCREEN ---------------- */

@Composable
fun FlashAuctions(
    onBack: () -> Unit
) {
    val items = listOf(
        FlashItem("iPhone 15 Pro Max 512GB", "Mobile", "₹1,28,000", 32, 14, "0m 22s", "Excellent"),
        FlashItem("Workstation HP Z8 G5", "Computer", "₹1,95,000", 14, 4, "0m 23s", "Very Good"),
        FlashItem("Microsoft Surface Pro 9", "Tablet", "₹58,000", 16, 7, "1m 10s", "Good"),
        FlashItem("MacBook Pro 16 M3 Max", "Laptop", "₹1,85,000", 28, 12, "2m 40s", "Excellent"),
        FlashItem("Asus ROG Zephyrus G14", "Laptop", "₹1,42,000", 21, 9, "3m 12s", "Very Good"),
        FlashItem("Samsung Odyssey G9", "Monitor", "₹72,000", 19, 6, "4m 30s", "Good"),
        FlashItem("iMac 24\" M1", "Computer", "₹1,10,000", 11, 3, "5m 05s", "Good"),
        FlashItem("Lenovo Legion 7i", "Laptop", "₹1,55,000", 26, 10, "6m 48s", "Excellent"),
        FlashItem("iPad Air M1", "Tablet", "₹45,000", 18, 8, "8m 10s", "Very Good"),
        FlashItem("Dell UltraSharp U2723QE", "Monitor", "₹65,000", 13, 5, "9m 55s", "Excellent")
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
                    "⚡ Flash Auctions",
                    color = Color(0xFFFFD700),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Ending soon – Act fast!",
                    color = Color(0x99FFD700),
                    fontSize = 12.sp
                )
            }
        }

        /* ---------------- ALERT ---------------- */

        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF3A0A0A), Color(0xFF120000))
                    ),
                    RoundedCornerShape(18.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "⏰ Limited time auctions",
                    color = Color(0xFFFFC107),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "LIVE",
                    color = Color.Black,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(Color.Red, CircleShape)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        /* ---------------- LIST (SCROLLABLE) ---------------- */

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { item ->
                FlashCard(item)
            }
        }
    }
}

/* ---------------- CARD ---------------- */

@Composable
private fun FlashCard(item: FlashItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF2A0A0A), Color.Black)
                ),
                RoundedCornerShape(22.dp)
            )
            .padding(16.dp)
    ) {
        Column {

            Text(
                item.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Text(
                item.category,
                color = Color(0x99FFFFFF),
                fontSize = 12.sp
            )

            Spacer(Modifier.height(10.dp))

            Text("Current Bid", color = Color(0x99FFD700), fontSize = 12.sp)

            Text(
                item.price,
                color = Color(0xFFFFD700),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SmallInfo("⏱ ${item.timeLeft}")
                SmallInfo("👥 ${item.bidders}")
                SmallInfo("📈 ${item.bids} bids")
            }

            Spacer(Modifier.height(10.dp))
            ConditionBadge(item.condition)
        }
    }
}

/* ---------------- SMALL UI ---------------- */

@Composable
private fun SmallInfo(text: String) {
    Text(text, color = Color(0x99FFD700), fontSize = 12.sp)
}

@Composable
private fun ConditionBadge(condition: String) {
    val color = when (condition) {
        "Excellent" -> Color(0xFF22C55E)
        "Very Good" -> Color(0xFF3B82F6)
        "Good" -> Color(0xFFFACC15)
        else -> Color(0xFFFB923C)
    }

    Row(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(6.dp))
        Text(condition, color = color, fontSize = 12.sp)
    }
}
