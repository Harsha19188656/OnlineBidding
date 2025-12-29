package com.example.onlinebidding.api

data class RegisterResponse(
    val status: Boolean? = null,
    val success: Boolean? = null,
    val message: String? = null,
    val error: String? = null,
    val user_id: Int? = null
)
