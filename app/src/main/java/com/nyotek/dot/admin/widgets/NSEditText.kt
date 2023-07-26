package com.nyotek.dot.admin.widgets

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.nyotek.dot.admin.common.utils.ColorResources

/**
 * View that extends EditText and handled the common functionality across modules
 */
class NSEditText : AppCompatEditText, TextWatcher {
    private lateinit var textChangeCallback: NSTextChangeCallback

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        setTextColor(ColorResources.getPrimaryColor())
        ColorResources.setCardBackground(this, 10f, 2, ColorResources.getWhiteColor(), ColorResources.getPrimaryColor())
        addTextChangedListener(this)
    }

    override fun afterTextChanged(editable: Editable?) {
        if (::textChangeCallback.isInitialized) {
            textChangeCallback.afterTextChange(editable.toString())
        }
    }

    override fun onTextChanged(
        text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int
    ) {
        // on text change no implementation so far
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // before text change no need to do anything
        if (::textChangeCallback.isInitialized) {
            textChangeCallback.beforeTextChange(s!!.length)
        }
    }

    /**
     * To set the listener to listen whether the text change has occurred
     *
     * @param textChangeCallback The listener to set
     */
    fun setAfterTextChangeListener(textChangeCallback: NSTextChangeCallback) {
        this.textChangeCallback = textChangeCallback
    }

    /**
     * To get the string of edittext
     *
     * @return text of edittext
     */
    fun getString(): String = text.toString().trim()
}

/**
 * The listener to listen for text changes in an edit text
 */
interface NSTextChangeCallback {
    /**
     * This method is called after the text changes happened in the edit text
     *
     * @param text The text entered by the user
     */
    fun afterTextChange(text: String)

    fun beforeTextChange(count: Int)
}