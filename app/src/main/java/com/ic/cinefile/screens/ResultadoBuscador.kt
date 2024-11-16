package com.ic.cinefile.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ic.cinefile.Navigation.screenRoute
import com.ic.cinefile.R
import com.ic.cinefile.ui.theme.dark_red
import com.ic.cinefile.ui.theme.sky_blue
import com.ic.cinefile.ui.theme.white
import com.ic.cinefile.viewModel.SearchState
import com.ic.cinefile.viewModel.UiState
import com.ic.cinefile.viewModel.userCreateViewModel

@Composable
fun Resultadobuscador(viewModel: userCreateViewModel, navController: NavController, title: String) {
    var buscador by remember { mutableStateOf("") }
    val searchState by viewModel.searchState.collectAsState()
    val averageRatingState by viewModel.averageRatingState.collectAsState()

    // Lista mutable para almacenar el estado de marcado de cada película
    val bookmarkedStates: MutableList<Boolean> = remember { mutableStateListOf() }
    var isSearching by remember { mutableStateOf(false) }
    var selectedGenre by remember { mutableStateOf("") }

    var isMenuExpanded by remember { mutableStateOf(false) }
    var isGenreMenuExpanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }

    val context = LocalContext.current

    val addScreenState = viewModel.uiState.collectAsState()

    val userRole = viewModel.getUserRole()

    // Validación de la entrada
    val sanitizedBuscador = buscador.filter { it.isLetterOrDigit() || it.isWhitespace() }.take(50)

    LaunchedEffect(addScreenState.value) {
        when (addScreenState.value) {
            is UiState.Error -> {
                val message = (addScreenState.value as UiState.Error).msg
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                viewModel.setStateToReady()
            }

            UiState.Loading -> {
                // Mostrar un diálogo de carga o algún indicador de progreso
            }

            UiState.Ready -> {
                //
            }
            is UiState.Success -> {
                val token = (addScreenState.value as UiState.Success).token
                viewModel.fetchUserData()
                viewModel.setStateToReady()

            }
        }
    }

    LaunchedEffect(title) {
        viewModel.searchMoviesByTitle(title)
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Black,
                content = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                    }

                    if(userRole=="admin"){
                        IconButton(onClick = { navController.navigate(screenRoute.HomeAdmin.route) }) {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = "Home",
                                tint = white
                            )
                        }
                    }else{
                        IconButton(onClick = { navController.navigate(screenRoute.HomeAdmin.route) }) {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = "Home",
                                tint = white
                            )
                        }
                        IconButton(onClick = { navController.navigate(screenRoute.PerfilAnuncios.route) }) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "User",
                                tint = white
                            )
                        }

                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.padding(6.dp, top = 16.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.padding(start = 12.dp),
                            painter = painterResource(id = R.drawable.baseline_search_24),
                            contentDescription = "Lupa"
                        )

                        TextField(
                            value = buscador,
                            onValueChange = { newBuscador -> buscador = newBuscador },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            placeholder = {
                                Text(
                                    text = "Buscar",
                                    style = TextStyle(
                                        color = Color.Gray,
                                        fontSize = 15.sp,
                                        letterSpacing = 0.1.em,
                                        fontWeight = FontWeight.Normal
                                    )
                                )
                            },
                            textStyle = TextStyle(color = Color.Black),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    isSearching = true
                                    navController.navigate("${screenRoute.ResultadoBuscador.route}/$sanitizedBuscador")
                                }
                            )
                        )
                    }
                }

                Box {
                    IconButton(onClick = { isMenuExpanded = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_checklist_24),
                            tint = Color.White,
                            contentDescription = "Filtros"
                        )
                    }

                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Mejores calificadas") },
                            onClick = {
                                selectedOption = "Mejores calificadas"
                                isMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Recientes") },
                            onClick = {
                                selectedOption = "Recientes"
                                isMenuExpanded = false
                                // Llamar función de búsqueda con filtro de recientes
                                viewModel.searchMoviesByTitle(title, sortBy = "release_date.desc")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Más viejas") },
                            onClick = {
                                selectedOption = "Más viejas"
                                isMenuExpanded = false
                                // Llamar función de búsqueda con filtro de más viejas
                                viewModel.searchMoviesByTitle(title, sortBy = "release_date.asc")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Género") },
                            onClick = {
                                isMenuExpanded = false
                                isGenreMenuExpanded = true
                            }
                        )
                    }
                    DropdownMenu(
                        expanded = isGenreMenuExpanded,
                        onDismissRequest = { isGenreMenuExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Acción") },
                            onClick = {
                                selectedOption = "Acción"
                                selectedGenre = "accion"
                                isGenreMenuExpanded = false
                                // Llamar función de búsqueda con filtro de género Acción
                                viewModel.searchMoviesByTitle(title, genre = "accion")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Aventura") },
                            onClick = {
                                selectedOption = "Aventura"
                                selectedGenre = "aventura"
                                isGenreMenuExpanded = false
                                // Llamar función de búsqueda con filtro de género Aventura
                                viewModel.searchMoviesByTitle(title, genre = "aventura")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Animación") },
                            onClick = {
                                selectedOption = "Animación"
                                selectedGenre = "animacion"
                                isGenreMenuExpanded = false
                                // Llamar función de búsqueda con filtro de género Animación
                                viewModel.searchMoviesByTitle(title, genre = "animacion")
                            }
                        )


                        DropdownMenuItem(
                            text = { Text("Comedia") },
                            onClick = {
                                selectedOption = "Comedia"
                                selectedGenre = "comedia"
                                isGenreMenuExpanded = false
                                // Llamar función de búsqueda con filtro de género Comedia
                                viewModel.searchMoviesByTitle(title, genre = "comedia")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Crimen") },
                            onClick = {
                                selectedOption = "Crimen"
                                selectedGenre = "crimen"
                                isGenreMenuExpanded = false
                                // Llamar función de búsqueda con filtro de género Crimen
                                viewModel.searchMoviesByTitle(title, genre = "crimen")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Documental") },
                            onClick = {
                                selectedOption = "Documental"
                                selectedGenre = "documental"
                                isGenreMenuExpanded = false
                                // Llamar función de búsqueda con filtro de género Documental
                                viewModel.searchMoviesByTitle(title, genre = "documental")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Drama") },
                            onClick = {
                                selectedOption = "Drama"
                                selectedGenre = "drama"
                                isGenreMenuExpanded = false
                                // Llamar función de búsqueda con filtro de género Drama
                                viewModel.searchMoviesByTitle(title, genre = "drama")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Familia") },
                            onClick = {
                                selectedOption = "Familia"
                                selectedGenre = "familia"
                                isGenreMenuExpanded = false
                                // Llamar función de búsqueda con filtro de género Familia
                                viewModel.searchMoviesByTitle(title, genre = "familia")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Fantasía") },
                            onClick = {
                                selectedOption = "Fantasía"
                                selectedGenre = "fantasia"
                                isGenreMenuExpanded = false
                                // Llamar función de búsqueda con filtro de género Fantasía
                                viewModel.searchMoviesByTitle(title, genre = "fantasia")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Historia") },
                            onClick = {
                                selectedOption = "Historia"
                                selectedGenre = "historia"
                                isGenreMenuExpanded = false
                                // Llamar función de búsqueda con filtro de género Historia
                                viewModel.searchMoviesByTitle(title, genre = "historia")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Terror") },
                            onClick = {
                                selectedOption = "Terror"
                                selectedGenre = "terror"
                                isGenreMenuExpanded = false
                                // Llamar función de búsqueda con filtro de género Terror
                                viewModel.searchMoviesByTitle(title, genre = "terror")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Música") },
                            onClick = {
                                selectedOption = "Música"
                                selectedGenre = "musica"
                                isGenreMenuExpanded = false
                                // Llamar función de búsqueda con filtro de género Música
                                viewModel.searchMoviesByTitle(title, genre = "musica")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Misterio") },
                            onClick = {
                                selectedOption = "Misterio"
                                selectedGenre = "misterio"
                                isGenreMenuExpanded = false
                                // Llamar función de búsqueda con filtro de género Misterio
                                viewModel.searchMoviesByTitle(title, genre = "misterio")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Romance") },
                            onClick = {
                                selectedOption = "Romance"
                                selectedGenre = "romance"
                                isGenreMenuExpanded = false
                                // Llamar función de búsqueda con filtro de género Romance
                                viewModel.searchMoviesByTitle(title, genre = "romance")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Suspenso") },
                            onClick = {
                                selectedOption = "Suspenso"
                                selectedGenre = "suspenso"
                                isGenreMenuExpanded = false
                                // Llamar función de búsqueda con filtro de género Suspenso
                                viewModel.searchMoviesByTitle(title, genre = "suspenso")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Bélica") },
                            onClick = {
                                selectedOption = "Bélica"
                                selectedGenre = "belica"
                                isGenreMenuExpanded = false
                                // Llamar función de búsqueda con filtro de género Bélica
                                viewModel.searchMoviesByTitle(title, genre = "belica")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Western") },
                            onClick = {
                                selectedOption = "Western"
                                selectedGenre = "western"
                                isGenreMenuExpanded = false
                                // Llamar función de búsqueda con filtro de género Western
                                viewModel.searchMoviesByTitle(title, genre = "western")
                            }
                        )
                    }


                }
            }

            // Mostrar los resultados de la búsqueda
            when (searchState) {
                is SearchState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }

                is SearchState.Success -> {
                    val movies =
                        (searchState as SearchState.Success).data?.moviesSearch ?: emptyList()
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(movies.size) { index ->
                            val movie = movies[index]


                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clickable {
                                        // Aquí navegas a la pantalla de descripción de la película
                                        navController.navigate("${screenRoute.descripcionPeli.route}/${movie.id}")
                                    }
                            ) {
                                Peli(
                                    poster = movie.posterUrl,
                                    titulo = movie.title ?:"sin titulo",
                                    fechaLanzamiento = movie.releaseDate ?: "sin fecha",
                                    categoria = movie.genres,
                                    userRole = userRole,
                                    isBookmarked = bookmarkedStates.getOrNull(movies.indexOf(movie)) ?: false,
                                    averageRating= movie.averageRating// Pasar el estado completo aquí

                                )

                                Spacer(modifier = Modifier.height(18.dp))
                            }
                        }
                    }
                }

                is SearchState.Error -> {
                    val message = (searchState as SearchState.Error).message
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error: $message",
                            color = Color.Red,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                else -> {
                    // Mostrar un mensaje o indicador por defecto cuando no hay datos aún
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron resultados.",
                            color = Color.Gray,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Peli(
    poster: String?,
    titulo: String,
    fechaLanzamiento: String?,
    categoria: String,
    isBookmarked: Boolean,
    userRole: String,
    averageRating: Double,
) {
    val displayedFechaLanzamiento = fechaLanzamiento ?: "sin fecha"

    Row(
        modifier = Modifier
            .background(Color.Black),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp, 150.dp)
                .clickable {
                    // Aquí puedes navegar a la pantalla de descripción de la película si es necesario
                    // navController.navigate(route = screenRoute.descripcionPeli.route + "/${movie.id}")
                }
        ) {
            if (poster != null) {
                AsyncImage(
                    model = poster,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder para imagen nula
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray)
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(
                        Color.Gray,
                        shape = RoundedCornerShape(bottomStart = 8.dp, topStart = 8.dp)
                    )
                    .clickable { }
                    .padding(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_star_rate_24),
                        contentDescription = null,
                        tint = Color.Yellow,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = String.format("%.2f", averageRating),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(15.dp))
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = titulo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                categoria.split(",").forEach { cat ->
                    Card(
                        modifier = Modifier.wrapContentSize(),
                        colors = CardDefaults.cardColors(containerColor = sky_blue)
                    ) {
                        Text(
                            text = cat,
                            color = Color.Black,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_calendar_24),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = displayedFechaLanzamiento,
                    color = Color.White,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (userRole == "admin") {
                    IconButton(
                        onClick = { /*ELIMINAR PELI*/ },
                        modifier = Modifier.align(Alignment.Top)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = dark_red
                        )
                    }
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_bookmark_24),
                        contentDescription = null,
                        tint = if (isBookmarked) Color.Yellow else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}