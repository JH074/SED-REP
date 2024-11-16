package com.ic.cinefile.screens.Administrador

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ic.cinefile.data.createMovieData
import com.ic.cinefile.ui.theme.black
import com.ic.cinefile.ui.theme.dark_red
import com.ic.cinefile.ui.theme.white
import com.ic.cinefile.viewModel.MovieState
import com.ic.cinefile.viewModel.userCreateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun editarPelicula(
    viewModel: userCreateViewModel,
    navController: NavController
) {

    val movieState by viewModel.movieState.collectAsState()
    val context = LocalContext.current
    val createMovie by viewModel.createMovie
    var coverPhoto by remember { mutableStateOf(createMovie.coverPhoto) }
    var title by remember { mutableStateOf(createMovie.title) }
    var sypnosis by remember { mutableStateOf(createMovie.synopsis) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = black,
                    titleContentColor = white
                ),
                title = {
                    Text(
                        text = "Editar película",
                        modifier = Modifier.padding(start = 65.dp)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(start = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "",
                            tint = white
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        val foto = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { resultUri: Uri? ->
                coverPhoto = resultUri?.toString() ?: ""
            }
        )

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(black)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (movieState) {
                is MovieState.Loading -> {
                    // Mostrar un indicador de carga mientras se obtienen los datos
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }

                is MovieState.Error -> {
                    // Mostrar un mensaje de error en caso de fallo
                    val message = (movieState as MovieState.Error).msg
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }

                is MovieState.Success -> {
                    // Mostrar la información de la película una vez cargada
                    val movie = (movieState as MovieState.Success).data

                    //La imagen
                    Box(
                        modifier = Modifier.padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        val coverPhotoToShow = if (coverPhoto.isNullOrEmpty()) {
                            movie.posterUrl
                        } else {
                            AsyncImage(
                                model = coverPhoto,
                                contentDescription = null,
                                modifier = Modifier.size(200.dp, 250.dp)
                            )
                        }
                        AsyncImage(
                            model = coverPhotoToShow,
                            contentDescription = null,
                            modifier = Modifier.size(200.dp, 250.dp)
                        )
                        IconButton(
                            onClick = {
                                foto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            modifier = Modifier.align(Alignment.BottomEnd),
                            colors = IconButtonDefaults.iconButtonColors(dark_red)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                                tint = white
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    //El nombre de la peli
                    var isTitleInitialized by remember { mutableStateOf(false) }

                    if (!isTitleInitialized) {
                        title = movie.title ?: "Sin título"
                        isTitleInitialized = true
                    }

                    TextField(
                        modifier = Modifier.width(300.dp),
                        value = title,
                        onValueChange = { input ->
                            title = input.take(50)
                        },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = black,
                            focusedContainerColor = black,
                            unfocusedLabelColor = white,
                            focusedLabelColor = white,
                            focusedIndicatorColor = white,
                            cursorColor = white,
                            focusedTextColor = white,
                            unfocusedTextColor = white
                        ),
                        placeholder = {
                            Text(
                                text = "Agregar título...",
                                style = TextStyle(
                                    color = white,
                                    fontSize = 15.sp,
                                    letterSpacing = 0.1.em,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                        },
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    //Para la descripción
                    var isDescripcionInitialized by remember { mutableStateOf(false) }

                    if (!isDescripcionInitialized) {
                        sypnosis = movie.description ?: "Sin description"
                        isDescripcionInitialized = true
                    }
                    TextField(
                        modifier = Modifier.width(300.dp),
                        value = sypnosis,
                        onValueChange = { input ->
                            sypnosis = input.take(200)
                        },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = black,
                            focusedContainerColor = black,
                            unfocusedLabelColor = white,
                            focusedLabelColor = white,
                            focusedIndicatorColor = white,
                            cursorColor = white,
                            focusedTextColor = white,
                            unfocusedTextColor = white
                        ),
                        placeholder = {
                            Text(
                                text = "Agregar sinopsis...",
                                style = TextStyle(
                                    color = white,
                                    fontSize = 15.sp,
                                    letterSpacing = 0.1.em,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botón para actualizar la película
                    Button(
                        onClick = {

                            val movieData = createMovieData(
                                title = title,
                                synopsis = sypnosis,
                                coverPhoto = if ((coverPhoto ?: "").startsWith("https://")) coverPhoto
                                    ?: "" else "", // Verifica HTTPS en la URL

                            )
                            // Llamar al método en tu ViewModel para actualizar la película
                            //viewModel.updateMovie(movieData)
                            Toast.makeText(
                                context,
                                "Actualizado con éxito",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.popBackStack()
                        },
                        modifier = Modifier.width(300.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = white,
                            contentColor = black
                        ),
                    ) {
                        Text(
                            text = "Guardar",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                            )
                        )
                    }

                }

                else -> {
                    //
                }
            }
        }
    }
}
