package com.example.onlinebidding.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AdminDashboard(
    onNavigateToCategory: (String) -> Unit,
    onCreateAdmin: () -> Unit,
    onLogout: () -> Unit
) {
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
                Text(
                    text = "Admin Dashboard",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFC107)
                )
                
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onLogout() }
                )
            }
            
            Spacer(Modifier.height(40.dp))
            
            // Product Categories
            Text(
                text = "Product Categories",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFFFC107)
            )
            
            Spacer(Modifier.height(20.dp))
            
            // Laptops
            AdminActionCard(
                title = "Laptops",
                description = "Manage laptop products",
                icon = Icons.Default.Home,
                onClick = { onNavigateToCategory("admin_laptop_list") }
            )
            
            Spacer(Modifier.height(16.dp))
            
            // Mobiles
            AdminActionCard(
                title = "Mobiles",
                description = "Manage mobile products",
                icon = Icons.Default.List,
                onClick = { onNavigateToCategory("admin_mobile_list") }
            )
            
            Spacer(Modifier.height(16.dp))
            
            // Computers
            AdminActionCard(
                title = "Computers",
                description = "Manage computer products",
                icon = Icons.Default.Star,
                onClick = { onNavigateToCategory("admin_computer_list") }
            )
            
            Spacer(Modifier.height(16.dp))
            
            // Monitors
            AdminActionCard(
                title = "Monitors",
                description = "Manage monitor products",
                icon = Icons.Default.ShoppingCart,
                onClick = { onNavigateToCategory("admin_monitor_list") }
            )
            
            Spacer(Modifier.height(16.dp))
            
            // Tablets
            AdminActionCard(
                title = "Tablets",
                description = "Manage tablet products",
                icon = Icons.Default.Favorite,
                onClick = { onNavigateToCategory("admin_tablet_list") }
            )
            
            Spacer(Modifier.height(32.dp))
            
            // Quick Actions
            Text(
                text = "Quick Actions",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFFFC107)
            )
            
            Spacer(Modifier.height(20.dp))
            
            // Create Admin Account Card
            AdminActionCard(
                title = "Create Admin Account",
                description = "Create a new admin account",
                icon = Icons.Default.Person,
                onClick = onCreateAdmin
            )
        }
    }
}

@Composable
fun AdminActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
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
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0x33FFC107), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color(0xFFAAAAAA)
                )
            }
            
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
