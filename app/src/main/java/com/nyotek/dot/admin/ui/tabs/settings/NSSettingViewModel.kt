package com.nyotek.dot.admin.ui.tabs.settings

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.BaseViewModel
import com.nyotek.dot.admin.common.NSLanguageConfig
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.NSThemeHelper
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.data.Repository
import com.nyotek.dot.admin.models.responses.NSSettingListResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NSSettingViewModel @Inject constructor(
    private val repository: Repository,
    val languageConfig: NSLanguageConfig,
    val themeHelper: NSThemeHelper,
    colorResources: ColorResources,
    application: Application
) : BaseViewModel(repository, languageConfig.dataStorePreference, colorResources, application) {

    var isLogout = NSSingleLiveEvent<Boolean>()
    var settingObserve = NSSingleLiveEvent<MutableList<NSSettingListResponse>>()


    fun getProfileListData() {
        colorResources.apply {
            getStringResource().apply {
                val profileItemList: MutableList<NSSettingListResponse> = arrayListOf()
                profileItemList.add(NSSettingListResponse(profile, R.drawable.ic_user))
                profileItemList.add(NSSettingListResponse(selectLanguage, R.drawable.ic_select_language))
                profileItemList.add(NSSettingListResponse(contactUs, R.drawable.ic_phone_settings))
                profileItemList.add(NSSettingListResponse(logout, R.drawable.ic_logout))
                settingObserve.postValue(profileItemList)
            }
        }
    }


    fun logout() = viewModelScope.launch {
        logoutApi()
    }

    private suspend fun logoutApi() {
        performApiCalls({ repository.remote.logout() }) { _, isSuccess ->
            hideProgress()
            if (isSuccess) {
                isLogout.value = true
            }
        }
    }
}