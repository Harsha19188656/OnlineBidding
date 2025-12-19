package com.example.onlinebidding.data.remote.model

data class ProductDto(
    val id: Int,
    val title: String,
    val description: String?,
    val category: String,
    val image_url: String?,
    val specs: String?,
    val condition: String?,
    val base_price: Double,
    val created_at: String?
)

data class AuctionDto(
    val id: Int,
    val product_id: Int,
    val start_price: Double,
    val current_price: Double,
    val status: String,
    val start_at: String?,
    val end_at: String?
)

data class AuctionListItem(
    val product: ProductDto,
    val auction: AuctionDto
)

data class AuctionListResponse(
    val success: Boolean,
    val items: List<AuctionListItem> = emptyList(),
    val error: String? = null
)

data class AuctionDetailsResponse(
    val success: Boolean,
    val product: ProductDto? = null,
    val auction: AuctionDto? = null,
    val bids: List<BidDto> = emptyList(),
    val error: String? = null
)

