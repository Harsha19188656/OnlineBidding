package com.example.onlinebidding.di

import com.example.onlinebidding.BuildConfig
import com.example.onlinebidding.data.remote.RetrofitClient
import com.example.onlinebidding.data.remote.ApiService
import com.example.onlinebidding.data.remote.model.TokenStore
import com.example.onlinebidding.data.repository.AppRepository

object ServiceLocator {
    private val tokenStore = TokenStore()
    
    // Base URL - TODO: Move to BuildConfig or gradle.properties for different environments
    // Updated to match current WiFi IP address
    private const val BASE_URL = "http://172.20.10.2/onlinebidding/"
    
    private val apiService: ApiService by lazy {
        RetrofitClient.create(BASE_URL, tokenStore)
    }
    
    val repository: AppRepository by lazy {
        AppRepository(apiService, tokenStore)
    }
    
    // Expose token store if needed for direct access
    val tokenStoreInstance: TokenStore
        get() = tokenStore
}
