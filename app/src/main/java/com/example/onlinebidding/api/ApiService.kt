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
    
    @POST("api/forgot-password.php")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<ForgotPasswordResponse>
    
    @POST("api/verify-otp.php")
    suspend fun verifyOTP(
        @Body request: VerifyOTPRequest
    ): Response<VerifyOTPResponse>
    
    @POST("api/reset-password.php")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<ResetPasswordResponse>
    
    @POST("api/google-signin.php")
    suspend fun googleSignIn(
        @Body request: GoogleSignInRequest
    ): Response<GoogleSignInResponse>
    
    @GET("api/auctions/list.php")
    suspend fun listAuctions(
        @Query("category") category: String? = null,
        @Query("search") search: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<AuctionListResponse>
    
    @GET("api/auctions/details.php")
    suspend fun auctionDetails(
        @Query("id") id: Int
    ): Response<AuctionDetailsResponse>
    
    @POST("api/bids/place.php")
    suspend fun placeBid(
        @Body request: PlaceBidRequest
    ): Response<PlaceBidResponse>
    
    @POST("api/payments/create.php")
    suspend fun createPayment(
        @Body request: CreatePaymentRequest
    ): Response<CreatePaymentResponse>
    
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

data class AuctionDetailsResponse(
    val success: Boolean,
    val product: ProductDto? = null,
    val auction: AuctionDto? = null,
    val bids: List<BidDto> = emptyList(),
    val error: String? = null
)

data class BidDto(
    val id: Int,
    val auction_id: Int,
    val user_id: Int,
    val amount: Double,
    val created_at: String?,
    val user_name: String? = null
)

data class PlaceBidRequest(
    val auction_id: Int,
    val amount: Double,
    val user_id: Int = 25  // TODO: Get from logged in user session - using 25 for now (Admin User)
)

data class PlaceBidResponse(
    val success: Boolean,
    val current_price: Double? = null,
    val error: String? = null
)

// Forgot Password Request/Response
data class ForgotPasswordRequest(
    val email: String
)

data class ForgotPasswordResponse(
    val success: Boolean,
    val message: String? = null,
    val error: String? = null,
    val otp: String? = null, // Development mode only - OTP in response
    val debug: String? = null, // Development mode debug message
    val mail_sent: Boolean? = null // Whether email was actually sent
)

// Verify OTP Request/Response
data class VerifyOTPRequest(
    val email: String,
    val otp: String
)

data class VerifyOTPResponse(
    val success: Boolean,
    val message: String? = null,
    val token: String? = null,
    val error: String? = null
)

// Reset Password Request/Response
data class ResetPasswordRequest(
    val email: String,
    val token: String,
    val newPassword: String
)

data class ResetPasswordResponse(
    val success: Boolean,
    val message: String? = null,
    val error: String? = null
)

// Google Sign-In Request/Response
data class GoogleSignInRequest(
    val idToken: String
)

data class GoogleSignInResponse(
    val success: Boolean,
    val message: String? = null,
    val token: String? = null,
    val user: GoogleUser? = null,
    val error: String? = null
)

data class GoogleUser(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String? = null,
    val role: String = "user"
)

// Payment Request/Response
data class CreatePaymentRequest(
    val user_id: Int,
    val auction_id: Int? = null,
    val amount: Double,
    val payment_method: String,
    val upi_id: String
)

data class CreatePaymentResponse(
    val success: Boolean,
    val payment_id: Int? = null,
    val transaction_id: String? = null,
    val status: String? = null,
    val message: String? = null,
    val error: String? = null
)
