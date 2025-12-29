package com.example.onlinebidding.screens.products

import com.example.onlinebidding.R
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

/* ---------------- DATA ---------------- */
data class Computer(
    val name: String,
    val specs: String,
    val rating: String,
    val price: String,
    val imageRes: Int
)

val computers = listOf(
    Computer(
        "Custom Gaming PC RTX",
        "64GB DDR5 · 2TB Gen5 NVMe + 4TB HDD",
        "4.9",
        "₹2,85,000",
        R.drawable.ic_pcgamming
    ),
    Computer(
        "Mac Studio M2 Ultra",
        "192GB Unified Memory · 4TB SSD",
        "5.0",
        "₹3,10,000",
        R.drawable.ic_macstudio
    ),
    Computer(
        "HP Z8 G5 Workstation",
        "128GB ECC DDR5 · 4TB NVMe RAID",
        "4.7",
        "₹1,95,000",
        R.drawable.ic_hp_z5
    )
)

/* ---------------- SCREEN ---------------- */
@Composable
fun PremiumComputerList(
    navController: NavHostController,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF050505), Color(0xFF111111))
                )
            )
            .padding(16.dp)
    ) {

        /* ---------- HEADER ---------- */
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            BasicText(
                text = "←",
                style = TextStyle(
                    color = Color(0xFFFFD54F),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(end = 14.dp)
            )

            BasicText(
                text = "Premium Computers",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        /* ---------- LIST ---------- */
        LazyColumn(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            itemsIndexed(computers) { index, computer ->
                PremiumComputerCard(
                    computer = computer,
                    navController = navController,
                    index = index
                )
            }
        }
    }
}

/* ---------------- CARD ---------------- */
@Composable
private fun PremiumComputerCard(
    computer: Computer,
    navController: NavHostController,
    index: Int
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 12.dp,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1A1A1A), Color(0xFF0D0D0D))
                    )
                )
                .border(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(Color(0xFFFFD54F), Color(0xFFFFA000))
                    ),
                    RoundedCornerShape(22.dp)
                )
                .padding(18.dp)
        ) {

            /* ---------- TOP ---------- */
            Row(verticalAlignment = Alignment.CenterVertically) {

                Image(
                    painter = painterResource(computer.imageRes),
                    contentDescription = computer.name,
                    modifier = Modifier
                        .size(76.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {

                    BasicText(
                        text = computer.name,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    BasicText(
                        text = computer.specs,
                        style = TextStyle(
                            color = Color(0xFFBBBBBB),
                            fontSize = 13.sp
                        )
                    )
                }

                BasicText(
                    text = "VIEW",
                    style = TextStyle(
                        color = Color(0xFFFFD54F),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable {
                        navController.navigate("computer_details/$index")
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            /* ---------- PRICE & RATING ---------- */
            Row(verticalAlignment = Alignment.CenterVertically) {
                BasicText(
                    text = "⭐ ${computer.rating}",
                    style = TextStyle(
                        color = Color(0xFFFFD54F),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.width(16.dp))

                BasicText(
                    text = computer.price,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            /* ---------- ACTIONS ---------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PremiumButton(
                    text = "Credits",
                    colors = listOf(Color(0xFF00E676), Color(0xFF00C853))
                )

                PremiumButton(
                    text = "Bid Now",
                    colors = listOf(Color(0xFFFFD54F), Color(0xFFFFA000))
                )
            }
        }
    }
}

/* ---------------- BUTTON ---------------- */
@Composable
fun PremiumButton(
    text: String,
    colors: List<Color>
) {
    Box(
        modifier = Modifier
            .width(140.dp)
            .height(44.dp)
            .shadow(6.dp, RoundedCornerShape(24.dp))
            .background(
                brush = Brush.horizontalGradient(colors),
                shape = RoundedCornerShape(24.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = text,
            style = TextStyle(
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        )
    }
}
