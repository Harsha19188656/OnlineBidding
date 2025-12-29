package com.example.onlinebidding.screens.products
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
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

/* -------------------- DATA MODELS -------------------- */

data class Seller(
    val name: String,
    val verified: Boolean,
    val rating: Double
)

data class Product(
    val name: String,
    val imageRes: Int,
    val currentBid: Int,
    val seller: Seller,
    val endTime: Int,
    val registeredBidders: Int,
    val specs: Map<String, String>,
    val condition: String,
    val conditionDetails: String
)

/* -------------------- UTILS -------------------- */

fun formatIndianRupees(amount: Int): String {
    return "₹%,d".format(amount)
}

/* -------------------- SCREEN -------------------- */

@Composable
fun MonitorDetailsScreen(
    product: Product?,
    onBack: () -> Unit,
    onPlaceBid: () -> Unit
) {
    if (product == null) {
        // Handle the case where the product is not found
        // You can show a loading indicator or an error message
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF111111), Color.Black)
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            /* ---------------- HEADER ---------------- */

            Row(
                modifier = Modifier
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFFFFC107)
                    )
                }

                Text(
                    text = product.name,
                    color = Color(0xFFFFECB3),
                    fontSize = 20.sp,
                    maxLines = 1,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            /* ---------------- CONTENT ---------------- */

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {

                /* Image */

                Image(
                    painter = painterResource(product.imageRes),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                /* Price Card */

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1C1C1C)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text("Current Bid", color = Color.Gray, fontSize = 12.sp)

                        Text(
                            formatIndianRupees(product.currentBid),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFC107)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    product.seller.name,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                if (product.seller.verified) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Verified",
                                        color = Color(0xFFFFC107),
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFC107),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    product.seller.rating.toString(),
                                    color = Color(0xFFFFC107),
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            InfoBox("Time Left", "${product.endTime}s")
                            InfoBox("Bidders", product.registeredBidders.toString())
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                /* Specifications */

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Specifications", color = Color(0xFFFFC107))
                        Spacer(modifier = Modifier.height(8.dp))
                        product.specs.forEach { (k, v) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(k, color = Color.Gray, fontSize = 13.sp)
                                Text(v, color = Color.White, fontSize = 13.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                /* Condition */

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Condition: ${product.condition}",
                            color = Color(0xFFFFC107)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            product.conditionDetails,
                            color = Color.LightGray,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        /* ---------------- PLACE BID BUTTON ---------------- */

        Button(
            onClick = onPlaceBid,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp)
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFC107)
            ),
            shape = RoundedCornerShape(50)
        ) {
            Text(
                "Place Bid",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

/* -------------------- SMALL COMPONENT -------------------- */

@Composable
private fun InfoBox(label: String, value: String) {
    Column(
        modifier = Modifier
            .background(Color(0xFF121212), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, color = Color.Gray, fontSize = 11.sp)
        Text(value, color = Color(0xFFFFC107), fontSize = 14.sp)
    }
}
