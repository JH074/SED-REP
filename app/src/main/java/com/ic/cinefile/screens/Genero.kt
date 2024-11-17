package com.ic.cinefile.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ic.cinefile.Navigation.screenRoute
import com.ic.cinefile.ui.theme.black
import com.ic.cinefile.ui.theme.white
import com.ic.cinefile.viewModel.userCreateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun contentGenero(viewModel: userCreateViewModel, navController: NavController) {

    val context = LocalContext.current

    val activity = context as Activity

    val selectedGender: MutableState<String> = remember { mutableStateOf("") }
    val accountData by viewModel.accountcreateAPIData
    var genero by remember { mutableStateOf(accountData.genere) }
    val buttonBorders: MutableState<Map<String, Color>> = remember {
        mutableStateOf(
            mapOf(
                "Hombre" to Color.Transparent,
                "Mujer" to Color.Transparent,
                "Prefiero no especificar" to Color.Transparent
            )
        )
    }

    // Función para actualizar los bordes de los botones
    fun updateButtonBorders(gender: String) {
        if (genero == gender) {
            // Si el mismo botón es clicado, deseleccionar
            genero = ""
            buttonBorders.value = buttonBorders.value.mapValues { Color.Transparent }
        } else {
            // Seleccionar el nuevo botón y deseleccionar el anterior
            genero = gender
            buttonBorders.value = buttonBorders.value.mapValues { Color.Transparent }
            buttonBorders.value = buttonBorders.value.toMutableMap().apply {
                this[gender] = Color.White // Borde blanco
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = black
                ),
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {

//                            val intent = Intent(context, RestContraActivity::class.java)
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//                            context.startActivity(intent)
//                            (context as Activity).finish()
                            navController.popBackStack()

                        }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Género",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier.padding(start = 16.dp)
            )

            Spacer(modifier = Modifier.height(120.dp))

            Button(
                onClick = {
                    updateButtonBorders("Hombre")
                },
                modifier = Modifier
                    .width(300.dp)
                    .border(2.dp, buttonBorders.value["Hombre"]!!), // Agregar borde blanco
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
            ) {
                Text(
                    text = "Hombre",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    updateButtonBorders("Mujer")
                },
                modifier = Modifier
                    .width(300.dp)
                    .border(2.dp, buttonBorders.value["Mujer"]!!), // Agregar borde blanco
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
            ) {
                Text(
                    text = "Mujer",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    updateButtonBorders("Prefiero no especificar")
                },
                modifier = Modifier
                    .width(300.dp)
                    .border(
                        2.dp,
                        buttonBorders.value["Prefiero no especificar"]!!
                    ), // Agregar borde blanco
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
            ) {
                Text(
                    text = "Prefiero no especificar",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    )
                )
            }

            Spacer(modifier = Modifier.height(120.dp))

            Button(
                onClick = {
                    if (genero.isNotEmpty()) {
                        viewModel.updateAccountData(accountData.copy(genere = genero))
                        navController.navigate(screenRoute.ElegirGeneros.route)
                    } else {
                        // Mostrar mensaje de validación usando un Toast
                        Toast.makeText(
                            context,
                            "Por favor, selecciona un género",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                },
                modifier = Modifier.width(300.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
            ) {
                Text(
                    text = "Siguiente",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    )
                )
            }
        }
    }
}


//@Preview(showSystemUi = true)
//@Composable
//fun PreviewSeleccionGeneroScreen() {
//    //val navController = rememberNavController()
//    contentGenero()
//}