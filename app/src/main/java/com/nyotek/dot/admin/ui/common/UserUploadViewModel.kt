package com.nyotek.dot.admin.ui.common

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.nyotek.dot.admin.base.BaseViewModel
import com.nyotek.dot.admin.common.NSLanguageConfig
import com.nyotek.dot.admin.common.callbacks.NSFileUploadCallback
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.data.Repository
import com.nyotek.dot.admin.models.responses.NSUploadFileResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UserUploadViewModel @Inject constructor(
    private val repository: Repository,
    val languageConfig: NSLanguageConfig,
    colorResources: ColorResources,
    application: Application
) : BaseViewModel(repository, languageConfig.dataStorePreference, colorResources, application) {

    fun manageUploadFile(imageUrl: String, bitmap: Bitmap, callback: NSFileUploadCallback) = viewModelScope.launch {
        manageUploadFileApi(imageUrl, bitmap, callback)
    }

    private suspend fun manageUploadFileApi(imageUrl: String, bitmap: Bitmap, callback: NSFileUploadCallback) {
        val file = File(imageUrl)
        val requestFile: RequestBody = file.asRequestBody("*/*".toMediaTypeOrNull())
        val myFile = MultipartBody.Part.createFormData("myFile", file.name, requestFile)

        performApiCalls(
            { repository.remote.uploadFile(myFile) }
        ) { response, isSuccess ->
            if (isSuccess) {
                val res = response[0] as NSUploadFileResponse?
                val url = res?.browserUrl ?: ""
                callback.onFileUrl(url, bitmap.width, bitmap.height)
            } else {
                hideProgress()
            }
        }
    }
}