package com.nyotek.dot.admin.repository

import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.callbacks.NSRetrofitCallback
import com.nyotek.dot.admin.repository.network.error.NSApiErrorHandler
import com.nyotek.dot.admin.repository.network.requests.NSSearchMobileRequest
import com.nyotek.dot.admin.repository.network.requests.NSSearchUserRequest
import com.nyotek.dot.admin.repository.network.responses.NSUserListResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * Repository class to handle data operations related to search
 */
object NSSearchRepository: BaseRepository() {

    /**
     * To search user data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun searchUserName(username: String,
                       viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val searchRequest = NSSearchUserRequest(username)
            apiManager.searchUserName(searchRequest, object :
                NSRetrofitCallback<NSUserListResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_SEARCH_USERNAME
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body())
                    }
                }

                override fun onRefreshToken() {
                    searchUserName(username, viewModelCallback)
                }
            })
        }
    }

    /**
     * To search mobile data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun searchMobileNumber(mobile: List<String>,
                       viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val searchRequest = NSSearchMobileRequest(mobile)
            apiManager.searchPhone(searchRequest, object :
                NSRetrofitCallback<NSUserListResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_SEARCH_MOBILE
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body())
                    }
                }

                override fun onRefreshToken() {
                    searchMobileNumber(mobile, viewModelCallback)
                }
            })
        }
    }
}