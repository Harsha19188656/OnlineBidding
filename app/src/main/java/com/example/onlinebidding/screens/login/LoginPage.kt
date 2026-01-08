package com.example.onlinebidding.screens.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip

@Composable
fun LoginPage(
    onLogin: (String, String, String) -> Unit, // email, password, loginType
    onForgotPassword: () -> Unit,
    onSignUp: (String) -> Unit, // Pass loginType: "User" or "Admin"
    onGoogleSignUp: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginType by remember { mutableStateOf("User") } // "User" or "Admin"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Spacer(modifier = Modifier.height(50.dp))

            /* -------- TITLE -------- */
            BasicText(
                text = "Welcome Back",
                style = TextStyle(
                    color = Color(0xFFFFB300),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            BasicText(
                text = "Sign in to continue bidding",
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            )

            Spacer(modifier = Modifier.height(40.dp))
            
            /* -------- LOGIN TYPE SELECTION -------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // User Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(
                            if (loginType == "User") 
                                Color(0xFFFFB300) 
                            else 
                                Color(0xFF1A1A1A),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { loginType = "User" },
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = "User",
                        style = TextStyle(
                            color = if (loginType == "User") Color.Black else Color(0xFFFFB300),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                
                // Admin Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(
                            if (loginType == "Admin") 
                                Color(0xFFFFB300) 
                            else 
                                Color(0xFF1A1A1A),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { loginType = "Admin" },
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = "Admin",
                        style = TextStyle(
                            color = if (loginType == "Admin") Color.Black else Color(0xFFFFB300),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            /* -------- EMAIL -------- */
            InputField(
                label = "Email",
                hint = "Enter your email",
                value = email
            ) { email = it }

            Spacer(modifier = Modifier.height(20.dp))

            /* -------- PASSWORD -------- */
            InputField(
                label = "Password",
                hint = "Enter your password",
                value = password,
                isPassword = true
            ) { password = it }

            Spacer(modifier = Modifier.height(14.dp))

            /* -------- FORGOT PASSWORD -------- */
            BasicText(
                text = "Forgot Password?",
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { onForgotPassword() },
                style = TextStyle(
                    color = Color(0xFFFFB300),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            /* -------- ERROR -------- */
            if (!errorMessage.isNullOrEmpty()) {
                BasicText(
                    text = errorMessage,
                    style = TextStyle(
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            /* -------- SIGN IN BUTTON -------- */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFF9800),
                                Color(0xFFFFB300)
                            )
                        ),
                        shape = RoundedCornerShape(30.dp)
                    )
                    .clickable(enabled = !isLoading) {
                        if (email.isBlank() || password.isBlank()) {
                            Toast
                                .makeText(
                                    context,
                                    "Email and Password required",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                            return@clickable
                        }
                        onLogin(email, password, loginType)
                    },
                contentAlignment = Alignment.Center
            ) {
                BasicText(
                    text = if (isLoading) "Please wait..." else "Sign In",
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            /* -------- OR DIVIDER -------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color.Gray.copy(alpha = 0.3f))
                )
                BasicText(
                    text = " OR ",
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color.Gray.copy(alpha = 0.3f))
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            /* -------- GOOGLE SIGN IN BUTTON -------- */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(30.dp)
                    )
                    .clickable(enabled = !isLoading) {
                        onGoogleSignUp()
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    // Google Icon (G)
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF4285F4),
                                        Color(0xFF34A853),
                                        Color(0xFFFBBC05),
                                        Color(0xFFEA4335)
                                    )
                                ),
                                shape = RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicText(
                            text = "G",
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    BasicText(
                        text = "Sign in with Google",
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            /* -------- POWERED BY TEXT -------- */
            BasicText(
                text = "2026©•Powered by SIMATS Engineering",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = TextStyle(
                    color = Color.Gray.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            /* -------- SIGN UP -------- */
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                BasicText(
                    text = "Don't have an account? ",
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                )
                BasicText(
                    text = "Sign Up",
                    modifier = Modifier.clickable { onSignUp(loginType) },
                    style = TextStyle(
                        color = Color(0xFFFFB300),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/* ================= INPUT FIELD ================= */

@Composable
private fun InputField(
    label: String,
    hint: String,
    value: String,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Column {
        BasicText(
            text = label,
            style = TextStyle(
                color = Color(0xFFFFB300),
                fontSize = 13.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            visualTransformation = if (isPassword)
                PasswordVisualTransformation()
            else
                androidx.compose.ui.text.input.VisualTransformation.None,
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 15.sp
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .background(
                            Color(0xFF111111),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        BasicText(
                            text = hint,
                            style = TextStyle(color = Color.Gray)
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}
