package com.nyotek.dot.admin.repository

import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.callbacks.NSRetrofitCallback
import com.nyotek.dot.admin.repository.network.error.NSApiErrorHandler
import com.nyotek.dot.admin.repository.network.responses.NSDocumentListResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * Repository class to handle data operations related to Document
 */
object NSDocumentRepository : BaseRepository() {

    fun getDocumentList(id: String,
                        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.getDocumentList(id, object :
                NSRetrofitCallback<NSDocumentListResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_DOCUMENT_LIST
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSDocumentListResponse())
                    }
                }

                override fun onRefreshToken() {
                    getDocumentList(id, viewModelCallback)
                }
            })
        }
    }
}