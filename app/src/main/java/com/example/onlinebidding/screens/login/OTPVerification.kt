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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OTPVerification(
    onVerify: () -> Unit,
    onBack: () -> Unit
) {
    var otp by remember { mutableStateOf(List(6) { "" }) }
    var timer by remember { mutableStateOf(30) }
    var canResend by remember { mutableStateOf(false) }

    // start countdown
    LaunchedEffect(timer) {
        if (timer > 0) {
            kotlinx.coroutines.delay(1000)
            timer--
        } else {
            canResend = true
        }
    }

    // glow animation
    val glowAnim = rememberInfiniteTransition()
    val glowScale by glowAnim.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            tween(3500, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        )
    )

    // input focus references
    val focusList = remember { MutableList<androidx.compose.ui.focus.FocusRequester?>(6) { null } }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0B0B0B), Color.Black)
                )
            )
            .drawBehind {
                drawRadialGlow(this, Color(0x22FFD700), glowScale)
            }
            .padding(22.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // BACK BUTTON
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0x11FFD700), CircleShape)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                BasicText("←", style = TextStyle(color = Color(0xFFFFD88A), fontSize = 22.sp))
            }

            // HEADER
            Column {
                BasicText(
                    "Enter OTP",
                    style = TextStyle(
                        fontSize = 32.sp,
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                Spacer(Modifier.height(6.dp))
                BasicText(
                    "We've sent a 6-digit code to your email",
                    style = TextStyle(color = Color(0xFFBFA77A), fontSize = 14.sp)
                )
            }

            // OTP BOXES
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                otp.forEachIndexed { index, digit ->

                    BasicTextField(
                        value = digit,
                        onValueChange = { v ->
                            if (v.length <= 1 && v.all { it.isDigit() }) {
                                val updated = otp.toMutableList()
                                updated[index] = v
                                otp = updated

                                if (v.isNotEmpty() && index < 5) {
                                    focusList[index + 1]?.requestFocus()
                                }

                                if (updated.all { it.isNotEmpty() }) {
                                    onVerify()
                                }
                            }
                        },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = Color(0xFFF7F7F7),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        ),
                        decorationBox = { inner ->
                            Box(
                                modifier = Modifier
                                    .size(width = 50.dp, height = 60.dp)
                                    .background(
                                        Color(0x15000000),
                                        RoundedCornerShape(14.dp)
                                    )
                                    .drawBehind {
                                        if (digit.isNotEmpty()) {
                                            drawRect(
                                                color = Color(0x33FFD700),
                                                topLeft = Offset.Zero,
                                                size = size
                                            )
                                        }
                                    }
                                    .padding(vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                inner()
                            }
                        }
                    )

                    Spacer(Modifier.width(10.dp))
                }
            }

            // TIMER
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                if (!canResend) {
                    BasicText(
                        "Resend OTP in ${timer}s",
                        style = TextStyle(color = Color(0xFFDDC170), fontSize = 14.sp)
                    )
                } else {
                    BasicText(
                        "↻  Resend OTP",
                        modifier = Modifier.clickable {
                            timer = 30
                            canResend = false
                            otp = List(6) { "" }
                        },
                        style = TextStyle(color = Color(0xFFFFC857), fontSize = 14.sp)
                    )
                }
            }

            // INFO BOX
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x11FFD700), RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                BasicText(
                    "Enter the OTP sent to your email to continue",
                    style = TextStyle(color = Color(0xFFBFA77A), fontSize = 13.sp),
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
        }
    }
}

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
