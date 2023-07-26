package com.nyotek.dot.admin.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.getRadius

class NSConstraintLayout : ConstraintLayout {

    constructor(context: Context) : super(context) {
        if (!isInEditMode) {
            init()
        }
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        if (!isInEditMode) {
            init()
        }
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        if (!isInEditMode) {
            init()
        }
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        if (!isInEditMode) {
            init()
        }
    }

    fun init() {
        when (this.id) {
            R.id.cl_brand_logo -> {
                ColorResources.setCardBackground(this, 10f, 2, ColorResources.getBackgroundColor(), ColorResources.getBorderColor())
            }
            R.id.cl_left_with_bg -> {
                ColorResources.setBackground(this, ColorResources.getPrimaryColor())
            }
            R.id.cl_login_bg, R.id.cl_item, R.id.cl_sign_up_bg -> {
                ColorResources.setBackground(this, ColorResources.getPrimaryColor())
            }
            R.id.cl_email, R.id.cl_password, R.id.cl_username -> {
                ColorResources.setCardBgWhiteBackground(this, 8f, 2, ColorResources.getPrimaryColor())
            }
            R.id.cl_header_top, R.id.cl_header_settings -> {
                ColorResources.setConstraintBackground(this)
            }
            R.id.cl_dashboard_bg, R.id.cl_settings_bg -> {
                ColorResources.setBackground(this)
            }
            R.id.cl_side_nav -> {
                ColorResources.setBackground(this, ColorResources.getSecondaryColor())
            }
            R.id.cl_view_more_vendor -> {
                ColorResources.setBackgroundTint(this, ColorResources.getPrimaryColor())
            }
            R.id.cl_search -> {
                ColorResources.setCardBackground(this, 8f, 1, ColorResources.getBackgroundColor(), ColorResources.getBorderColor())
            }
            R.id.cl_address -> {
                ColorResources.setCardBackground(this, 6f, 1, ColorResources.getSecondaryColor(), ColorResources.getSecondaryDarkColor())
            }
            R.id.cl_border_bg, R.id.cl_user_list -> {
                ColorResources.setCardBackground(this, 10f, 1, ColorResources.getWhiteColor(), ColorResources.getBorderColor())
            }
            R.id.cl_theme_spinner, R.id.cl_role_spinner -> {
                ColorResources.setCardBackground(this, 10f, 1, ColorResources.getWhiteColor(), ColorResources.getBorderColor())
            }
            R.id.cl_vehicle_detail_bottom -> {
                ColorResources.setCardBackground(this, getRadius(10f), 0, ColorResources.getBackgroundColor(), ColorResources.getBackgroundColor())
            }
            R.id.cl_vehicle_detail_sub_bottom -> {
                ColorResources.setCardBackground(this, getRadius(10f), 0, ColorResources.getWhiteColor(), ColorResources.getWhiteColor())
            }
        }
    }
}