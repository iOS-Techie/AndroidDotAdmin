package com.nyotek.dot.admin.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.getRadius

class NSWhiteTextView : AppCompatTextView {
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
        setTextColor(ColorResources.getWhiteColor())
        when (id) {
            R.id.btn_login, R.id.btn_reset -> {
                ColorResources.setCardBackground(this, getRadius(8f), 0, ColorResources.getPrimaryColor())
            }
           R.id.tv_save_settings, R.id.tv_header_btn, R.id.tv_service_submit, R.id.tv_submit_app, R.id.tv_save, R.id.tv_add_address, R.id.tv_create_vehicle, R.id.tv_add_employee, R.id.tv_send_invite, R.id.tv_create -> {
                ColorResources.setCardBackground(this, 5f, 0, ColorResources.getPrimaryColor())
            }
            R.id.btn_save_address -> {
                ColorResources.setCardBackground(this, 100f, 0, ColorResources.getPrimaryColor())
            }
        }
    }
}