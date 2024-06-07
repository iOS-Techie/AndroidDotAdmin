package com.nyotek.dot.admin.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.utils.ColorResources
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NSCommonEditText : AppCompatEditText {

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
        when (id) {
            R.id.et_service_description -> {
                colorResources.setCardBackground(this, 10f, 1, colorResources.getWhiteColor(), colorResources.getBorderColor())
            }
        }
        setTextColor(colorResources.getPrimaryColor())
        setHintTextColor(colorResources.getTabSecondaryColor())
    }
}