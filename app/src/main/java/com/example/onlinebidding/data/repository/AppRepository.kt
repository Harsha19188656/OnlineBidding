package com.example.onlinebidding.data.repository

import com.example.onlinebidding.data.remote.ApiService
import com.example.onlinebidding.data.remote.model.*

class AppRepository(
    private val api: ApiService,
    private val tokenStore: TokenStore
    ) {

    suspend fun login(email: String, password: String): LoginResponse {
        val response = api.login(LoginRequest(email, password))
        if (response.success && !response.token.isNullOrBlank()) {
            tokenStore.token = response.token
        }
        return response
    }

    suspend fun register(name: String, email: String, password: String): ApiMessageResponse {
        return api.register(RegisterRequest(email = email, password = password, name = name))
    }

    suspend fun listAuctions(category: String? = null, search: String? = null, limit: Int = 20, offset: Int = 0): AuctionListResponse {
        return api.listAuctions(category, search, limit, offset)
    }

    suspend fun auctionDetails(id: Int): AuctionDetailsResponse = api.auctionDetails(id)

    suspend fun placeBid(auctionId: Int, amount: Double): PlaceBidResponse =
        api.placeBid(PlaceBidRequest(auction_id = auctionId, amount = amount))
}

