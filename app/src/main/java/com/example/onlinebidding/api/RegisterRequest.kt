package com.example.onlinebidding.api

data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String,
    val gender: String,
    val password: String,
    val is_admin: Int = 0 // 0 for user, 1 for admin
)
