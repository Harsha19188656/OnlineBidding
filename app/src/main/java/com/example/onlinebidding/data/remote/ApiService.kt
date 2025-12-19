package com.example.onlinebidding.data.remote

import com.example.onlinebidding.data.remote.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("api/login.php")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @POST("api/register.php")
    suspend fun register(@Body body: RegisterRequest): ApiMessageResponse

    @GET("api/auctions/list.php")
    suspend fun listAuctions(
        @Query("category") category: String? = null,
        @Query("search") search: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): AuctionListResponse

    @GET("api/auctions/details.php")
    suspend fun auctionDetails(
        @Query("id") id: Int
    ): AuctionDetailsResponse

    @POST("api/bids/place.php")
    suspend fun placeBid(@Body body: PlaceBidRequest): PlaceBidResponse
}

