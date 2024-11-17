package com.ic.cinefile.screens

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ic.cinefile.Navigation.screenRoute
import com.ic.cinefile.data.accountLoginData
import com.ic.cinefile.ui.theme.LoadingAnimation
import com.ic.cinefile.ui.theme.black
import com.ic.cinefile.ui.theme.white
import com.ic.cinefile.viewModel.UiState
import com.ic.cinefile.viewModel.userCreateViewModel

@Composable
fun Login(viewModel: userCreateViewModel, navController: NavController) {

    val context = LocalContext.current

    val showErrorToast by viewModel.showErrorToast.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    if (showErrorToast) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        viewModel.hideErrorToast() // Oculta el Toast después de mostrarlo
    }

    val accountLoginData by viewModel.accountLoginAPIData
    var email by remember { mutableStateOf(accountLoginData.email) }
    var password by remember { mutableStateOf(accountLoginData.password) }
    var passwordVisible by remember { mutableStateOf(false) } // Estado para mostrar u ocultar la contraseña

    var isValid by remember { mutableStateOf(true) }
    fun isValidEmail(email: String): Boolean {
        // Expresión regular para validar un correo que termina en ".com"
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.com$")
        return emailRegex.matches(email)
    }

    val addScreenState = viewModel.uiState.collectAsState()
    when (addScreenState.value) {
        is UiState.Error -> {
            val message = (addScreenState.value as UiState.Error).msg
            Toast.makeText(LocalContext.current, message, Toast.LENGTH_SHORT).show()
            viewModel.setStateToReady()
        }
        UiState.Loading -> {
            LoadingAnimation() // Llamada a la animación de carga
        }
        UiState.Ready -> {
            //
        }
        is UiState.Success -> {
            //showMessage(context, "Token: ${(addScreenState.value as UiState.Success).token}")
            // Navegar a HomeAdmin cuando se obtiene un estado de éxito
            navController.navigate(screenRoute.HomeAdmin.route)
            viewModel.setStateToReady()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¡Bienvenido \n de nuevo!",
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = email,
            onValueChange = {
                email = it
                isValid = isValidEmail(email)
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
                    text = "Correo",
                    style = TextStyle(
                        color = white,
                        fontSize = 16.sp,
                        letterSpacing = 0.1.em,
                        fontWeight = FontWeight.Normal,
                    ),
                )
            },
            modifier = Modifier.width(300.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    // Lógica cuando se presiona Done
                    hideKeyboard(context)
                }
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            value = password,
            onValueChange = {
                if (it.length <= 8) { // Validar que la contraseña no tenga más de 8 caracteres
                    password = it
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
                    text = "Contraseña",
                    style = TextStyle(
                        color = white,
                        fontSize = 16.sp,
                        letterSpacing = 0.1.em,
                        fontWeight = FontWeight.Normal,
                    ),
                )
            },
            modifier = Modifier.width(300.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None // Mostrar la contraseña
            } else {
                PasswordVisualTransformation() // Ocultar la contraseña
            },
            keyboardActions = KeyboardActions(
                onDone = {
                    hideKeyboard(context)
                }
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(
            onClick = { passwordVisible = !passwordVisible },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(50.dp)
        ) {
            Text(
                text = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        val userData = accountLoginData(
                            email = email,
                            password = password,
                        )

                        viewModel.loginUser(userData)

                    } else {
                        Toast.makeText(context, "Formato de correo incorrecto", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Por favor, completa todos los campos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier.width(300.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
        ) {
            Text(
                text = "Iniciar sesión",
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "¿No tienes cuenta? Crea una acá",
            style = TextStyle(
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
            ),
            modifier = Modifier.clickable {
                navController.navigate(screenRoute.CrearCuenta.route)
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}

fun hideKeyboard(current: Context) {
    val inputMethodManager =
        current.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow((current as Activity).window.decorView.windowToken, 0)
}

fun showMessage(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}
