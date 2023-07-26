package com.nyotek.dot.admin.common

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class NestedInnerRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var lastY: Float = 0f

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                lastY = ev.rawY
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaY = ev.rawY - lastY
                lastY = ev.rawY

                // Check if it's the first item and scrolling up or the last item and scrolling down
                val isScrollingUp = deltaY > 0 && !canScrollVertically(-1)
                val isScrollingDown = deltaY < 0 && !canScrollVertically(1)

                // Allow parent to intercept the touch event if we reach the first or last item
                parent.requestDisallowInterceptTouchEvent(isScrollingUp || isScrollingDown)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> parent.requestDisallowInterceptTouchEvent(false)
        }
        return super.onInterceptTouchEvent(ev)
    }
}