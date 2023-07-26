package com.nyotek.dot.admin.ui.common

import android.app.Application
import android.text.TextUtils
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.repository.NSSearchRepository
import com.nyotek.dot.admin.repository.NSUserRepository
import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.responses.NSUserDetail
import com.nyotek.dot.admin.repository.network.responses.NSUserListResponse

class NSUserViewModel(application: Application) : NSViewModel(application) {
    var isSearchUserListCall = NSSingleLiveEvent<MutableList<NSUserDetail>>()

    fun search(strValue: String, isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }

        val response: NSGenericViewModelCallback = object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                isProgressShowing.value = false
                if (data is NSUserListResponse) {
                    isSearchUserListCall.value = data.data
                }
            }

            override fun onError(errors: List<Any>) {
                handleError(errors)
            }

            override fun onFailure(failureMessage: String?) {
                if (NSConstants.REFRESH_TOKEN_ENABLE == failureMessage) {
                    search(strValue, isShowProgress)
                } else {
                    handleFailure(failureMessage)
                }
            }

            override fun <T> onNoNetwork(localData: T) {
                handleNoNetwork()
            }

        }

        if (TextUtils.isDigitsOnly(strValue)) {
            val mobileString: MutableList<String> = arrayListOf()
            mobileString.add(strValue)
            NSSearchRepository.searchMobileNumber(mobileString, response)
        } else {
            NSSearchRepository.searchUserName(strValue, response)
        }
    }

    override fun apiResponse(data: Any) {
        isProgressShowing.value = false
    }
}