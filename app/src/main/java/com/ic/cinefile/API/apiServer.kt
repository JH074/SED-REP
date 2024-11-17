package com.ic.cinefile.API

import com.google.gson.GsonBuilder
import com.ic.cinefile.API.methods.Methods
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit


object apiServer {

    val BASE_URL = "http://192.168.0.33:3500/" // O la dirección correcta

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Tiempo de espera para conectarse
        .readTimeout(30, TimeUnit.SECONDS)    // Tiempo de espera para leer datos
        .writeTimeout(30, TimeUnit.SECONDS)   // Tiempo de espera para escribir datos
        .build()

    val methods: Methods by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Agrega el cliente configurado
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(Methods::class.java)
    }
}


