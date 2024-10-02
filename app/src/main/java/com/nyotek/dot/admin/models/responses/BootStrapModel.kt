package com.nyotek.dot.admin.models.responses

import com.google.gson.annotations.SerializedName

class BootStrapModel (
    @field:SerializedName("data")
    val data: BootStrapData? = null,
)

data class BootStrapData (
    @field:SerializedName("theme")
    val themeModel: NSGetThemeData? = null,
    @SerializedName("locales")
    var locales: MutableList<LanguageSelectModel>? = arrayListOf(),
    @SerializedName("strings")
    var strings: HashMap<String, HashMap<String, String>>? = hashMapOf()
)
