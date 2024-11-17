package com.ic.cinefile.API.Model.movies

import com.google.gson.annotations.SerializedName

data class movieResponseAdminResponse(
    @SerializedName("data")
    val data: List<MovieAdmin>

)

data class MovieAdmin(

    @SerializedName("_id")
    val _id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("synopsis")
    val synopsis: String,

    @SerializedName("duration")
    val duration: String,

    @SerializedName("actors")
    val actors: List<Actor>,

    @SerializedName("coverPhoto")
    val coverPhoto: String,

    @SerializedName("categories")
    val categories: List<String>,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updateAt")
    val updatedAt: String,

    @SerializedName("__v")
    val __v: Int,

    @SerializedName("id")
    val id:Int
)

data class Actor(

    @SerializedName("_id")
    val _id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("profileUrl")
    val profileUrl: String
)

data class createMovieResponse(
    @SerializedName("message")
    val message: String
)