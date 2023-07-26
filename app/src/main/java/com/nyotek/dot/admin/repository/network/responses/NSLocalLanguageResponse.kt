package com.nyotek.dot.admin.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class NSLocalLanguageResponse(

    @SerializedName("data")
    @Expose
    var data: MutableList<LanguageSelectModel> = arrayListOf()
)