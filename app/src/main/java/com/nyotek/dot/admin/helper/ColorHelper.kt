package com.nyotek.dot.admin.helper

import com.nyotek.dot.admin.BuildConfig
import com.nyotek.dot.admin.models.responses.NSGetThemeData


object ColorHelper {

    var primary = if(BuildConfig.TYPE.contentEquals("0")) "#FF001D52" else "#FF35005F"
    var primaryLight = if(BuildConfig.TYPE.contentEquals("0")) "#FF7F8EA9" else "#FF866A9B"
    var secondary =  if(BuildConfig.TYPE.contentEquals("0")) "#FFFFFAF4" else "#FFF1F4FB"
    var secondaryDark =  if(BuildConfig.TYPE.contentEquals("0")) "#FFF8EBDF" else "#FFD8DFEF"
    var background =  if(BuildConfig.TYPE.contentEquals("0")) "#FFEEF1F8" else "#FFF1E9F7"
    var tabSecondary =  if(BuildConfig.TYPE.contentEquals("0")) "#FF808080" else "#FF808080"
    var categorySegment =  if(BuildConfig.TYPE.contentEquals("0")) "#00000000" else "#40360060"
    var categorySegmentBackground =  if(BuildConfig.TYPE.contentEquals("0")) "#FFD8DFEF" else "#FFD8DFEF"
    var categorySelectedBackground =  if(BuildConfig.TYPE.contentEquals("0")) "#FFFF6337" else "#FFFF6337"
    var categoryUnselectedBackground =  if(BuildConfig.TYPE.contentEquals("0")) "#FFFFFFFF" else "#FFFFFFFF"
    var categorySelectedText =  if(BuildConfig.TYPE.contentEquals("0")) "#FFFFFFFF" else "#FFFFFFFF"
    var categoryUnselectedText =  if(BuildConfig.TYPE.contentEquals("0")) "#FF35005F" else "#FF35005F"
    var primaryTitle =  if(BuildConfig.TYPE.contentEquals("0")) "#FF001D52" else "#FF35005F"
    var secondaryTitle =  if(BuildConfig.TYPE.contentEquals("0")) "#FF7F8EA9" else "#FF866A9B"
    var buttonBackground =  if(BuildConfig.TYPE.contentEquals("0")) "#FF35005F" else "#FF35005F"
    var buttonTitle =  if(BuildConfig.TYPE.contentEquals("0")) "#FFFFFFFF" else "#FFFFFFFF"
    var success =  if(BuildConfig.TYPE.contentEquals("0")) "#FF0FCE6E" else "#FF4BB543"
    var error =  if(BuildConfig.TYPE.contentEquals("0")) "#FFE74B3C" else "#FFE74B3C"

    fun getThemeData(): NSGetThemeData {
        val theme = NSGetThemeData(primary = primary, primaryLight = primaryLight, secondary = secondary, secondaryDark = secondaryDark, background = background, tabSecondary = tabSecondary
        , categorySegment = categorySegment, categorySegmentBackground = categorySegmentBackground, categoryUnselectedBackground = categoryUnselectedBackground, categorySelectedBackground = categorySelectedBackground
        , categorySelectedText = categorySelectedText, categoryUnselectedText = categoryUnselectedText, primaryTitle = primaryTitle, secondaryTitle = secondaryTitle, buttonBackground = buttonBackground,
            buttonTitle = buttonTitle, success = success, error = error)
        return theme
    }
}