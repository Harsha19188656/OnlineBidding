package com.example.onlinebidding.data.remote.model

data class BidDto(
    val id: Int,
    val auction_id: Int,
    val user_id: Int,
    val amount: Double,
    val created_at: String?
)

data class PlaceBidRequest(
    val auction_id: Int,
    val amount: Double
)

data class PlaceBidResponse(
    val success: Boolean,
    val current_price: Double? = null,
    val error: String? = null
)

