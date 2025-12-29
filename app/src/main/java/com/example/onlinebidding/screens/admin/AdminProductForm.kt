package com.example.onlinebidding.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onlinebidding.api.*
import kotlinx.coroutines.launch

@Composable
fun AdminProductForm(
    token: String,
    productId: Int? = null, // null for new product
    initialProduct: AdminProductItem? = null,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var title by remember { mutableStateOf(initialProduct?.title ?: "") }
    var description by remember { mutableStateOf(initialProduct?.description ?: "") }
    var category by remember { mutableStateOf(initialProduct?.category ?: "laptop") }
    var imageUrl by remember { mutableStateOf(initialProduct?.image_url ?: "") }
    var conditionLabel by remember { mutableStateOf(initialProduct?.condition_label ?: "") }
    var basePrice by remember { mutableStateOf(initialProduct?.base_price?.toString() ?: "") }
    var startPrice by remember { mutableStateOf(initialProduct?.start_price?.toString() ?: "") }
    
    // Specs as key-value pairs
    var specsText by remember { 
        mutableStateOf(
            initialProduct?.specs?.entries?.joinToString("\n") { "${it.key}:${it.value}" } ?: ""
        )
    }
    
    var isLoading by remember { mutableStateOf(false) }
    
    val categories = listOf("laptop", "mobile", "computer", "monitor", "tablet")
    
    fun parseSpecs(): Map<String, String> {
        if (specsText.isBlank()) return emptyMap()
        val map = mutableMapOf<String, String>()
        specsText.split("\n").forEach { line ->
            val trimmedLine = line.trim()
            if (trimmedLine.isBlank()) return@forEach
            
            // Handle both "Key:Value" and "Key : Value" formats
            val colonIndex = trimmedLine.indexOf(':')
            if (colonIndex > 0 && colonIndex < trimmedLine.length - 1) {
                val key = trimmedLine.substring(0, colonIndex).trim()
                val value = trimmedLine.substring(colonIndex + 1).trim()
                if (key.isNotBlank() && value.isNotBlank()) {
                    map[key] = value
                }
            }
        }
        return map
    }
    
    fun saveProduct() {
        // Validate required fields
        if (title.isBlank()) {
            Toast.makeText(context, "Title is required", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (basePrice.isBlank()) {
            Toast.makeText(context, "Base price is required", Toast.LENGTH_SHORT).show()
            return
        }
        
        val basePriceValue = basePrice.toDoubleOrNull()
        if (basePriceValue == null || basePriceValue <= 0) {
            Toast.makeText(context, "Base price must be greater than 0", Toast.LENGTH_SHORT).show()
            return
        }
        
        isLoading = true
        scope.launch {
            try {
                if (productId == null) {
                    // Add new product
                    android.util.Log.d("AdminProductForm", "Adding product: title=$title, category=$category, basePrice=$basePriceValue")
                    val request = AdminAddProductRequest(
                        title = title.trim(),
                        description = description.takeIf { it.isNotBlank() }?.trim(),
                        category = category,
                        image_url = imageUrl.takeIf { it.isNotBlank() }?.trim(),
                        specs = parseSpecs().takeIf { it.isNotEmpty() },
                        condition_label = conditionLabel.takeIf { it.isNotBlank() }?.trim(),
                        base_price = basePriceValue,
                        start_price = startPrice.toDoubleOrNull()?.takeIf { it > 0 }
                    )
                    android.util.Log.d("AdminProductForm", "Request: $request")
                    
                    android.util.Log.d("AdminProductForm", "Sending request to: ${RetrofitInstance::class.java.simpleName}")
                    android.util.Log.d("AdminProductForm", "Token: ${token.take(20)}...")
                    
                    val response = RetrofitInstance.api.adminAddProduct(
                        token = "Bearer $token",
                        request = request
                    )
                    
                    android.util.Log.d("AdminProductForm", "Response code: ${response.code()}")
                    android.util.Log.d("AdminProductForm", "Response body: ${response.body()}")
                    
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(context, "Product added successfully! Changes will reflect on user side.", Toast.LENGTH_SHORT).show()
                        onSave()
                    } else {
                        // Read error body only once
                        val errorBody = try {
                            response.errorBody()?.string()
                        } catch (e: Exception) {
                            null
                        }
                        
                        android.util.Log.e("AdminProductForm", "Error response: $errorBody")
                        android.util.Log.e("AdminProductForm", "Error code: ${response.code()}")
                        android.util.Log.e("AdminProductForm", "Request sent: title=$title, category=$category, base_price=$basePriceValue")
                        
                        val errorMsg = when (response.code()) {
                            500 -> {
                                val serverError = if (errorBody != null && errorBody.isNotBlank()) {
                                    try {
                                        val errorJson = org.json.JSONObject(errorBody)
                                        errorJson.optString("error", errorBody.take(200))
                                    } catch (e: Exception) {
                                        errorBody.take(200) // Show first 200 chars of error
                                    }
                                } else {
                                    "Server error (500)"
                                }
                                "$serverError\n\nTroubleshooting:\n1. Check XAMPP Apache & MySQL\n2. Check: C:\\xampp\\apache\\logs\\error.log\n3. Test API: http://10.148.199.81/onlinebidding/api/admin/products/add.php\n4. Verify database tables exist"
                            }
                            401 -> "Authentication failed. Please login again."
                            404 -> "API endpoint not found. Check backend URL."
                            400 -> {
                                val bodyError = response.body()?.error ?: "Invalid request data"
                                "Bad request: $bodyError"
                            }
                            else -> {
                                val bodyError = if (errorBody != null) {
                                    try {
                                        val errorJson = org.json.JSONObject(errorBody)
                                        errorJson.optString("error", "Failed to add product: ${response.code()}")
                                    } catch (e: Exception) {
                                        response.body()?.error ?: "Failed to add product: ${response.code()}"
                                    }
                                } else {
                                    response.body()?.error ?: "Failed to add product: ${response.code()}"
                                }
                                bodyError
                            }
                        }
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    }
                } else {
                    // Update existing product
                    val response = RetrofitInstance.api.adminUpdateProduct(
                        token = "Bearer $token",
                        request = AdminUpdateProductRequest(
                            product_id = productId,
                            title = title,
                            description = description.takeIf { it.isNotBlank() },
                            category = category,
                            image_url = imageUrl.takeIf { it.isNotBlank() },
                            specs = parseSpecs().takeIf { it.isNotEmpty() },
                            condition_label = conditionLabel.takeIf { it.isNotBlank() },
                            base_price = basePrice.toDoubleOrNull()
                        )
                    )
                    
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(context, "Product updated successfully! Changes will reflect on user side.", Toast.LENGTH_SHORT).show()
                        onSave()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMsg = if (errorBody != null) {
                            try {
                                val errorJson = org.json.JSONObject(errorBody)
                                errorJson.optString("error", "Failed to update product")
                            } catch (e: Exception) {
                                response.body()?.error ?: "Failed to update product"
                            }
                        } else {
                            response.body()?.error ?: "Failed to update product"
                        }
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminProductForm", "Exception: ${e.message}", e)
                val errorMsg = when {
                    e.message?.contains("Unable to resolve host", ignoreCase = true) == true -> 
                        "❌ Cannot connect to backend server!\n\nCheck:\n1. Backend URL: http://10.148.199.81/onlinebidding/\n2. XAMPP Apache is running\n3. Phone and PC on same WiFi\n4. Test in browser first"
                    e.message?.contains("timeout", ignoreCase = true) == true -> 
                        "⏱️ Request timeout!\n\nServer took too long to respond.\nCheck if backend is running."
                    e.message?.contains("Connection refused", ignoreCase = true) == true ->
                        "❌ Connection refused!\n\nBackend server is not running.\nStart XAMPP Apache service."
                    e.message?.contains("Failed to connect", ignoreCase = true) == true ->
                        "❌ Failed to connect!\n\nCannot reach backend server.\nCheck network and XAMPP."
                    else -> "❌ Error: ${e.message ?: "Unknown error occurred"}\n\nCheck backend connection and logs."
                }
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            } finally {
                isLoading = false
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color.Black, Color(0xFF0B0B0B))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onBack() }
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = if (productId == null) "Add Product" else "Edit Product",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFC107)
                )
            }
            
            Spacer(Modifier.height(24.dp))
            
            // Form Fields
            // Title field - Required
            Column {
                Text(
                    text = "Title *",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFFFC107),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                AdminFormField("", title) { title = it }
            }
            Spacer(Modifier.height(16.dp))
            
            AdminFormField("Description", description, isMultiLine = true) { description = it }
            Spacer(Modifier.height(16.dp))
            
            // Category Dropdown
            Text(
                text = "Category *",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFFFC107),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (category == cat) Color(0xFFFFC107) else Color(0xFF1A1A1A),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { category = cat }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (category == cat) Color.Black else Color(0xFFFFC107)
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            AdminFormField("Base Price (₹) *", basePrice) { basePrice = it.filter { c -> c.isDigit() || c == '.' } }
            Spacer(Modifier.height(16.dp))
            
            AdminFormField("Start Price (₹) (optional)", startPrice) { startPrice = it.filter { c -> c.isDigit() || c == '.' } }
            Spacer(Modifier.height(16.dp))
            
            AdminFormField("Image URL", imageUrl) { imageUrl = it }
            Spacer(Modifier.height(16.dp))
            
            AdminFormField("Condition", conditionLabel) { conditionLabel = it }
            Spacer(Modifier.height(16.dp))
            
            // Specs
            Text(
                text = "Specifications (Format: Key:Value, one per line)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFFFC107),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            AdminFormField("e.g., RAM:16GB\nStorage:512GB", specsText, isMultiLine = true) { specsText = it }
            
            Spacer(Modifier.height(32.dp))
            
            // Save Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        if (isLoading) Color(0xFF555555) else Color(0xFFFFC107),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable(enabled = !isLoading) { saveProduct() },
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = if (productId == null) "Add Product" else "Update Product",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
            
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
fun AdminFormField(
    label: String,
    value: String,
    isMultiLine: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFFFC107),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 15.sp
            ),
            singleLine = !isMultiLine,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1A1A1A), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    if (value.isEmpty() && label.contains(":")) {
                        Text(
                            text = label,
                            color = Color(0xFF666666),
                            fontSize = 15.sp
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

