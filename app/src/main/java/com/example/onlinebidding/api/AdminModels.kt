package com.example.onlinebidding.api

import retrofit2.Response

// Admin Product Request Models
data class AdminAddProductRequest(
    val title: String,
    val description: String?,
    val category: String, // laptop, mobile, computer, monitor, tablet
    val image_url: String?,
    val specs: Map<String, String>?,
    val condition_label: String?,
    val base_price: Double,
    val start_price: Double?
)

data class AdminUpdateProductRequest(
    val product_id: Int,
    val title: String? = null,
    val description: String? = null,
    val category: String? = null,
    val image_url: String? = null,
    val specs: Map<String, String>? = null,
    val condition_label: String? = null,
    val base_price: Double? = null
)

data class AdminDeleteProductRequest(
    val product_id: Int
)

// Admin Product Response Models
data class AdminProductResponse(
    val success: Boolean,
    val message: String? = null,
    val product_id: Int? = null,
    val auction_id: Int? = null,
    val error: String? = null
)

data class AdminProductItem(
    val id: Int,
    val title: String,
    val description: String?,
    val category: String,
    val image_url: String?,
    val specs: Map<String, String>?,
    val condition_label: String?,
    val base_price: Double,
    val auction_id: Int?,
    val start_price: Double?,
    val current_price: Double?,
    val auction_status: String?,
    val created_at: String?
)

data class AdminProductListResponse(
    val success: Boolean,
    val products: List<AdminProductItem> = emptyList(),
    val total: Int = 0,
    val count: Int = 0,
    val error: String? = null
)

