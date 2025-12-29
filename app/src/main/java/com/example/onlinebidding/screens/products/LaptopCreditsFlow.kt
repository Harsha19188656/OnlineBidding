package com.example.onlinebidding.screens.products

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.onlinebidding.R
import kotlinx.coroutines.delay

private val darkBackground = Brush.verticalGradient(
    listOf(Color(0xFF0D0D0D), Color.Black)
)

@Composable
fun CreditsScreen(
    itemName: String,
    requiredCredits: Int = 10,
    creditPrice: Int = 10,
    onBack: () -> Unit,
    onPay: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(darkBackground)
                .padding(20.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFFFFC107)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Buy Credits",
                color = Color(0xFFFFC107),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(text = "Credits required", color = Color.Gray, fontSize = 13.sp)
            Text(
                text = "$requiredCredits credits",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            val totalCost = requiredCredits * creditPrice
            Text(text = "Total cost", color = Color.Gray, fontSize = 13.sp)
            Text(
                text = "₹$totalCost",
                color = Color(0xFFFFC107),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Each credit costs ₹$creditPrice. Use credits to place bids on $itemName.",
                color = Color(0xFFB0B0B0),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onPay,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "Pay ₹$totalCost to bid on $itemName",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, Color(0xFFFFC107))
            ) {
                Text(text = "Cancel", color = Color(0xFFFFC107), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PaymentMethodScreen(
    amount: Int,
    onBack: () -> Unit,
    onPay: (String) -> Unit
) {
    val cardShape = RoundedCornerShape(18.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackground)
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
            onClick = { onPay("phonepe") },
            shape = cardShape
        )

        Spacer(modifier = Modifier.height(12.dp))

        PaymentMethodCard(
            label = "Google Pay",
            imageRes = R.drawable.ic_gpay,
            onClick = { onPay("gpay") },
            shape = cardShape
        )
    }
}

@Composable
private fun PaymentMethodCard(
    label: String,
    imageRes: Int,
    onClick: () -> Unit,
    shape: RoundedCornerShape
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
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

@Composable
fun PaymentProcessingScreen(
    method: String,
    onFinished: () -> Unit
) {
    LaunchedEffect(method) {
        delay(3000L)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color(0xFFFFC107))
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "Processing Payment...",
                color = Color(0xFFFFC107),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Please wait while we process your ${method.replaceFirstChar { it.uppercase() }} payment",
                color = Color(0xFFB0B0B0),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

@Composable
fun PaymentSuccessScreen(
    itemName: String,
    onStartBid: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .background(Color(0x33FFC107), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Payment Successful!",
                color = Color(0xFFFFC107),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your credits have been added successfully",
                color = Color.White,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(26.dp))
            Button(
                onClick = onStartBid,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Start Bid", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
