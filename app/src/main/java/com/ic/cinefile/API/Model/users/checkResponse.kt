package com.ic.cinefile.API.Model.users

import com.google.gson.annotations.SerializedName

data class checkResponse(
    @SerializedName(value= "exists")
    val exists: Boolean,

    @SerializedName(value= "message")
    val message: String
)
