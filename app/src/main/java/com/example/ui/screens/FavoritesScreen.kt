package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.ui.components.FallbackAsyncImage
import com.example.model.IPTVChannel
import com.example.ui.IPTVViewModel
import com.example.ui.theme.Charcoal
import com.example.ui.theme.NeonGreen

@Composable
fun FavoritesScreen(viewModel: IPTVViewModel) {
    val favorites by viewModel.favorites.collectAsState()
    val channels by viewModel.channels.collectAsState()
    
    val favoriteChannels = favorites.mapNotNull { fav ->
        if (fav.type == "SERIES") {
            val series = viewModel.seriesList.value.find { it.id == fav.streamId }
            if (series != null) {
                IPTVChannel(
                    id = series.id,
                    name = series.name,
                    url = "",
                    logo = series.cover,
                    type = "SERIES"
                )
            } else {
                IPTVChannel(
                    id = fav.streamId,
                    name = fav.name,
                    url = fav.streamUrl,
                    logo = fav.logoUrl,
                    type = fav.type
                )
            }
        } else {
            channels.find { it.id == fav.streamId } ?: IPTVChannel(
                id = fav.streamId,
                name = fav.name,
                url = fav.streamUrl,
                logo = fav.logoUrl,
                type = fav.type
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "MEUS FAVORITOS",
            color = NeonGreen,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (favoriteChannels.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Você ainda não tem favoritos.", color = Color.Gray)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoriteChannels) { ch ->
                    FavoriteCard(
                        channel = ch,
                        onClick = {
                            if (ch.type == "SERIES") {
                                val series = viewModel.seriesList.value.find { it.id == ch.id }
                                if (series != null) {
                                    viewModel.selectSeries(series)
                                    viewModel.navigateTo(com.example.ui.Screen.SERIES)
                                }
                            } else {
                                viewModel.selectChannel(ch)
                            }
                        },
                        onRemove = {
                            if (ch.type == "SERIES") {
                                val series = viewModel.seriesList.value.find { it.id == ch.id }
                                if (series != null) {
                                    viewModel.toggleFavoriteSeries(series)
                                } else {
                                    viewModel.toggleFavorite(ch)
                                }
                            } else {
                                viewModel.toggleFavorite(ch)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteCard(channel: IPTVChannel, onClick: () -> Unit, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Charcoal)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (channel.type == "LIVE") 100.dp else 180.dp)
            ) {
                FallbackAsyncImage(
                    title = channel.name,
                    logoUrl = channel.logo,
                    type = channel.type,
                    contentScale = if (channel.type == "LIVE") ContentScale.Fit else ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().padding(if (channel.type == "LIVE") 12.dp else 0.dp)
                )
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(32.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                ) {
                    Icon(imageVector = Icons.Rounded.Delete, contentDescription = "Remover", tint = Color.Red, modifier = Modifier.size(18.dp))
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = channel.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (channel.type == "LIVE") "Canal" else if (channel.type == "MOVIE") "Filme" else "Série",
                    color = NeonGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
