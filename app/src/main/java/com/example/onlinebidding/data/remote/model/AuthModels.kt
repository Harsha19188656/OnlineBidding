package com.example.onlinebidding.data.remote.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val token: String? = null,
    val user_id: Int? = null,
    val error: String? = null
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

data class ApiMessageResponse(
    val success: Boolean,
    val error: String? = null,
    val message: String? = null
)

class TokenStore {
    var token: String? = null
}

