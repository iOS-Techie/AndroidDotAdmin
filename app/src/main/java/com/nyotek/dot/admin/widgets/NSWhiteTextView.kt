package com.nyotek.dot.admin.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.extension.getRadius
import com.nyotek.dot.admin.common.utils.ColorResources
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NSWhiteTextView : AppCompatTextView {

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

    private fun init() {
        setTextColor(colorResources.getWhiteColor())
        when (id) {
            R.id.btn_login, R.id.btn_reset -> {
                colorResources.setCardBackground(
                    this,
                    getRadius(8f),
                    0,
                    colorResources.getPrimaryColor()
                )
            }

            R.id.tv_update_driver -> {
                colorResources.setCardBackground(
                    this,
                    getRadius(4f),
                    0,
                    colorResources.getPrimaryColor()
                )
            }

            R.id.tv_header_btn, R.id.tv_service_submit, R.id.tv_submit_app, R.id.tv_save, R.id.tv_add_address, R.id.tv_create_vehicle, R.id.tv_add_employee, R.id.tv_send_invite, R.id.tv_create -> {
                colorResources.setCardBackground(this, 5f, 0, colorResources.getPrimaryColor())
            }

            R.id.btn_save_address, R.id.tv_see_all, R.id.tv_order_places, R.id.tv_driver_id, R.id.tv_customer_id, R.id.tv_vendor_id -> {
                colorResources.setCardBackground(this, 100f, 0, colorResources.getPrimaryColor())
            }

            R.id.tv_update_status -> {
                colorResources.setBackgroundTint(this, colorResources.getPrimaryColor())
            }
            R.id.tv_assign_driver -> {
                colorResources.setBackgroundTint(this, colorResources.getPrimaryColor())
            }
            R.id.tv_order_cancel -> {
                colorResources.setBackgroundTint(this, colorResources.getErrorColor())
            }
        }
    }
}