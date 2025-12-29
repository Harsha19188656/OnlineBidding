package com.example.onlinebidding.model

data class Seller(
    val name: String,
    val verified: Boolean = true,
    val rating: Double
)

data class Product(
    val id: Int,
    val name: String,
    val imageRes: Int,
    val currentBid: Int,
    val seller: Seller,
    val condition: String? = null,
    val conditionDetails: String? = null,
    val specs: Map<String, String>? = null
)

