package com.ic.cinefile.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.ic.cinefile.API.Model.movies.wishListResponse
import com.ic.cinefile.Navigation.screenRoute
import com.ic.cinefile.R
import com.ic.cinefile.components.LoadingProgressDialog
import com.ic.cinefile.ui.theme.black
import com.ic.cinefile.ui.theme.grisComment
import com.ic.cinefile.ui.theme.light_red
import com.ic.cinefile.ui.theme.montserratFamily
import com.ic.cinefile.ui.theme.white
import com.ic.cinefile.viewModel.MoviesReated
import com.ic.cinefile.viewModel.RecentMoviestState
import com.ic.cinefile.viewModel.UiState
import com.ic.cinefile.viewModel.UserDataState
import com.ic.cinefile.viewModel.WishlistGetState
import com.ic.cinefile.viewModel.userCreateViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilAnuncios(
    viewModel: userCreateViewModel,
    navController: NavController
) {
    val addScreenState = viewModel.uiState.collectAsState()
    val userDataState by viewModel.userDataState.collectAsState()
    val context = LocalContext.current
    val wishlisGetState by viewModel.wishlisGetState.collectAsState()
    val averageRating by viewModel.averageRatingState.collectAsState()
    val moviesReatedState by viewModel.moviesReatedState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.getWishlist()
        viewModel.getMoviesReated()
    }

    LaunchedEffect(addScreenState.value) {
        when (addScreenState.value) {
            is UiState.Error -> {
                val message = (addScreenState.value as UiState.Error).msg
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                viewModel.setStateToReady()
            }

            UiState.Loading -> {

            }

            UiState.Ready -> {}
            is UiState.Success -> {
                val token = (addScreenState.value as UiState.Success).token
                viewModel.fetchUserData(token) // Llama a getUserData para obtener la información del usuario
                viewModel.setStateToReady()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = black,
                    titleContentColor = white
                ),
                title = {},
                navigationIcon = {
                    Icon(
                        modifier = Modifier.clickable { navController.popBackStack() },
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "",
                        tint = white
                    )
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Black,
                content = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.navigate(screenRoute.Home.route) }) {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = "Home",
                                tint = white
                            )
                        }
                        IconButton(onClick = {
                            navController.navigate(screenRoute.PerfilAnuncios.route)
                        }) {
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
                .background(black),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // FOTO DE PERFIL DEL USUARIO Y NOMBRE
            when (userDataState) {
                is UserDataState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                is UserDataState.Success -> {
                    val user = (userDataState as UserDataState.Success).userData.user
                    val nombreUsuario = user.username
                    val avatarUsuario = getAvatarResource(user.avatarUrl)
                    val generoUsuario = user.gender
                    val fechaNacimiento = user.yearOfBirth

                    //ICONO QUE LLEVA A PANEL DE CONFIGURACIONES
                    Box(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(end = 20.dp)
                            .clickable { navController.navigate(screenRoute.Configuraciones.route) }
                    ){
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = white
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = avatarUsuario),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                        )
                        Column {
                            Text(
                                color = white,
                                fontSize = 20.sp,
                                text = nombreUsuario,
                                modifier = Modifier
                                    .padding(start = 12.dp)
                            )
                            Text(
                                color = white,
                                fontSize = 14.sp,
                                text = generoUsuario,
                                modifier = Modifier
                                    .padding(start = 12.dp)
                            )
                            Text(
                                color = white,
                                fontSize = 12.sp,
                                text = "fecha de nacimiento: $fechaNacimiento",
                                modifier = Modifier
                                    .padding(start = 12.dp)
                            )
                        }
                    }
                }

                else -> { /* Manejar otros estados si es necesario */
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // BOTÓN PARA QUITAR ANUNCIOS
            Button(
                onClick = { /*lo de quitar anuncios y actualizar el peril sin anuncios*/ },
                colors = ButtonDefaults.buttonColors(containerColor = grisComment),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.hide_source_24),
                    contentDescription = null,
                    tint = light_red
                )
                Text(
                    text = "Eliminar anuncios\nDisfruta una mejor experiencia \nsin anuncios",
                    modifier = Modifier.padding(start = 8.dp),
                    textAlign = TextAlign.Center,
                    color = white
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            // LISTAS
            LazyColumn (
                modifier = Modifier.padding(10.dp).fillMaxWidth()
            ){
                //LISTA DE DESEOS
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    when (wishlisGetState) {
                        is WishlistGetState.Success -> {
                            val movies =
                                (wishlisGetState as WishlistGetState.Success).data.wishlist
                            Column {
                                Text(
                                    text = "Lista de deseos",
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
                                                    navController.navigate("${screenRoute.descripcionPeli.route}/${movie.movieId}")
                                                }
                                        )
                                        {
                                            AsyncImage(
                                                model = movie.poster,
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

                        is WishlistGetState.Loading -> {
                            // Aquí puedes mostrar un diálogo de carga o un indicador de progreso
                            LoadingProgressDialog()
                        }

                        is WishlistGetState.Error -> {
                            // Aquí puedes manejar el estado de error
                            Text(
                                text = "Error: ${(wishlisGetState as WishlistGetState.Error).errorMessage}",
                                color = Color.Red,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    is WishlistGetState.Ready->{

                    }

                    }


                    //LISTA DE CALIFICADAS
                    Spacer(modifier = Modifier.height(40.dp))

                    when (moviesReatedState) {
                        is MoviesReated.Success -> {
                            val movies =
                                (moviesReatedState as MoviesReated.Success).data.ratedMovies
                            Column {
                                Text(
                                    text = "Peliculas calificadas",
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
                                                    navController.navigate("${screenRoute.descripcionPeli.route}/${movie.movieId}")
                                                }
                                        )
                                        {
                                            AsyncImage(
                                                model = movie.poster,
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

                        is MoviesReated.Loading -> {
                            // Aquí puedes mostrar un diálogo de carga o un indicador de progreso
                            LoadingProgressDialog()
                        }

                        is MoviesReated.Error -> {
                            // Aquí puedes manejar el estado de error
                            Text(
                                text = "Error: ${(moviesReatedState as MoviesReated.Error).errorMessage}",
                                color = Color.Red,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        is MoviesReated.Ready->{

                        }

                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

//para iterar los posters de pelis
@Composable
fun Section(movies: List<Int>) {
    Column() {
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(movies.size) { index ->
                Poster(movies[index])
            }
        }
    }
}

//Poster de la peli
@Composable
fun Poster(imageRes: Int) {
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = null,
        modifier = Modifier
            .size(150.dp, 230.dp)
            .clickable { /*que te lleve a la descripcion de esa peli */ },
        contentScale = ContentScale.Crop
    )
}

fun getAvatarResource(avatarName: String): Int {
    return when (avatarName) {
        "avatar1" -> R.drawable.avatar1
        "avatar2" -> R.drawable.avatar2
        "avatar3" -> R.drawable.avatar3
        "avatar4" -> R.drawable.avatar4
        "avatar5" -> R.drawable.avatar5
        "avatar6" -> R.drawable.avatar6
        else -> R.drawable.avatar4 // Imagen por defecto si el nombre del avatar no coincide
    }
}
/*@Preview
@Composable
fun perfilAnunciosPreview() {
    PerfilAnuncios(userCreateViewModel(), rememberNavController())
}*/