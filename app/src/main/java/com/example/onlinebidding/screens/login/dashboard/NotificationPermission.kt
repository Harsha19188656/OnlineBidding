package com.example.onlinebidding.screens.products

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Missing imports FIXED ↓↓↓
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.PaddingValues
// --------------------------------------------------------------

@Composable
fun NotificationPermission(onComplete: () -> Unit) {

    // Infinite animations
    val infinite = rememberInfiniteTransition()

    val blobScale by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val glowScale by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Bell entrance animation
    val bellScale = remember { Animatable(0f) }
    val bellRotation = remember { Animatable(-180f) }

    LaunchedEffect(Unit) {
        bellScale.animateTo(1f, spring(dampingRatio = 0.6f))
        bellRotation.animateTo(0f, spring(dampingRatio = 0.6f))
    }

    // Shimmer
    var buttonSize by remember { mutableStateOf(IntSize.Zero) }

    val shimmerX by infinite.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // Background gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF0B0B0B), Color.Black)
                        )
                    )
            )

            // Radial glow (350px)
            Box(
                modifier = Modifier
                    .size(350.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = 90.dp)
                    .graphicsLayer {
                        scaleX = blobScale
                        scaleY = blobScale
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .clip(CircleShape)
                        .background(Color(0x26FFD700))
                        .blur(28.dp)
                )
            }

            // MAIN CONTENT
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // Bell icon animation
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .graphicsLayer {
                            scaleX = bellScale.value
                            scaleY = bellScale.value
                            rotationZ = bellRotation.value
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Box(
                        modifier = Modifier
                            .size(112.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFD700))
                            .shadow(4.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Bell",
                            tint = Color(0xFFFFBF00),
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .graphicsLayer {
                                scaleX = glowScale
                                scaleY = glowScale
                            }
                            .clip(CircleShape)
                            .background(Color(0x33FFBF00))
                            .blur(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Stay Updated",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Get notified about auctions, bids, and exclusive deals",
                    fontSize = 14.sp,
                    color = Color(0xFFEBDEB8).copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 320.dp)
                )

                Spacer(modifier = Modifier.height(36.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 360.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // Primary Button (Shimmer)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { buttonSize = it.size }
                    ) {

                        Button(
                            onClick = { onComplete() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                            shape = RoundedCornerShape(999.dp),
                            contentPadding = PaddingValues()
                        ) {

                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {

                                if (buttonSize.width > 0) {

                                    val startX = shimmerX * buttonSize.width
                                    val endX = startX + (buttonSize.width * 0.3f)

                                    val brush = Brush.linearGradient(
                                        listOf(
                                            Color.Transparent,
                                            Color.White.copy(alpha = 0.3f),
                                            Color.Transparent
                                        ),
                                        start = Offset(startX, 0f),
                                        end = Offset(endX, 0f)
                                    )

                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .background(brush)
                                    )
                                }

                                Text(
                                    text = "Allow Notifications",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )
                            }
                        }
                    }

                    // Outlined Button
                    OutlinedButton(
                        onClick = { onComplete() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        border = BorderStroke(2.dp, Color(0x66FFB400)),
                        shape = RoundedCornerShape(999.dp)
                    ) {
                        Text(
                            text = "Maybe Later",
                            fontSize = 16.sp,
                            color = Color(0xFFFFBF80)
                        )
                    }
                }
            }
        }
    }
}
