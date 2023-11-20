package com.nyotek.dot.admin.common.utils

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.cardview.widget.CardView
import com.nyotek.dot.admin.common.NSApplication

object ColorResources {
    var resourceData = NSApplication.getInstance().getThemeModel()
    val states = arrayOf(
        intArrayOf(android.R.attr.state_enabled)
    )
    val statesEnableDisable = arrayOf(
        intArrayOf(android.R.attr.state_enabled),
        intArrayOf(-android.R.attr.state_enabled)
    )

    val singleStates = arrayOf(
        intArrayOf()
    )

    fun getBrandLogo(): String {
        return if (resourceData.brandLogo?.isNotEmpty() == true) {
            resourceData.brandLogo!!
        } else {
            ""
        }
    }

    fun getBackgroundImages(): List<String> {
        return resourceData.backgroundImages
    }

    fun setMainBackgroundColor(view: View) {
        with(resourceData) {
            view.setBackgroundColor(getColor(background))
        }
    }

    fun getPrimaryColor(): Int {
        return getColor(resourceData.primary)
    }

    fun getPrimaryLightColor(): Int {
        return getColor(resourceData.primaryLight)
    }

    fun getWhiteColor(): Int {
        return getColor(resourceData.white)
    }

    fun getBlackColor(): Int {
        return getColor(resourceData.black)
    }

    fun getErrorColor(): Int {
        return getColor(resourceData.error)
    }

    fun getGreenColor(): Int {
        return getColor(resourceData.success)
    }

    fun getGrayColor(): Int {
        return getColor(resourceData.gray)
    }

    fun getLightGrayColor(): Int {
        return getColor(resourceData.lightGray)
    }

    fun getSecondaryDarkColor(): Int {
        return getColor(resourceData.secondaryDark)
    }

    fun getBorderColor(): Int {
        return getColor(resourceData.borderColor)
    }

    fun getSecondaryColor(): Int {
        return getColor(resourceData.secondary)
    }

    fun getSecondaryGrayColor(): Int {
        return getColor("#808080")
    }

    fun getBackgroundColor(): Int {
        return getColor(resourceData.background)
    }

    fun getTabSecondaryColor(): Int {
        return getColor(resourceData.tabSecondary)
    }

    fun getHintColorStateGray(): ColorStateList {
        with(resourceData) {
            val colors = intArrayOf(
                getColor(tabSecondary)
            )
            return ColorStateList(states, colors)
        }
    }

    fun getColor(color: String?): Int {
        return Color.parseColor(if (color != null && color.isNotEmpty()) color else "#FFFFFF")
    }

    fun setGreenTint(view: View) {
        view.setBackgroundColor(getGreenColor())
    }

    fun setCardDarkTint(view: View) {
        view.setBackgroundColor(getSecondaryDarkColor())
    }

    fun setBackground(view: View, color: Int = getBackgroundColor()) {
        view.setBackgroundColor(color)
    }

    fun setBackgroundTint(view: View, color: Int = getBackgroundColor()) {
        val colors = intArrayOf(
            color
        )
        view.backgroundTintList = ColorStateList(states, colors)
    }

    fun setViewEnableDisableTint(view: View) {
        view.backgroundTintList = getViewEnableDisableState()
    }

    fun getViewEnableDisableState(): ColorStateList {
        val colors = intArrayOf(
            getPrimaryColor(),
            getTabSecondaryColor()
        )
        return ColorStateList(statesEnableDisable, colors)
    }

    fun getViewState(primary: Int, tabSecondary: Int): ColorStateList {
        val colors = intArrayOf(
            primary,
            tabSecondary
        )
        return ColorStateList(statesEnableDisable, colors)
    }

    fun setCardBackground(view: CardView) {
        view.setCardBackgroundColor(getBackgroundColor())
    }

    fun setConstraintBackground(view: View) {
        view.setBackgroundColor(getSecondaryColor())
    }

    fun setCardBackground(view: View, radius: Float, width: Int = 2, bgColor: Int = getSecondaryColor(), stroke: Int = getSecondaryDarkColor()) {
        setCardMainBackground(view, radius, width, bgColor, stroke)
    }

    fun setBlankBackground(view: View, radius: Float, width: Int = 2, stroke: Int = getSecondaryDarkColor()) {
        setCardMainBackground(view, radius, width, getWhiteColor(), stroke, true)
    }


    fun setWhiteBackgroundRadius5(view: View) {
        setCardMainBackground(view, 8f, 0, getWhiteColor(), getWhiteColor())
    }

    fun setCardBgWhiteBackground(view: View, radius: Float, width: Int = 2, stroke: Int = getSecondaryDarkColor()) {
        setCardMainBackground(view, radius, width, getWhiteColor(), stroke)
    }

    private fun setCardMainBackground(
        view: View,
        radius: Float,
        width: Int = 2,
        color: Int,
        stroke: Int,
        isOnlyBorder: Boolean = false
    ) {
        val gD = GradientDrawable()
        if (!isOnlyBorder) {
            gD.setColor(color)
        }
        gD.shape = GradientDrawable.RECTANGLE
        gD.cornerRadius = radius
        gD.setStroke(width, stroke)
        view.background = gD
    }

    fun setCardDashMainBackground(
        view: View,
        radius: Float,
        width: Int = 2,
        color: Int,
        stroke: Int,
        isOnlyBorder: Boolean = false
    ) {
        val gD = GradientDrawable()
        if (!isOnlyBorder) {
            gD.setColor(color)
        }
        gD.shape = GradientDrawable.RECTANGLE
        gD.cornerRadius = radius
        gD.setStroke(width, stroke, 10f, 6f)
        view.background = gD
    }

    fun getPrimaryColorState(): ColorStateList {
        with(resourceData) {
            val colors = intArrayOf(
                getColor(primary)
            )
            return  ColorStateList(singleStates, colors)
        }
    }
}