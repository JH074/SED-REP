package com.ic.cinefile.components.seccComentarios

import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import com.ic.cinefile.R
import com.ic.cinefile.data.accountLoginData
import com.ic.cinefile.data.commentData
import com.ic.cinefile.screens.getAvatarResource
import com.ic.cinefile.ui.theme.grisComment
import com.ic.cinefile.ui.theme.white
import com.ic.cinefile.viewModel.CommentListState
import com.ic.cinefile.viewModel.CommentPostState
import com.ic.cinefile.viewModel.UiState
import com.ic.cinefile.viewModel.UserDataState
import com.ic.cinefile.viewModel.userCreateViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun comentarios(
    viewModel: userCreateViewModel,
    movieId: Int
) {
    //mostrar más comentarios
    var showComments by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()
    //imagen del usuario
    val sendComment by viewModel.postCommentState

    var commentText by remember { mutableStateOf(sendComment.commentText) }
    val addScreenState = viewModel.uiState.collectAsState()
    val userDataState by viewModel.userDataState.collectAsState()
    val commentState by viewModel.commentsState.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(addScreenState.value) {
        when (addScreenState.value) {
            is UiState.Error -> {
                val message = (addScreenState.value as UiState.Error).msg
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                viewModel.setStateToReady()
            }

            UiState.Loading -> {
                //
            }

            UiState.Ready -> {
                //
            }

            is UiState.Success -> {
                val token = (addScreenState.value as UiState.Success).token
                viewModel.fetchUserData(token) // Llama a getUserData para obtener la información del usuario
                viewModel.setStateToReady()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getComments(movieId) // Llamada para obtener los comentarios de la película
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Gray)
    ) {

        Button(
            onClick = { showComments = !showComments },
            colors = ButtonDefaults.buttonColors(Color.Gray)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Comentarios",
                    fontSize = 18.sp,
                    color = white
                )
                Spacer(modifier = Modifier.width(180.dp))
                Icon(
                    painter = painterResource(id = R.drawable.navigate_next_24),
                    contentDescription = null,
                    tint = white
                )

            }
        }

        if (showComments) {
            ModalBottomSheet(
                onDismissRequest = { showComments = false },
                sheetState = bottomSheetState,
                containerColor = Color.Gray,
                modifier = Modifier.fillMaxHeight(0.5f)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Comentarios",
                        fontSize = 18.sp,
                        color = white
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .padding(16.dp)
                ) {

                    when (commentState) {
                        is CommentListState.Success -> {
                            val comments = (commentState as CommentListState.Success).comments

                            items(comments.size) { index ->
                                val comentario = comments[index]
                                unComentario(
                                    movieId = movieId,
                                    viewModel = viewModel,
                                    id = comentario.id,
                                    parentId = comentario.parentId,
                                    username = comentario.user.username,
                                    description = comentario.commentText,
                                    createdAt = comentario.createdAt, // Pasar fecha y hora de creación
                                    imagePainter = painterResource(
                                        id = getAvatarResource(
                                            comentario.user.avatar
                                        )
                                    )
                                )
                            }
                        }

                        else -> {
                            // Manejar otros estados si es necesario
                        }
                    }
                    item {
                        Divider(Modifier.padding(bottom = 6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {


                            when (userDataState) {
                                is UserDataState.Success -> {
                                    val user =
                                        (userDataState as UserDataState.Success).userData.user
                                    val avatarUsuario = getAvatarResource(user.avatarUrl)
                                    //Avatar del usuario
                                    Image(
                                        painter = painterResource(id = avatarUsuario),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                    )
                                }

                                else -> {
                                    //
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))

                            //Campo para escribir el comentario
                            TextField(
                                modifier = Modifier.fillMaxWidth(0.9f),
                                value = commentText,
                                onValueChange = { input ->
                                    commentText = input.take(150)
                                },
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = grisComment,
                                    focusedContainerColor = grisComment,
                                    unfocusedLabelColor = white,
                                    focusedLabelColor = white,
                                    focusedIndicatorColor = white,
                                    cursorColor = white,
                                    focusedTextColor = white,
                                    unfocusedTextColor = white
                                ),
                                placeholder = {
                                    Text(
                                        text = "Agregar un comentario...",
                                        style = TextStyle(
                                            color = white,
                                            fontWeight = FontWeight.Normal,
                                        )
                                    )
                                },
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            IconButton(onClick = {

                                val userData = commentData(
                                    movieId = movieId,
                                    commentText = commentText
                                )

                                viewModel.postComment(movieId, userData)
                                Log.d("activity", "userData:$userData")
                                commentText = ""

                            }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_send_24),
                                    contentDescription = null,
                                    tint = white
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


/*@Preview
@Composable
fun comentariosPreview(){
    comentarios()
}*/
