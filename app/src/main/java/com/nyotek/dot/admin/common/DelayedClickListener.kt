package com.nyotek.dot.admin.common

import android.view.View

class DelayedClickListener(
    private val delay: Long,
    private val onClickListener: () -> Unit
) : View.OnClickListener {
    override fun onClick(v: View?) {
        v?.isEnabled = false
        v?.postDelayed({
            v.isEnabled = true
            onClickListener.invoke()
        }, delay)
    }
}