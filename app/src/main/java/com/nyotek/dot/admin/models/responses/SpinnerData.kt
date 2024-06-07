package com.nyotek.dot.admin.models.responses

import com.google.gson.annotations.SerializedName

data class SpinnerData(

	@field:SerializedName("id")
	var id: MutableList<String> = arrayListOf(),

	@field:SerializedName("title")
    var title: MutableList<String> = arrayListOf()
)