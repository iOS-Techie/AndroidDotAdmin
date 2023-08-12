package com.nyotek.dot.admin.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.utils.ColorResources

class NSPrimaryTextView : AppCompatTextView {

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
        setTextColor(ColorResources.getPrimaryColor())
        when (id) {
            R.id.tv_back_settings, R.id.tv_cancel_service, R.id.tv_cancel_app, R.id.tv_cancel -> {
                ColorResources.setBlankBackground(this, 5f, 1, ColorResources.getPrimaryColor())
            }
            R.id.tv_modify -> {
                ColorResources.setBlankBackground(this, 5f, 1, ColorResources.getPrimaryColor())
            }
            R.id.tv_select_all_title -> {
                text = NSApplication.getInstance().getStringModel().selectAll
            }
        }
    }
}