package com.example.onlinebidding.utils

import com.example.onlinebidding.screens.products.fallbackComputers
import com.example.onlinebidding.screens.products.monitorList

/**
 * Utility functions for calculating credits based on product price
 */

/**
 * Parse price string (e.g., "₹1,85,000") to numeric value
 */
fun parsePrice(priceString: String): Double {
    // Remove ₹, commas, and spaces, then convert to double
    val cleaned = priceString.replace("₹", "")
        .replace(",", "")
        .replace(" ", "")
        .trim()
    return cleaned.toDoubleOrNull() ?: 0.0
}

/**
 * Calculate required credits based on product price
 * - 0 to 1 lakh (₹0 - ₹100,000): 7 credits
 * - 1 lakh to 1.5 lakh (₹100,000 - ₹150,000): 10 credits
 * - 1.5 lakh to 2.5 lakh (₹150,000 - ₹250,000): 12 credits
 * - Above 2.5 lakh: 15 credits (default)
 */
fun calculateCredits(price: Double): Int {
    return when {
        price >= 0 && price < 100000 -> 7
        price >= 100000 && price < 150000 -> 10
        price >= 150000 && price < 250000 -> 12
        else -> 15 // For prices above 2.5 lakh
    }
}

/**
 * Get product price based on type and index
 */
fun getProductPrice(type: String, index: Int): Double {
    return when (type) {
        "laptop" -> {
            // Laptop prices from LaptopList.kt
            val laptopPrices = listOf(
                "₹1,85,000", // MacBook Pro 16" M3 Max
                "₹95,000",   // Dell XPS 15 OLED
                "₹1,42,000"  // ASUS ROG Zephyrus G16
            )
            if (index < laptopPrices.size) {
                parsePrice(laptopPrices[index])
            } else 0.0
        }
        "mobile" -> {
            // Mobile prices from AppNavHost.kt
            val mobiles = listOf(
                "₹1,28,000", // iPhone 15 Pro Max
                "₹98,000",   // Samsung Galaxy S24 Ultra
                "₹54,000"    // OnePlus 12 Pro
            )
            if (index < mobiles.size) {
                parsePrice(mobiles[index])
            } else 0.0
        }
        "computer" -> {
            if (index < fallbackComputers.size) {
                parsePrice(fallbackComputers[index].price)
            } else 0.0
        }
        "monitor" -> {
            if (index < monitorList.size) {
                parsePrice(monitorList[index].price)
            } else 0.0
        }
        "tablet" -> {
            // Tablet prices from TabletListScreen.kt (currentBid values)
            val tabletPrices = listOf(
                85000.0, // iPad Pro 12.9" M2
                72000.0, // Samsung Galaxy Tab S9
                58000.0  // Microsoft Surface Pro 9
            )
            if (index < tabletPrices.size) {
                tabletPrices[index]
            } else 0.0
        }
        else -> 0.0
    }
}

