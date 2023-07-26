package com.nyotek.dot.admin.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.utils.ColorResources

class NSCommonEditText : AppCompatEditText {

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
        when (id) {
            R.id.et_service_description -> {
                ColorResources.setCardBackground(this, 10f, 1, ColorResources.getWhiteColor(), ColorResources.getBorderColor())
            }
        }
        setTextColor(ColorResources.getPrimaryColor())
        setHintTextColor(ColorResources.getTabSecondaryColor())
    }
}