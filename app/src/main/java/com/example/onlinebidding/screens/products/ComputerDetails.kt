package com.example.onlinebidding.screens.products

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onlinebidding.R

@Composable
fun ComputerDetailsScreen(
    product: Product?,
    onBack: () -> Unit
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

        /* ---------- HEADER ---------- */
        Row(
            modifier = Modifier.padding(16.dp),
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
                color = Color.White,
                fontSize = 20.sp,
                maxLines = 1
            )
        }

        /* ---------- CONTENT ---------- */
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            Image(
                painter = painterResource(product.imageRes),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "₹${product.currentBid}",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFC107)
            )

            Spacer(Modifier.height(8.dp))

            /* Seller Rating */
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = product.seller.rating.toString(),
                    color = Color(0xFFFFC107)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Condition",
                color = Color(0xFFFFC107),
                fontSize = 16.sp
            )

            Text(
                text = product.conditionDetails,
                color = Color.LightGray,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ComputerDetailsScreenPreview() {
    ComputerDetailsScreen(
        product = Product(
            name = "Macbook Pro 2022, M2, 512GB",
            imageRes = R.drawable.ic_macbook,
            currentBid = 250000,
            seller = Seller(
                name = "Suresh Kumar",
                verified = true,
                rating = 4.8
            ),
            endTime = 1,
            registeredBidders = 120,
            specs = mapOf(
                "RAM" to "16GB",
                "Storage" to "512GB SSD",
                "Display" to "13.3-inch Retina"
            ),
            condition = "Used - Like New",
            conditionDetails = "This Macbook has been gently used for 6 months and is in excellent condition."
        ),
        onBack = {}
    )
}
