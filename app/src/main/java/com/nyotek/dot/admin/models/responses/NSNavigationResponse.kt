package com.nyotek.dot.admin.models.responses

import androidx.fragment.app.Fragment
import com.google.gson.annotations.SerializedName

class NSNavigationResponse(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("icon") val icon: Int = 0,
    @SerializedName("s_icon") val sIcon: Int = 0,
    @SerializedName("title") val title: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("fragment") val fragment: Fragment? = null
)