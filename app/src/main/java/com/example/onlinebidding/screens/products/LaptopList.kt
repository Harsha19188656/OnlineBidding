package com.example.onlinebidding.products

import androidx.compose.foundation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
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
import androidx.navigation.NavHostController
import com.example.onlinebidding.R

/* ---------------- DATA ---------------- */

data class Laptop(
    val name: String,
    val specs: String,
    val rating: Double,
    val price: String,
    val imageRes: Int
)

private val laptops = listOf(
    Laptop(
        "MacBook Pro 16\" M3",
        "Apple M3 Max · 48GB · 1TB SSD",
        4.9,
        "₹1,85,000",
        R.drawable.ic_macbook
    ),
    Laptop(
        "Dell XPS 15 OLED",
        "Intel i7-13700H · 32GB · 1TB SSD",
        4.7,
        "₹95,000",
        R.drawable.ic_dell_xps
    ),
    Laptop(
        "ASUS ROG Zephyrus G16",
        "Ryzen 9 7940HS · 32GB · 2TB SSD",
        4.8,
        "₹1,42,000",
        R.drawable.ic_asus_rog
    )
)

/* ---------------- COMPOSABLE ---------------- */

@Composable
fun LaptopList(
    navController: NavHostController,
    onBack: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color.Black, Color(0xFF0B0B0B))
                )
            )
    ) {

        /* ---------- HEADER ---------- */

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, null, tint = Color(0xFFFFC107))
            }

            Text(
                "Laptops",
                color = Color(0xFFFFC107),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Text(laptops.size.toString(), color = Color(0xFFFFC107))
        }

        /* ---------- SEARCH ---------- */

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .height(48.dp)
                .border(1.dp, Color(0xFFFFC107), RoundedCornerShape(14.dp))
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, null, tint = Color(0xFFFFC107))
            Spacer(Modifier.width(8.dp))
            Text("Search laptops...", color = Color.Gray)
        }

        Spacer(Modifier.height(14.dp))

        /* ---------- LIST ---------- */

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            items(laptops.size) { index ->
                LaptopCard(laptop = laptops[index], navController = navController, index = index)
            }
        }
    }
}

/* ---------------- CARD ---------------- */

@Composable
private fun LaptopCard(
    laptop: Laptop,
    navController: NavHostController,
    index: Int
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF3A2A00), RoundedCornerShape(22.dp))
            .background(Color(0xFF141414), RoundedCornerShape(22.dp))
            .padding(14.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Image(
                painter = painterResource(laptop.imageRes),
                contentDescription = laptop.name,
                modifier = Modifier
                    .size(74.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        laptop.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "View",
                        color = Color(0xFFFFC107),
                        fontSize = 12.sp,
                        modifier = Modifier.clickable {
                            navController.navigate("laptop_details/$index")
                        }
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    laptop.specs,
                    color = Color(0xFFB0B0B0),
                    fontSize = 12.sp,
                    maxLines = 2
                )

                Spacer(Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(laptop.rating.toString(), color = Color(0xFFFFC107), fontSize = 12.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(laptop.price, color = Color(0xFFFFC107), fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    navController.navigate("credits/laptop/$index/${laptop.name}")
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
            ) { Text("Credits", color = Color.Black) }

            Button(
                onClick = {
                    val itemId = "laptop_$index"
                    if (com.example.onlinebidding.screens.products.CreditsState.hasCreditsForItem(itemId)) {
                        navController.navigate("auction_detail/$index")
                    } else {
                        navController.navigate("credits/laptop/$index/${laptop.name}")
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
            ) { Text("Bid", color = Color.Black) }
        }
    }
}
