package com.example.onlinebidding.api

import retrofit2.Response
import retrofit2.http.*
import okhttp3.RequestBody

interface ApiService {

    @POST("api/register.php")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>
    
    @POST("api/login.php")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>
    
    @GET("api/auctions/list.php")
    suspend fun listAuctions(
        @Query("category") category: String? = null,
        @Query("search") search: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<AuctionListResponse>
    
    // Admin endpoints
    @GET("api/admin/products/list.php")
    suspend fun adminListProducts(
        @Header("Authorization") token: String,
        @Query("category") category: String? = null,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): Response<AdminProductListResponse>
    
    @POST("api/admin/products/add.php")
    suspend fun adminAddProduct(
        @Header("Authorization") token: String,
        @Body request: AdminAddProductRequest
    ): Response<AdminProductResponse>
    
    @POST("api/admin/products/update.php")
    suspend fun adminUpdateProduct(
        @Header("Authorization") token: String,
        @Body request: AdminUpdateProductRequest
    ): Response<AdminProductResponse>
    
    @POST("api/admin/products/delete.php")
    suspend fun adminDeleteProduct(
        @Header("Authorization") token: String,
        @Body request: AdminDeleteProductRequest
    ): Response<AdminProductResponse>
}

// Response data classes for auctions list
data class AuctionListItem(
    val product: ProductDto,
    val auction: AuctionDto,
    // Additional fields for LaptopList compatibility
    val name: String? = null,
    val specs: String? = null,
    val rating: Double? = null,
    val price: String? = null,
    val image_url: String? = null
)

data class ProductDto(
    val id: Int,
    val title: String,
    val description: String?,
    val category: String,
    val image_url: String?,
    val specs: String?,
    val condition: String?,
    val base_price: Double,
    val created_at: String? = null
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

data class AuctionListResponse(
    val success: Boolean,
    val items: List<AuctionListItem> = emptyList(),
    val count: Int = 0,
    val error: String? = null
)
