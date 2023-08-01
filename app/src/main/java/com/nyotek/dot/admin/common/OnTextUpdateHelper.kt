package com.nyotek.dot.admin.common

import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class OnTextUpdateHelper(private val etText: EditText, private val currentText: String, private val callback: ((String) -> Unit)) {

    private val handler = Handler(Looper.getMainLooper())
    private val textChangedCallback =
        Runnable { callback.invoke(etText.text.toString().trim()) }

    init {
        etText.addTextChangedListener (object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                handler.removeCallbacks(textChangedCallback)
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != currentText) {
                    handler.postDelayed(textChangedCallback, 500)
                }
            }
        })
    }
}