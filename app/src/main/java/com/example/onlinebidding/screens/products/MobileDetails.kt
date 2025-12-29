package com.example.onlinebidding.screens.products

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.onlinebidding.R
import com.example.onlinebidding.screens.products.CreditsState

/* ---------------- DATA ---------------- */

data class MobileProduct(
    val name: String,
    val description: String,
    val rating: Double,
    val price: String,
    val image: Int
)

/* ---------------- SCREEN ---------------- */

@Composable
fun MobileDetails(
    product: MobileProduct?,
    navController: NavHostController? = null,
    index: Int = 0,
    onBack: () -> Unit = {}
) {
    if (product == null) {
        // Handle the case where the product is not found
        // You can show a loading indicator or an error message
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        /* Top Bar */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFFFFC107),
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBack() }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = product.name,
                color = Color(0xFFFFC107),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        /* Product Card */
        Column(
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1A1A1A), Color(0xFF0F0F0F))
                    )
                )
                .padding(16.dp)
        ) {

            Image(
                painter = painterResource(id = product.image),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = product.name,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = product.description,
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(18.dp)
                )

                Text(
                    text = "${product.rating}",
                    color = Color(0xFFFFC107),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = product.price,
                    color = Color(0xFFFFC107),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val itemId = "mobile_$index"
                val hasCredits = CreditsState.hasCreditsForItem(itemId)
                
                Button(
                    onClick = {
                        navController?.navigate("credits/mobile/$index/${product?.name ?: ""}")
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFF00C853)),
                    modifier = Modifier.weight(1f),
                    enabled = navController != null
                ) {
                    Text(if (hasCredits) "Credits Added" else "Credits", color = Color.Black)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = {
                        if (hasCredits) {
                            navController?.navigate("mobile_auction_detail/$index")
                        } else {
                            navController?.navigate("credits/mobile/$index/${product?.name ?: ""}")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFFFF9800)),
                    modifier = Modifier.weight(1f),
                    enabled = navController != null
                ) {
                    Text("Bid Now", color = Color.Black)
                }
            }
        }
    }
}
