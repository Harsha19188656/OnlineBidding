package com.example.onlinebidding.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchResults(
    onBack: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF111111), Color.Black)
                    )
                )
                .padding(20.dp)
        ) {

            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "←",
                    fontSize = 26.sp,
                    color = Color(0xFFFFD700),
                    modifier = Modifier
                        .clickable { onBack() }
                        .padding(end = 12.dp)
                )

                Text(
                    text = "Search Results",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Placeholder Results
            repeat(5) { index ->
                SearchResultItem(
                    title = "Auction Item ${index + 1}",
                    subtitle = "Live bidding • Ends soon"
                )
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .background(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF1A1A1A), Color(0xFF0E0E0E))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFFFD700)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            fontSize = 13.sp,
            color = Color(0xFFBFA77A)
        )
    }
}
