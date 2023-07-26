package com.nyotek.dot.admin.common.callbacks

interface NSFileUploadCallback {
    fun onFileUrl(url: String, width: Int, height: Int)
}