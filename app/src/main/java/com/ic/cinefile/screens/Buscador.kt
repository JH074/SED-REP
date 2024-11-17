package com.ic.cinefile.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.ic.cinefile.Navigation.screenRoute
import com.ic.cinefile.R
import com.ic.cinefile.components.LoadingProgressDialog
import com.ic.cinefile.ui.theme.montserratFamily
import com.ic.cinefile.ui.theme.white
import com.ic.cinefile.viewModel.MostViewsMoviestState
import com.ic.cinefile.viewModel.RecentMoviestState
import com.ic.cinefile.viewModel.TopMoviestState
import com.ic.cinefile.viewModel.UiState
import com.ic.cinefile.viewModel.UserPreferences
import com.ic.cinefile.viewModel.userCreateViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun Buscador(viewModel: userCreateViewModel, navController: NavController) {
    var buscador by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val userRole = viewModel.getUserRole()

    // Validación de la entrada
    val sanitizedBuscador = buscador.filter { it.isLetterOrDigit() || it.isWhitespace() }.take(50)

    val context = LocalContext.current
    val addScreenState = viewModel.uiState.collectAsState()
    val recentMoviesState by viewModel.recentMoviesState.collectAsState()
    val mostViewsMoviesState by viewModel.mostViewsMoviesState.collectAsState()
    val topMoviesState by viewModel.topMoviesState.collectAsState()
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


    LaunchedEffect(Unit) {
        viewModel.getRecentMoviesData() // Llama a getUserData para obtener la información del usuario
        viewModel.getMostViewMoviesData()
    }


    Scaffold(
        containerColor = Color.Black,
        topBar = {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .padding(6.dp)
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSearching) {
                        // Mostrar la flecha hacia atrás solo si estamos en modo de búsqueda
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black,
                            modifier = Modifier
                                .padding(6.dp)
                                .clickable {
                                    isSearching = false
                                    focusManager.clearFocus() // Liberar el enfoque del TextField y cerrar el teclado
                                    keyboardController?.hide() // Cerrar el teclado si está visible
                                }
                        )
                    } else {
                        // Mostrar la lupa solo en la pantalla principal
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_search_24),
                            contentDescription = "Lupa",
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }

                    TextField(
                        value = buscador,
                        onValueChange = { newBuscador ->
                            buscador = newBuscador
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
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
                                ),
                                modifier = Modifier.fillMaxWidth()
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
                                focusManager.clearFocus() // Liberar el enfoque del TextField y cerrar el teclado
                                keyboardController?.hide() // Cerrar el teclado si está visible
                                navController.navigate("${screenRoute.ResultadoBuscador.route}/$sanitizedBuscador")

                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester) // Asociar el FocusRequester al TextField
                            .onFocusChanged { focusState ->
                                isSearching = focusState.isFocused
                            }
                    )
                }
            }
        },
        bottomBar = {
            if (!isSearching) {
                BottomAppBar(
                    containerColor = Color.Black,
                    content = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if(userRole == "admin" || userRole == "superAdmin"){
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
                    }
                )
            }
        }
    ) { innerPadding ->
        // Contenido del Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Black)
        ) {
            if (isSearching) {
                // Mostrar la pantalla de historial de búsqueda dentro de la columna del Scaffold
                SearchHistoryScreen(
                    onBackClick = { isSearching = false },
                    recentSearches = viewModel.recentSearches.collectAsState().value,
                    navController = navController
                )
            } else {
                // Contenido principal
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    //peliculas recientes
                    when (recentMoviesState) {
                        is RecentMoviestState.Success -> {
                            val movies =
                                (recentMoviesState as RecentMoviestState.Success).data.moviesRecent
                            Column {
                                Text(
                                    text = "Películas mas recientes",
                                    style = TextStyle(
                                        color = Color.White,
                                        textAlign = TextAlign.Start,
                                        fontFamily = montserratFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 20.sp
                                    ),
                                    modifier = Modifier.padding(8.dp)
                                )

                                LazyRow {
                                    items(movies.size) { index ->
                                        val movie = movies[index]
                                        Box(
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .clickable {
                                                    // Aquí navegas a la pantalla de descripción de la película
                                                    navController.navigate(route = screenRoute.descripcionPeli.route + "/${movie.id}")
                                                }
                                        )
                                        {
                                            AsyncImage(
                                                model = movie.posterUrl,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .padding(4.dp)
                                                    .height(200.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        is RecentMoviestState.Loading -> {
                            // Aquí puedes mostrar un diálogo de carga o un indicador de progreso
                            LoadingProgressDialog()
                        }

                        is RecentMoviestState.Error -> {
                            // Aquí puedes manejar el estado de error
                            Text(
                                text = "Error: ${(recentMoviesState as RecentMoviestState.Error).msg}",
                                color = Color.Red,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        is RecentMoviestState.Ready -> {
                            // Aquí puedes manejar el estado de preparación inicial si es necesario
                        }
                    }
//mas visto
                    when (mostViewsMoviesState) {
                        is MostViewsMoviestState.Success -> {
                            val movies =
                                (mostViewsMoviesState as MostViewsMoviestState.Success).data.moviesMostViews
                            Column {
                                Text(
                                    text = "Películas más vistas",
                                    style = TextStyle(
                                        color = Color.White,
                                        textAlign = TextAlign.Start,
                                        fontFamily = montserratFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 20.sp
                                    ),
                                    modifier = Modifier.padding(8.dp)
                                )

                                LazyRow {
                                    items(movies.size) { index ->
                                        val movie = movies[index]
                                        Box(
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .clickable {
                                                    // Aquí navegas a la pantalla de descripción de la película
                                                    navController.navigate(route = screenRoute.descripcionPeli.route + "/${movie.id}")
                                                }
                                        ) {
                                            AsyncImage(
                                                model = movie.posterUrl,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .padding(4.dp)
                                                    .height(200.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        is MostViewsMoviestState.Loading -> {
                            // Aquí puedes mostrar un diálogo de carga o un indicador de progreso
                            LoadingProgressDialog()
                        }

                        is MostViewsMoviestState.Error -> {
                            // Aquí puedes manejar el estado de error
                            Text(
                                text = "Error: ${(mostViewsMoviesState as MostViewsMoviestState.Error).msg}",
                                color = Color.Red,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        is MostViewsMoviestState.Ready -> {
                            // Aquí puedes manejar el estado de preparación inicial si es necesario
                        }
                    }


                    when (topMoviesState) {
                        is TopMoviestState.Loading -> {
                            // Aquí puedes mostrar un diálogo de carga o un indicador de progreso
                            LoadingProgressDialog()
                        }
                        is TopMoviestState.Success -> {
                            val movies =
                                (topMoviesState as TopMoviestState.Success).data.topRatedMovies
                            Column {
                                Text(
                                    text = "Mejores calificadas",
                                    style = TextStyle(
                                        color = Color.White,
                                        textAlign = TextAlign.Start,
                                        fontFamily = montserratFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 20.sp
                                    ),
                                    modifier = Modifier.padding(8.dp)
                                )

                                LazyRow {
                                    items(movies.size) { index ->
                                        val movie = movies[index]
                                        Box(
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .clickable {
                                                    // Aquí navegas a la pantalla de descripción de la película
                                                    navController.navigate(route = screenRoute.descripcionPeli.route + "/${movie.id}")
                                                }
                                        ) {
                                            AsyncImage(
                                                model = movie.posterUrl,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .padding(4.dp)
                                                    .height(200.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }



                        is TopMoviestState.Error -> {
                            // Aquí puedes manejar el estado de error
                            Text(
                                text = "Error: ${(topMoviesState as TopMoviestState.Error).msg}",
                                color = Color.Red,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        is TopMoviestState.Ready -> {
                            // Aquí puedes manejar el estado de preparación inicial si es necesario
                        }
                    }
                }
            }
        }
    }
}




@Composable
fun SearchHistoryScreen(onBackClick: () -> Unit, recentSearches: List<String>, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Historial de Búsqueda",
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Mostrar el historial de búsqueda real
        for (search in recentSearches) {
            Text(
                text = search,
                color = Color.Gray,
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .clickable {
                        // Navegar a la pantalla de resultados con el término de búsqueda seleccionado
                        navController.navigate("${screenRoute.ResultadoBuscador.route}/$search")
                    }
            )
        }
    }
}

@Preview
@Composable
fun BuscadorPreview(){
    val nav = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current
    val userPreferences = UserPreferences(context)
    val viewModel = userCreateViewModel(userPreferences)
    Buscador(viewModel, nav)
}
