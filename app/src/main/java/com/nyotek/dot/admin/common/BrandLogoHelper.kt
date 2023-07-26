package com.nyotek.dot.admin.common

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.drjacky.imagepicker.ImagePicker
import com.nyotek.dot.admin.common.callbacks.NSFileUploadCallback
import com.nyotek.dot.admin.common.utils.visible
import com.nyotek.dot.admin.ui.common.NSFileUploadViewModel

class BrandLogoHelper(fragment: Fragment, private val callback: NSFileUploadCallback) {

    val fileViewModel: NSFileUploadViewModel by lazy {
        ViewModelProvider(fragment)[NSFileUploadViewModel::class.java]
    }
    private var activity: Activity? = null
    private var brandLogoDataResult: ActivityResultLauncher<Intent>? = null
    private var ivBrandLogo: ImageView? = null
    private var tvTextSizeImage: TextView? = null
    private var isUpload: Boolean = false
    private var isFill: Boolean = false

    init {
        fragment.apply {

            brandLogoDataResult =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                    val resultCode = result.resultCode
                    val data = result.data

                    when (resultCode) {
                        Activity.RESULT_OK -> {
                            val fileUri = data!!.data!!
                            setBrandLogo(isUpload, fileUri.path.toString(), isFill)
                        }
                        ImagePicker.RESULT_ERROR -> {
                            Toast.makeText(activity, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }

    fun initView(activity: Activity, imageView: ImageView?, tvTextSize: TextView?,) {
        this.activity = activity
        ivBrandLogo = imageView
        tvTextSizeImage = tvTextSize
    }

    fun openImagePicker(activity: Activity, imageView: ImageView?, tvTextSize: TextView?, isNeedUpload: Boolean, isFill: Boolean) {
        this.activity = activity
        ivBrandLogo = imageView
        tvTextSizeImage = tvTextSize
        isUpload = isNeedUpload
        this.isFill = isFill
        brandLogoDataResult?.launch(
            ImagePicker.with(activity)
                .galleryOnly()
                .crop()
                .cropFreeStyle()
                .createIntent()
        )
    }

    fun setBrandLogo(isUpload: Boolean, logoUrl: String, isFill: Boolean) {
        Glide.with(activity?.applicationContext!!)
            .asBitmap()
            .load(logoUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    ivBrandLogo?.scaleType = if (isFill) ImageView.ScaleType.CENTER_CROP else ImageView.ScaleType.FIT_CENTER
                    ivBrandLogo?.setImageBitmap(resource)
                    setWidthHeightOnImage(resource)

                    if (isUpload) {
                        fileViewModel.manageUploadFile(logoUrl, bitmap = resource,callback)
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
    }

    private fun setWidthHeightOnImage(bitmap: Bitmap) {
        val size = "${bitmap.width}X${bitmap.height}"
        tvTextSizeImage?.text = size
        tvTextSizeImage?.visible()
    }
}