package com.example.onlinebidding.screens.products

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onlinebidding.R

@Composable
fun LastPaymentScreen(
    onBack: () -> Unit,
    onPaymentMethodSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFFFFC107)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        
        Text(
            text = "Payment Method",
            color = Color(0xFFFFC107),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "Select your preferred payment method",
            color = Color(0xFFB0B0B0),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        PaymentMethodCard(
            label = "PhonePe",
            imageRes = R.drawable.ic_phone,
            onClick = { onPaymentMethodSelected("phonepe") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        PaymentMethodCard(
            label = "Google Pay",
            imageRes = R.drawable.ic_gpay,
            onClick = { onPaymentMethodSelected("gpay") }
        )
    }
}

@Composable
private fun PaymentMethodCard(
    label: String,
    imageRes: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        border = BorderStroke(1.dp, Color(0x33FFC107))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = label,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "UPI Payment", color = Color(0xFFB0B0B0), fontSize = 14.sp)
                }
            }
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "Next",
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
