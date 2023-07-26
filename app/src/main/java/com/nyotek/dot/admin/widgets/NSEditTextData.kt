package com.nyotek.dot.admin.widgets

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

/**
 * View that extends EditText and handled the common functionality across modules
 */
class NSEditTextData : AppCompatEditText, TextWatcher {

    private var selectedText = ""

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        addTextChangedListener(this)
    }

    override fun afterTextChanged(editable: Editable?) {

    }

    override fun onTextChanged(
        tex: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int
    ) {
        // on text change no implementation so far
        if (selectedText != tex.toString()) {
            selectedText = tex.toString()
            setText(convertToArabic(tex.toString()))
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // before text change no need to do anything

    }

    /**
     * To get the string of edittext
     *
     * @return text of edittext
     */
    fun getString(): String = text.toString().trim()

    private fun convertToArabic(value: String): String {
        return (value + "")
            .replace("١".toRegex(), "1").replace("٢".toRegex(),"2")
            .replace("٣".toRegex(), "3").replace("٤".toRegex(), "4")
            .replace("٥".toRegex(), "5").replace("٦".toRegex(), "6")
            .replace("٧".toRegex(), "7").replace("٨".toRegex(), "8")
            .replace("٩".toRegex(), "9").replace("٠".toRegex(), "0")
    }
}