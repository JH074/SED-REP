package com.ic.cinefile.Navigation

sealed class screenRoute(var route: String) {
    object CrearCuenta: screenRoute("CrearCuenta")
    object CrearPerfil: screenRoute("CrearPerfil")
    object Genero: screenRoute("Genero")

    object ElegirGeneros:screenRoute("ElegirGeneros")
    object contentAvatar:screenRoute("contentAvatar")

    object HomeAppScreen:screenRoute("HomeAppScreen")
    object Login:screenRoute("Login")

    object Home:screenRoute("Home")

    object Buscador:screenRoute("Buscador")

    object descripcionPeli:screenRoute("descripcionPeli")
    object descripcionPeli2:screenRoute("descripcionPeli2")

    object Notificaciones:screenRoute("Notificaciones")
    object PerfilAnuncios:screenRoute("PerfilAnuncios")

    object unComentario: screenRoute("unComentario/{movieId}/{id}/{username}/{parentId}/{description}/{imagePainter}/{createdAt}")

    object ResultadoBuscador:screenRoute("ResultadoBuscador")
    object Calificadas:screenRoute("Calificadas")
    object Lista_deseos:screenRoute("Lista_deseos")

    object HomeAdmin:screenRoute("HomeAdmin")

    object AgregarPeliAdmin:screenRoute("AgregarPeliAdmin")
    object ListaDeseos:screenRoute("ListaDeseos")
    object Configuraciones:screenRoute("Configuraciones")

    object EditarPelicula: screenRoute("EditarPelicula")


}