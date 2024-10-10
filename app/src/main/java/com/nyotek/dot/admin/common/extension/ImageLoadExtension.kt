package com.nyotek.dot.admin.common.extension

import android.widget.ImageView
import coil.load
import coil.request.CachePolicy
import coil.size.Scale
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.NSConstants

fun ImageView.setCoil(url: String?, exScale: String?, corners: Float = 4f) {
    load(url) {
        scale(if (exScale == NSConstants.FILL) Scale.FILL else Scale.FIT).error(R.drawable.ic_place_holder_img).placeholder(
            R.drawable.ic_place_holder_progress)
        memoryCachePolicy(CachePolicy.ENABLED)
        diskCachePolicy(CachePolicy.ENABLED)
        transformations(RoundedCornersTransformation(corners, corners, corners,corners))
    }
}

fun ImageView.setCoilCircle(url: String?) {
    load(url) {
        scale(Scale.FILL).error(R.drawable.ic_place_holder_home).placeholder(R.drawable.ic_place_holder_progress)
        memoryCachePolicy(CachePolicy.ENABLED)
        diskCachePolicy(CachePolicy.ENABLED)
        transformations(CircleCropTransformation())
    }
}

fun ImageView.setCoilCircle(url: Int) {
    load(url) {
        scale(Scale.FILL).placeholder(R.drawable.ic_place_holder_progress).error(R.drawable.ic_place_holder_img)
        memoryCachePolicy(CachePolicy.ENABLED)
        diskCachePolicy(CachePolicy.ENABLED)
        transformations(CircleCropTransformation())
    }
}

fun ImageView.setCoilCenter(url: String?) {
    load(url) {
        scale(Scale.FILL).error(R.drawable.ic_place_holder_product).placeholder(R.drawable.ic_place_holder_progress)
        memoryCachePolicy(CachePolicy.ENABLED)
        diskCachePolicy(CachePolicy.ENABLED)
    }
}

fun ImageView.setCoilCircleImage(url: Int?) {
    load(url) {
        scale(Scale.FILL).error(R.drawable.ic_place_holder_home).placeholder(R.drawable.ic_place_holder_progress)
        memoryCachePolicy(CachePolicy.ENABLED)
        diskCachePolicy(CachePolicy.ENABLED)
        transformations(CircleCropTransformation())
    }
}

fun ImageView.setCoil(resource: Int = R.drawable.ic_place_holder_product, url: String? = null) {
    load(url) {
        placeholder(R.drawable.ic_place_holder_progress)
        error(resource)
        memoryCachePolicy(CachePolicy.ENABLED)
        diskCachePolicy(CachePolicy.ENABLED)
    }
}

fun ImageView.setCoil(url: String?, exScale: String?, placeHolder: Int = R.drawable.ic_place_holder_home, corners: Float = 4f) {
    if (placeHolder > 0) {
        load(url) {
            scale(if (exScale == NSConstants.FILL) Scale.FILL else Scale.FIT).
            placeholder(R.drawable.ic_place_holder_progress).error(placeHolder)
            memoryCachePolicy(CachePolicy.ENABLED)
            diskCachePolicy(CachePolicy.ENABLED)
            transformations(RoundedCornersTransformation(corners, corners, corners, corners))
        }
    } else {
        load(url) {
            scale(if (exScale == NSConstants.FILL) Scale.FILL else Scale.FIT).placeholder(R.drawable.ic_place_holder_progress)
            memoryCachePolicy(CachePolicy.ENABLED)
            diskCachePolicy(CachePolicy.ENABLED)
            transformations(RoundedCornersTransformation(corners, corners, corners, corners))
        }
    }
}

fun ImageView.setCoil(url: Int?, exScale: String?, placeHolder: Int = R.drawable.ic_place_holder_home, corners: Float = 4f) {
    if (placeHolder > 0) {
        load(url) {
            scale(if (exScale == NSConstants.FILL) Scale.FILL else Scale.FIT)
                .placeholder(R.drawable.ic_place_holder_progress).error(placeHolder)
            memoryCachePolicy(CachePolicy.ENABLED)
            diskCachePolicy(CachePolicy.ENABLED)
            transformations(RoundedCornersTransformation(corners, corners, corners, corners))
        }
    } else {
        load(url) {
            scale(if (exScale == NSConstants.FILL) Scale.FILL else Scale.FIT).placeholder(R.drawable.ic_place_holder_progress)
            memoryCachePolicy(CachePolicy.ENABLED)
            diskCachePolicy(CachePolicy.ENABLED)
            transformations(RoundedCornersTransformation(corners, corners, corners, corners))
        }
    }
}

