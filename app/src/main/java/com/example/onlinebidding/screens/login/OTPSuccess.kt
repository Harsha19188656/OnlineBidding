package com.example.onlinebidding.screens.login

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun OTPSuccess(onContinue: () -> Unit) {

    LaunchedEffect(Unit) {
        delay(2500)
        onContinue()
    }

    val iconScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(stiffness = 200f)
    )

    val rotation by animateFloatAsState(
        targetValue = 360f,
        animationSpec = spring(stiffness = 150f)
    )

    val ripple = rememberInfiniteTransition()
    val rippleScale by ripple.animateFloat(
        initialValue = 1f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            tween(2000, easing = LinearEasing),
            RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF111111), Color.Black)
                )
            )
            .drawBehind {
                drawRadialGlow(this, Color(0x33FFD700))
            },
        contentAlignment = Alignment.Center
    ) {

        // Ripple animation circles
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer {
                        scaleX = rippleScale + (index * 0.1f)
                        scaleY = rippleScale + (index * 0.1f)
                        alpha = 1f - rippleScale / 3f
                    }
                    .border(
                        width = 1.dp,
                        color = Color(0x55FFD700),
                        shape = CircleShape
                    )
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // Success icon
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier
                    .size(110.dp)
                    .graphicsLayer {
                        scaleX = iconScale
                        scaleY = iconScale
                        rotationZ = rotation
                    }
            )

            Spacer(Modifier.height(16.dp))

            GradientText(
                text = "Verification Success!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "OTP verified successfully",
                color = Color(0xFFE8D9A8),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { i ->
                    val anim = rememberInfiniteTransition()
                    val scale by anim.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.5f,
                        animationSpec = infiniteRepeatable(
                            tween(800, delayMillis = i * 200),
                            RepeatMode.Reverse
                        )
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                alpha = scale
                            }
                            .background(Color(0xFFFFC107), CircleShape)
                    )
                }
            }
        }
    }
}

private fun drawRadialGlow(scope: DrawScope, color: Color) {
    scope.drawCircle(
        brush = Brush.radialGradient(
            listOf(color, Color.Transparent),
            radius = scope.size.minDimension * 0.6f,
            center = scope.center
        )
    )
}

@Composable
fun GradientText(
    text: String,
    fontSize: androidx.compose.ui.unit.TextUnit,
    fontWeight: FontWeight
) {
    Text(
        text = text,
        style = TextStyle(
            brush = Brush.linearGradient(
                listOf(Color(0xFFFFD700), Color(0xFFFF9800))
            ),
            fontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center
        )
    )
}
