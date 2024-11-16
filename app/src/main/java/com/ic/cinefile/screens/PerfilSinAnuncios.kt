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
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ic.cinefile.Navigation.screenRoute
import com.ic.cinefile.R
import com.ic.cinefile.ui.theme.black
import com.ic.cinefile.ui.theme.white
import com.ic.cinefile.viewModel.UiState
import com.ic.cinefile.viewModel.UserDataState
import com.ic.cinefile.viewModel.userCreateViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilSinAnuncios(
    viewModel: userCreateViewModel,
    navController: NavController
) {
    val addScreenState = viewModel.uiState.collectAsState()
    val userDataState by viewModel.userDataState.collectAsState()
    val context = LocalContext.current
    val averageRating by viewModel.averageRatingState.collectAsState()

    val userRole = viewModel.getUserRole()

    LaunchedEffect(addScreenState.value) {
        when (addScreenState.value) {
            is UiState.Error -> {
                val message = (addScreenState.value as UiState.Error).msg
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                viewModel.setStateToReady()
            }

            UiState.Loading -> {
                viewModel.startLoadingTimer()
            }

            UiState.Ready -> {}
            is UiState.Success -> {
                val token = (addScreenState.value as UiState.Success).token
                viewModel.fetchUserData() // Llama a getUserData para obtener la información del usuario
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
                actions = {

                    IconButton(onClick = { /* Acción del botón de configuración */ }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Configuración",
                            tint = white
                        )
                    }
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
                        if (viewModel.showReloadButton) {
                            Button(
                                onClick = { viewModel.reload() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text(
                                    text = "Recargar",
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }


                is UserDataState.Success -> {
                    val user = (userDataState as UserDataState.Success).userData.user
                    val nombreUsuario = user.username
                    val avatarUsuario = getAvatarResourcesinanuncios(user.avatarUrl)
                    val generoUsuario = user.gender
                    val fechaNacimiento = user.yearOfBirth

                    //ICONO QUE LLEVA A PANEL DE CONFIGURACIONES
                    Box(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(end = 20.dp)
                            .clickable { /*NAVEGAR A CONFIGRACIONES*/ }
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

                else -> {
                    if (viewModel.showReloadButton) {
                        Button(
                            onClick = { viewModel.reload() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Recargar",
                                color = Color.Black
                            )
                        }
                    }
                }

            }

            Spacer(modifier = Modifier.height(15.dp))


            // LISTAS
            LazyColumn (
                modifier = Modifier.padding(10.dp)
            ){
                //LISTA DE DESEOS
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Lista de deseos",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = white,
                        modifier = Modifier
                            .padding(start = 15.dp)
                            .clickable { /*que abra la lista de todas las pelis en lista de deseos*/ }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SectionSinanuncios(movies = listOf(R.drawable.deadpoll))

                    //LISTA DE CALIFICADAS
                    Spacer(modifier = Modifier.height(40.dp))
                    Text(
                        text = "Calificadas",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = white,
                        modifier = Modifier
                            .padding(start = 15.dp)
                            .clickable { /*que abra la lista de todas las pelis calificadas*/ }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SectionSinanuncios(movies = listOf(R.drawable.deadpoll,R.drawable.deadpoll, R.drawable.deadpoll, R.drawable.deadpoll,R.drawable.deadpoll, R.drawable.deadpoll))
                }
            }
        }
    }
}

//para iterar los posters de pelis
@Composable
fun SectionSinanuncios (movies: List<Int>) {
    Column() {
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(movies.size) { index ->
                Postersinanuncios(movies[index])
            }
        }
    }
}

//Poster de la peli
@Composable
fun Postersinanuncios(imageRes: Int) {
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = null,
        modifier = Modifier
            .size(150.dp, 230.dp)
            .clickable { /*que te lleve a la descripcion de esa peli */ },
        contentScale = ContentScale.Crop
    )
}

fun getAvatarResourcesinanuncios(avatarName: String): Int {
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