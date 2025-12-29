package com.example.onlinebidding.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onlinebidding.api.*
import kotlinx.coroutines.launch

@Composable
fun AdminProductList(
    token: String,
    category: String? = null,
    onBack: () -> Unit,
    onEditProduct: (Int) -> Unit,
    onAddProduct: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var products by remember { mutableStateOf<List<AdminProductItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf(category ?: "all") }
    
    val categories = listOf("all", "laptop", "mobile", "computer", "monitor", "tablet")
    
    fun loadProducts() {
        isLoading = true
        errorMessage = null
        scope.launch {
            try {
                val cat = if (selectedCategory == "all") null else selectedCategory
                val response = RetrofitInstance.api.adminListProducts(
                    token = "Bearer $token",
                    category = cat
                )
                
                if (response.isSuccessful && response.body()?.success == true) {
                    products = response.body()?.products ?: emptyList()
                } else {
                    errorMessage = response.body()?.error ?: "Failed to load products"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
    
    LaunchedEffect(selectedCategory) {
        loadProducts()
    }
    
    // Reload when returning to this screen (from form)
    LaunchedEffect(Unit) {
        // This will trigger reload when screen is first shown
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color.Black, Color(0xFF0B0B0B))
                )
            )
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                        text = "Manage Products",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFC107)
                    )
                }
                
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Product",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onAddProduct() }
                )
            }
            
            Spacer(Modifier.height(24.dp))
            
            // Category Filter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (selectedCategory == cat)
                                    Color(0xFFFFC107)
                                else
                                    Color(0xFF1A1A1A),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedCategory = cat }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (selectedCategory == cat) Color.Black else Color(0xFFFFC107)
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(20.dp))
            
            // Loading or Error
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFFFC107))
                }
            } else if (errorMessage != null) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage ?: "Error loading products",
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            } else if (products.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No products found",
                        color = Color(0xFFAAAAAA),
                        fontSize = 16.sp
                    )
                }
            } else {
                // Product List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(products) { product ->
                        AdminProductListItem(
                            product = product,
                            onEdit = { onEditProduct(product.id) },
                            onDelete = {
                                scope.launch {
                                    try {
                                        val response = RetrofitInstance.api.adminDeleteProduct(
                                            token = "Bearer $token",
                                            request = AdminDeleteProductRequest(product.id)
                                        )
                                        if (response.isSuccessful && response.body()?.success == true) {
                                            Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_SHORT).show()
                                            // Reload list
                                            loadProducts()
                                        } else {
                                            Toast.makeText(context, response.body()?.error ?: "Delete failed", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminProductListItem(
    product: AdminProductItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF1A1A1A), Color(0xFF0F0F0F))
                ),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Product Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = product.category.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                    fontSize = 14.sp,
                    color = Color(0xFFFFC107)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "₹${product.base_price}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFFFE082)
                )
                if (product.description != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = product.description.take(50) + if (product.description.length > 50) "..." else "",
                        fontSize = 12.sp,
                        color = Color(0xFFAAAAAA)
                    )
                }
            }
            
            // Actions
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onEdit() }
                )
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onDelete() }
                )
            }
        }
    }
}

