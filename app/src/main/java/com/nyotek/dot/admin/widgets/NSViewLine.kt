package com.nyotek.dot.admin.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.extension.getColorWithAlpha
import com.nyotek.dot.admin.common.utils.ColorResources
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NSViewLine : View {

    @Inject
    lateinit var colorResources: ColorResources

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
            R.id.view_divider_dashboard, R.id.view_divider_dashboard_vertical, R.id.view_line_text, R.id.view_line_text_divider, R.id.view_line_text_sub, R.id.view_line_divider -> {
                colorResources.setBackground(this,getColorWithAlpha(colorResources.getPrimaryColor(), 5f))
            }
            R.id.view_line_decoration -> {
                colorResources.setBackground(this,getColorWithAlpha(colorResources.getPrimaryColor(), 100f))
            }
            R.id.view_line_dispatch -> {
                colorResources.setCardBackground(this, 0f, 0, colorResources.getBorderColor(), colorResources.getBorderColor())
            }
            R.id.view_line_side -> {
                colorResources.setBackground(this, colorResources.getPrimaryColor())
            }
            R.id.view_line_left -> {
                colorResources.setBackgroundTint(this, colorResources.getPrimaryColor())
            }
            else -> {
                colorResources.setBackground(this, colorResources.getBorderColor())
            }
        }
    }
}