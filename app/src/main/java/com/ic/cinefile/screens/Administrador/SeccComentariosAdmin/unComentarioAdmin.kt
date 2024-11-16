package com.ic.cinefile.screens.Administrador.SeccComentariosAdmin

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ic.cinefile.R
import com.ic.cinefile.data.commentData
import com.ic.cinefile.screens.getAvatarResource
import com.ic.cinefile.ui.theme.dark_red
import com.ic.cinefile.ui.theme.white
import com.ic.cinefile.viewModel.DeleteCommentState
import com.ic.cinefile.viewModel.RepliesToCommentState
import com.ic.cinefile.viewModel.UiState
import com.ic.cinefile.viewModel.userCreateViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun unComentarioAdmin(
    movieId: Int,
    id: String,
    username: String,
    parentId: String?,
    description: String,
    imagePainter: Painter,
    createdAt: String,
    viewModel: userCreateViewModel,
) {
    var showResponses by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()
    val userDataState by viewModel.userDataState.collectAsState()
    val sendComment by viewModel.postCommentState
    val repliesState by viewModel.repliesToCommentState.collectAsState()
    var commentText by remember { mutableStateOf(sendComment.commentText) }
    val commentState by viewModel.commentsState.collectAsState()

    // Formato de entrada para parsear la fecha y hora
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

// Formato de salida para mostrar la fecha y hora en un formato legible
    val outputFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy hh:mm a", Locale.getDefault())

// Parsear la fecha y hora del comentario
    val parsedDate = inputFormat.parse(createdAt)
    val formattedDateTime = outputFormat.format(parsedDate)

    val deleteCommentState by viewModel.deleteCommentState.collectAsState()



    val addScreenState = viewModel.uiState.collectAsState()

    val context = LocalContext.current
    LaunchedEffect(addScreenState.value) {
        when (addScreenState.value) {
            is UiState.Error -> {
                val message = (addScreenState.value as UiState.Error).msg
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                viewModel.setStateToReady()
            }
            UiState.Loading -> {}
            UiState.Ready -> {}
            is UiState.Success -> {
                val token = (addScreenState.value as UiState.Success).token
                viewModel.fetchUserData()
                viewModel.setStateToReady()
            }
        }
    }


    LaunchedEffect(deleteCommentState) {
        when (deleteCommentState) {
            is DeleteCommentState.Error -> {
                val message = (deleteCommentState as DeleteCommentState.Error).errorMessage
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                viewModel.resetDeleteCommentState()
            }
            DeleteCommentState.Loading -> {
                Toast.makeText(context, "Eliminando comentario...", Toast.LENGTH_SHORT).show()
            }
            is DeleteCommentState.Success -> {
                val message = (deleteCommentState as DeleteCommentState.Success).message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                viewModel.resetDeleteCommentState()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = imagePainter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Column {
                Text(
                    text = username,
                    fontWeight = FontWeight.Bold,
                    color = white,
                    modifier = Modifier.padding(start = 12.dp)
                )
                Text(
                    text = description,
                    modifier = Modifier.padding(start = 12.dp)
                )
                Text(
                    text = "Publicado el $formattedDateTime",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = white
                    ),
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }

        IconButton(
            onClick = { showResponses = !showResponses },
            modifier = Modifier.padding(start = 35.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_comment_24),
                contentDescription = "",
                tint = white,
                modifier = Modifier.size(18.dp)
            )
        }

        //Eliminar comentario
        IconButton(
            onClick = {



                /*ELIMINAR LA COMENTARIO*/

                viewModel.deleteComment(id)



            },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = dark_red
            )
        }

        if (showResponses) {
            ModalBottomSheet(
                onDismissRequest = { showResponses = false },
                sheetState = bottomSheetState,
                containerColor = Color.Gray,
                modifier = Modifier.fillMaxHeight(0.5f)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Respuestas",
                        fontSize = 18.sp,
                        color = white
                    )
                }

                // Call getRepliesToComment function here
                getRepliesToComment(
                    movieId = movieId,
                    parentId = id,
                    viewModel = viewModel
                )

                respuestasAdmin(
                    movieId = movieId,
                    id = id,
                    parentId = parentId,
                    username = username,
                    imagePainter = imagePainter,
                    description = description,
                    createdAt = createdAt
                )
                LazyColumn(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Mostrar las respuestas
                    when (repliesState) {
                        is RepliesToCommentState.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                        is RepliesToCommentState.Success -> {
                            val replies = (repliesState as RepliesToCommentState.Success).replies
                            items(replies.size) { index ->
                                val reply = replies[index]
                                unComentarioAdmin(
                                    movieId = movieId,
                                    id = reply.id,
                                    parentId = reply.parentId,
                                    username = reply.user.username,
                                    description = reply.commentText,
                                    createdAt = reply.createdAt,
                                    imagePainter = painterResource(id = getAvatarResource(reply.user.avatarUrl)),
                                    viewModel = viewModel
                                )
                            }
                        }
                        is RepliesToCommentState.Error -> {
                            val errorMessage = (repliesState as RepliesToCommentState.Error).message
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(errorMessage, color = Color.Red)
                                }
                            }
                        }
                        else -> {}
                    }

                    // Agregar una respuesta
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Responder",
                                fontSize = 18.sp,
                                color = white
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            TextField(
                                value = commentText,
                                onValueChange = { commentText = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(text = "Escribe tu comentario...")
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    val userData = commentData(
                                        movieId = movieId,
                                        commentText = commentText,
                                        parentId=id
                                    )
                                    viewModel.postComment(movieId, userData, parentId=id)
                                    commentText = ""
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text(text = "Enviar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun getRepliesToComment(
    movieId: Int,
    parentId: String,
    viewModel: userCreateViewModel
) {
    LaunchedEffect(key1 = parentId) {
        viewModel.getRepliesToComment(movieId, parentId)
    }
}