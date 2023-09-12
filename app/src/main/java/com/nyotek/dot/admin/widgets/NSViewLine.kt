package com.nyotek.dot.admin.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.getColorWithAlpha

class NSViewLine : View {

    constructor(context: Context?) : super(context) {
        if (!isInEditMode) {
            init()
        }
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        if (!isInEditMode) {
            init()
        }
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        if (!isInEditMode) {
            init()
        }
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        when (id) {
            R.id.view_divider_dashboard, R.id.view_divider_dashboard_vertical, R.id.view_line_text, R.id.view_line_text_divider, R.id.view_line_text_sub -> {
                ColorResources.setBackground(this,getColorWithAlpha(ColorResources.getPrimaryColor(), 5f))
            }
            R.id.view_line_decoration -> {
                ColorResources.setBackground(this,getColorWithAlpha(ColorResources.getPrimaryColor(), 100f))
            }
            else -> {
                ColorResources.setBackground(this, ColorResources.getBorderColor())
            }
        }
    }
}