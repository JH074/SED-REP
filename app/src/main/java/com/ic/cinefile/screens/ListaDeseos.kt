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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ic.cinefile.Navigation.screenRoute
import com.ic.cinefile.R
import com.ic.cinefile.components.LoadingProgressDialog
import com.ic.cinefile.ui.theme.black
import com.ic.cinefile.ui.theme.grisComment
import com.ic.cinefile.ui.theme.grisStar
import com.ic.cinefile.ui.theme.light_yellow
import com.ic.cinefile.ui.theme.montserratFamily
import com.ic.cinefile.ui.theme.sky_blue
import com.ic.cinefile.ui.theme.white
import com.ic.cinefile.viewModel.AverageRatingState
import com.ic.cinefile.viewModel.UiState
import com.ic.cinefile.viewModel.WishlistGetState
import com.ic.cinefile.viewModel.userCreateViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Lista_deseos(
    viewModel: userCreateViewModel, navController: NavController

) {
    //Listas de valores para prueba

    val wishlisGetState by viewModel.wishlisGetState.collectAsState()

    val averageRatingState by viewModel.averageRatingState.collectAsState()
    val context = LocalContext.current

    val addScreenState = viewModel.uiState.collectAsState()

    val userRole = viewModel.getUserRole()

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

            UiState.Ready -> {}
            is UiState.Success -> {
                val token = (addScreenState.value as UiState.Success).token
                viewModel.fetchUserData()
                viewModel.setStateToReady()

            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getWishlist()
    }

    Scaffold(topBar = {
        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
            containerColor = black, titleContentColor = white
        ), title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_bookmark_24),
                    contentDescription = null,
                    tint = white
                )
                Text(text = "Lista de deseos")
            }
        })
    }, bottomBar = {
        BottomAppBar(
            containerColor = Color.Black, content = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if (userRole == "admin") {
                        IconButton(onClick = { navController.navigate(screenRoute.HomeAdmin.route) }) {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = "Home",
                                tint = white
                            )
                        }
                    } else {
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

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(black),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            when (wishlisGetState) {
                is WishlistGetState.Success -> {
                    val movies = (wishlisGetState as WishlistGetState.Success).data.wishlist
                    Column {


                        LazyColumn {
                            items(movies.size) { index ->
                                val movie = movies[index]

                                Box(modifier = Modifier
                                    .padding(4.dp)
                                    .clickable {
                                        // Aquí navegas a la pantalla de descripción de la película
                                        navController.navigate("${screenRoute.descripcionPeli.route}/${movie.movieId}")
                                    }) {

                                    BloquePeliDL(
                                        poster = movie.poster,
                                        titulo = movie.title ?: "sin categoria",
                                        fechaLanzamiento = movie.releaseDate ?: "sin fecha",
                                        categoria = movie.genres,
                                        averageRating = averageRatingState // Pasar el estado completo aquí

                                    )
                                    Spacer(modifier = Modifier.height(18.dp))


                                }


                            }
                        }
                    }
                }

                is WishlistGetState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White, modifier = Modifier.size(50.dp)
                        )
                    }
                }

                is WishlistGetState.Error -> {
                    // Aquí puedes manejar el estado de error
                    Text(
                        text = "Error: ${(wishlisGetState as WishlistGetState.Error).errorMessage}",
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                is WishlistGetState.Ready -> {

                }

            }


        }
    }

}


//Contenedor de la peli y su info básica
@Composable
fun BloquePeliDL(
    poster: String?,
    titulo: String,
    fechaLanzamiento: String?,
    categoria: String?,
    averageRating: AverageRatingState
) {
    val displayedFechaLanzamiento = fechaLanzamiento ?: "sin fecha"

    Row(
        modifier = Modifier.background(black),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        //Poster de la peli
        Box(
            modifier = Modifier.size(100.dp, 150.dp)
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

            Box(modifier = Modifier
                .align(Alignment.BottomEnd)
                .background(
                    grisComment,
                    shape = RoundedCornerShape(bottomStart = 8.dp, topStart = 8.dp)
                )
                .clickable {
                    //navController.navigate(route = screenRoute.descripcionPeli.route + "/${movie.id}")
                }
                .padding(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_star_rate_24),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = when (averageRating) {
                            is AverageRatingState.Success -> {
                                String.format(
                                    "%.2f",
                                    (averageRating as AverageRatingState.Success).averageRating
                                )
                            }

                            is AverageRatingState.Loading -> {
                                "..."
                            }

                            is AverageRatingState.Error -> {
                                "Error"
                            }

                            else -> "0.0"
                        },
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
                categoria?.split(",")?.forEach { cat ->
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
                    text = displayedFechaLanzamiento, color = Color.White, fontSize = 14.sp
                )
            }


        }
    }
}