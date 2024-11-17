package com.ic.cinefile.screens

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ic.cinefile.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.ic.cinefile.Navigation.screenRoute
import com.ic.cinefile.components.LoadingProgressDialog
import com.ic.cinefile.screens.Administrador.DeleteDialogAdmin
import com.ic.cinefile.ui.theme.black
import com.ic.cinefile.ui.theme.dark_red
import com.ic.cinefile.ui.theme.montserratFamily
import com.ic.cinefile.ui.theme.white
import com.ic.cinefile.viewModel.DeleteMovieState
import com.ic.cinefile.viewModel.GetMovieCreate
import com.ic.cinefile.viewModel.LogoutResult
import com.ic.cinefile.viewModel.RecentMoviestState
import com.ic.cinefile.viewModel.UiState
import com.ic.cinefile.viewModel.UserDataState
import com.ic.cinefile.viewModel.userCreateViewModel
import kotlinx.coroutines.launch
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAdmin(viewModel: userCreateViewModel, navController: NavController) {
    val context = LocalContext.current
    val addScreenState = viewModel.uiState.collectAsState()
    val userDataState by viewModel.userDataState.collectAsState()
    var isFocused by remember { mutableStateOf(false) }
    //Del menu hamburguesa
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val logoutResult by viewModel.logoutResult.collectAsState()

    val userRole = viewModel.getUserRole()
    val deleteMovieState by viewModel.deleteMovieState.collectAsState()

// Variable para almacenar el ID de la película seleccionada para eliminar
    var selectedMovieId by remember { mutableStateOf<String?>(null) }
    //Modal de eliminar:


    val openAlertDialog = remember { mutableStateOf(false) }


    LaunchedEffect(deleteMovieState) {
        when (deleteMovieState) {
            is DeleteMovieState.Success -> {
                Toast.makeText(context, "Película eliminada exitosamente", Toast.LENGTH_SHORT).show()
                viewModel.resetDeleteMovieState()
                // Actualiza la lista de películas después de eliminar
                viewModel.getMovieCreate()
            }
            is DeleteMovieState.Error -> {
                val errorMessage = (deleteMovieState as DeleteMovieState.Error).errorMessage
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                viewModel.resetDeleteMovieState()
            }
            else -> { /* No hacer nada para otros estados */ }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchUserData()

    }

    if (openAlertDialog.value) {
        DeleteDialogAdmin(
            onDismissRequest = {
                openAlertDialog.value = false
            },
            onConfirmation = {
                openAlertDialog.value = false
                selectedMovieId?.let { movieId ->
                    viewModel.deleteMovie(movieId)
                }
            },
            dialogText = ""
        )
    }



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
              // Llama a getUserData para obtener la información del usuario
                viewModel.setStateToReady()

            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getMovieCreate()

    }

    LaunchedEffect(Unit) {
        viewModel.startLoadingTimer()
    }

    // Manejar el resultado del logout
    LaunchedEffect(logoutResult) {
        when (logoutResult) {
            is LogoutResult.Success -> {
                navController.navigate(screenRoute.Login.route) {
                    popUpTo(0)
                }
            }
            is LogoutResult.Error -> {
                val message = (logoutResult as LogoutResult.Error).message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
            else -> {
                //
            }
        }
    }


    val getMovieCreateState by viewModel.getMovieCreate.collectAsState()

    ModalNavigationDrawer(drawerState = drawerState, gesturesEnabled = true, drawerContent = {
        ModalDrawerSheet(
            drawerContainerColor = black
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {


                when (userDataState) {
                    is UserDataState.Loading -> {
                        //
                    }

                    is UserDataState.Success -> {
                        val user = (userDataState as UserDataState.Success).userData.user
                        val avatarUsuario = getAvatarResourcesinanuncios(user.avatarUrl)
                        val username = user.username

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
                            Text(
                                text = username,
                                color = white,
                                modifier = Modifier.padding(start = 12.dp),
                                fontSize = 20.sp
                            )

                        }
                    }

                    else -> { /* Manejar otros estados si es necesario */
                    }
                }

            }
            Divider()
            Spacer(modifier = Modifier.height(10.dp))
            if(userRole=="admin"){
                Text(text = "")
            }else{
                NavigationDrawerItem(label = {
                    Text(
                        text = "Calificadas", color = white, fontSize = 16.sp
                    )
                }, selected = false, icon = {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = white
                    )
                }, onClick = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                    navController.navigate(screenRoute.Calificadas.route) {
                        popUpTo(0)
                    }
                }, colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent,
                    selectedContainerColor = Color.Transparent
                )
                )
                NavigationDrawerItem(
                    label = { Text(text = "Lista de deseos", color = white, fontSize = 16.sp) },
                    selected = false,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = white
                        )
                    },
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        navController.navigate(screenRoute.Lista_deseos.route) {
                            popUpTo(0)
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        selectedContainerColor = Color.Transparent
                    )
                )

                NavigationDrawerItem(label = {
                    Text(
                        text = "Políticas de privacidad", color = white, fontSize = 16.sp
                    )
                }, selected = false, icon = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = white
                    )
                }, onClick = {
                    coroutineScope.launch {
                        drawerState.close()
                    }/*PARA IR A LAS POLITICAS DE PRIVACIDAD*/
                }, colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent,
                    selectedContainerColor = Color.Transparent
                )
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            NavigationDrawerItem(
                label = { Text(text = "Cerrar sesión", color = white, fontSize = 16.sp) },
                selected = false,
                icon = {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = white
                    )
                },
                onClick = {
                    coroutineScope.launch {
                        drawerState.close()
                    }/*LOGICA PA SALIR DE LA APP*/
                    viewModel.logout()
                },
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent,
                    selectedContainerColor = Color.Transparent
                )
            )

        }
    }) {

        Scaffold(topBar = {
            TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                containerColor = black
            ), title = {}, navigationIcon = {
                IconButton(onClick = {
                    coroutineScope.launch {
                        drawerState.open()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "",
                        tint = white,
                        modifier = Modifier.size(40.dp)
                    )
                }
            })
        }, bottomBar = {
            BottomAppBar(containerColor = Color.Black, content = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
            })
        }) { innerPadding ->

            // Insertar aquí el botón de recarga
            if (viewModel.showReloadButton) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¿Crees que está tardando mucho?",
                        color = Color.White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.reload() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text(
                            text = "Recargar",
                            color = Color.Black,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            if (userDataState is UserDataState.Loading) {
                LoadingAnimation()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(black)
                        .verticalScroll(rememberScrollState())
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {

                        Box(
                            modifier = Modifier
                                .clickable {
                                    if (!isFocused) {
                                        navController.navigate(screenRoute.Buscador.route)
                                    }
                                }
                                .padding(6.dp)
                                .background(
                                    color = white, shape = RoundedCornerShape(24.dp)
                                )
                                .fillMaxWidth(0.8f)
                                .height(55.dp),
                            contentAlignment = Alignment.CenterStart,

                            ) {
                            Icon(
                                modifier = Modifier.padding(6.dp),
                                painter = painterResource(id = R.drawable.baseline_search_24),
                                contentDescription = "Lupa"
                            )
                            Text(
                                text = "Buscar", style = TextStyle(
                                    color = Color.Gray,
                                    fontSize = 15.sp,
                                    letterSpacing = 0.1.em,
                                    fontWeight = FontWeight.Normal
                                ), modifier = Modifier.padding(start = 50.dp)
                            )

                        }
                        if (userRole != "admin") {
                            IconButton(onClick = {
                                navController.navigate(route = screenRoute.Notificaciones.route)
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_notifications_24),
                                    tint = Color.White,
                                    contentDescription = "notificaciones"
                                )
                            }
                        }
                    }

                    if (userRole == "admin") {



                        //Para agregar una peli
                        Button(
                            onClick = { /*NAVEGAR A AGREGARPELI*/
                                navController.navigate(screenRoute.AgregarPeliAdmin.route)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            )
                        ) {
                            Row {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = null,
                                    tint = white
                                )
                                Text(
                                    text = "Agregar película",
                                    style = TextStyle(
                                        color = white,
                                        fontSize = 18.sp,
                                        textAlign = TextAlign.Left,
                                        fontWeight = FontWeight.Normal
                                    )
                                )
                            }
                        }

                        //PELICULAS CREADAS POR ADMINISTRADOR
                        when (getMovieCreateState) {
                            is GetMovieCreate.Success -> {
                                val movies =
                                    (getMovieCreateState as GetMovieCreate.Success).data.data
                                Column {
                                    Text(
                                        text = "Peliculas creadas por adminsitrador",
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
                                            val bitmap = base64ToBitmap(movie.coverPhoto ?: "") // Convierte Base64 a Bitmap

                                            Box(
                                                modifier = Modifier
                                                    .padding(4.dp)
                                                    .clickable {
                                                        //   navController.navigate(route = screenRoute.descripcionPeli2.route + "/${movie.id}")
                                                        navController.navigate(route = screenRoute.descripcionPeli2.route + "/${movie.id}")

                                                        // Aquí navegas a la pantalla de descripción de la película
                                                    }
                                            )
                                            {
                                                // Agrega el prefijo si falta en la cadena Base64
                                                if (bitmap != null) {
                                                    // Usa BitmapPainter para renderizar el Bitmap
                                                    Image(
                                                        painter = BitmapPainter(bitmap.asImageBitmap()),
                                                        contentDescription = "Imagen de la película",
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .clip(RoundedCornerShape(8.dp)),
                                                        contentScale = ContentScale.Crop // Ajustar la imagen al contenedor
                                                    )
                                                }else {
                                                    // Muestra un marcador de posición si el bitmap no se puede decodificar
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .background(Color.Gray),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(text = "Sin imagen", color = Color.White)
                                                    }
                                                }
                                                IconButton(
                                                    onClick = {
                                                        selectedMovieId = movie._id
                                                        openAlertDialog.value = true},
                                                    modifier = Modifier
                                                        .align(Alignment.TopEnd)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = null,
                                                        tint = dark_red
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            is GetMovieCreate.Loading -> {
                                // Aquí puedes mostrar un diálogo de carga o un indicador de progreso
                                LoadingProgressDialog()
                            }

                            is GetMovieCreate.Error -> {
                                // Aquí puedes manejar el estado de error
                                Text(
                                    text = "Error: ${(getMovieCreateState as GetMovieCreate.Error).errorMessage}",
                                    color = Color.Red,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }

                            is GetMovieCreate.Ready -> {
                                // Aquí puedes manejar el estado de preparación inicial si es necesario
                            }
                        }

                        when (userDataState) {
                            is UserDataState.Success -> {
                                val movieCategories =
                                    (userDataState as UserDataState.Success).userData.movies
                                movieCategories.forEach { (category, movies) ->
                                    Box(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = category, style = TextStyle(
                                                color = Color.White,
                                                textAlign = TextAlign.Start,
                                                fontFamily = montserratFamily,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 20.sp
                                            )
                                        )
                                    }

                                    LazyRow {
                                        items(movies.size) { index ->
                                            val movie = movies[index]
                                            Log.d("MoviePoster", "Poster URL: ${movie.posterUrl}")

                                            Box(modifier = Modifier
                                                .padding(4.dp)
                                                .clickable {
                                                    // Aquí navegas a la pantalla de descripción de la película
                                                    navController.navigate(route = screenRoute.descripcionPeli.route + "/${movie.id}")
                                                }) {
                                                val painter =
                                                    rememberAsyncImagePainter(model = movie.posterUrl)
                                                val painterState = painter.state

                                                Box(
                                                    modifier = Modifier
                                                        .height(200.dp)
                                                        .width(150.dp)
                                                ) {
                                                    if (painterState is AsyncImagePainter.State.Error) {
                                                        Text(
                                                            text = "Error al cargar la imagen",
                                                            color = Color.Red,
                                                            modifier = Modifier.padding(16.dp)
                                                        )
                                                    } else {
                                                        AsyncImage(
                                                            model = movie.posterUrl,
                                                            contentDescription = null,
                                                            contentScale = ContentScale.Crop,
                                                            modifier = Modifier.fillMaxSize()
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            is UserDataState.Loading -> {
                                // Mostrar diálogo de carga o indicador de progreso
                                LoadingProgressDialog()
                            }

                            else -> {}
                        }




                    }




                    else {



                        when (userDataState) {
                            is UserDataState.Success -> {
                                val movieCategories =
                                    (userDataState as UserDataState.Success).userData.movies
                                movieCategories.forEach { (category, movies) ->
                                    Box(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = category, style = TextStyle(
                                                color = Color.White,
                                                textAlign = TextAlign.Start,
                                                fontFamily = montserratFamily,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 20.sp
                                            )
                                        )
                                    }

                                    LazyRow {
                                        items(movies.size) { index ->
                                            val movie = movies[index]
                                            Log.d("MoviePoster", "Poster URL: ${movie.posterUrl}")

                                            Box(modifier = Modifier
                                                .padding(4.dp)
                                                .clickable {
                                                    // Aquí navegas a la pantalla de descripción de la película
                                                    navController.navigate(route = screenRoute.descripcionPeli.route + "/${movie.id}")
                                                }) {
                                                val painter =
                                                    rememberAsyncImagePainter(model = movie.posterUrl)
                                                val painterState = painter.state

                                                Box(
                                                    modifier = Modifier
                                                        .height(200.dp)
                                                        .width(150.dp)
                                                ) {
                                                    if (painterState is AsyncImagePainter.State.Error) {
                                                        Text(
                                                            text = "Error al cargar la imagen",
                                                            color = Color.Red,
                                                            modifier = Modifier.padding(16.dp)
                                                        )
                                                    } else {
                                                        AsyncImage(
                                                            model = movie.posterUrl,
                                                            contentDescription = null,
                                                            contentScale = ContentScale.Crop,
                                                            modifier = Modifier.fillMaxSize()
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            is UserDataState.Loading -> {
                                // Mostrar diálogo de carga o indicador de progreso
                                LoadingProgressDialog()
                            }

                            else -> {}
                        }















                    }





                }
            }
        }
    }
}


@Composable
fun LoadingAnimation() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.White, modifier = Modifier.size(36.dp)
        )
    }
}


fun base64ToBitmap(base64Str: String): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(
            base64Str.substringAfter("base64,"), // Elimina el prefijo "data:image/jpeg;base64,"
            Base64.DEFAULT
        )
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        null
    }
}