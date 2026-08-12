package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.ui.components.FallbackAsyncImage
import com.example.model.IPTVChannel
import com.example.model.IPTVSeries
import com.example.ui.IPTVUiState
import com.example.ui.IPTVViewModel
import com.example.ui.theme.Charcoal
import com.example.ui.theme.NeonGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesScreen(viewModel: IPTVViewModel) {
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    val seriesList by viewModel.seriesList.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val blockedItems by viewModel.blockedItems.collectAsState()
    val selectedSeries by viewModel.selectedSeries.collectAsState()
    val seriesSeasons by viewModel.seriesSeasons.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val watchHistory by viewModel.watchHistory.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showPinDialogForCat by remember { mutableStateOf<String?>(null) }
    var pinInput by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf("") }

    // Dialogo PIN omitido para encurtar

    var selectedSeasonNum by remember { mutableIntStateOf(1) }

    val sortedCategories = remember(categories, blockedItems) {
        val seriesCats = categories.filter { cat -> cat.type == "SERIES" }
        seriesCats.sortedBy { it.name }
    }

    LaunchedEffect(sortedCategories) {
        if (selectedCategoryId.isEmpty()) {
            selectedCategoryId = "all_series"
        }
    }

    var filteredSeries by remember { mutableStateOf<List<IPTVSeries>>(emptyList()) }

    LaunchedEffect(seriesList, searchQuery, selectedCategoryId) {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
            var list = seriesList

            if (selectedCategoryId.isNotEmpty() && selectedCategoryId != "all_series") {
                list = list.filter { it.categoryId == selectedCategoryId }
            }

            if (searchQuery.isNotEmpty()) {
                list = list.filter { it.name.contains(searchQuery, ignoreCase = true) }
            }

            filteredSeries = list.sortedBy { it.name }
        }
    }

    val isLandscape = androidx.compose.ui.platform.LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val columnsCount = if (isLandscape) 5 else 2

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("series_screen")
    ) {
        if (selectedSeries == null) {
            Row(modifier = Modifier.fillMaxSize().then(if (uiState is IPTVUiState.Loading) Modifier.blur(16.dp) else Modifier)) {

                // Barra Lateral MENOR (130.dp)
                LazyColumn(
                    modifier = Modifier
                        .width(130.dp)
                        .fillMaxHeight()
                        .background(Color(0xFF050505)),
                    contentPadding = PaddingValues(vertical = 16.dp, horizontal = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "Categorias",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                        )
                    }
                    item {
                        CategoryBadge(
                            title = "🎬 TODAS",
                            selected = selectedCategoryId == "all_series"
                        ) { selectedCategoryId = "all_series" }
                    }
                    items(items = sortedCategories, key = { it.id }) { cat ->
                        CategoryBadge(
                            title = cat.name,
                            selected = selectedCategoryId == cat.id
                        ) { selectedCategoryId = cat.id }
                    }
                }

                Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Buscar série...", color = Color.Gray) },
                            leadingIcon = { Icon(imageVector = Icons.Rounded.Search, contentDescription = "Search", tint = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonGreen,
                                unfocusedBorderColor = Color.DarkGray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                        )
                    }

                    if (filteredSeries.isEmpty() && uiState !is IPTVUiState.Loading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Rounded.Tv, contentDescription = "Empty", tint = Color.Gray, modifier = Modifier.size(64.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Nenhuma série encontrada", color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(columnsCount),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp),
                            contentPadding = PaddingValues(bottom = 80.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(items = filteredSeries, key = { it.id }) { ser ->
                                SeriesCardItem(series = ser, modifier = Modifier.animateItem()) {
                                    viewModel.selectSeries(ser)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // TELA DE DETALHES DE SÉRIE INTACTA
            val scrollState = rememberScrollState()
            val activeSeason = seriesSeasons.find { it.number == selectedSeasonNum } ?: seriesSeasons.firstOrNull()

            Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
                Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
                    FallbackAsyncImage(
                        title = selectedSeries!!.name,
                        logoUrl = selectedSeries!!.cover,
                        type = "SERIES",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black))))
                    IconButton(
                        onClick = { viewModel.selectSeries(null) },
                        modifier = Modifier.align(Alignment.TopStart).padding(16.dp).background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    ) { Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Voltar", tint = Color.White) }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = selectedSeries!!.name, color = Color.White, fontWeight = FontWeight.Black, fontSize = 24.sp)
                    Row(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (selectedSeries!!.year.isNotEmpty()) Text(text = selectedSeries!!.year, color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        if (seriesSeasons.isNotEmpty()) Text(text = "${seriesSeasons.size} Temporada(s)", color = Color.LightGray, fontSize = 13.sp)
                    }
                    Text(
                        text = selectedSeries!!.plot.ifEmpty { "Sem sinopse." },
                        color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp, modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    if (seriesSeasons.isNotEmpty()) {
                        Text("SELECIONE A TEMPORADA", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(bottom = 8.dp))
                        LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(seriesSeasons) { season ->
                                Box(
                                    modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(if (selectedSeasonNum == season.number) NeonGreen else Charcoal).clickable { selectedSeasonNum = season.number }.padding(horizontal = 16.dp, vertical = 10.dp)
                                ) { Text("Temp. ${season.number}", color = if (selectedSeasonNum == season.number) Color.Black else Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        activeSeason?.let { ssn ->
                            Text("EPISÓDIOS (${ssn.episodes.size})", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(bottom = 12.dp))
                            ssn.episodes.forEach { ep ->
                                val hist = watchHistory.find { it.streamId == ep.id }
                                val progress = if (hist != null && hist.durationMs > 0) (hist.positionMs.toFloat() / hist.durationMs.toFloat()).coerceIn(0f, 1f) else null
                                val isSelected = viewModel.selectedChannel.collectAsState().value?.id == ep.id
                                EpisodeItemCard(episode = ep, watchProgress = progress, isSelected = isSelected) { viewModel.selectChannel(ep) }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SeriesCardItem(series: IPTVSeries, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("series_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Charcoal),
        border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.3f))
    ) {
        Column {
            // CORREÇÃO AQUI: AspectRatio resolve o problema das capas esticadas!
            SubcomposeAsyncImage(
                model = series.cover,
                contentDescription = series.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.68f),
                error = {
                    Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray), contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Rounded.Tv, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                    }
                }
            )
            Text(
                text = series.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun EpisodeItemCard(episode: IPTVChannel, watchProgress: Float? = null, isSelected: Boolean = false, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) NeonGreen.copy(alpha = 0.2f) else Charcoal)
            .border(1.dp, if (isSelected) NeonGreen else Color.Transparent, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(width = 100.dp, height = 64.dp).clip(RoundedCornerShape(4.dp)).background(Color.Black)) {
            SubcomposeAsyncImage(model = episode.logo, contentDescription = episode.name, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = "Play", tint = NeonGreen, modifier = Modifier.align(Alignment.Center).size(24.dp).background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp)))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "EP ${episode.episodeNumber}: ${episode.name}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (watchProgress != null && watchProgress > 0f) {
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(progress = { watchProgress }, modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)), color = NeonGreen, trackColor = Color.DarkGray)
            }
        }
    }
}