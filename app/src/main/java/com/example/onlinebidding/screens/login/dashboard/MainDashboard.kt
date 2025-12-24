package com.example.onlinebidding.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainDashboard(
    userName: String,
    onNavigate: (String) -> Unit
) {
    val scroll = rememberScrollState()

    val categories = listOf(
        DashboardCategory("Laptops", Icons.Default.Home, 24, "laptop_list"),
        DashboardCategory("Mobiles", Icons.Default.List, 36, "mobile_list"),
        DashboardCategory("Computers", Icons.Default.Star, 18, "computer_list"),
        DashboardCategory("Monitors", Icons.Default.ShoppingCart, 15, "monitor_list"),
        DashboardCategory("Tablets", Icons.Default.Favorite, 22, "tablet_list")
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(20.dp)
        ) {

            /* ---------------- HEADER ---------------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Welcome back,",
                        fontSize = 13.sp,
                        color = Color(0x99FFD700)
                    )
                    Text(
                        text = userName,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color(0xFFFFC107)
                    )
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.clickable {
                            onNavigate("profile")
                        }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            /* ---------------- SEARCH ---------------- */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color(0xFF1A1A1A), RoundedCornerShape(16.dp))
                    .clickable { onNavigate("search") }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, null, tint = Color(0x99FFD700))
                    Spacer(Modifier.width(12.dp))
                    Text("Search auctions...", color = Color(0x66FFD700))
                }
            }

            Spacer(Modifier.height(24.dp))

            /* ---------------- QUICK ACTIONS ---------------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Trending",
                    subtitle = "Hot auctions",
                    icon = Icons.Default.Star,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate("trending_auctions") }
                )

                QuickActionCard(
                    title = "Flash",
                    subtitle = "Ending soon",
                    icon = Icons.Default.Favorite,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate("flash") }
                )
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = "Browse Categories",
                fontSize = 18.sp,
                color = Color(0xCCFFD700)
            )

            Spacer(Modifier.height(16.dp))

            categories.forEach {
                CategoryRow(it) { onNavigate(it.destination) }
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(24.dp))

            StatsBanner()
        }
    }
}

/* ---------------- MODELS ---------------- */

data class DashboardCategory(
    val name: String,
    val icon: ImageVector,
    val count: Int,
    val destination: String
)

/* ---------------- COMPONENTS ---------------- */

@Composable
private fun CategoryRow(
    category: DashboardCategory,
    onNavigate: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF1A1A1A), Color(0xFF0E0E0E))
                ),
                RoundedCornerShape(18.dp)
            )
            .clickable { onNavigate() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0x55FFD700), Color(0x22FF9800))
                    ),
                    RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(category.icon, null, tint = Color(0xFFFFD700))
        }

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(category.name, fontSize = 18.sp, color = Color(0xFFFFE082))
            Text(
                "${category.count} live auctions",
                fontSize = 13.sp,
                color = Color(0x99FFD700)
            )
        }

        Icon(Icons.Default.ArrowForward, null, tint = Color(0x66FFD700))
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A1F00), Color(0xFF120C00))
                ),
                RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Icon(icon, null, tint = Color(0xFFFFC107))
        Spacer(Modifier.height(8.dp))
        Text(title, color = Color(0xFFFFE082), fontSize = 16.sp)
        Text(subtitle, color = Color(0x99FFD700), fontSize = 12.sp)
    }
}

@Composable
private fun StatsBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0x332196F3), Color.Transparent)
                ),
                RoundedCornerShape(18.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem("78", "Active Now")
        StatItem("15", "Max Bidders")
        StatItem("2m", "Avg Time")
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 22.sp, color = Color(0xFFFFC107))
        Text(label, fontSize = 12.sp, color = Color(0x99FFD700))
    }
}
