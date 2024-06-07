package com.nyotek.dot.admin.models.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class NSLocalLanguageResponse(

    @SerializedName("data")
    @Expose
    var data: MutableList<LanguageSelectModel> = arrayListOf()
)