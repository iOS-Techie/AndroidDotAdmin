package com.nyotek.dot.admin.repository

import com.nyotek.dot.admin.common.NSUserManager
import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.callbacks.NSRetrofitCallback
import com.nyotek.dot.admin.repository.network.error.NSApiErrorHandler
import com.nyotek.dot.admin.repository.network.requests.CreateSocialRequest
import com.nyotek.dot.admin.repository.network.responses.NSErrorResponse
import com.nyotek.dot.admin.repository.network.responses.NSSocialResponse
import com.nyotek.dot.admin.repository.network.responses.NSUserDetailResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * Repository class to handle data operations related to social
 */
object NSSocialRepository: BaseRepository() {

    /**
     * To get social info
     *
     * @param viewModelCallback The callback to communicate back to view model
     */
    fun getSocialInfo(isApi: Boolean, viewModelCallback: NSGenericViewModelCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.getSocialInfo(object : NSRetrofitCallback<NSSocialResponse>(
                viewModelCallback, NSApiErrorHandler.ERROR_SOCIAL_INFO
            ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body())
                    }
                }

                override fun onRefreshToken() {
                    getSocialInfo(isApi, viewModelCallback)
                }
            })
        }
    }

    fun createSocialInfo(request: CreateSocialRequest, viewModelCallback: NSGenericViewModelCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.createSocialInfo(request, object : NSRetrofitCallback<NSSocialResponse>(
                viewModelCallback, NSApiErrorHandler.ERROR_CREATE_SOCIAL_INFO
            ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSSocialResponse())
                    }
                }

                override fun onRefreshToken() {
                    createSocialInfo(request, viewModelCallback)
                }
            })
        }
    }

    fun updateSocialProfileImage(request: String, viewModelCallback: NSGenericViewModelCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            val map: HashMap<String, String> = hashMapOf()
            map["profile_pic_url"] = request
            apiManager.updateSocialProfileImage(map, object : NSRetrofitCallback<NSErrorResponse>(
                viewModelCallback, NSApiErrorHandler.ERROR_UPDATE_SOCIAL_PROFILE_IMAGE
            ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSErrorResponse())
                    }
                }

                override fun onRefreshToken() {
                    updateSocialProfileImage(request, viewModelCallback)
                }
            })
        }
    }

    fun updateSocialFirstName(request: String, viewModelCallback: NSGenericViewModelCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            val map: HashMap<String, String> = hashMapOf()
            map["first_name"] = request
            apiManager.updateFirstName(map, object : NSRetrofitCallback<NSErrorResponse>(
                viewModelCallback, NSApiErrorHandler.ERROR_UPDATE_FIRST_NAME
            ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSErrorResponse())
                    }
                }

                override fun onRefreshToken() {
                    updateSocialFirstName(request, viewModelCallback)
                }
            })
        }
    }

    fun updateSocialLastName(request: String, viewModelCallback: NSGenericViewModelCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            val map: HashMap<String, String> = hashMapOf()
            map["last_name"] = request
            apiManager.updateLastName(map, object : NSRetrofitCallback<NSErrorResponse>(
                viewModelCallback, NSApiErrorHandler.ERROR_UPDATE_LAST_NAME
            ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSErrorResponse())
                    }
                }

                override fun onRefreshToken() {
                    updateSocialLastName(request, viewModelCallback)
                }
            })
        }
    }

    fun updateSocialBiography(request: String, viewModelCallback: NSGenericViewModelCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            val map: HashMap<String, String> = hashMapOf()
            map["biography"] = request
            apiManager.updateBiography(map, object : NSRetrofitCallback<NSErrorResponse>(
                viewModelCallback, NSApiErrorHandler.ERROR_UPDATE_BIOGRAPHY
            ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSErrorResponse())
                    }
                }

                override fun onRefreshToken() {
                    updateSocialBiography(request, viewModelCallback)
                }
            })
        }
    }

    fun updateSocialDob(year: Int, month: Int, day: Int, viewModelCallback: NSGenericViewModelCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            val map: HashMap<String, Int> = hashMapOf()
            map["birth_year"] = year
            map["birth_month"] = month
            map["birth_day"] = day
            apiManager.updateDob(map, object : NSRetrofitCallback<NSErrorResponse>(
                viewModelCallback, NSApiErrorHandler.ERROR_UPDATE_BIOGRAPHY
            ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSErrorResponse())
                    }
                }

                override fun onRefreshToken() {
                    updateSocialDob(year, month, day, viewModelCallback)
                }
            })
        }
    }

    fun getUserDetail(viewModelCallback: NSGenericViewModelCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.getUserDetailData(object : NSRetrofitCallback<NSUserDetailResponse>(
                viewModelCallback, NSApiErrorHandler.ERROR_USER_DETAIL
            ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val users: NSUserDetailResponse = ((response.body()?: NSUserDetailResponse()) as NSUserDetailResponse)
                        NSUserManager.setUserDetail(users.data)
                        viewModelCallback.onSuccess(users)
                    }
                }

                override fun onRefreshToken() {
                    getUserDetail(viewModelCallback)
                }
            })
        }
    }
}