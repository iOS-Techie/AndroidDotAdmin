package com.nyotek.dot.admin.ui.common

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.viewModelScope
import com.nyotek.dot.admin.base.BaseViewModel
import com.nyotek.dot.admin.common.NSLanguageConfig
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.data.Repository
import com.nyotek.dot.admin.models.requests.NSSearchMobileRequest
import com.nyotek.dot.admin.models.requests.NSSearchUserRequest
import com.nyotek.dot.admin.models.responses.NSUserDetail
import com.nyotek.dot.admin.models.responses.NSUserListResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NSUserViewModel @Inject constructor(
    private val repository: Repository,
    val languageConfig: NSLanguageConfig,
    colorResources: ColorResources,
    application: Application
) : BaseViewModel(repository, languageConfig.dataStorePreference, colorResources, application) {
    var isSearchUserListCall = NSSingleLiveEvent<MutableList<NSUserDetail>>()

    fun search(strValue: String, isShowProgress: Boolean) = viewModelScope.launch {
        searchApi(strValue, isShowProgress)
    }

    private suspend fun searchApi(strValue: String, isShowProgress: Boolean) {
        if (isShowProgress) showProgress()

        performApiCalls(
            {
                if (TextUtils.isDigitsOnly(strValue)) {
                    val mobileString: MutableList<String> = arrayListOf()
                    mobileString.add(strValue)
                    repository.remote.searchPhone(NSSearchMobileRequest(mobileString))
                } else {
                    repository.remote.searchUserName(NSSearchUserRequest(strValue))
                }
            }
        ) { response, isSuccess ->
            if (isSuccess) {
                val res = response[0] as NSUserListResponse?
                isSearchUserListCall.value = res?.data
            } else {
                hideProgress()
            }
        }
    }
}