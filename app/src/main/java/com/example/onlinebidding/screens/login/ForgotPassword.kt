package com.example.onlinebidding.screens.login

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Polished minimal ForgotPassword screen that compiles without Material.
 * Keeps same simple APIs (BasicText / BasicTextField) so no new Gradle deps needed.
 */
@Composable
fun ForgotPassword(
    onSendOTP: () -> Unit,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }

    // subtle radial glow animation for background
    val glowAnim = rememberInfiniteTransition()
    val glowFraction by glowAnim.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.36f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // shimmer animation for the button
    val shimmerAnim = rememberInfiniteTransition()
    val shimmerX by shimmerAnim.animateFloat(
        initialValue = -1f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF050505), Color(0xFF080808))
                )
            )
            .drawBehind {
                // radial glow centered near upper half
                drawRadialGlow(this, Color(0x22FFD700), glowFraction)
            }
            .padding(20.dp)
    ) {
        // center card for better focus
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0x0F000000))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                // Top row: back button + spacer
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0x12FFD700))
                            .clickable { onBack() }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicText(
                            text = "←",
                            style = TextStyle(color = Color(0xFFFFD88A), fontSize = 18.sp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }

                // Title + subtitle
                Column {
                    BasicText(
                        "Forgot Password?",
                        style = TextStyle(
                            color = Color(0xFFFFD700),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    BasicText(
                        "Enter your email to receive a 6-digit OTP",
                        style = TextStyle(color = Color(0xFFBFA77A), fontSize = 13.sp)
                    )
                }

                // Input label + field (clean, padded)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    BasicText(
                        "Email Address",
                        style = TextStyle(color = Color(0xFFBFA77A), fontSize = 12.sp)
                    )

                    BasicTextField(
                        value = email,
                        onValueChange = { email = it },
                        singleLine = true,
                        textStyle = TextStyle(color = Color(0xFFF7F1E6), fontSize = 16.sp),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0x0F111111))
                                    .padding(horizontal = 14.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    BasicText("✉️", style = TextStyle(fontSize = 16.sp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Box(modifier = Modifier.weight(1f)) {
                                        if (email.isEmpty()) {
                                            BasicText(
                                                "your.email@example.com",
                                                style = TextStyle(color = Color(0xFFBFA77A).copy(alpha = 0.6f))
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    BasicText(
                        "We'll send a 6-digit OTP to this email",
                        style = TextStyle(color = Color(0xFFBFA77A).copy(alpha = 0.7f), fontSize = 12.sp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Send OTP button with icon + shimmer + disabled state
                val enabled = email.isNotBlank()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            brush = if (enabled)
                                Brush.horizontalGradient(listOf(Color(0xFFFFB300), Color(0xFFFF9900)))
                            else
                                Brush.horizontalGradient(listOf(Color(0xFF444444), Color(0xFF353535)))
                        )
                        .clickable(enabled = enabled) { if (enabled) onSendOTP() },
                    contentAlignment = Alignment.Center
                ) {
                    // shimmer band
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .drawBehind {
                                val bandWidth = size.width * 0.28f
                                val xPos = (shimmerX * (size.width + bandWidth)) - bandWidth
                                drawRect(
                                    color = Color.White.copy(alpha = if (enabled) 0.12f else 0.06f),
                                    topLeft = Offset(xPos, 0f),
                                    size = Size(bandWidth, size.height)
                                )
                            }
                    )

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        BasicText(
                            text = "✉️",
                            style = TextStyle(fontSize = 16.sp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        BasicText(
                            text = "Send OTP",
                            style = TextStyle(
                                color = if (enabled) Color.Black else Color.DarkGray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                // small footer text
                BasicText(
                    "Didn't receive OTP? Check spam or try again after a minute.",
                    style = TextStyle(color = Color(0xFFBFA77A).copy(alpha = 0.6f), fontSize = 11.sp)
                )
            }
        }
    }
}

// helper radial glow drawer (keeps earlier implementation)
private fun drawRadialGlow(
    scope: DrawScope,
    color: Color,
    alpha: Float
) {
    scope.drawCircle(
        brush = Brush.radialGradient(
            listOf(color.copy(alpha = alpha), Color.Transparent)
        ),
        radius = scope.size.minDimension * 0.45f,
        center = scope.center
    )
}
