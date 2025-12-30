package com.example.onlinebidding.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

@Composable
fun GetStartedScreen(
    onGetStarted: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            /* ---------- Scales of Justice Icon ---------- */
            // Custom scales of justice icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .drawBehind {
                        val centerX = size.width / 2
                        val centerY = size.height / 2
                        val scaleWidth = size.width * 0.6f
                        val scaleHeight = size.height * 0.4f
                        val plateSize = scaleWidth * 0.25f
                        
                        // Draw the base/stand
                        drawLine(
                            color = Color(0xFF4FC3F7),
                            start = Offset(centerX, centerY + scaleHeight * 0.3f),
                            end = Offset(centerX, centerY + scaleHeight * 0.6f),
                            strokeWidth = 8f
                        )
                        
                        // Draw the horizontal beam
                        drawLine(
                            color = Color(0xFF4FC3F7),
                            start = Offset(centerX - scaleWidth * 0.4f, centerY),
                            end = Offset(centerX + scaleWidth * 0.4f, centerY),
                            strokeWidth = 6f
                        )
                        
                        // Draw left plate (scale)
                        drawOval(
                            color = Color(0xFF4FC3F7),
                            topLeft = Offset(centerX - scaleWidth * 0.4f - plateSize / 2, centerY - plateSize / 2),
                            size = Size(plateSize, plateSize),
                            style = Stroke(width = 4f)
                        )
                        
                        // Draw right plate (scale)
                        drawOval(
                            color = Color(0xFF4FC3F7),
                            topLeft = Offset(centerX + scaleWidth * 0.4f - plateSize / 2, centerY - plateSize / 2),
                            size = Size(plateSize, plateSize),
                            style = Stroke(width = 4f)
                        )
                        
                        // Draw connecting lines from beam to plates
                        drawLine(
                            color = Color(0xFF4FC3F7),
                            start = Offset(centerX - scaleWidth * 0.4f, centerY),
                            end = Offset(centerX - scaleWidth * 0.4f, centerY - plateSize / 2),
                            strokeWidth = 3f
                        )
                        drawLine(
                            color = Color(0xFF4FC3F7),
                            start = Offset(centerX + scaleWidth * 0.4f, centerY),
                            end = Offset(centerX + scaleWidth * 0.4f, centerY - plateSize / 2),
                            strokeWidth = 3f
                        )
                    }
            )

            Spacer(modifier = Modifier.height(32.dp))

            /* ---------- BIDHERE Title ---------- */
            Text(
                text = "BIDHERE",
                color = Color(0xFFFFD700), // Bright yellow
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            /* ---------- Tagline ---------- */
            Text(
                text = "Premium auctions at your fingertips",
                color = Color(0xFFFFB74D), // Orange-yellow
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(48.dp))

            /* ---------- Features List ---------- */
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Fast Bids
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Lightning bolt icon - custom drawn
                    Box(
                        modifier = Modifier.size(24.dp)
                            .drawBehind {
                                // Draw lightning bolt shape
                                val path = Path().apply {
                                    moveTo(size.width * 0.5f, 0f)
                                    lineTo(size.width * 0.3f, size.height * 0.4f)
                                    lineTo(size.width * 0.5f, size.height * 0.4f)
                                    lineTo(size.width * 0.2f, size.height)
                                    lineTo(size.width * 0.7f, size.height * 0.6f)
                                    lineTo(size.width * 0.5f, size.height * 0.6f)
                                    lineTo(size.width * 0.8f, 0f)
                                    close()
                                }
                                drawPath(
                                    path = path,
                                    color = Color(0xFFFFD700), // Bright yellow
                                    style = androidx.compose.ui.graphics.drawscope.Fill
                                )
                            }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Fast Bids",
                        color = Color(0xFFFFB74D), // Orange-yellow
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Live Prices
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Grid icon with trending line - using custom drawing
                    Box(
                        modifier = Modifier.size(24.dp)
                            .drawBehind {
                                // Draw grid pattern
                                val gridSize = size.width / 3
                                for (i in 0..3) {
                                    drawLine(
                                        color = Color.White,
                                        start = Offset(i * gridSize, 0f),
                                        end = Offset(i * gridSize, size.height),
                                        strokeWidth = 1f
                                    )
                                    drawLine(
                                        color = Color.White,
                                        start = Offset(0f, i * gridSize),
                                        end = Offset(size.width, i * gridSize),
                                        strokeWidth = 1f
                                    )
                                }
                                // Draw trending up line (red) - diagonal from bottom-left to top-right
                                drawLine(
                                    color = Color.Red,
                                    start = Offset(2f, size.height - 2f),
                                    end = Offset(size.width - 2f, 2f),
                                    strokeWidth = 2f
                                )
                            }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Live Prices",
                        color = Color(0xFFFFB74D), // Orange-yellow
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Verified
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Verified",
                        color = Color(0xFFFFB74D), // Orange-yellow
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            /* ---------- Get Started Button ---------- */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        Color(0xFFFF9800), // Solid orange
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onGetStarted() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Get Started",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            /* ---------- Bottom Text ---------- */
            Text(
                text = "Join exclusive premium auctions",
                color = Color(0xFFFFB74D), // Orange-yellow
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
