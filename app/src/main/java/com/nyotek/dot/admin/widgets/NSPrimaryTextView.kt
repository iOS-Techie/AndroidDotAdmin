package com.nyotek.dot.admin.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.NSUtilities
import com.nyotek.dot.admin.common.utils.ColorResources
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NSPrimaryTextView : AppCompatTextView {

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
        setTextColor(colorResources.getPrimaryColor())
        when (id) {
             R.id.tv_cancel_service, R.id.tv_cancel_app, R.id.tv_cancel -> {
                colorResources.setBlankBackground(this, 5f, 1, colorResources.getPrimaryColor())
            }
            R.id.tv_modify -> {
                colorResources.setBlankBackground(this, 5f, 1, colorResources.getPrimaryColor())
            }
            R.id.tv_select_all_title -> {
                text = NSUtilities.getStringResource().selectAll
            }
        }
    }
}