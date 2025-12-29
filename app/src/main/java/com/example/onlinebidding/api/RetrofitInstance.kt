package com.example.onlinebidding.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    // ⚠️ IMPORTANT: Update this IP to match your backend server IP
    // For Android Emulator: Use "http://10.0.2.2/onlinebidding/"
    // For Physical Device: Use your computer's WiFi IP
    // Your current IPv4 address: 172.20.10.2
    // Backend location: C:\xampp\htdocs\onlinebidding
    
    // Change this based on what you're using:
    // - Android Emulator: "http://10.0.2.2/onlinebidding/"
    // - Physical Device: "http://172.20.10.2/onlinebidding/"
    private const val BASE_URL = "http://10.148.199.81/onlinebidding/"   // ✅ Your WiFi IPv4 address (change to http://10.0.2.2/onlinebidding/ for emulator)

    val api: ApiService by lazy {
        // Add logging interceptor for debugging
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        // Configure OkHttpClient with timeouts
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS) // Connection timeout
            .readTimeout(30, TimeUnit.SECONDS)   // Read timeout
            .writeTimeout(30, TimeUnit.SECONDS)  // Write timeout
            .build()
        
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
