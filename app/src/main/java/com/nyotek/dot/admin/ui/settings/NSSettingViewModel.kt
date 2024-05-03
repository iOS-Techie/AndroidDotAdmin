package com.nyotek.dot.admin.ui.settings

import android.app.Application
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.repository.NSUserRepository
import com.nyotek.dot.admin.repository.network.responses.NSSettingListResponse

/**
 * The view model class for profile. It handles the business logic to communicate with the model for the profile item and provides the data to the observing UI component.
 */
class NSSettingViewModel(application: Application) : NSViewModel(application) {
    var isLogout = NSSingleLiveEvent<Boolean>()
    var settingObserve = NSSingleLiveEvent<MutableList<NSSettingListResponse>>()
    var strUserDetail: String? = null


    /**
     * Get profile list data
     *
     */
    fun getProfileListData() {
        with(stringResource) {
            val profileItemList: MutableList<NSSettingListResponse> = arrayListOf()
            profileItemList.add(NSSettingListResponse(profile, R.drawable.ic_user))
            profileItemList.add(NSSettingListResponse(selectLanguage, R.drawable.ic_select_language))
            profileItemList.add(NSSettingListResponse(contactUs, R.drawable.ic_phone_settings))
            profileItemList.add(NSSettingListResponse(logout, R.drawable.ic_logout))
            settingObserve.postValue(profileItemList)
        }
    }

    /**
     * logout data
     *
     */
    fun logout() {
        showProgress()
        callCommonApi({ obj ->
            NSUserRepository.logout(obj)
        }, { _, _ ->
            hideProgress()
            isLogout.value = true
        })
    }

    override fun apiResponse(data: Any) {

    }
}