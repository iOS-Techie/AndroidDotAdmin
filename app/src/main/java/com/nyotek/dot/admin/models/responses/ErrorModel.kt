package com.nyotek.dot.admin.models.responses

import com.google.gson.annotations.SerializedName

data class ErrorModel (
    @field:SerializedName("code")
    var code: Int? = -1,

    @field:SerializedName("error")
    var error: String? = ""
)