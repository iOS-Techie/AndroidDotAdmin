package com.nyotek.dot.admin.ui.common

import android.app.Application
import android.graphics.Bitmap
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.common.callbacks.NSFileUploadCallback
import com.nyotek.dot.admin.repository.NSThemeRepository
import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.requests.NSFleetNameUpdateRequest
import com.nyotek.dot.admin.repository.network.responses.VehicleDataItem
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.NSUploadFileResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class NSFileUploadViewModel(application: Application) : NSViewModel(application) {

    fun manageUploadFile(imageUrl: String, bitmap: Bitmap, callback: NSFileUploadCallback) {
        val file = File(imageUrl)
        val requestFile: RequestBody = file.asRequestBody("*/*".toMediaTypeOrNull())
        val myFile = MultipartBody.Part.createFormData("myFile", file.name, requestFile)
        NSThemeRepository.uploadFile(myFile, object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                if (data is NSUploadFileResponse) {
                    val url =  data.browserUrl?:""
                    callback.onFileUrl(url, bitmap.width, bitmap.height)
                }
            }

            override fun onError(errors: List<Any>) {
               handleError(errors)
            }

            override fun onFailure(failureMessage: String?) {
              handleFailure(failureMessage)
            }

            override fun <T> onNoNetwork(localData: T) {
                handleNoNetwork()
            }

        })
    }

    override fun apiResponse(data: Any) {

    }
}