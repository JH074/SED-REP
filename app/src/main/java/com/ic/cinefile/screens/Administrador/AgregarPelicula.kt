package com.ic.cinefile.screens.Administrador

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ic.cinefile.API.Model.movies.ActorName
import com.ic.cinefile.Navigation.screenRoute
import com.ic.cinefile.components.botonGeneros
import com.ic.cinefile.components.gridGeneros
import com.ic.cinefile.components.valoresGeneros.generos
import com.ic.cinefile.data.Actor
import com.ic.cinefile.data.createMovieData
import com.ic.cinefile.screens.showMessage
import com.ic.cinefile.ui.theme.black
import com.ic.cinefile.ui.theme.dark_red
import com.ic.cinefile.ui.theme.white
import com.ic.cinefile.viewModel.SearchActorsState
import com.ic.cinefile.viewModel.UiState
import com.ic.cinefile.viewModel.userCreateViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarPeliAdmin(
    viewModel: userCreateViewModel,
    navController: NavController
) {

    val createMovie by viewModel.createMovie
    var title by remember { mutableStateOf(createMovie.title) }
    var sypnosis by remember { mutableStateOf(createMovie.synopsis) }
    var duration by remember { mutableStateOf(createMovie.duration) }
    val generosSeleccionados =
        remember { mutableStateListOf<String>().apply { addAll(createMovie.categories) } }
    var coverPhoto by remember { mutableStateOf(createMovie.coverPhoto) }
    var actorName by remember { mutableStateOf("") }
    var actorProfileUrl by remember { mutableStateOf("") }
    val searchActorsState by viewModel.searchActorsState.collectAsState()
    val context = LocalContext.current
    var selectedActors by remember { mutableStateOf(listOf<ActorName>()) }

    val showErrorToast by viewModel.showErrorToast.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    if (showErrorToast) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        viewModel.hideErrorToast()
    }
    // Manejar la selección de un actor
    // Manejar la selección de un actor
    val onActorSelected: (ActorName) -> Unit = { actorName ->
        val actor = Actor(actorName.name, actorName.profileUrl)
        viewModel.addActor(actor)
        selectedActors = selectedActors + actorName
        // Limpiar el campo de búsqueda o restablecer el estado de búsqueda
        viewModel.clearSearchActorsState()
    }
    // Función para manejar la eliminación de un actor seleccionado
    val onActorRemoved: (ActorName) -> Unit = { actorName ->
        selectedActors = selectedActors - actorName
    }
    val addScreenState = viewModel.uiState.collectAsState()
    when (addScreenState.value) {
        is UiState.Error -> {
            val message = (addScreenState.value as UiState.Error).msg
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.setStateToReady()
        }

        UiState.Loading -> {
            com.ic.cinefile.ui.theme.LoadingAnimation()
        }

        UiState.Ready -> {}
        is UiState.Success -> {
            showMessage(context, "Token: ${(addScreenState.value as UiState.Success).token}")
            val userRole = viewModel.getUserRole()
            navController.navigate(screenRoute.HomeAdmin.route)
            viewModel.setStateToReady()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = black,
                    titleContentColor = white
                ),
                title = {
                    Text(
                        text = "Agregar una película",
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

        var buscador by remember { mutableStateOf("") }


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
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sección para subir el poster de la película
            Box(
                modifier = Modifier.padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                val coverPhotoToShow = if (coverPhoto.isNullOrEmpty()) {
                    "https://ih1.redbubble.net/image.1893341687.8294/fposter,small,wall_texture,product,750x1000.jpg"
                } else {
                    AsyncImage(
                        model = coverPhoto,
                        contentDescription = null,
                        modifier = Modifier.size(150.dp, 200.dp)
                    )
                }
                AsyncImage(
                    model = coverPhotoToShow,
                    contentDescription = null,
                    modifier = Modifier.size(150.dp, 200.dp)
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


            // Sección para ingresar el título de la película
            Text(
                text = "Título",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 64.dp),
                style = TextStyle(
                    color = white,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Left,
                    fontWeight = FontWeight.Normal
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
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


            // Sección para ingresar la sinopsis de la película
            Text(
                text = "Sinopsis",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 64.dp),
                style = TextStyle(
                    color = white,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Left,
                    fontWeight = FontWeight.Normal
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
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


            // Sección para ingresar la duración de la película
            Text(
                text = "Duración",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 64.dp),
                style = TextStyle(
                    color = white,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Left,
                    fontWeight = FontWeight.Normal
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            TextField(
                modifier = Modifier.width(300.dp),
                value = duration,
                onValueChange = { duration = it },
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
                        text = "00:00",
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


            // Sección para buscar actores

            val validActorNameRegex = Regex("^[a-zA-Z0-9 ]+$")
            val coroutineScope = rememberCoroutineScope()
            var searchJob by remember { mutableStateOf<Job?>(null) }

            Text(
                text = "Actores",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 64.dp),
                style = TextStyle(
                    color = white,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Left,
                    fontWeight = FontWeight.Normal
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            TextField(
                value = buscador,
                onValueChange = { newBuscador ->
                    // Valida que el texto ingresado cumpla con los criterios de seguridad
                    if (newBuscador.length <= 50 && newBuscador.matches(validActorNameRegex)) {
                        buscador = newBuscador

                        // Cancelar cualquier búsqueda en curso
                        searchJob?.cancel()

                        // Debouncing: espera 500ms antes de realizar la búsqueda
                        searchJob = coroutineScope.launch {
                            delay(500) // Espera de 500ms para evitar llamadas excesivas
                            if (newBuscador.isNotEmpty()) {
                                viewModel.searchActorsByName(newBuscador) // Llama al ViewModel solo si hay texto
                            } else {
                                viewModel.clearSearchActorsState() // Limpia el estado de búsqueda si está vacío
                            }
                        }
                    }
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
                        text = "Buscar actor por nombre...",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 15.sp,
                            letterSpacing = 0.1.em,
                            fontWeight = FontWeight.Normal
                        )
                    )
                },
                leadingIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search Icon",
                            tint = Color.White
                        )
                    }
                },
                shape = RoundedCornerShape(15.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))


            // Sección para mostrar los resultados de búsqueda de actores
            when (searchActorsState) {
                is SearchActorsState.Loading -> {
                    CircularProgressIndicator()
                }

                is SearchActorsState.Error -> {
                    val message = (searchActorsState as SearchActorsState.Error).errorMessage
                    Text(
                        text = message,
                        color = Color.Red
                    )
                }

                is SearchActorsState.Success -> {
                    val actors = (searchActorsState as SearchActorsState.Success).actors.actors
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(actors.size) { index ->
                            val actorName = actors[index]
                            ActorItem(
                                actorName = actorName,
                                onClick = {
                                    onActorSelected(actorName)
                                }
                            )
                        }
                    }
                }

                is SearchActorsState.Ready -> {
                    Text(
                        text = "",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                else -> {
                    //
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sección para mostrar los actores seleccionados
            if (selectedActors.isNotEmpty()) {
                Text(
                    text = "Actores seleccionados:",
                    style = TextStyle(
                        color = white,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                )
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(selectedActors.size) { index ->
                        val actorName = selectedActors[index]
                        ActorItem(
                            actorName = actorName,
                            onClick = {
                                onActorRemoved(actorName)
                            }
                        )
                    }
                }
            }
            // Sección para seleccionar categorías de la película
            Text(
                text = "Categorías",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 64.dp),
                style = TextStyle(
                    color = white,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Left,
                    fontWeight = FontWeight.Normal
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyVerticalGrid(
                modifier = Modifier
                    .width(300.dp)
                    .height(480.dp),
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(generos.entries) { genero ->
                    val (defaultColor, selectedColor) = gridGeneros(genero)
                    val isGenreSelected = generosSeleccionados.contains(genero.name)
                    val isMaxReached = generosSeleccionados.size >= 6

                    val isEnabled = !isMaxReached || isGenreSelected

                    botonGeneros(
                        generos = genero,
                        selectedColor = if (isGenreSelected) selectedColor else defaultColor,
                        defaultColor = defaultColor,
                        onClick = {
                            if (isGenreSelected) {
                                generosSeleccionados.remove(genero.name)
                            } else {
                                if (!isMaxReached) {
                                    if (!generosSeleccionados.contains(genero.name)) {
                                        generosSeleccionados.add(genero.name)
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "¡Máximo 6 géneros!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        isEnabled = isEnabled
                    )
                }
            }


            // Botón para guardar la película
            Button(
                onClick = {
                    val movieData = createMovieData(
                        title = title,
                        synopsis = sypnosis,
                        duration = duration,
                        actors = selectedActors,
                        coverPhoto = if ((coverPhoto ?: "").startsWith("https://")) coverPhoto
                            ?: "" else "", // Verifica HTTPS en la URL,
                        categories = generosSeleccionados
                    )
                    // Llamar al método en tu ViewModel para guardar la película
                    viewModel.createMovie(movieData)
                    Toast.makeText(
                        context,
                        "Agregado con éxito",
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
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ActorItem(actorName: ActorName, onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .clickable(onClick = onClick), // Hacer la columna clickeable


        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (actorName.profileUrl == null) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "s/a",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            AsyncImage(
                model = actorName.profileUrl,
                contentDescription = "Actor Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = actorName.name,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

