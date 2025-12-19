package com.example.onlinebidding.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CategoryHighlight(
    onCategorySelected: (String) -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Browse Categories",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700)
            )

            CategoryRow("Laptops", 24) { onCategorySelected("laptops") }
            CategoryRow("Mobiles", 36) { onCategorySelected("mobiles") }
            CategoryRow("Computers", 18) { onCategorySelected("computers") }
            CategoryRow("Monitors", 15) { onCategorySelected("monitors") }
            CategoryRow("Tablets", 22) { onCategorySelected("tablets") }
        }
    }
}

@Composable
private fun CategoryRow(
    title: String,
    count: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .background(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF1A1A1A), Color(0xFF0F0F0F))
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(52.dp)
                .background(
                    brush = Brush.linearGradient(
                        listOf(Color(0xFFFFD700), Color(0xFFFFA000))
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title.first().toString(),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFFFD700)
            )
            Text(
                text = "$count live auctions",
                fontSize = 13.sp,
                color = Color(0xFFBFA77A)
            )
        }

        Text(
            text = "›",
            fontSize = 28.sp,
            color = Color(0xFFFFD700)
        )
    }
}
