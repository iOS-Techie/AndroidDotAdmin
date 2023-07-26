package com.nyotek.dot.admin.common

import android.os.SystemClock
import android.view.View

class SafeClickListener(private val onClickListener: () -> Unit) : View.OnClickListener {
    private var lastClickTime = 0L

    override fun onClick(v: View?) {
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - lastClickTime >= CLICK_COOL_DOWN) {
            lastClickTime = currentTime
            onClickListener.invoke()
        }
    }

    companion object {
        private const val CLICK_COOL_DOWN = 1000L
    }
}