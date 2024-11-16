//package com.ic.cinefile.screens
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.Divider
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.ic.cinefile.R
//import com.ic.cinefile.ui.theme.black
//import com.ic.cinefile.ui.theme.white
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun Notificaciones(
//    //usuario: nombreUsuario
//    //navController: NavController
//) {
//    //variables de prueba guardan el nombre de usuario y la foto
//    var nombreUsuario = "JOAQUIN"
//    var avatarUsuario = painterResource(id = R.drawable.reynolds)
//    //lista de notificaciones que caen al usuario
//    val notificaciones = listOf<String>("","","")
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                colors = topAppBarColors(
//                    containerColor = black,
//                    titleContentColor = white
//                ),
//                title = {
//                    Text("Notificaciones", modifier = Modifier.padding(start = 100.dp))
//                },
//                navigationIcon = {
//                    Icon(
//                        modifier = Modifier.clickable { /*navController.popBackStack() para volver atrás*/ },
//                        imageVector = Icons.Filled.ArrowBack,
//                        contentDescription = "",
//                        tint = white
//                    )
//                }
//            )
//        }
//    ) { innerPadding ->
//        if (notificaciones.isEmpty()) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(black),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = "Sin notificaciones",
//                    color = white
//                )
//            }
//        } else {
//            LazyColumn(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(innerPadding)
//                    .background(black),
//                verticalArrangement = Arrangement.Top,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                items(notificaciones) { notificacion ->
//                    Card(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(60.dp)
//                            .clickable { /*logica para ir al comentario*/ },
//                        colors = CardDefaults.cardColors(containerColor = black),
//                    ) {
//                        Divider()
//                        Row(
//                            modifier = Modifier.padding(10.dp),
//                            verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.Center
//                        ) {
//                            Image(
//                                painter = avatarUsuario,
//                                contentDescription = null,
//                                contentScale = ContentScale.Crop,
//                                modifier = Modifier
//                                    .size(40.dp)
//                                    .clip(CircleShape)
//                            )
//                            Text(
//                                text = "$nombreUsuario respondió tu comentario",
//                                color = white,
//                                modifier = Modifier.padding(start = 12.dp)
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Preview
//@Composable
//fun notificacionesPreview() {
//    Notificaciones()
//}

package com.ic.cinefile.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.ic.cinefile.R
import com.ic.cinefile.ui.theme.black
import com.ic.cinefile.ui.theme.white
import com.ic.cinefile.API.Model.users.NotificationResponse
import com.ic.cinefile.viewModel.NotificationState
import com.ic.cinefile.viewModel.UiState
import com.ic.cinefile.viewModel.userCreateViewModel
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Notificaciones(
    viewModel:userCreateViewModel,
    navController: NavController,
) {
    val notificationState by viewModel.notificationState.collectAsState()
    val context = LocalContext.current
    val addScreenState = viewModel.uiState.collectAsState()

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
                viewModel.fetchUserData() // Llama a getUserData para obtener la información del usuario
                viewModel.setStateToReady()
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getNotifications()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = black,
                    titleContentColor = white
                ),
                title = {
                    Text("Notificaciones", modifier = Modifier.padding(start = 100.dp))
                },
                navigationIcon = {
                    Icon(
                        modifier = Modifier.clickable { navController.popBackStack()  },
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "",
                        tint = white
                    )
                }
            )
        }
    ) { innerPadding ->
        when (notificationState) {

            is NotificationState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(black),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = white)
                }
            }
            is NotificationState.Ready->{

            }
            is NotificationState.Success -> {
                val notifications = (notificationState as NotificationState.Success).notifications

                if (notifications.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sin notificaciones",
                            color = white
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(black),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(notifications) { notification ->
                            NotificationItem(
                                notification = notification
                            )
                        }
                    }
                }
            }
            is NotificationState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(black),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (notificationState as NotificationState.Error).message,
                        color = Color.Red,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: NotificationResponse,
) {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

    // Formato de salida para mostrar la fecha y hora en un formato legible
    val outputFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy hh:mm a", Locale.getDefault())

    // Parsear la fecha y hora del comentario
    val parsedDate = inputFormat.parse(notification.createdAt)
    val formattedDateTime = outputFormat.format(parsedDate)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = CardDefaults.cardColors(containerColor = black),
    ) {
        Divider()
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            val avatarUsuario = getAvatarResource(notification.avatar)

            Image(
                painter = painterResource(id = avatarUsuario),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
            )
            Text(
                text = "${notification.message} respondió tu comentario",
                color = white,
                modifier = Modifier.padding(start = 12.dp)
            )
            Text(
                text = formattedDateTime ,
                color = white,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

//@Preview
//@Composable
//fun NotificacionesPreview() {
//    Notificaciones(
//        viewModel = UserCreateViewModel(),
//        onNotificationClick = {}
//    )
//}

fun getAvatarResourceNotification(avatarName: String): Int {
    // Aquí podrías implementar la lógica para mapear el nombre del avatar a un recurso de imagen
    return when (avatarName) {
        "avatar1" -> R.drawable.avatar1
        "avatar2" -> R.drawable.avatar2
        "avatar3"->R.drawable.avatar3
        "avatar4"->R.drawable.avatar4
        "avatar5"->R.drawable.avatar5
        "avatar6"->R.drawable.avatar6
        else -> R.drawable.avatar4 // O un recurso por defecto si no se encuentra
    }
}