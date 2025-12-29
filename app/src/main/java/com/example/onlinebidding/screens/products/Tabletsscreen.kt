package com.example.onlinebidding.screens.products

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.onlinebidding.screens.products.CreditsState

/* ---------------------------------------------------------------------
   SCREEN
------------------------------------------------------------------------*/

@Composable
fun Tabletsscreen(
    product: Product?,
    navController: NavHostController? = null,
    index: Int = 0,
    onBack: () -> Unit,
    onPlaceBid: (() -> Unit)? = null
) {
    if (product == null) {
        // Handle the case where the product is not found
        // You can show a loading indicator or an error message
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        /* BACKGROUND GRADIENT */
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF1A1A1A),
                            Color.Black,
                            Color.Black
                        )
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {

            /* ---------------- HEADER ---------------- */

            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { -40 },
                    animationSpec = tween(300)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
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
                        text = product.name,
                        color = Color(0xFFFFECB3),
                        fontSize = 20.sp,
                        maxLines = 1,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            /* ---------------- SCROLL CONTENT ---------------- */

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {

                /* IMAGE */

                Image(
                    painter = painterResource(product.imageRes),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.DarkGray)
                )

                Spacer(modifier = Modifier.height(16.dp))

                /* CURRENT BID CARD */

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A1A1A)
                    ),
                    border = BorderStroke(1.dp, Color(0x33FFC107))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(
                            "Current Bid",
                            fontSize = 12.sp,
                            color = Color(0x99FFE082)
                        )

                        Text(
                            text = "₹${product.currentBid}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFC107),
                            modifier = Modifier.padding(vertical = 6.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    product.seller.name,
                                    color = Color(0xFFFFECB3),
                                    fontSize = 14.sp
                                )

                                if (product.seller.verified) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Verified",
                                        fontSize = 10.sp,
                                        color = Color(0xFFFFC107),
                                        modifier = Modifier
                                            .border(
                                                1.dp,
                                                Color(0x33FFC107),
                                                RoundedCornerShape(50)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFC107),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    product.seller.rating.toString(),
                                    fontSize = 12.sp,
                                    color = Color(0xFFFFC107),
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            InfoBox("Time Left", "${product.endTime}s")
                            InfoBox("Bidders", product.registeredBidders.toString())
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                /* SPECIFICATIONS */

                SectionCard("Specifications") {
                    product.specs.forEach { (key, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(key, color = Color(0x99FFE082), fontSize = 13.sp)
                            Text(value, color = Color(0xFFFFECB3), fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                /* CONDITION */

                SectionCard("Condition: ${product.condition}") {
                    Text(
                        product.conditionDetails,
                        fontSize = 13.sp,
                        color = Color(0xB3FFE082)
                    )
                }

                Spacer(modifier = Modifier.height(120.dp))
            }
        }

        /* ---------------- PLACE BID BUTTON ---------------- */
        
        val itemId = "tablet_$index"
        val hasCredits = CreditsState.hasCreditsForItem(itemId)

        Button(
            onClick = {
                if (hasCredits) {
                    onPlaceBid?.invoke() ?: navController?.navigate("bid_comments/tablet/$index")
                } else {
                    navController?.navigate("credits/tablet/$index/${product?.name ?: ""}")
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp)
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFA000)
            ),
            enabled = navController != null || onPlaceBid != null
        ) {
            Text(
                if (hasCredits) "Place Bid" else "Pay Credits to Bid",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

/* ---------------------------------------------------------------------
   REUSABLE COMPOSABLES
------------------------------------------------------------------------*/

@Composable
private fun InfoBox(title: String, value: String) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .background(Color(0xFF121212), RoundedCornerShape(14.dp))
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, fontSize = 11.sp, color = Color(0x99FFE082))
        Text(value, fontSize = 14.sp, color = Color(0xFFFFC107))
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        border = BorderStroke(1.dp, Color(0x33FFC107))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = Color(0xFFFFC107), fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}
