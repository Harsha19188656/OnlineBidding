package com.example.onlinebidding.screens.products

import com.example.onlinebidding.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

/* ------------------ ROUTE ------------------ */
const val AUCTION_DETAILS_ROUTE = "auction_details"

/* ------------------ DATA ------------------ */
data class Laptop(
    val name: String,
    val specs: String,
    val rating: String,
    val price: String,
    val imageRes: Int
)

private val laptops = listOf(
    Laptop(
        "MacBook Pro 16\" M3",
        "Apple M3 Max · 48GB · 1TB SSD",
        "4.9",
        "₹1,85,000",
        R.drawable.ic_macbook
    ),
    Laptop(
        "Dell XPS 15 OLED",
        "i7-13700H · 32GB · 1TB SSD",
        "4.7",
        "₹95,000",
        R.drawable.ic_dell_xps
    ),
    Laptop(
        "ASUS ROG Zephyrus G16",
        "Ryzen 9 · 32GB · 2TB SSD",
        "4.8",
        "₹1,42,000",
        R.drawable.ic_asus_rog
    )
)

/* ------------------ SCREEN ------------------ */
@Composable
fun LaptopList(
    navController: NavHostController,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {

        /* ---------- HEADER ---------- */
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            BasicText(
                text = "←",
                style = TextStyle(
                    color = Color(0xFFFFC107),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(end = 12.dp)
            )

            BasicText(
                text = "Laptops",
                style = TextStyle(
                    color = Color(0xFFFFC107),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        /* ---------- SEARCH BAR ---------- */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFF333333),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicText(
                text = "Search laptops...",
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        /* ---------- LIST ---------- */
        LazyColumn {
            items(laptops) { laptop ->
                LaptopCard(
                    laptop = laptop,
                    navController = navController
                )
            }
        }
    }
}

/* ------------------ CARD ------------------ */
@Composable
fun LaptopCard(
    laptop: Laptop,
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF111111), Color(0xFF000000))
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFF3A2A00),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(16.dp)
    ) {

        /* ---------- TOP ROW ---------- */
        Row(verticalAlignment = Alignment.CenterVertically) {

            Image(
                painter = painterResource(id = laptop.imageRes),
                contentDescription = laptop.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF121212)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                BasicText(
                    text = laptop.name,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                BasicText(
                    text = laptop.specs,
                    style = TextStyle(
                        color = Color.LightGray,
                        fontSize = 13.sp
                    )
                )
            }

            /* ✅ ONLY NAVIGATION */
            BasicText(
                text = "View",
                style = TextStyle(
                    color = Color(0xFFFFC107),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.clickable {
                    navController.navigate(AUCTION_DETAILS_ROUTE)
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        /* ---------- RATING & PRICE ---------- */
        Row(verticalAlignment = Alignment.CenterVertically) {
            BasicText(
                text = "⭐ ${laptop.rating}",
                style = TextStyle(
                    color = Color(0xFFFFC107),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            BasicText(
                text = laptop.price,
                style = TextStyle(
                    color = Color(0xFFFFC107),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        /* ---------- BUTTONS ---------- */
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ActionButton(
                text = "Credits",
                colors = listOf(Color(0xFF00C853), Color(0xFF009624))
            )
            ActionButton(
                text = "Bid",
                colors = listOf(Color(0xFFFFA000), Color(0xFFFF6F00))
            )
        }
    }
}

/* ------------------ BUTTON ------------------ */
@Composable
fun ActionButton(
    text: String,
    colors: List<Color>
) {
    Box(
        modifier = Modifier
            .width(130.dp)
            .height(40.dp)
            .background(
                brush = Brush.horizontalGradient(colors),
                shape = RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = text,
            style = TextStyle(
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        )
    }
}
