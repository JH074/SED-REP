package com.ic.cinefile.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ic.cinefile.Navigation.screenRoute
import com.ic.cinefile.R
import com.ic.cinefile.components.botonGuardar
import com.ic.cinefile.components.seccComentarios.comentarios
import com.ic.cinefile.components.ratingStars
import com.ic.cinefile.data.witchListData
import com.ic.cinefile.screens.Administrador.DeleteDialogAdmin
import com.ic.cinefile.ui.theme.black
import com.ic.cinefile.ui.theme.dark_red
import com.ic.cinefile.ui.theme.light_yellow
import com.ic.cinefile.ui.theme.sky_blue
import com.ic.cinefile.ui.theme.white
import com.ic.cinefile.viewModel.AverageRatingForUserState
import com.ic.cinefile.viewModel.AverageRatingState
import com.ic.cinefile.viewModel.GetMovieCreateState
import com.ic.cinefile.viewModel.UiState
import com.ic.cinefile.viewModel.userCreateViewModel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter

@Composable
fun descripcionPeli2(
    onClick: () -> Unit,
    viewModel: userCreateViewModel,
    navController: NavController,
    movieId: Int,
) {

    var isBookmarked by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val addScreenState = viewModel.uiState.collectAsState()

    val getmovieCreateState by viewModel.getMovieCreateState.collectAsState()
    val userRole = viewModel.getUserRole()

    val averageRating by viewModel.averageRatingState.collectAsState()
    val averageRatingForUser by viewModel.averageRatingForUserState.collectAsState()
    LaunchedEffect(addScreenState.value) {
        when (addScreenState.value) {
            is UiState.Error -> {
                val message = (addScreenState.value as UiState.Error).msg
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                viewModel.setStateToReady()
            }

            UiState.Loading -> {
                // Puedes agregar algún indicador de carga general aquí si es necesario
            }

            UiState.Ready -> {
                //
            }

            is UiState.Success -> {
                val token = (addScreenState.value as UiState.Success).token
                viewModel.fetchUserData() // Llama a getUserData para obtener la información del usuario
                viewModel.setStateToReady()
            }
        }
    }

    LaunchedEffect(movieId) {
        viewModel.getMovieCreateById(movieId)
        viewModel.getAverageRating(movieId)
        viewModel.getRatingForUser(movieId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        when (getmovieCreateState) {
            is GetMovieCreateState.Loading -> {
                // Mostrar un indicador de carga mientras se obtienen los datos
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.Center)
                )
            }

            is GetMovieCreateState.Error -> {
                // Mostrar un mensaje de error en caso de fallo
                val message = (getmovieCreateState as GetMovieCreateState.Error).msg
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }

            is GetMovieCreateState.Success -> {
                // Mostrar la información de la película una vez cargada
                val movie = (getmovieCreateState as GetMovieCreateState.Success).data

                //Modal de eliminar:
                val openAlertDialog = remember { mutableStateOf(false) }
                if (openAlertDialog.value) {
                    DeleteDialogAdmin(
                        //lo que hace si se da en "eliminar", logica de eliminar la peli
                        onConfirmation = { openAlertDialog.value = false },
                        //lo que hace si se le da "atrás"
                        onDismissRequest = { openAlertDialog.value = false },
                        dialogText = "Pelicula a eliminar: \n${movie.title}"
                    )
                }
                val bitmap = base64ToBitmap(movie.coverPhoto ?: "") // Convierte Base64 a Bitmap

                //Imagen de fondo

                if (bitmap != null) {
                    Image(
                        painter = BitmapPainter(bitmap.asImageBitmap()), // Renderiza el Bitmap
                        contentDescription = "Imagen de la película",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxHeight(0.7f)
                            .fillMaxWidth()
                    )
                } else {
                    // Muestra un marcador de posición si no se puede decodificar el Base64
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.7f)
                            .fillMaxWidth()
                            .background(Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Imagen no disponible",
                            color = Color.White
                        )
                    }
                }
                //Back
                IconButton(
                    onClick = { navController.popBackStack() },
                    colors = IconButtonDefaults.iconButtonColors(black)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "",
                        tint = white
                    )
                }

                //Card de información de la peli
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(450.dp)
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        //Titulo y guardar
                        item {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = movie.title ?: "Sin título", // Evitar null
                                    fontSize = 28.sp,
                                    color = Color.White,
                                    modifier = Modifier.fillMaxWidth(0.85f)
                                )
                                //guardado o no
                                if (userRole == "admin" || userRole == "superAdmin") {

                                    IconButton(
                                        onClick = { navController.navigate(screenRoute.EditarPelicula.route + "/${movie.id}") },
                                        modifier = Modifier.align(Alignment.Top)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = null,
                                            tint = white
                                        )
                                    }

                                } else {
                                    botonGuardar(
                                        onClick = {
                                            isBookmarked = !isBookmarked
                                            val data = witchListData(movieId = movie.id)

                                            viewModel.addToWishlist(data)
                                        },
                                        isBookmarked = isBookmarked,
                                        modifier = Modifier.align(Alignment.Top)
                                    )
                                }
                            }
                        }

                        //Tiempo, calificacion y fecha de peli
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painterResource(id = R.drawable.baseline_time_24),
                                    contentDescription = "Time",
                                    tint = white
                                )
                                Text(
                                    text = movie.duration,
                                    color = white,
                                    fontSize = 16.sp,
                                    modifier = Modifier
                                        .padding(start = 6.dp, end = 8.dp)
                                )
                                Icon(
                                    painterResource(id = R.drawable.baseline_star_rate_24),
                                    contentDescription = "rate",
                                    tint = light_yellow
                                )
                                Text(
                                    text = when (val state = averageRating) {
                                        is AverageRatingState.Success -> {
                                            state.averageRating?.takeIf { it != 0.0 }
                                                ?.let { String.format("%.2f", it) } ?: "0.0"

                                        }

                                        else -> "0.0"
                                    },
                                    color = white,
                                    fontSize = 16.sp,
                                    modifier = Modifier
                                        .padding(start = 4.dp, end = 8.dp)
                                )
                                Icon(
                                    painterResource(id = R.drawable.baseline_calendar_24),
                                    contentDescription = "Calendar",
                                    tint = white
                                )
                                Text(
                                    text = movie.createdAt
                                        ?: "", // Asegurarse de manejar valores nulos
                                    color = white,
                                    fontSize = 16.sp,
                                    modifier = Modifier
                                        .padding(start = 6.dp, end = 8.dp)
                                )
                            }
                        }

                        // Categorias
                        item {
                            Spacer(modifier = Modifier.height(15.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                movie.categories.forEach { category ->
                                    Card(
                                        modifier = Modifier
                                            .wrapContentSize(),
                                        colors = CardDefaults.cardColors(containerColor = sky_blue)
                                    ) {
                                        Text(
                                            text = category.trim(),
                                            color = Color.Black,
                                            modifier = Modifier.padding(6.dp)
                                        )
                                    }
                                }
                            }
                        }

                        //DESCRIPCION PELI
                        item {
                            Spacer(modifier = Modifier.height(15.dp))
                            Text(
                                text = movie.synopsis ?: "",
                                lineHeight = 20.sp,
                                color = Color.White
                            )
                        }

                        //CALIFICACION
                        item {
                            Spacer(modifier = Modifier.height(15.dp))
                            Text(
                                text = "Calificar",
                                color = white,
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                            )
                            ratingStars(
//
                                rating = when (val state = averageRatingForUser) {
                                    is AverageRatingForUserState.Success -> state.rating
                                    else -> 0
                                }, // Mostrar la calificación del usuario
                                onRatingChanged = { rating ->
                                    viewModel.rateMovie(movieId, rating.toDouble())
                                }
                            )


                        }

                        //ACTORES
                        item {
                            Spacer(modifier = Modifier.height(15.dp))
                            Text(
                                text = "Actores",
                                lineHeight = 20.sp,
                                color = white,
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                            )
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(movie.actors.size) { index ->
                                    val actor = movie.actors[index]
                                    Column(
                                        modifier = Modifier
                                            .wrapContentSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {

                                        if (!actor.profileUrl.isNullOrEmpty()) {
                                            AsyncImage(
                                                model = actor.profileUrl,
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(60.dp)
                                                    .clip(CircleShape)
                                            )
                                        } else {
                                            // Mostrar la imagen de reemplazo si no hay URL de imagen
                                            val placeholderActorImage =
                                                painterResource(id = R.drawable.sin_foto)
                                            Image(
                                                painter = placeholderActorImage,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(60.dp)
                                                    .clip(CircleShape)
                                            )
                                        }

                                        Text(
                                            text = actor.name,
                                            color = Color.White,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(top = 6.dp)
                                        )
                                    }
                                }
                            }
                        }

                        //COMENTARIOS
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                            comentarios(
                                viewModel = viewModel,
                                movieId = movieId
                            )
                        }
                    }
                }

            }

            else -> {
                //
            }
        }
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun descripcionPeliPreview() {
//    var isBookmarked by remember { mutableStateOf(false) }
//    descripcionPeli(onClick = { isBookmarked = !isBookmarked })
//}






