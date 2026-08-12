package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.foundation.focusable
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.example.model.IPTVCategory
import com.example.model.IPTVChannel
import com.example.model.EPGProgram
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.ui.IPTVUiState
import com.example.ui.IPTVViewModel
import com.example.ui.theme.Charcoal
import com.example.ui.theme.GraySurface
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenDim

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveTVScreen(viewModel: IPTVViewModel) {
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    val channels by viewModel.channels.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val blockedItems by viewModel.blockedItems.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val currentEPG by viewModel.currentEPG.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf("") }
    var isSortAZ by remember { mutableStateOf(true) }
    var showPinDialogForCat by remember { mutableStateOf<String?>(null) }
    var pinInput by remember { mutableStateOf("") }
    
    if (showPinDialogForCat != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showPinDialogForCat = null; pinInput = "" },
            title = { androidx.compose.material3.Text("Conteúdo Bloqueado", color = Color.White) },
            text = { 
                Column {
                    androidx.compose.material3.Text("Digite o PIN para acessar esta categoria:", color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.material3.OutlinedTextField(
                        value = pinInput,
                        onValueChange = { if (it.length <= 4) pinInput = it },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        singleLine = true,
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    viewModel.checkParentalPin(pinInput, onSuccess = {
                        selectedCategoryId = showPinDialogForCat!!
                        showPinDialogForCat = null
                        pinInput = ""
                    }, onFailure = {
                        pinInput = ""
                    })
                }) { androidx.compose.material3.Text("Desbloquear", color = NeonGreen) }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showPinDialogForCat = null; pinInput = "" }) {
                    androidx.compose.material3.Text("Cancelar", color = Color.Gray)
                }
            },
            containerColor = com.example.ui.theme.Charcoal
        )
    }

    // Filter categories to only LIVE ones
    val liveCategories = categories.filter { it.type == "LIVE" }
    
    // Set initial active category if empty
    LaunchedEffect(liveCategories) {
        if (selectedCategoryId.isEmpty()) {
            selectedCategoryId = "all_channels"
        }
    }

    var filteredChannels by remember { mutableStateOf<List<IPTVChannel>>(emptyList()) }
    
    LaunchedEffect(channels, categories, searchQuery, selectedCategoryId, isSortAZ, favorites) {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
            val liveCategoryIds = categories.filter { it.type == "LIVE" }.map { it.id }.toSet()
            var list = channels.filter { it.type == "LIVE" && liveCategoryIds.contains(it.categoryId) }
            
            if (selectedCategoryId.isNotEmpty() && selectedCategoryId != "all_favs" && selectedCategoryId != "all_channels") {
                list = list.filter { it.categoryId == selectedCategoryId }
            } else if (selectedCategoryId == "all_favs") {
                val favIds = favorites.filter { it.type == "LIVE" }.map { it.streamId }
                list = list.filter { it.id in favIds }
            }

            if (searchQuery.isNotEmpty()) {
                list = list.filter { it.name.contains(searchQuery, ignoreCase = true) }
            }

            filteredChannels = if (isSortAZ) {
                list.sortedBy { it.name }
            } else {
                list.sortedByDescending { it.name }
            }
        }
    }

    val isLandscape = androidx.compose.ui.platform.LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("live_tv_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize().then(if (uiState is IPTVUiState.Loading) Modifier.blur(16.dp) else Modifier)) {


            // BOTTOM CONTENT
            Row(modifier = Modifier.fillMaxSize()) {
                // Left Sidebar for Categories
                LazyColumn(
                    modifier = Modifier
                        .width(200.dp)
                        .fillMaxHeight()
                        .background(Color(0xFF050505)),
                    contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "Categorias",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                        )
                    }
                    item {
                        CategoryBadge(
                            title = "🌐 TODOS CANAIS",
                            selected = selectedCategoryId == "all_channels"
                        ) { selectedCategoryId = "all_channels" }
                    }
                    item {
                        CategoryBadge(
                            title = "⭐ FAVORITOS",
                            selected = selectedCategoryId == "all_favs"
                        ) { selectedCategoryId = "all_favs" }
                    }
                    items(liveCategories, key = { it.id }) { cat ->
                        CategoryBadge(
                            title = cat.name,
                            selected = selectedCategoryId == cat.id
                        ) { 
                                if (viewModel.isCategoryBlocked(cat.id, cat.name)) {
                                    showPinDialogForCat = cat.id
                                } else {
                                    selectedCategoryId = cat.id 
                                }
                            }
                    }
                }
                
                // Middle Content Area: Channels List
                Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    // Header Sort
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = { isSortAZ = !isSortAZ },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Charcoal)
                        ) {
                            Icon(
                                imageVector = if (isSortAZ) Icons.Rounded.SortByAlpha else Icons.Rounded.Sort,
                                contentDescription = "Sort",
                                tint = NeonGreen
                            )
                        }
                    }

                    // Channels List
                    if (filteredChannels.isEmpty() && uiState !is IPTVUiState.Loading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .width(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Rounded.SearchOff, contentDescription = "Empty", tint = Color.Gray, modifier = Modifier.size(64.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Nenhum canal encontrado", color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            contentPadding = PaddingValues(bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredChannels, key = { it.id }) { ch ->
                                val isFav = favorites.any { it.streamId == ch.id && it.type == "LIVE" }
                                val isSelected = false
                                LiveChannelListItem(
                                    channel = ch,
                                    isFav = isFav,
                                    isSelected = isSelected,
                                    onToggleFav = { viewModel.toggleFavorite(ch) },
                                    onClick = { 
                                        viewModel.selectChannel(ch)
                                focusManager.clearFocus() 
                                    }
                                )
                            }
                        }
                    }
                }


            }
        }
    }
}
@Composable
fun LiveChannelCard(channel: IPTVChannel, isFav: Boolean, onToggleFav: () -> Unit, onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .border(if (isFocused) 2.dp else 0.dp, if (isFocused) NeonGreen else Color.Transparent, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("live_channel_card"),
        colors = CardDefaults.cardColors(containerColor = Charcoal)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(Color.Black)
            ) {
                SubcomposeAsyncImage(
                    model = channel.logo,
                    contentDescription = channel.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    error = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = channel.name.take(2).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                )
                // Favorite Button Overlay
                IconButton(
                    onClick = onToggleFav,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(28.dp)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                ) {
                    Icon(
                        imageVector = if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (isFav) Color.Red else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                // Resolution indicator
                if (channel.resolution.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(NeonGreenDim)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(channel.resolution, color = NeonGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = channel.name,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                // EPG Details
                if (channel.epgTitle.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Rounded.PlayCircle, contentDescription = "EPG", tint = NeonGreen, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = channel.epgTitle,
                            color = NeonGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                if (channel.epgNextTitle.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Rounded.Update, contentDescription = "EPG Next", tint = Color.Gray, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = channel.epgNextTitle,
                            color = Color.Gray,
                            fontSize = 9.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LiveChannelListItem(
    channel: IPTVChannel,
    isFav: Boolean,
    isSelected: Boolean = false,
    onToggleFav: () -> Unit,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Charcoal)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .border(if (isFocused || isSelected) 2.dp else 1.dp, if (isFocused || isSelected) NeonGreen else NeonGreen.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (channel.logo.isNotEmpty()) {
            AsyncImage(
                model = channel.logo,
                contentDescription = channel.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(com.example.ui.theme.MatteBlack)
                    .padding(8.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(com.example.ui.theme.MatteBlack),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Rounded.Tv, contentDescription = null, tint = Color.Gray)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.width(300.dp)) {
            Text(
                text = channel.name,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = channel.categoryName,
                color = Color.Gray,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = onToggleFav) {
            Icon(
                imageVector = if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFav) Color.Red else Color.Gray
            )
        }
    }
}
@Composable
fun CategoryBadge(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) NeonGreenDim else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (selected) NeonGreen else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = title,
            color = if (selected) NeonGreen else Color.LightGray,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}


        