package com.nyotek.dot.admin.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.extension.getRadius
import com.nyotek.dot.admin.common.utils.ColorResources
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ImageIconView : AppCompatImageView {

    @Inject
    lateinit var colorResources: ColorResources

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
                    setColorFilter(colorResources.getGrayColor())
                }
                R.id.iv_delete -> {
                    setColorFilter(colorResources.getErrorColor())
                }
                R.id.iv_edit_brand_logo -> {
                    colorResources.setCardBackground(this, getRadius(5f), 0, colorResources.getPrimaryColor())
                    setColorFilter(colorResources.getWhiteColor())
                }
                R.id.ic_driver_detail_user -> {
                    colorResources.setCardBackground(this, getRadius(5f), 1, colorResources.getWhiteColor(), colorResources.getBorderColor())

                }
                R.id.iv_driver_img, R.id.iv_model_img -> {
                    colorResources.setCardBackground(this, getRadius(100f), 0, colorResources.getBackgroundColor(), colorResources.getBackgroundColor())
                }
                R.id.iv_delete_employee -> {
                    colorResources.setCardBackground(this, getRadius(3f), 0, colorResources.getErrorColor(), colorResources.getErrorColor())
                    setColorFilter(colorResources.getWhiteColor())
                }
                else -> {
                    setColorFilter(colorResources.getPrimaryColor())
                }
            }
        } else {
            //Glide.with(context).load(ColorResources.getBrandLogo()).into(this)
            Glide.with(context).load(R.drawable.app_icon_blue).into(this)
        }
    }
}