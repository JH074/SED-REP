package com.ic.cinefile.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ic.cinefile.screens.Administrador.AgregarPeliAdmin
import com.ic.cinefile.screens.Administrador.editarPelicula
import com.ic.cinefile.screens.Buscador
import com.ic.cinefile.screens.Calificadas
import com.ic.cinefile.screens.Configuraciones
import com.ic.cinefile.screens.CrearCuenta
import com.ic.cinefile.screens.CrearPerfil
import com.ic.cinefile.screens.ElegirGeneros
import com.ic.cinefile.screens.HomeAdmin
import com.ic.cinefile.screens.HomeAppScreen
import com.ic.cinefile.screens.Lista_deseos
import com.ic.cinefile.screens.Login
import com.ic.cinefile.screens.Notificaciones
import com.ic.cinefile.screens.PerfilAnuncios
import com.ic.cinefile.screens.Resultadobuscador
import com.ic.cinefile.screens.contentAvatar
import com.ic.cinefile.screens.contentGenero
import com.ic.cinefile.screens.descripcionPeli
import com.ic.cinefile.screens.descripcionPeli2
import com.ic.cinefile.viewModel.userCreateViewModel

@Composable
fun AppNavigation(
    viewModel: userCreateViewModel,
    navController: NavHostController = rememberNavController()

) {

    NavHost(
        navController = navController,
        startDestination = screenRoute.HomeAppScreen.route
    )
    {
        composable(route = screenRoute.CrearCuenta.route) {
            CrearCuenta(viewModel, navController)
        }
        composable(route = screenRoute.CrearPerfil.route) {
            CrearPerfil(viewModel, navController)
        }
        composable(route = screenRoute.Genero.route) {
            contentGenero(viewModel, navController)
        }

        composable(route = screenRoute.ElegirGeneros.route) {
            ElegirGeneros(viewModel, navController)
        }
        composable(route = screenRoute.contentAvatar.route) {
            contentAvatar(viewModel, navController)
        }
        composable(route = screenRoute.HomeAppScreen.route) {
            HomeAppScreen(navController)
        }
        composable(route = screenRoute.Login.route) {
            Login(viewModel, navController)
        }

        composable(route = screenRoute.Buscador.route) {
            Buscador(viewModel, navController)
        }


        composable(
            route = "${screenRoute.descripcionPeli.route}/{movieId}",
            arguments = listOf(navArgument("movieId") {
                type = NavType.IntType
            })

        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId")
                ?: throw IllegalArgumentException("Movie ID missing")

            descripcionPeli(
                onClick = {}, viewModel, navController, movieId
            )
        }

        composable(
            route = "${screenRoute.descripcionPeli2.route}/{movieId}",
            arguments = listOf(navArgument("movieId") {
                type = NavType.IntType
            })

        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId")
                ?: throw IllegalArgumentException("Movie ID missing")

            descripcionPeli2(
                onClick = {}, viewModel, navController, movieId
            )
        }

        composable(route = screenRoute.PerfilAnuncios.route) {
            PerfilAnuncios(viewModel, navController)
        }
        composable(route = screenRoute.Notificaciones.route) {
            Notificaciones(
                viewModel = viewModel,
                navController = navController,
            )

        }
        composable(
            route = "${screenRoute.ResultadoBuscador.route}/{title}",
            arguments = listOf(navArgument("title") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: ""

            Resultadobuscador(viewModel, navController, title)
        }

        composable(route = screenRoute.Calificadas.route) {
            Calificadas(viewModel, navController)
        }
        composable(route = screenRoute.Lista_deseos.route) {
            Lista_deseos(viewModel, navController)
        }
        composable(route = screenRoute.Configuraciones.route) {
            Configuraciones(viewModel, navController)
        }

        //admin
        composable(route = screenRoute.HomeAdmin.route) {
            HomeAdmin(viewModel, navController)
        }

        composable(route = screenRoute.AgregarPeliAdmin.route) {
            AgregarPeliAdmin(viewModel, navController)
        }

        composable(
            route = "${screenRoute.EditarPelicula.route}/{movieId}",
            arguments = listOf(navArgument("movieId") {
                type = NavType.IntType
            })

        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId")
                ?: throw IllegalArgumentException("Movie ID missing")

            editarPelicula(
                onClick = {}, viewModel, navController, movieId
                //viewModel, navController
            )
        }

    }


}


