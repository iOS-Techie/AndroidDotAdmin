package com.nyotek.dot.admin.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.extension.getRadius
import com.nyotek.dot.admin.common.utils.ColorResources
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NSConstraintLayout : ConstraintLayout {

    @Inject
    lateinit var colorResources: ColorResources

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
            R.id.cl_driver_detail, R.id.cl_dispatch_detail -> {
                colorResources.setBackground(this)
            }
            R.id.cl_brand_logo -> {
                colorResources.setCardBackground(this, 10f, 2, colorResources.getBackgroundColor(), colorResources.getBorderColor())
            }
            R.id.cl_left_with_bg -> {
                colorResources.setBackground(this, colorResources.getPrimaryColor())
            }
            R.id.cl_login_bg, R.id.cl_item, R.id.cl_sign_up_bg -> {
                colorResources.setBackground(this, colorResources.getPrimaryColor())
            }
            R.id.cl_email, R.id.cl_password, R.id.cl_username -> {
                colorResources.setCardBgWhiteBackground(this, 8f, 2, colorResources.getPrimaryColor())
            }
            R.id.cl_header_top -> {
                colorResources.setConstraintBackground(this)
            }
            R.id.cl_dashboard_bg, R.id.cl_settings_bg -> {
                colorResources.setBackground(this)
            }
            R.id.cl_side_nav -> {
                colorResources.setBackground(this, colorResources.getSecondaryColor())
            }
            R.id.cl_view_more_vendor -> {
                colorResources.setBackgroundTint(this, colorResources.getPrimaryColor())
            }
            R.id.cl_search -> {
                colorResources.setCardBackground(this, 8f, 1, colorResources.getBackgroundColor(), colorResources.getBorderColor())
            }
            R.id.cl_address -> {
                colorResources.setCardBackground(this, 6f, 1, colorResources.getSecondaryColor(), colorResources.getSecondaryDarkColor())
            }
            R.id.cl_border_bg, R.id.cl_dispatch_view -> {
                colorResources.setCardBackground(this, 10f, 1, colorResources.getWhiteColor(), colorResources.getBorderColor())
            }
            R.id.cl_dispatch_border_bg -> {
                colorResources.setCardBackground(this, 8f, 1, colorResources.getWhiteColor(), colorResources.getBorderColor())
            }
            R.id.cl_theme_spinner, R.id.cl_role_spinner -> {
                colorResources.setCardBackground(this, 10f, 1, colorResources.getWhiteColor(), colorResources.getBorderColor())
            }
            R.id.cl_vehicle_detail_bottom -> {
                colorResources.setCardBackground(this, getRadius(6f), 0, colorResources.getBackgroundColor(), colorResources.getBackgroundColor())
            }
            R.id.cl_vehicle_detail_sub_bottom -> {
                colorResources.setCardBackground(this, getRadius(6f), 0, colorResources.getWhiteColor(), colorResources.getWhiteColor())
            }
            R.id.cl_track -> {
                colorResources.setCardBackground(this, getRadius(100f), 1, colorResources.getWhiteColor(), colorResources.getPrimaryColor())
            }
            R.id.cl_top_create_fleet -> {
                colorResources.setCardBackground(this, 0f, 1, colorResources.getWhiteColor(), colorResources.getBorderColor())
            }
        }
    }
}