package com.example.onlinebidding.screens.login

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onlinebidding.api.RetrofitInstance
import com.example.onlinebidding.api.VerifyOTPRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OTPVerification(
    email: String,
    onVerify: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var otp by remember { mutableStateOf(List(6) { "" }) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Focus requesters for auto-focus
    val focusRequesters = remember { List(6) { FocusRequester() } }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color.Black
                    ),
                    radius = 1000f
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF2A2A3E), CircleShape)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        "←",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            BasicText(
                "Enter OTP",
                style = TextStyle(
                    color = Color(0xFFFFB84D),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            // Subtitle
            BasicText(
                "We've sent a 6-digit code to\n$email",
                style = TextStyle(
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // OTP Input Fields
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                otp.forEachIndexed { index, digit ->
                    BasicTextField(
                        value = digit,
                        onValueChange = { v ->
                            if (v.length <= 1 && v.all { it.isDigit() }) {
                                val updated = otp.toMutableList()
                                updated[index] = v

                                // Auto-focus next field
                                if (v.isNotEmpty() && index < 5) {
                                    focusRequesters[index + 1].requestFocus()
                                }

                                otp = updated

                                // Auto-verify when all fields filled
                                if (updated.all { it.isNotEmpty() }) {
                                    val otpString = updated.joinToString("")
                                    scope.launch {
                                        isLoading = true
                                        errorMessage = null
                                        try {
                                            delay(300) // Small delay for better UX
                                            val response = RetrofitInstance.api.verifyOTP(
                                                VerifyOTPRequest(email, otpString)
                                            )

                                            android.util.Log.d("OTPVerification", "📡 Response Code: ${response.code()}")
                                            android.util.Log.d("OTPVerification", "📦 Response Body: ${response.body()}")
                                            
                                            if (response.isSuccessful) {
                                                val responseBody = response.body()
                                                
                                                if (responseBody?.success == true) {
                                                    val resetToken = responseBody.token
                                                    
                                                    if (resetToken != null) {
                                                        android.util.Log.d("OTPVerification", "✅ OTP Verified - Token: $resetToken")
                                                        Toast.makeText(
                                                            context,
                                                            "OTP Verified Successfully",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        // Pass the reset token (not OTP) to navigate to reset password screen
                                                        onVerify(resetToken)
                                                    } else {
                                                        val error = "Invalid response from server. Token missing."
                                                        android.util.Log.e("OTPVerification", "❌ $error")
                                                        errorMessage = error
                                                        otp = List(6) { "" }
                                                        focusRequesters[0].requestFocus()
                                                    }
                                                } else {
                                                    val error = responseBody?.error ?: responseBody?.message ?: "Invalid OTP. Please try again."
                                                    android.util.Log.e("OTPVerification", "❌ API Error: $error")
                                                    errorMessage = error
                                                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                                    otp = List(6) { "" }
                                                    focusRequesters[0].requestFocus()
                                                }
                                            } else {
                                                val errorBody = response.errorBody()?.string()
                                                android.util.Log.e("OTPVerification", "❌ HTTP Error ${response.code()}: $errorBody")
                                                val errorMsg = when (response.code()) {
                                                    400 -> {
                                                        val error = errorBody?.let { 
                                                            try {
                                                                val json = org.json.JSONObject(it)
                                                                json.getString("error")
                                                            } catch (e: Exception) {
                                                                "Invalid or expired OTP. Please request a new one."
                                                            }
                                                        } ?: "Invalid or expired OTP. Please request a new one."
                                                        error
                                                    }
                                                    404 -> "OTP verification endpoint not found. Check backend API."
                                                    500 -> "Server error. Check backend logs."
                                                    else -> "Failed to verify OTP (Error ${response.code()})"
                                                }
                                                errorMessage = errorMsg
                                                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                                otp = List(6) { "" }
                                                focusRequesters[0].requestFocus()
                                            }
                                        } catch (e: java.net.UnknownHostException) {
                                            val errorMsg = "Cannot connect to server. Check your internet connection."
                                            android.util.Log.e("OTPVerification", "❌ Network Error: ${e.message}", e)
                                            errorMessage = errorMsg
                                            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                            otp = List(6) { "" }
                                            focusRequesters[0].requestFocus()
                                        } catch (e: java.net.SocketTimeoutException) {
                                            val errorMsg = "Connection timeout. Please try again."
                                            android.util.Log.e("OTPVerification", "❌ Timeout Error: ${e.message}", e)
                                            errorMessage = errorMsg
                                            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                            otp = List(6) { "" }
                                            focusRequesters[0].requestFocus()
                                        } catch (e: Exception) {
                                            val errorMsg = "Verification failed: ${e.message ?: "Unknown error"}"
                                            android.util.Log.e("OTPVerification", "❌ Exception: ${e.message}", e)
                                            errorMessage = errorMsg
                                            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                            otp = List(6) { "" }
                                            focusRequesters[0].requestFocus()
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                if (digit.isNotEmpty()) Color(0xFFFFB84D).copy(alpha = 0.2f)
                                else Color(0xFF2A2A3E),
                                RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 2.dp,
                                color = if (digit.isNotEmpty()) Color(0xFFFFB84D)
                                else Color(0xFF3A3A4E),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .focusRequester(focusRequesters[index])
                            .padding(4.dp),
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                if (index < 5) {
                                    focusRequesters[index + 1].requestFocus()
                                }
                            }
                        )
                    )
                    if (index < 5) {
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                }
            }

            // Error Message
            if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.Red.copy(alpha = 0.1f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        errorMessage!!,
                        style = TextStyle(
                            color = Color(0xFFFF6B6B),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }

            // Loading indicator
            if (isLoading) {
                BasicText(
                    "Verifying...",
                    style = TextStyle(
                        color = Color(0xFFFFB84D),
                        fontSize = 14.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Resend OTP option
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicText(
                    "Didn't receive OTP? ",
                    style = TextStyle(
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                )
                BasicText(
                    "Resend",
                    modifier = Modifier.clickable {
                        // TODO: Implement resend OTP
                        Toast.makeText(context, "Resend functionality coming soon", Toast.LENGTH_SHORT).show()
                    },
                    style = TextStyle(
                        color = Color(0xFFFFB84D),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Helper text
            BasicText(
                "Enter the OTP sent to your email to continue",
                style = TextStyle(
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            )
        }
    }

    // Auto-focus first field on launch
    LaunchedEffect(Unit) {
        delay(300)
        focusRequesters[0].requestFocus()
    }
}

private fun drawRadialGlow(scope: DrawScope, color: Color, alpha: Float) {
    scope.drawCircle(
        brush = Brush.radialGradient(
            listOf(color.copy(alpha = alpha), Color.Transparent)
        ),
        radius = scope.size.minDimension * 0.45f,
        center = scope.center
    )
}
