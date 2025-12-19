package com.example.onlinebidding.screens.login

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CreateProfile(
    onComplete: (name: String, phone: String, location: String) -> Unit
) {
    val focusManager = LocalFocusManager.current

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    val glowTransition = rememberInfiniteTransition(label = "")
    val glowAlpha by glowTransition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF070707), Color(0xFF0B0B0B))
                )
            )
            .drawBehind { drawRadialGlow(this, Color(0x22FFD700), glowAlpha) }
            .padding(20.dp)
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            BasicText(
                text = "Create Profile",
                style = TextStyle(
                    color = Color(0xFFFFD700),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(Modifier.height(6.dp))

            BasicText(
                text = "Tell us about yourself",
                style = TextStyle(color = Color(0xFFBFA77A), fontSize = 14.sp)
            )

            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .shadow(12.dp, RoundedCornerShape(16.dp))
                    .background(Color(0x11000000), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {

                FieldLabel("Full Name")
                InputField(name, "John Doe", "👤") { name = it }

                Spacer(Modifier.height(14.dp))

                FieldLabel("Phone Number")
                InputField(phone, "+91 9876543210", "📞") {
                    phone = it.filter { c -> c.isDigit() || c == '+' || c == ' ' }
                }

                Spacer(Modifier.height(14.dp))

                FieldLabel("Location")
                InputField(location, "City, Country", "📍") { location = it }
            }

            Spacer(Modifier.weight(1f))

            val enabled = name.isNotBlank()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(16.dp, RoundedCornerShape(30.dp))
                    .background(
                        if (enabled)
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFFB300), Color(0xFFFF9800))
                            )
                        else Brush.horizontalGradient(
                            listOf(Color(0xFF4A4A4A), Color(0xFF3A3A3A))
                        ),
                        RoundedCornerShape(30.dp)
                    )
                    .clickable(enabled = enabled) {
                        focusManager.clearFocus()
                        onComplete(
                            name.trim(),
                            phone.trim(),
                            location.trim()
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                BasicText(
                    text = "Continue",
                    style = TextStyle(
                        color = if (enabled) Color.Black else Color(0xFF222222),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

/* ---------- helpers ---------- */

@Composable
private fun FieldLabel(text: String) {
    BasicText(
        text = text,
        style = TextStyle(color = Color(0xFFBFA77A), fontSize = 13.sp)
    )
}

@Composable
private fun InputField(
    value: String,
    placeholder: String,
    leadingIcon: String,
    onChange: (String) -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = onChange,
        singleLine = true,
        textStyle = TextStyle(color = Color(0xFFF7F1E6), fontSize = 16.sp),
        decorationBox = { inner ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color(0xFF0C0C0C), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicText(leadingIcon, style = TextStyle(fontSize = 18.sp))
                    Spacer(Modifier.width(10.dp))
                    Box(Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            BasicText(
                                text = placeholder,
                                style = TextStyle(
                                    color = Color(0xFFBFA77A).copy(alpha = 0.6f),
                                    fontSize = 14.sp
                                )
                            )
                        }
                        inner()
                    }
                }
            }
        }
    )
}

private fun drawRadialGlow(scope: DrawScope, color: Color, alpha: Float) {
    scope.drawCircle(
        brush = Brush.radialGradient(
            listOf(color.copy(alpha = alpha), Color.Transparent)
        ),
        radius = scope.size.minDimension * 0.6f,
        center = scope.center
    )
}
