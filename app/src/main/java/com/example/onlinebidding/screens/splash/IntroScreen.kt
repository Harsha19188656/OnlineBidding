package splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun IntroScreen(
    onComplete: () -> Unit
) {
    // Navigate after delay
    LaunchedEffect(Unit) {
        delay(2500)
        onComplete()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "hammerHit")

    /* ---------- Glow pulse on hit ---------- */
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1.2f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                1.2f at 0
                1.6f at 400   // impact glow
                1.25f at 700
                1.2f at 1200
            }
        ),
        label = "glowScale"
    )

    /* ---------- Hammer hit rotation ---------- */
    val hammerRotation by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                -20f at 0
                -5f at 300
                35f at 450    // HIT
                25f at 650
                -20f at 1200
            }
        ),
        label = "hammerRotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF1A1A1A),
                        Color.Black,
                        Color(0xFF1A1A1A)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        /* ---------- Impact glow orb ---------- */
        Box(
            modifier = Modifier
                .size(300.dp)
                .scale(glowScale)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color(0x44FFD700),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )

        /* ---------- Radiating rings ---------- */
        repeat(3) { index ->
            val ringScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 3f + index,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1800,
                        delayMillis = index * 300
                    )
                ),
                label = "ring$index"
            )

            Box(
                modifier = Modifier
                    .size(150.dp)
                    .scale(ringScale)
                    .border(
                        1.dp,
                        Color(0x33FFD700),
                        CircleShape
                    )
            )
        }

        /* ---------- AUCTION HAMMER (HIT ANIMATION) ---------- */
        Canvas(
            modifier = Modifier
                .size(120.dp)
                .rotate(hammerRotation)
        ) {
            rotate(45f, pivot = center) {

                // Hammer head (top)
                drawRoundRect(
                    color = Color(0xFFFFC107),
                    topLeft = Offset(size.width * 0.15f, size.height * 0.15f),
                    size = Size(size.width * 0.45f, size.height * 0.18f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(18f)
                )

                // Hammer head (bottom)
                drawRoundRect(
                    color = Color(0xFFFFB300),
                    topLeft = Offset(size.width * 0.15f, size.height * 0.33f),
                    size = Size(size.width * 0.45f, size.height * 0.18f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(18f)
                )

                // Handle
                drawRoundRect(
                    color = Color(0xFFFFC107),
                    topLeft = Offset(size.width * 0.45f, size.height * 0.42f),
                    size = Size(size.width * 0.45f, size.height * 0.12f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(22f)
                )
            }
        }

        /* ---------- Corner accents ---------- */
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(96.dp)
                .border(
                    2.dp,
                    Color(0x66FFD700),
                    RoundedCornerShape(topStart = 24.dp)
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(96.dp)
                .border(
                    2.dp,
                    Color(0x66FFD700),
                    RoundedCornerShape(bottomEnd = 24.dp)
                )
        )
    }
}
