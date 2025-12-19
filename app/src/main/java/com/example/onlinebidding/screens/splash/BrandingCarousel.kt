package com.example.onlinebidding.screens.splash

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Polished BrandingCarousel composable.
 * - onComplete invoked when finishes slides or taps Finish.
 * - Shows Skip (top-right), animated gold orb, slide text, dots, and Next/Finish button.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BrandingCarousel(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    autoAdvanceSeconds: Int = 5 // optional auto advance per slide
) {
    val slides = remember {
        listOf(
            SlideData(
                title = "Bid Smart,",
                subtitle = "Win Big.",
                description = "Access premium products from verified sellers"
            ),
            SlideData(
                title = "Where Every",
                subtitle = "Second Counts.",
                description = "Real-time auctions with live countdowns"
            ),
            SlideData(
                title = "Your Price.",
                subtitle = "Your Power.",
                description = "Compete with limited bidders for the best deals"
            )
        )
    }

    var index by rememberSaveable { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    val currentOnComplete by rememberUpdatedState(onComplete)

    // background orb animation
    val infinite = rememberInfiniteTransition()
    val orbScale by infinite.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val orbGlowAlpha by infinite.animateFloat(
        initialValue = 0.06f,
        targetValue = 0.26f,
        animationSpec = infiniteRepeatable(tween(2600, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )

    // shimmer for button
    val shimmerProgress by infinite.animateFloat(
        initialValue = -1f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing))
    )

    // auto-advance slides
    LaunchedEffect(index) {
        // auto advance only if not last slide
        if (index < slides.lastIndex) {
            delay((autoAdvanceSeconds * 1000).toLong())
            index = (index + 1).coerceAtMost(slides.lastIndex)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top row: Spacer and Skip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Skip",
                color = Color(0xFFFFC857),
                fontSize = 14.sp,
                modifier = Modifier
                    .clickable {
                        // jumping to finish (complete)
                        currentOnComplete()
                    }
                    .padding(6.dp)
            )
        }

        // Center content with animated orb + slide text
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // animated glowing orb in background
            Canvas(modifier = Modifier
                .wrapContentSize()
                .size((220 * orbScale).dp)
                .offset(y = (-20).dp)
            ) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x44FFC107), Color.Transparent),
                        center = Offset(cx, cy),
                        radius = size.minDimension * 0.6f
                    ),
                    radius = size.minDimension * 0.5f,
                    center = Offset(cx, cy),
                    style = Fill
                )
            }

            // Slide text with small animated content change
            AnimatedContent(targetState = index) { i ->
                val s = slides[i]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = "${s.title}\n${s.subtitle}",
                        color = Color(0xFFFFD700),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = s.description,
                        color = Color(0xFFBFA77A),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }

        // Dots indicator
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 18.dp)
        ) {
            slides.forEachIndexed { i, _ ->
                val active = i == index
                val dotWidth by animateFloatAsState(targetValue = if (active) 28f else 8f)
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width((dotWidth).dp)
                        .shadow(if (active) 6.dp else 0.dp, RoundedCornerShape(6.dp))
                        .background(
                            brush = if (active)
                                Brush.horizontalGradient(listOf(Color(0xFFFFB300), Color(0xFFFF9800)))
                            else Brush.horizontalGradient(listOf(Color(0x11FFD700), Color(0x11FFD700))),
                            shape = RoundedCornerShape(6.dp)
                        )
                )
            }
        }

        // Next / Finish button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(16.dp, RoundedCornerShape(30.dp))
                .drawBehind {
                    // glow under the button
                    val bandRadius = size.minDimension * 0.6f
                    drawCircle(
                        brush = Brush.radialGradient(listOf(Color(0x33FFC107), Color.Transparent)),
                        radius = bandRadius,
                        center = Offset(size.width / 2f, -size.height * 0.8f)
                    )
                }
                .background(
                    brush = Brush.horizontalGradient(listOf(Color(0xFFFFB300), Color(0xFFFF9800))),
                    shape = RoundedCornerShape(30.dp)
                )
                .clickable {
                    if (index < slides.lastIndex) index++ else currentOnComplete()
                },
            contentAlignment = Alignment.Center
        ) {
            // shimmer overlay
            Canvas(modifier = Modifier.matchParentSize()) {
                val bandWidth = size.width * 0.28f
                val x = (shimmerProgress + 1f) / 2f * (size.width + bandWidth) - bandWidth
                drawRect(
                    color = Color.White.copy(alpha = 0.10f),
                    topLeft = Offset(x, 0f),
                    size = Size(bandWidth, size.height)
                )
            }

            Text(
                text = if (index == slides.lastIndex) "Finish" else "Next",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private data class SlideData(val title: String, val subtitle: String, val description: String)
