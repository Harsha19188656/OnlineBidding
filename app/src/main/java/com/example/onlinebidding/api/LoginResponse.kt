package com.example.onlinebidding.api

data class LoginResponse(
    val success: Boolean,
    val token: String? = null,
    val user_id: Int? = null,
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val role: String? = null, // "admin" or "user"
    val error: String? = null
)

