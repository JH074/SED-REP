package com.ic.cinefile.data
import com.google.gson.annotations.SerializedName

data class editCommentData(
    @SerializedName(value="commentText")
    val commentText: String="",

)
