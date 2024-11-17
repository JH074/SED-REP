package com.ic.cinefile.data

import com.google.gson.annotations.SerializedName

data class accountRegisterData(
    @SerializedName(value="_id")
    var id:String="",

    @SerializedName(value="username")
    var username:String="",

    @SerializedName(value="email")
    var email:String="",

    @SerializedName(value="password")
    var password:String="",

    @SerializedName(value="year_nac")
    var year_nac:String="",

    @SerializedName(value="genere")
    var genere:String="",

    @SerializedName("movie_genere")
    val movieGenereList: MutableList<String> = mutableListOf(),

    @SerializedName(value="avatar")
    var avatar: String=""
){
    override fun toString(): String {
        val movieGenresString = movieGenereList.joinToString(", ", "movieGenereList=[", "]")
        return "accountRegisterData(id=$id, username=$username, email=$email, password=$password, year_nac=$year_nac, genere=$genere, $movieGenresString, avatar=$avatar)"
    }
}