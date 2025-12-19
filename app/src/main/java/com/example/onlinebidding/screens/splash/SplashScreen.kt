package com.example.onlinebidding.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onComplete: () -> Unit) {

    LaunchedEffect(Unit) {
        delay(3000)
        onComplete()
    }

    val glowScale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        glowScale.animateTo(1.5f, tween(2000, easing = LinearOutSlowInEasing))
    }

    val textAlpha = remember { Animatable(0f) }
    val textScale = remember { Animatable(0.8f) }

    LaunchedEffect(Unit) {
        delay(300)
        textAlpha.animateTo(1f, tween(1000))
        textScale.animateTo(1f, tween(1000))
    }

    val letterSpacing = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        delay(500)
        letterSpacing.animateTo(0.2f, tween(1500))
    }

    val infinite = rememberInfiniteTransition()
    val dotAnim = (0..2).map { index ->
        infinite.animateFloat(
            initialValue = 0.5f,
            targetValue = 1.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, delayMillis = index * 200),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        // Background gradient
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black,
                            Color.Black,
                            Color(0xFF1A1A1A)
                        )
                    )
                )
        )

        // Gold Glow
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.Center)
                .scale(glowScale.value)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x33FFD700), Color.Transparent),
                        radius = 400f
                    ),
                    CircleShape
                )
                .blur(80.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {

            Text(
                text = "BIDHERE",
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = letterSpacing.value.sp,
                color = Color(0xFFFFD700),
                modifier = Modifier
                    .scale(textScale.value)
                    .graphicsLayer { alpha = textAlpha.value }
            )

            Text(
                text = "Premium Auctions",
                fontSize = 14.sp,
                color = Color(0x99FFC107),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .graphicsLayer { alpha = textAlpha.value },
                textAlign = TextAlign.Center
            )
        }

        // Top line
        Box(
            Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, Color(0xFFFFB300), Color.Transparent)
                    )
                )
        )

        // Bottom line
        Box(
            Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, Color(0xFFFFB300), Color.Transparent)
                    )
                )
        )

        // Dots
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 70.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            dotAnim.forEach { anim ->
                Box(
                    modifier = Modifier
                        .size((anim.value * 8).dp)
                        .background(Color(0xFFFFB300), CircleShape)
                )
            }
        }
    }
}
