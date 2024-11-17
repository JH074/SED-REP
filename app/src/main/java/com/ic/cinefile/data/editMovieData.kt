package com.ic.cinefile.data

import com.google.gson.annotations.SerializedName

data class editMovieData(

    @SerializedName(value="title")
    val title: String?,

    @SerializedName(value="synopsis")
    val synopsis: String?,

    @SerializedName(value="coverPhoto")
    val coverPhoto: String?

)
