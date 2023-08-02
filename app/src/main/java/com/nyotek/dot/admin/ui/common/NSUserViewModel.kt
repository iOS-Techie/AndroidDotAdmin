package com.nyotek.dot.admin.ui.common

import android.app.Application
import android.text.TextUtils
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.repository.NSSearchRepository
import com.nyotek.dot.admin.repository.NSThemeRepository
import com.nyotek.dot.admin.repository.NSUserRepository
import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.responses.NSUploadFileResponse
import com.nyotek.dot.admin.repository.network.responses.NSUserDetail
import com.nyotek.dot.admin.repository.network.responses.NSUserListResponse

class NSUserViewModel(application: Application) : NSViewModel(application) {
    var isSearchUserListCall = NSSingleLiveEvent<MutableList<NSUserDetail>>()

    fun search(strValue: String, isShowProgress: Boolean) {
        if (isShowProgress) showProgress()

        callCommonApi({ obj ->
            if (TextUtils.isDigitsOnly(strValue)) {
                val mobileString: MutableList<String> = arrayListOf()
                mobileString.add(strValue)
                NSSearchRepository.searchMobileNumber(mobileString, obj)
            } else {
                NSSearchRepository.searchUserName(strValue, obj)
            }
        }, { data, _ ->
            hideProgress()
            if (data is NSUserListResponse) {
                isSearchUserListCall.value = data.data
            }
        })
    }

    override fun apiResponse(data: Any) {
        isProgressShowing.value = false
    }
}