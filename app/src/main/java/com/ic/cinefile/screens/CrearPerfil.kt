package com.ic.cinefile.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import com.ic.cinefile.Navigation.screenRoute
import com.ic.cinefile.ui.theme.black
import com.ic.cinefile.ui.theme.dark_blue
import com.ic.cinefile.ui.theme.white
import com.ic.cinefile.viewModel.userCreateViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearPerfil(viewModel: userCreateViewModel, navController: NavController) {

    val context = LocalContext.current

    val accountData by viewModel.accountcreateAPIData
    var username by remember { mutableStateOf(accountData.username) }
    var year_nac by remember { mutableStateOf(accountData.year_nac) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Crea tu perfil",
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                color = white,
                fontSize = 28.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
            )
        )

        Spacer(modifier = Modifier.height(70.dp))


        TextField(
            modifier = Modifier.width(300.dp), // Ajustar el ancho para que coincida con el botón de selección de fecha
            value = username,
            maxLines = 1, // Limitar a una línea para evitar saltos de línea
            onValueChange = {

                if (username.length <= 15) { // Limitar a 15 caracteres
                    username = it
                }
            },
            singleLine = true,
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
                    text = "Nombre de usuario (máximo 15 caracteres)",
                    style = TextStyle(
                        color = white,
                        fontSize = 16.sp,
                        letterSpacing = 0.1.em,
                        fontWeight = FontWeight.Normal,
                    )
                )
            }
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Fecha de nacimiento",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 64.dp),
            style = TextStyle(
                color = white,
                fontSize = 15.sp,
                letterSpacing = 0.1.em,
                textAlign = TextAlign.Left,
                fontWeight = FontWeight.Normal,
            )
        )
        Spacer(modifier = Modifier.height(4.dp))

        val hidden: MutableState<Boolean> = remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = 1578096000000)
        Button(
            onClick = { hidden.value = !hidden.value },
            modifier = Modifier
                .width(300.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = white,
                contentColor = black
            ),
        ) {
            Row {
                Text(text = year_nac, fontSize = 15.sp)
                Spacer(modifier = Modifier.width(130.dp))
                Icon(Icons.Filled.DateRange, contentDescription = "")
            }
        }
        if (hidden.value) {
            DatePickerDialog(
                onDismissRequest = { hidden.value = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            hidden.value = false
                            year_nac = datePickerState.selectedDateMillis?.let { selectedDate ->
                                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                                calendar.timeInMillis = selectedDate
                                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                sdf.format(calendar.time)
                            } ?: "DD/MM/YYYY"
                        }
                    ) {
                        Text(
                            "Confirmar",
                            color = black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        currentYearContentColor = dark_blue,
                        selectedYearContainerColor = dark_blue,
                        selectedDayContainerColor = dark_blue,
                        todayContentColor = dark_blue,
                        todayDateBorderColor = dark_blue,
                    )
                )
            }

            Spacer(modifier = Modifier.height(100.dp))

            Button(
                onClick = {
                    if (username.isNotEmpty() && year_nac != "DD/MM/YYYY") {
                        try {
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val birthDate =
                                sdf.parse(year_nac) ?: throw ParseException("Invalid date", 0)
                            val age = Calendar.getInstance().let { current ->
                                current[Calendar.YEAR] - Calendar.getInstance()
                                    .apply { time = birthDate }[Calendar.YEAR]
                            }
                            if (age >= 12) {
                                viewModel.updateAccountData(
                                    accountData.copy(
                                        username = username,
                                        year_nac = year_nac
                                    )
                                )
                                navController.navigate(screenRoute.Genero.route)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Debes ser mayor de 12 años",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: ParseException) {
                            Toast.makeText(context, "Fecha inválida", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                modifier = Modifier.width(300.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = white,
                    contentColor = black
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
//
//@Preview(showBackground = true)
//@Composable
//fun CrearPerfilPreview() {
//    //val navController = rememberNavController()
//    CrearPerfil()
//}