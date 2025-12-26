package com.example.onlinebidding.screens.login.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------------- DATA MODEL ----------------
data class UserData(
    val name: String,
    val email: String,
    val phone: String?,
    val totalBids: Int,
    val wins: Int,
    val credits: Int
)

// ---------------- SCREEN ----------------
@Composable
fun ProfileScreen(
    userData: UserData,
    onBack: () -> Unit
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

        Column {

            /* -------- HEADER -------- */
            Row(
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

                Spacer(Modifier.width(16.dp))

                Text(
                    text = "Profile",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFFC107)
                )
            }

            Spacer(Modifier.height(32.dp))

            /* -------- AVATAR -------- */
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color(0x33212121))
                        .border(
                            1.dp,
                            Color(0x66FFC107),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = userData.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFFFC107)
                )

                Text(
                    text = "Premium Member",
                    fontSize = 12.sp,
                    color = Color(0x99FFC107)
                )
            }

            Spacer(Modifier.height(32.dp))

            /* -------- PERSONAL INFO -------- */
            SectionTitle("Personal Information")

            InfoCard(
                icon = Icons.Default.Person,
                label = "Full Name",
                value = userData.name
            )

            InfoCard(
                icon = Icons.Default.Email,
                label = "Email Address",
                value = userData.email
            )

            InfoCard(
                icon = Icons.Default.Phone,
                label = "Mobile Number",
                value = userData.phone ?: "Not provided"
            )

            Spacer(Modifier.height(28.dp))

            /* -------- ACCOUNT STATS -------- */
            SectionTitle("Account Stats")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatCard("Total Bids", userData.totalBids)
                StatCard("Wins", userData.wins)
                StatCard("Credits", userData.credits)
            }
        }
    }
}

/* ---------------- COMPONENTS ---------------- */

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFFFFC107),
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF141414), Color(0xFF0C0C0C))
                ),
                RoundedCornerShape(16.dp)
            )
            .border(
                1.dp,
                Color(0x33FFC107),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0x33212121))
                    .border(1.dp, Color(0x66FFC107), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp))
            }

            Spacer(Modifier.width(12.dp))

            Column {
                Text(label, fontSize = 12.sp, color = Color(0x99FFC107))
                Text(value, fontSize = 14.sp, color = Color(0xFFFFE082))
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: Int) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF141414), Color(0xFF0C0C0C))
                ),
                RoundedCornerShape(18.dp)
            )
            .border(
                1.dp,
                Color(0x33FFC107),
                RoundedCornerShape(18.dp)
            )
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFC107)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            color = Color(0x99FFC107)
        )
    }
}
