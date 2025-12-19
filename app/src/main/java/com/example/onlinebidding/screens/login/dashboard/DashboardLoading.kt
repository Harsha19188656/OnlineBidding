package com.example.onlinebidding.screens.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun DashboardLoading(
    onComplete: () -> Unit
) {
    var hasNavigated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!hasNavigated) {
            hasNavigated = true
            delay(2500)
            onComplete()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "loading")

    val orbRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing)
        ),
        label = "orbRotation"
    )

    val orbScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbScale"
    )

    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing)
        ),
        label = "progress"
    )

    val centerGlowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "centerGlow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF111111), Color.Black, Color.Black)
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .size(400.dp)
                .scale(orbScale)
                .rotate(orbRotation)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0x26FFD700), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Box(modifier = Modifier.size(128.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 8.dp.toPx()
                    val radius = size.minDimension / 2 - strokeWidth

                    drawCircle(
                        color = Color(0x26FFD700),
                        radius = radius,
                        style = Stroke(strokeWidth)
                    )

                    drawArc(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                        ),
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(centerGlowScale)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0x4DFFD700), Color.Transparent)
                            ),
                            CircleShape
                        )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Loading Dashboard...",
                color = Color(0xB3FFD700),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { index ->
                    val dotScale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.5f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, delayMillis = index * 200),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot$index"
                    )

                    val dotAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.5f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, delayMillis = index * 200),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "alpha$index"
                    )

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .scale(dotScale)
                            .background(
                                Color(0xFFFFC107).copy(alpha = dotAlpha),
                                CircleShape
                            )
                    )
                }
            }
        }
    }
}
