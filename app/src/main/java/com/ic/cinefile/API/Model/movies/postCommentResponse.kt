package com.ic.cinefile.API.Model.movies

import com.google.gson.annotations.SerializedName

data class postCommentResponse(
    @SerializedName(value="message")
    val message: String

)
