package com.example.onlinebidding.screens.search

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ---------------- DATA ---------------- */

data class SearchProduct(
    val name: String,
    val category: String,
    val bid: Int,
    val condition: String,
    val bids: Int,
    val timeLeft: String
)

/* ---------------- MORE LAPTOPS ---------------- */

private val products = listOf(
    SearchProduct("MacBook Pro 16\"", "Laptop", 185000, "Excellent", 12, "0m left"),
    SearchProduct("Dell XPS 15 OLED", "Laptop", 95000, "Very Good", 8, "0m left"),
    SearchProduct("ASUS ROG Zephyrus", "Laptop", 142000, "Excellent", 6, "0m left"),
    SearchProduct("HP Spectre x360", "Laptop", 118000, "Very Good", 9, "1m left"),
    SearchProduct("Lenovo Legion 5 Pro", "Laptop", 135000, "Excellent", 11, "2m left"),
    SearchProduct("Acer Predator Helios", "Laptop", 129000, "Good", 7, "3m left"),
    SearchProduct("MSI Stealth 15M", "Laptop", 110000, "Very Good", 5, "4m left")
)

private fun formatPrice(value: Int) = "₹%,d".format(value)

/* ---------------- SCREEN ---------------- */

@Composable
fun Search(
    onBack: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0B0B0B), Color.Black)
                )
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            /* ---------- HEADER ---------- */
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.ArrowBack,
                    null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onBack() }
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Search Auctions",
                    color = Color(0xFFFFE082),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(14.dp))

            /* ---------- SEARCH BAR ---------- */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(Color(0xFF151515), RoundedCornerShape(14.dp))
                    .border(1.dp, Color(0x33FFC107), RoundedCornerShape(14.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(12.dp))
                Icon(Icons.Default.Search, null, tint = Color(0x66FFC107))
                Spacer(Modifier.width(8.dp))
                TextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = {
                        Text(
                            "Search products, brands, categories...",
                            color = Color(0x66FFC107),
                            fontSize = 14.sp
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(10.dp))

            /* ---------- RESULT COUNT + FILTER ---------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("15 results found", color = Color(0x99FFC107), fontSize = 13.sp)

                Box(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0x33FFC107), Color(0x22111111))
                            ),
                            RoundedCornerShape(20.dp)
                        )
                        .border(1.dp, Color(0x55FFC107), RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text("Filter", color = Color(0xFFFFE082), fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(12.dp))

            /* ---------- TRENDING ---------- */
            Text(
                "Trending Searches",
                color = Color(0xFFFFC107),
                fontSize = 13.sp
            )

            Spacer(Modifier.height(6.dp))

            FlowRow {
                listOf("MacBook Pro", "iPhone 15", "Gaming PC", "iPad Pro").forEach {
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp, bottom = 8.dp)
                            .background(Color(0xFF151515), RoundedCornerShape(20.dp))
                            .border(1.dp, Color(0x33FFC107), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(it, color = Color(0xFFFFE082), fontSize = 12.sp)
                    }
                }
            }

            /* ✅ NO GAP HERE ANYMORE */

            Spacer(Modifier.height(8.dp))

            /* ---------- PRODUCT LIST ---------- */
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(products) { product ->
                    ProductCard(product)
                }
            }
        }
    }
}

/* ---------------- PRODUCT CARD ---------------- */

@Composable
private fun ProductCard(product: SearchProduct) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFF121212), RoundedCornerShape(18.dp))
            .border(1.dp, Color(0x33FFC107), RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Row {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(Color.Black, RoundedCornerShape(12.dp))
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    product.name,
                    color = Color(0xFFFFE082),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Text(
                    product.category,
                    color = Color(0x99FFC107),
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(6.dp))

                Text("Current Bid", color = Color(0x99FFC107), fontSize = 11.sp)
                Text(
                    formatPrice(product.bid),
                    color = Color(0xFFFFC107),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                if (product.condition == "Excellent")
                                    Color(0xFF4CAF50) else Color(0xFF2196F3),
                                CircleShape
                            )
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "${product.condition}   ${product.bids} bids   ${product.timeLeft}",
                        color = Color(0x99FFC107),
                        fontSize = 11.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(Color(0xFFFFC107), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", color = Color.Black, fontSize = 14.sp)
            }
        }
    }
}

/* -------- SIMPLE FLOW ROW -------- */

@Composable
private fun FlowRow(content: @Composable () -> Unit) {
    Row(modifier = Modifier.wrapContentHeight()) { content() }
}
