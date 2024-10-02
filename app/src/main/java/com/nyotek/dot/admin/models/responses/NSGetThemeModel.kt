package com.nyotek.dot.admin.models.responses

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
	
	@field:SerializedName("white")
	val white: String? = "#FFFFFF",
	
	@field:SerializedName("cancel_button")
	val cancelButton: String? = "#FF6A69",
	
	@field:SerializedName("border_color")
	val borderColor: String? = "#E1E7F6",
	
	@field:SerializedName("log_bg_color")
	val logBgColor: String? = "#E5E4E9",
	
	@field:SerializedName("map_selection_bg")
	val mapSelectionBg: String? = "#EDE5DC",
	
	@field:SerializedName("categorySegment")
	val categorySegment: String? = null,
	
	@field:SerializedName("categorySegmentBackground")
	val categorySegmentBackground: String? = null,
	
	@field:SerializedName("categorySelectedBackground")
	val categorySelectedBackground: String? = null,
	
	@field:SerializedName("categoryUnselectedBackground")
	val categoryUnselectedBackground: String? = null,
	
	@field:SerializedName("categorySelectedText")
	val categorySelectedText: String? = null,
	
	@field:SerializedName("categoryUnselectedText")
	val categoryUnselectedText: String? = null,
	
	@field:SerializedName("brand_logo")
	val brandLogo: String? = null,
	
	@field:SerializedName("logo_hide_title")
	val logoHideTitle: Boolean? = false,
	
	@field:SerializedName("primaryTitle")
	val primaryTitle: String? = null,
	
	@field:SerializedName("secondaryTitle")
	val secondaryTitle: String? = null,
	
	@field:SerializedName("buttonBackground")
	val buttonBackground: String? = null,
	
	@field:SerializedName("buttonTitle")
	val buttonTitle: String? = null,
	
	@field:SerializedName("placeholder_logo")
	val placeholderLogo: String? = null,
	
	@field:SerializedName("backgrounds")
	val backgroundImages: List<String>? = arrayListOf()
)