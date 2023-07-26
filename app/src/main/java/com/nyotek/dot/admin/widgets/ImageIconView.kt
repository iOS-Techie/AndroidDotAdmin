package com.nyotek.dot.admin.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.getRadius

class ImageIconView : AppCompatImageView {
    constructor(context: Context) : super(context) {
        if (!isInEditMode) {
            setImageIcon(context)
        }
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        if (!isInEditMode) {
            setImageIcon(context)
        }
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        if (!isInEditMode) {
            setImageIcon(context)
        }
    }

    private fun setImageIcon(context: Context) {
        if (id != R.id.iv_brand_icon) {
            when (id) {
                R.id.iv_clear_data -> {
                    setColorFilter(ColorResources.getGrayColor())
                }
                R.id.iv_delete -> {
                    setColorFilter(ColorResources.getErrorColor())
                }
                R.id.iv_edit_brand_logo -> {
                    ColorResources.setCardBackground(this, getRadius(5f), 0, ColorResources.getPrimaryColor())
                    setColorFilter(ColorResources.getWhiteColor())
                }
                else -> {
                    setColorFilter(ColorResources.getPrimaryColor())
                }
            }
        } else {
            //Glide.with(context).load(ColorResources.getBrandLogo()).into(this)
            Glide.with(context).load(R.drawable.app_icon_blue).into(this)
        }
    }
}