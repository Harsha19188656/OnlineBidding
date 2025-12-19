package com.example.onlinebidding.screens.welcome

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
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
 * Welcome.kt — Welcome screen with BIDHERE wordmark + Premium Auctions tagline.
 *
 * Usage:
 * Welcome(email = "user@example.com", onContinue = { /* navigate */ })
 */
@Composable
fun Welcome(
    email: String,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    // animations
    val infinite = rememberInfiniteTransition()
    val orbScale by infinite.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val glowAlpha by infinite.animateFloat(
        initialValue = 0.06f,
        targetValue = 0.28f,
        animationSpec = infiniteRepeatable(tween(2200), RepeatMode.Reverse)
    )
    val shimmer by infinite.animateFloat(
        initialValue = -1f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing))
    )

    Surface(modifier = modifier.fillMaxSize(), color = Color.Black) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(colors = listOf(Color(0xFF000000), Color(0xFF040404)))
                )
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top wordmark + tagline
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // BIDHERE wordmark
                    BasicText(
                        text = "BIDHERE",
                        style = TextStyle(
                            color = Color(0xFFFFD54F),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Premium Auctions tagline with small gavel emoji
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BasicText(
                            text = "🔨",
                            style = TextStyle(fontSize = 14.sp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        BasicText(
                            text = "Premium Auctions",
                            style = TextStyle(color = Color(0xFFBFA77A), fontSize = 14.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Avatar + small check badge (center)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size((110f * orbScale).dp)
                        .drawBehind {
                            // inner soft glow behind avatar (centered)
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0x55FFC107), Color.Transparent)
                                ),
                                radius = size.minDimension * 0.4f,
                                center = Offset(size.width / 2f, size.height / 2f)
                            )
                        }
                ) {
                    // avatar circle
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .shadow(8.dp, CircleShape)
                            .background(color = Color(0xFF1A1A1A), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicText(
                            text = "👤",
                            style = TextStyle(fontSize = 36.sp)
                        )
                    }

                    // small check badge overlapping bottom-right
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 10.dp, y = 10.dp)
                            .size(34.dp)
                            .shadow(6.dp, CircleShape)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFFFFA000), Color(0xFFFFC107))
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicText(
                            text = "✔",
                            style = TextStyle(fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Welcome text
                BasicText(
                    text = "Welcome!",
                    style = TextStyle(
                        color = Color(0xFFFFC107),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Email text
                BasicText(
                    text = email,
                    style = TextStyle(color = Color(0xFFBFA77A), fontSize = 14.sp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Continue button with shimmer and glow
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .shadow(20.dp, RoundedCornerShape(34.dp))
                        .drawBehind { drawRadialGlow(this, Color(0x33FFC107), glowAlpha) }
                        .background(
                            brush = Brush.horizontalGradient(listOf(Color(0xFFFFB300), Color(0xFFFF9800))),
                            shape = RoundedCornerShape(34.dp)
                        )
                        .clickable { onContinue() },
                    contentAlignment = Alignment.Center
                ) {
                    // shimmer overlay
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .drawBehind {
                                val bandWidth = size.width * 0.28f
                                val x = (shimmer + 1f) / 2f * (size.width + bandWidth) - bandWidth
                                drawRect(
                                    color = Color.White.copy(alpha = 0.12f),
                                    topLeft = Offset(x, 0f),
                                    size = Size(bandWidth, size.height)
                                )
                            }
                    )

                    BasicText(
                        text = "Continue",
                        style = TextStyle(color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

// helper to draw a soft radial glow
private fun drawRadialGlow(scope: DrawScope, color: Color, alpha: Float) {
    scope.drawCircle(
        brush = Brush.radialGradient(listOf(color.copy(alpha = alpha), Color.Transparent)),
        radius = scope.size.minDimension * 0.9f,
        center = Offset(scope.size.width / 2f, scope.size.height / 2f)
    )
}
