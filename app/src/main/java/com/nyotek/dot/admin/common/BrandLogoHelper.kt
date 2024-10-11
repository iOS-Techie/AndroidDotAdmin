package com.nyotek.dot.admin.common

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.drjacky.imagepicker.ImagePicker
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.callbacks.NSFileUploadCallback
import com.nyotek.dot.admin.common.extension.setSafeOnClickListener
import com.nyotek.dot.admin.ui.common.UserUploadViewModel

class BrandLogoHelper(fragment: Fragment, private val callback: NSFileUploadCallback) {

    val fileViewModel: UserUploadViewModel by lazy {
        ViewModelProvider(fragment)[UserUploadViewModel::class.java]
    }
    private var activity: Activity? = null
    private var brandLogoDataResult: ActivityResultLauncher<Intent>? = null
    private var ivBrandLogo: ImageView? = null
    private var tvTextSizeImage: TextView? = null
    private var isUpload: Boolean = false
    private var isFill: Boolean = false
    private var cbFill: CheckBox? = null
    private var clCheckFill: ConstraintLayout? = null
    private var cbFit: CheckBox? = null
    private var clCheckFit: ConstraintLayout? = null

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
        ivBrandLogo?.setImageResource(R.drawable.ic_place_holder_img_second)
    }

    fun logoFillFit(cbFill: CheckBox?,clCheckFill: ConstraintLayout?, cbFit: CheckBox?, clCheckFit: ConstraintLayout?, url: String?) {
        this.cbFill = cbFill
        this.cbFit = cbFit
        this.clCheckFit = clCheckFit
        this.clCheckFill = clCheckFill
        clCheckFill?.setSafeOnClickListener {
            cbFit?.isChecked = false
            cbFill?.isChecked = true
            setBrandLogo(
                false,
                url ?: "",
                cbFill?.isChecked == true
            )
        }

        clCheckFit?.setSafeOnClickListener {
            cbFit?.isChecked = true
            cbFill?.isChecked = false
            setBrandLogo(
                false,
                url ?: "",
                cbFill?.isChecked == true
            )
        }
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

        if (cbFill != null) {
            logoFillFit(cbFill, clCheckFill, cbFit, clCheckFit, logoUrl)
        }
    }

    private fun setWidthHeightOnImage(bitmap: Bitmap) {
        val size = "${bitmap.width}X${bitmap.height}"
        tvTextSizeImage?.text = size
       // tvTextSizeImage?.visible()
    }
}