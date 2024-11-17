package com.ic.cinefile.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ic.cinefile.Navigation.screenRoute
import com.ic.cinefile.ui.theme.black
import com.ic.cinefile.ui.theme.white
import com.ic.cinefile.viewModel.CheckEmailState
import com.ic.cinefile.viewModel.userCreateViewModel
import java.util.regex.Pattern

@Composable
fun CrearCuenta(viewModel: userCreateViewModel, navController: NavController) {

    val context = LocalContext.current

    fun isValidEmail(email: String): Boolean {
        val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.com$")
        return Pattern.compile(emailPattern.toString()).matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=!])(?=\\S+\$).{8,}\$"
        val passwordMatcher = Regex(passwordPattern)
        return passwordMatcher.matches(password)
    }

    val accountData by viewModel.accountcreateAPIData
    var email by remember { mutableStateOf(accountData.email) }
    var password by remember { mutableStateOf(accountData.password) }
    val checkEmailState by viewModel.checkEmailState.collectAsState()

    var emailError by remember { mutableStateOf("") }
    var isEmailValid by remember { mutableStateOf(false) }
    LaunchedEffect(email) {
        if (isValidEmail(email)) {
            viewModel.checkEmailExists(email)
        } else {
            isEmailValid = false
            emailError = "Correo inválido"
        }
    }
    LaunchedEffect(checkEmailState) {
        when (checkEmailState) {
            is CheckEmailState.Success -> {
                val state = checkEmailState as CheckEmailState.Success
                if (state.exists) {
                    emailError = "Este correo ya está registrado. Intenta con otro."
                    isEmailValid = false
                } else {
                    emailError = ""
                    isEmailValid = true
                }
            }
            is CheckEmailState.Error -> {
                emailError = "Error al verificar el correo."
                isEmailValid = false
            }
            else -> {
                emailError = ""
                isEmailValid = false
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¡Bienvenido!",
            modifier = Modifier.fillMaxWidth(),
            style = androidx.compose.ui.text.TextStyle(
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
            )
        )

        Spacer(modifier = Modifier.height(70.dp))

        TextField(
            modifier = Modifier.width(300.dp),
            value = email,
            onValueChange = {
                if (it.length <= 35) {
                    email = it
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
                    text = "Correo (ejemplo@dominio.com)",
                    style = androidx.compose.ui.text.TextStyle(
                        color = white,
                        fontSize = 16.sp,
                        letterSpacing = 0.1.em,
                        fontWeight = FontWeight.Normal,
                    )
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
            ),
            singleLine = true,
            isError = emailError.isNotEmpty()

        )
        if (emailError.isNotEmpty()) {
            Text(
                text = emailError,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            modifier = Modifier.width(300.dp),
            value = password,
            onValueChange = {
                if (it.length <= 8) {
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
                    text = "Contraseña (máximo 8 caracteres)",
                    style = androidx.compose.ui.text.TextStyle(
                        color = white,
                        fontSize = 16.sp,
                        letterSpacing = 0.1.em,
                        fontWeight = FontWeight.Normal,
                    ),
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = VisualTransformation.None, // Contraseña visible
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "La contraseña debe contener al menos ocho caracteres, al menos un número, letras mayúsculas e minúsculas y caracteres especiales",
            color = Color.White,
            modifier = Modifier.width(300.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val correo = email
                val contrasena = password

                if (correo.isEmpty() || contrasena.isEmpty()) {
                    Toast.makeText(context, "No dejes los campos vacíos", Toast.LENGTH_SHORT).show()
                } else if (!isValidEmail(correo)) {
                    Toast.makeText(context, "Formato de correo inválido", Toast.LENGTH_SHORT).show()
                } else if (!isValidPassword(contrasena)) {
                    Toast.makeText(context, "Contraseña no válida", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.updateAccountData(accountData.copy(email = email, password = password))
                    navController.navigate(screenRoute.CrearPerfil.route)
                }
            },
            modifier = Modifier.width(300.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = white,
                contentColor = black
            ),
            enabled = email.isNotEmpty() && isEmailValid && password.isNotEmpty() && isValidPassword(password)

        ) {
            Text(
                text = "Registrarse",
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
            )
        }


        Spacer(modifier = Modifier.height(100.dp))

        Text(
            text = "¿Ya tienes una cuenta?",
            style = androidx.compose.ui.text.TextStyle(
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
            ),
            modifier = Modifier.clickable {
                navController.navigate(screenRoute.Login.route)
            }
        )
    }
}