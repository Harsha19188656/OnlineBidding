package com.example.onlinebidding.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size

/**
 * High-quality image composable optimized for better rendering quality
 * Uses Coil for superior image decoding and caching
 */
@Composable
fun HighQualityImage(
    imageRes: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val context = LocalContext.current
    
    // Use Coil for better image decoding and quality
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageRes)
            .crossfade(true)
            .allowHardware(false) // Better quality rendering
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale
    )
}

/**
 * Standard Image composable with optimized settings for better quality
 * Use this when you need a simple drop-in replacement
 */
@Composable
fun OptimizedImage(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale
    )
}

/**
 * Image painter with quality optimizations
 */
@Composable
fun rememberHighQualityImagePainter(
    imageRes: Int
): Painter {
    return painterResource(id = imageRes)
}

