package com.nyotek.dot.admin.widgets

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputLayout
import com.nyotek.dot.admin.common.utils.ColorResources

class NSTextInputLayout : TextInputLayout {
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
        this.hintTextColor = ColorResources.getHintColorStateGray()
    }
}