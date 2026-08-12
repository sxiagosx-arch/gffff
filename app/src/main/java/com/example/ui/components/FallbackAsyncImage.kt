package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.util.ArtworkFetcher

@Composable
fun FallbackAsyncImage(
    title: String,
    logoUrl: String,
    type: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    var validUrl by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(title, logoUrl) {
        isLoading = true
        validUrl = ArtworkFetcher.getValidImageUrl(logoUrl, title, type)
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = modifier.background(Color.DarkGray))
    } else if (validUrl.isNullOrEmpty()) {
        Box(modifier = modifier.background(Color.DarkGray), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (type == "MOVIE") Icons.Rounded.Movie else Icons.Rounded.Tv, 
                contentDescription = null, 
                tint = Color.Gray, 
                modifier = Modifier.size(32.dp)
            )
        }
    } else {
        SubcomposeAsyncImage(
            model = validUrl,
            contentDescription = title,
            contentScale = contentScale,
            modifier = modifier,
            error = {
                Box(modifier = modifier.background(Color.DarkGray), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (type == "MOVIE") Icons.Rounded.Movie else Icons.Rounded.Tv, 
                        contentDescription = null, 
                        tint = Color.Gray, 
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        )
    }
}
