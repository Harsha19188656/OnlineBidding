package com.example.onlinebidding.screens.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onlinebidding.api.RegisterRequest
import com.example.onlinebidding.api.RegisterResponse
import com.example.onlinebidding.api.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.Response

// Password validation function
private fun validatePassword(password: String): String? {
    if (password.length < 8) {
        return "Password must be at least 8 characters"
    }
    if (!password.any { it.isLetter() }) {
        return "Password must contain at least one letter"
    }
    if (!password.any { it.isDigit() }) {
        return "Password must contain at least one number"
    }
    return null
}

@Composable
fun CreateAccount(
    isAdmin: Boolean = false, // If true, creates admin account
    onAccountCreated: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            BasicText(
                text = if (isAdmin) "Create Admin Account" else "Create Account",
                style = TextStyle(
                    color = Color(0xFFFFD700),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            
            if (isAdmin) {
                Spacer(Modifier.height(8.dp))
                BasicText(
                    text = "Create a new admin account",
                    style = TextStyle(
                        color = Color(0xFFAAAAAA),
                        fontSize = 14.sp
                    )
                )
            }

            Spacer(Modifier.height(24.dp))

            InputField("Name", "Your name", name) { name = it }
            Spacer(Modifier.height(12.dp))

            InputField("Email", "your@email.com", email) { email = it }
            Spacer(Modifier.height(12.dp))

            InputField("Phone Number", "+91 9876543210", phone) { 
                phone = it.filter { c -> c.isDigit() || c == '+' || c == ' ' }
            }
            Spacer(Modifier.height(12.dp))

            // Gender Selection
            Column {
                BasicText(
                    text = "Gender",
                    style = TextStyle(color = Color(0xFFBFA77A))
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GenderOption("Male", gender == "Male") { gender = "Male" }
                    GenderOption("Female", gender == "Female") { gender = "Female" }
                    GenderOption("Other", gender == "Other") { gender = "Other" }
                }
            }
            Spacer(Modifier.height(12.dp))

            InputField("Password", "Create password (min 8 chars, 1 letter, 1 number)", password, true) { 
                password = it
                passwordError = validatePassword(it)
            }
            
            // Show password error if any
            if (passwordError != null && password.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                BasicText(
                    text = passwordError!!,
                    style = TextStyle(color = Color(0xFFFF5252), fontSize = 12.sp)
                )
            }

            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .background(
                        if (loading) Color.Gray else Color(0xFFFFB300),
                        RoundedCornerShape(30.dp)
                    )
                    .clickable(enabled = !loading) {

                        // Validate all fields
                        if (name.isBlank() || email.isBlank() || password.isBlank() || phone.isBlank() || gender.isBlank()) {
                            Toast.makeText(
                                context,
                                "All fields are required",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@clickable
                        }
                        
                        // Validate email format
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            Toast.makeText(
                                context,
                                "Please enter a valid email address",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@clickable
                        }
                        
                        // Validate password strength
                        val passwordValidation = validatePassword(password)
                        if (passwordValidation != null) {
                            Toast.makeText(
                                context,
                                passwordValidation,
                                Toast.LENGTH_LONG
                            ).show()
                            return@clickable
                        }

                        loading = true

                        scope.launch {
                            try {
                                val response: Response<RegisterResponse> =
                                    RetrofitInstance.api.register(
                                        RegisterRequest(
                                            name = name,
                                            email = email,
                                            phone = phone,
                                            gender = gender,
                                            password = password,
                                            is_admin = if (isAdmin) 1 else 0
                                        )
                                    )

                                loading = false

                                if (response.isSuccessful) {
                                    val responseBody = response.body()
                                    if (responseBody?.status == true || responseBody?.success == true) {
                                        Toast.makeText(
                                            context,
                                            responseBody.message ?: "Registration successful",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onAccountCreated()
                                    } else {
                                        // Show error from response body
                                        val errorMsg = responseBody?.error ?: responseBody?.message ?: "Unknown error. Status: ${responseBody?.status}, Success: ${responseBody?.success}"
                                        android.util.Log.e("Registration", "Failed: $errorMsg")
                                        Toast.makeText(
                                            context,
                                            "Registration failed: $errorMsg",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } else {
                                    val errorBody = response.errorBody()?.string()
                                    android.util.Log.e("Registration", "HTTP ${response.code()}: $errorBody")
                                    val errorMsg = if (errorBody != null) {
                                        try {
                                            // Try to parse as JSON
                                            val errorJson = org.json.JSONObject(errorBody)
                                            errorJson.optString("error", errorJson.optString("message", "HTTP ${response.code()}"))
                                        } catch (e: Exception) {
                                            "HTTP ${response.code()}: $errorBody"
                                        }
                                    } else {
                                        "HTTP ${response.code()}: ${response.message()}"
                                    }
                                    Toast.makeText(
                                        context,
                                        "Registration failed: $errorMsg",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            } catch (e: Exception) {
                                loading = false
                                val errorMsg = when {
                                    e.message?.contains("Unable to resolve host") == true -> "Cannot connect to server. Check your internet connection and server IP."
                                    e.message?.contains("Failed to connect") == true -> "Cannot connect to server. Make sure backend is running."
                                    e.message?.contains("timeout") == true -> "Connection timeout. Server may be down."
                                    else -> "Error: ${e.message ?: e.localizedMessage}"
                                }
                                Toast.makeText(
                                    context,
                                    errorMsg,
                                    Toast.LENGTH_LONG
                                ).show()
                                e.printStackTrace()
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                BasicText(
                    text = if (loading) "Please wait..." else "Sign Up",
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(Modifier.height(12.dp))

            BasicText(
                text = "Back to Login",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { onBack() },
                style = TextStyle(
                    color = Color(0xFFFFD700),
                    fontSize = 14.sp
                )
            )
        }
    }
}

@Composable
private fun InputField(
    label: String,
    hint: String,
    value: String,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Column {
        BasicText(text = label, style = TextStyle(color = Color(0xFFBFA77A)))
        Spacer(Modifier.height(6.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            visualTransformation = if (isPassword)
                PasswordVisualTransformation()
            else
                androidx.compose.ui.text.input.VisualTransformation.None,
            textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
            decorationBox = { inner ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color(0xFF111111), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        BasicText(hint, style = TextStyle(color = Color.Gray))
                    }
                    inner()
                }
            }
        )
    }
}

@Composable
private fun GenderOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFFFFD700)
            )
        )
        BasicText(
            text = label,
            style = TextStyle(color = Color.White)
        )
    }
}
