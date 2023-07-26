package com.nyotek.dot.admin.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.nyotek.dot.admin.common.utils.ColorResources

class NSGrayTextView : AppCompatTextView {
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
        setTextColor(ColorResources.getGrayColor())
    }
}