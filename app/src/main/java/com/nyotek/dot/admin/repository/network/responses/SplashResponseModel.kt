package com.nyotek.dot.admin.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SplashResponseModel(
    @SerializedName("theme_model")
    @Expose
    var themeModel: NSGetThemeModel? = null,
    @SerializedName("local_response")
    @Expose
    var localResponse: NSLocalLanguageResponse? = null
)