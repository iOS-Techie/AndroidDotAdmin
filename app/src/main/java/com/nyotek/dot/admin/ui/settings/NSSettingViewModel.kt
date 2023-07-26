package com.nyotek.dot.admin.ui.settings

import android.app.Activity
import android.app.Application
import com.google.gson.Gson
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.repository.NSUserRepository
import com.nyotek.dot.admin.repository.network.responses.NSCommonResponse
import com.nyotek.dot.admin.repository.network.responses.NSLogoutResponse
import com.nyotek.dot.admin.repository.network.responses.NSNotificationDeRegisterResponse
import com.nyotek.dot.admin.repository.network.responses.NSSettingListResponse
import com.nyotek.dot.admin.repository.network.responses.NSUserDetailResponse

/**
 * The view model class for profile. It handles the business logic to communicate with the model for the profile item and provides the data to the observing UI component.
 */
class NSSettingViewModel(application: Application) : NSViewModel(application) {
    var profileItemList: MutableList<NSSettingListResponse> = arrayListOf()
    var settingUserList: MutableList<NSCommonResponse> = arrayListOf()
    var isLogout = NSSingleLiveEvent<Boolean>()
    var isNotificationDeRegister = NSSingleLiveEvent<Boolean>()
    var isSettingUserAvailable = NSSingleLiveEvent<Boolean>()
    var strUserDetail: String? = null
    var userData: NSUserDetailResponse? = null

    fun getJsonUserDetail(activity: Activity) {
        if (!strUserDetail.isNullOrEmpty()) {
            userData = Gson().fromJson(strUserDetail, NSUserDetailResponse::class.java)
            getCommonUserDetail(activity)
        }
    }

    private fun getCommonUserDetail(activity: Activity) {
        activity.resources.apply {
            settingUserList.clear()
            settingUserList.add(NSCommonResponse(stringResource.email, userData?.data?.email?:""))
            settingUserList.add(NSCommonResponse(stringResource.username, userData?.data?.username?:""))
            settingUserList.add(NSCommonResponse(stringResource.mobile, userData?.data?.mobile?:""))
            isSettingUserAvailable.value = true
        }
    }

    /**
     * Get profile list data
     *
     * @param activity The activity's context
     */
    fun getProfileListData(activity: Activity) {
        with(activity) {
            with(stringResource) {
                profileItemList.clear()
                profileItemList.add(NSSettingListResponse(selectLanguage, R.drawable.ic_select_language))
                profileItemList.add(NSSettingListResponse(contactUs, R.drawable.ic_phone_settings))
                profileItemList.add(NSSettingListResponse(logout, R.drawable.ic_logout))
            }
        }
    }

    /**
     * logout data
     *
     */
    fun logout(isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        NSUserRepository.logout(viewModelCallback = this@NSSettingViewModel)
    }

    override fun apiResponse(data: Any) {
        if (data is NSNotificationDeRegisterResponse) {
            isProgressShowing.value = false
            isNotificationDeRegister.value = true
        } else if (data is NSLogoutResponse) {
            isProgressShowing.value = false
            isLogout.value = true
        }
    }
}