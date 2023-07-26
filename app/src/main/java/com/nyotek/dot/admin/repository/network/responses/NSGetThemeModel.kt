package com.nyotek.dot.admin.repository.network.responses

import com.google.gson.annotations.SerializedName

data class NSGetThemeModel(
    @field:SerializedName("data")
	val data: NSGetThemeData? = null,

	@field:SerializedName("error")
	val error: String? = null
)

data class NSGetThemeData(
	@field:SerializedName("theme_id")
	val themeId: String? = null,

	@field:SerializedName("service_id")
	val serviceId: String? = null,

	@field:SerializedName("primary")
	val primary: String? = null,

	@field:SerializedName("primaryLight")
	val primaryLight: String? = null,

	@field:SerializedName("secondary")
	val secondary: String? = null,

	@field:SerializedName("secondaryDark")
	val secondaryDark: String? = null,

	@field:SerializedName("borderColor")
	val borderColor: String? = "#FFE1E7F6",

	@field:SerializedName("background")
	val background: String? = null,

	@field:SerializedName("tabSecondary")
	val tabSecondary: String? = null,

	@field:SerializedName("success")
	val success: String? = null,

	@field:SerializedName("error")
	val error: String? = null,

	@field:SerializedName("black")
	val black: String? = "#000000",

	@field:SerializedName("gray")
	val gray: String? = "#AFAEB1",

	@field:SerializedName("light_gray")
	val lightGray: String? = "#C7C7C7",

	@field:SerializedName("white")
	val white: String? = "#FFFFFF",

	@field:SerializedName("categorySegment")
	val categorySegment: String? = "#FFFFFF",

	@field:SerializedName("brand_logo")
	val brandLogo: String? = null,

	@field:SerializedName("backgrounds")
	val backgroundImages: List<String> = arrayListOf()
)
