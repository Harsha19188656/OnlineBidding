package com.example.onlinebidding.screens.products

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.onlinebidding.R
import com.example.onlinebidding.screens.products.CreditsState

/* ---------------- DATA ---------------- */
data class MonitorProduct(
    val name: String,
    val specs: String,
    val rating: Double,
    val price: String,
    val image: Int
)

val monitorList = listOf(
    MonitorProduct("Samsung Odyssey G9 49\"", "Premium Device", 4.8, "₹95,000", R.drawable.ic_monitor_samsung),
    MonitorProduct("LG UltraFine 5K 27\"", "Premium Device", 4.9, "₹68,000", R.drawable.ic_monitor_lg),
    MonitorProduct("Dell UltraSharp U3423WE", "Premium Device", 4.7, "₹52,000", R.drawable.ic_monitor_dell)
)

/* ---------------- SCREEN ---------------- */
@Composable
fun MonitorList(
    navController: NavHostController,
    onBack: () -> Unit = {},
    userToken: String? = null,
    userRole: String? = null
) {
    val isAdmin = userRole == "admin"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color.Black, Color(0xFF0B0B0B))
                )
            )
            .padding(16.dp)
    ) {

        /* ---------- HEADER ---------- */
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFFFFC107),
                modifier = Modifier
                    .size(26.dp)
                    .clickable { onBack() }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Monitors",
                color = Color(0xFFFFC107),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = monitorList.size.toString(),
                    color = Color(0xFFFFC107),
                    fontWeight = FontWeight.Bold
                )
                
                // Admin-only Add button
                if (isAdmin && userToken != null) {
                    IconButton(onClick = {
                        navController.navigate("admin_product_form")
                    }) {
                        Icon(Icons.Default.Add, "Add Monitor", tint = Color(0xFFFFC107))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        /* ---------- SEARCH (UI ONLY) ---------- */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(
                    1.dp,
                    Color(0xFF3A2A00),
                    RoundedCornerShape(14.dp)
                )
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, null, tint = Color(0xFFFFC107))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Search monitors...", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(18.dp))

        /* ---------- LIST ---------- */
        LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            items(monitorList.size) { index ->
                MonitorCard(
                    monitor = monitorList[index],
                    navController = navController,
                    index = index,
                    isAdmin = isAdmin,
                    userToken = userToken,
                    onDelete = {
                        // For now, just show a message since monitor list uses hardcoded data
                        // TODO: Connect to backend and implement actual delete
                    }
                )
            }
        }
    }
}

/* ---------------- CARD ---------------- */
@Composable
fun MonitorCard(
    monitor: MonitorProduct,
    navController: NavHostController,
    index: Int,
    isAdmin: Boolean = false,
    userToken: String? = null,
    onDelete: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val itemId = "monitor_$index"
    val hasCredits = com.example.onlinebidding.screens.products.CreditsState.hasCreditsForItem(itemId)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1A1A1A), Color(0xFF0D0D0D))
                ),
                RoundedCornerShape(22.dp)
            )
            .border(
                1.dp,
                Color(0xFF3A2A00),
                RoundedCornerShape(22.dp)
            )
            .padding(16.dp)
    ) {

        /* ---------- TOP ---------- */
        Row(verticalAlignment = Alignment.CenterVertically) {

            Image(
                painter = painterResource(id = monitor.image),
                contentDescription = monitor.name,
                modifier = Modifier
                    .size(78.dp)
                    .clip(RoundedCornerShape(14.dp))
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = monitor.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Admin-only Delete button
                        if (isAdmin && userToken != null) {
                            Icon(
                                Icons.Default.Delete,
                                "Delete",
                                tint = Color.Red,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable {
                                        android.widget.Toast.makeText(context, "Delete functionality requires backend connection", android.widget.Toast.LENGTH_SHORT).show()
                                        onDelete()
                                    }
                            )
                        }
                        Text(
                            text = "View",
                            color = Color(0xFFFFC107),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                navController.navigate("monitor_details/$index")
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = monitor.specs,
                    color = Color(0xFFB0B0B0),
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = monitor.rating.toString(),
                        color = Color(0xFFFFC107),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = monitor.price,
                        color = Color(0xFFFFC107),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        /* ---------- ACTIONS ---------- */
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Button(
                onClick = {
                    navController.navigate("credits/monitor/$index/${monitor.name}")
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    if (hasCredits) "Credits Added" else "Credits",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    if (hasCredits) {
                        navController.navigate("monitor_auction_detail/$index")
                    } else {
                        navController.navigate("credits/monitor/$index/${monitor.name}")
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Bid", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}