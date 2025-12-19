package com.example.onlinebidding.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GetStartedScreen(
    onGetStarted: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "premium")

    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black,
                        Color(0xFF0F0F0F),
                        Color.Black
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        /* ---------- Background glow ---------- */
        Box(
            modifier = Modifier
                .size(360.dp)
                .scale(glowScale)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color(0x33FFD700),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            /* ---------- Title ---------- */
            Text(
                text = "WELCOME TO",
                color = Color(0x99FFD700),
                fontSize = 14.sp,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "BIDHERE",
                color = Color(0xFFFFD700),
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            /* ---------- Premium button ---------- */
            Box(
                modifier = Modifier
                    .width(240.dp)
                    .height(58.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color(0xFFFFD700),
                                Color(0xFFFFB300),
                                Color(0xFFFF9800)
                            )
                        ),
                        shape = RoundedCornerShape(30.dp)
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.6f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(30.dp)
                    )
                    .clickable { onGetStarted() },
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "GET STARTED",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        /* ---------- Corner accents ---------- */
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(80.dp)
                .border(
                    1.dp,
                    Color(0x44FFD700),
                    RoundedCornerShape(topStart = 24.dp)
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(80.dp)
                .border(
                    1.dp,
                    Color(0x44FFD700),
                    RoundedCornerShape(bottomEnd = 24.dp)
                )
        )
    }
}
