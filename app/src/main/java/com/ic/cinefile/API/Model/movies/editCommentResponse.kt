package com.ic.cinefile.API.Model.movies
import com.google.gson.annotations.SerializedName

data class editCommentResponse(
    @SerializedName(value="message")
    val message: String
)
