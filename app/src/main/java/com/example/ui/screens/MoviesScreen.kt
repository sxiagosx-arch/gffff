package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.ui.components.FallbackAsyncImage
import com.example.model.IPTVChannel
import com.example.ui.IPTVUiState
import com.example.ui.IPTVViewModel
import com.example.ui.theme.Charcoal
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenDim

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(viewModel: IPTVViewModel) {
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    val channels by viewModel.channels.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf("") }
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

    var detailMovie by remember { mutableStateOf<IPTVChannel?>(null) }
    val movieCategories = categories.filter { cat -> cat.type == "MOVIE" }

    LaunchedEffect(movieCategories) {
        if (selectedCategoryId.isEmpty() && movieCategories.isNotEmpty()) {
            selectedCategoryId = "all_movies"
        }
    }

    var filteredMovies by remember { mutableStateOf<List<IPTVChannel>>(emptyList()) }

    LaunchedEffect(channels, searchQuery, selectedCategoryId) {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
            var list = channels.filter { it.type == "MOVIE" }

            if (selectedCategoryId.isNotEmpty() && selectedCategoryId != "all_movies") {
                list = list.filter { it.categoryId == selectedCategoryId }
            }

            if (searchQuery.isNotEmpty()) {
                list = list.filter { it.name.contains(searchQuery, ignoreCase = true) }
            }

            filteredMovies = list
        }
    }

    val isLandscape = androidx.compose.ui.platform.LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val columnsCount = if (isLandscape) 5 else 2

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("movies_screen")
    ) {
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
                        title = "🎬 TODOS",
                        selected = selectedCategoryId == "all_movies" || selectedCategoryId.isEmpty()
                    ) { selectedCategoryId = "all_movies" }
                }
                items(items = movieCategories, key = { it.id }) { cat ->
                    CategoryBadge(
                        title = cat.name,
                        selected = selectedCategoryId == cat.id
                    ) { selectedCategoryId = cat.id }
                }
            }

            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar filme...", color = Color.Gray, fontSize = 14.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Rounded.Search, contentDescription = "Search", tint = NeonGreen) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Charcoal,
                        unfocusedContainerColor = Charcoal,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = NeonGreen
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                if (filteredMovies.isEmpty() && uiState !is IPTVUiState.Loading) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Rounded.Movie, contentDescription = "Empty", tint = Color.Gray, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Nenhum filme disponível", color = Color.Gray, fontSize = 14.sp)
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
                        items(items = filteredMovies, key = { it.id }) { mv ->
                            MovieCardItem(movie = mv, modifier = Modifier.animateItem()) {
                                detailMovie = mv
                                focusManager.clearFocus()
                            }
                        }
                    }
                }
            }
        }

        if (detailMovie != null) {
            androidx.compose.ui.window.Dialog(onDismissRequest = { detailMovie = null }, properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)) {
                detailMovie?.let { movie ->
                    val isFav = favorites.any { it.streamId == movie.id && it.type == "MOVIE" }
                    MovieDetailsSheet(
                        movie = movie,
                        isFav = isFav,
                        onToggleFav = { viewModel.toggleFavorite(movie) },
                        onClose = { detailMovie = null }
                    ) {
                        viewModel.selectChannel(movie)
                        detailMovie = null
                    }
                }
            }
        }
    }
}

@Composable
fun MovieCardItem(movie: IPTVChannel, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("movie_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Charcoal),
        border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.3f))
    ) {
        Column {
            // CORREÇÃO AQUI: AspectRatio resolve o problema das capas esticadas!
            SubcomposeAsyncImage(
                model = movie.logo,
                contentDescription = movie.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.68f),
                error = {
                    Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray), contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Rounded.Movie, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                    }
                }
            )
            Text(
                text = movie.name,
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

// O restante das funções do MoviesScreen fica igual (MovieDetailsSheet, etc)
@Composable
fun MovieDetailsSheet(
    movie: IPTVChannel,
    isFav: Boolean,
    onToggleFav: () -> Unit,
    onClose: () -> Unit,
    onPlay: () -> Unit
) {
    val isTv = com.example.util.DeviceUtil.isTv(LocalContext.current)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = if (isTv) Alignment.CenterEnd else Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(if (isTv) 0.4f else 1f)
                .fillMaxHeight(if (isTv) 1f else 0.7f)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = if (isTv) 0.dp else 24.dp, bottomStart = if (isTv) 24.dp else 0.dp))
                .background(com.example.ui.theme.MatteBlack)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Detalhes", color = Color.Gray, fontSize = 14.sp)
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Rounded.Close, contentDescription = "Close", tint = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    FallbackAsyncImage(
                        title = movie.name,
                        logoUrl = movie.logo,
                        type = movie.type,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(120.dp)
                            .aspectRatio(0.68f)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = movie.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = movie.categoryName, color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = onToggleFav,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = if (isFav) NeonGreen else Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isFav) NeonGreen else Color.DarkGray)
                        ) {
                            Icon(imageVector = if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, contentDescription = "Fav")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isFav) "SALVO" else "SALVAR")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onPlay,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = "Play", tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("REPRODUZIR AGORA", color = Color.Black, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "SINOPSE", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(
                    text = movie.description.ifEmpty { "Explore e descubra os segredos desta incrível produção." },
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (movie.director.isNotEmpty()) {
                    MetadataLine(label = "Diretor", value = movie.director)
                }
                if (movie.cast.isNotEmpty()) {
                    MetadataLine(label = "Elenco", value = movie.cast)
                }
            }
        }
    }
}

@Composable
fun MetadataLine(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(text = label.uppercase(), color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 10.sp)
        Text(text = value, color = Color.LightGray, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
    }
}