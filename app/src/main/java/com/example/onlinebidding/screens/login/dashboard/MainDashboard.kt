package com.example.onlinebidding.screens.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onlinebidding.R

@Composable
fun MainDashboard(
    userName: String,
    onNavigate: (String) -> Unit
) {
    val scroll = rememberScrollState()

    val categories = listOf(
        DashboardCategory("Laptops", R.drawable.ic_laptops, 3, "laptop_list"),
        DashboardCategory("Mobiles", R.drawable.ic_mobiles, 3, "mobile_list"),
        DashboardCategory("Computers", R.drawable.ic_computer, 3, "computer_list"),
        DashboardCategory("Monitors", R.drawable.ic_monitors, 3, "monitor_list"),
        DashboardCategory("Tablets", R.drawable.ic_tablets, 3, "tablet_list")
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

                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.clickable {
                        onNavigate("profile")
                    }
                )
            }

            Spacer(Modifier.height(24.dp))

            /* ---------------- SEARCH ---------------- */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF1A1A1A), Color(0xFF151515))
                        ),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onNavigate("search") }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0x99FFD700),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Search auctions...",
                        fontSize = 15.sp,
                        color = Color(0x99FFD700)
                    )
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
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFFFD700)
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
    val imageRes: Int,
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
                RoundedCornerShape(16.dp)
            )
            .clickable { onNavigate() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon container - perfectly fitted circle
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0x66FFD700), Color(0x33FF9800))
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(category.imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                category.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFFFE082)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "${category.count} live auctions",
                fontSize = 13.sp,
                color = Color(0x99FFD700)
            )
        }

        Spacer(Modifier.width(8.dp))
        
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = Color(0x99FFD700),
            modifier = Modifier.size(20.dp)
        )
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
            .padding(18.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFFFC107),
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.height(10.dp))
        Text(
            title,
            color = Color(0xFFFFE082),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(2.dp))
        Text(
            subtitle,
            color = Color(0x99FFD700),
            fontSize = 12.sp
        )
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
