package com.example.onlinebidding.screens.interest

import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ------------------------------------------------ */
/* DATA MODEL */
/* ------------------------------------------------ */
private data class InterestItemData(
    val key: String,
    val title: String,
    val drawableName: String
)

/* ------------------------------------------------ */
/* SCREEN */
/* ------------------------------------------------ */
@Composable
fun InterestSelection(
    onComplete: (List<String>) -> Unit,
    onNavigateToLaptopList: () -> Unit,
    onNavigateToMobileList: () -> Unit,
    onNavigateToComputerList: () -> Unit,
    onNavigateToMonitorList: () -> Unit,
    onNavigateToTabletList: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    val ctx = LocalContext.current
    val selected = remember { mutableStateMapOf<String, Boolean>() }

    fun toggle(key: String) {
        selected[key] = !(selected[key] ?: false)
    }

    val items = listOf(
        InterestItemData("laptops", "Laptops", "ic_laptops"),
        InterestItemData("mobiles", "Mobiles", "ic_mobiles"),
        InterestItemData("computers", "Computers", "ic_computer"),
        InterestItemData("tablets", "Tablets", "ic_tablets"),
        InterestItemData("monitors", "Monitors", "ic_monitors")
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF050505)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF221500), Color(0xFF0E0A00))
                    )
                )
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {

            /* ---------- HEADER + GRID ---------- */
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopCenter)
            ) {

                Text(
                    text = "Your Interests",
                    color = Color(0xFFFFD700),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Choose categories you want to follow — tap a tile to open its list",
                    color = Color(0xFFBFA77A),
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items) { item ->
                        val isSelected = selected[item.key] == true

                        InterestTilePolished(
                            ctx = ctx,
                            title = item.title,
                            drawableName = item.drawableName,
                            selected = isSelected,
                            onClick = {
                                when (item.key) {
                                    "laptops" -> onNavigateToLaptopList()
                                    "mobiles" -> onNavigateToMobileList()
                                    "computers" -> onNavigateToComputerList()
                                    "monitors" -> onNavigateToMonitorList()
                                    "tablets" -> onNavigateToTabletList()
                                }
                                toggle(item.key)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            /* ---------- BOTTOM CONTROLS ---------- */
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val count = selected.values.count { it }

                Text(
                    text = "$count selected",
                    color = Color(0xFFBFA77A),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Button(
                    onClick = {
                        onComplete(selected.filter { it.value }.keys.toList())
                    },
                    enabled = count > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = "Continue",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (onBack != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Back",
                        color = Color(0xFFFFD700),
                        modifier = Modifier
                            .clickable { onBack() }
                            .padding(top = 6.dp)
                    )
                }
            }
        }
    }
}

/* ------------------------------------------------ */
/* TILE — FIXED CIRCLE IMAGE */
/* ------------------------------------------------ */
@Composable
private fun InterestTilePolished(
    ctx: Context,
    title: String,
    drawableName: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val drawableId = rememberDrawableIdOrZero(ctx, drawableName)
    val bgColor = if (selected) Color(0xFF151515) else Color(0xFF0F0F0F)
    val elevation = if (selected) 12.dp else 6.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .animateContentSize(tween(220))
            .shadow(elevation)
            .clickable(onClick = onClick, role = Role.Button),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            val circleBrush = Brush.radialGradient(
                colors = if (selected)
                    listOf(Color(0xFF2B2B2B), Color(0xFF141414))
                else
                    listOf(Color(0xFF2E2E2E), Color(0xFF1B1B1B))
            )

            /* 🔥 PERFECT CIRCLE IMAGE FIX 🔥 */
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(circleBrush),
                contentAlignment = Alignment.Center
            ) {
                if (drawableId != 0) {
                    val painter: Painter = painterResource(id = drawableId)
                    Image(
                        painter = painter,
                        contentDescription = title,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),   // ✅ HARD CLIP
                        contentScale = ContentScale.Crop // ✅ REMOVE WHITE SQUARE
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                color = if (selected) Color(0xFFFFD700) else Color(0xFFBFA77A),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/* ------------------------------------------------ */
/* DRAWABLE RESOLVER */
/* ------------------------------------------------ */
@Composable
private fun rememberDrawableIdOrZero(
    context: Context,
    name: String
): Int {
    return remember(name) {
        context.resources.getIdentifier(name, "drawable", context.packageName)
    }
}
