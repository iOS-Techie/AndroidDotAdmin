package com.nyotek.dot.admin.ui.tabs.settings.profile

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nyotek.dot.admin.base.BaseViewModel
import com.nyotek.dot.admin.common.NSLanguageConfig
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.data.Repository
import com.nyotek.dot.admin.models.requests.CreateSocialRequest
import com.nyotek.dot.admin.models.responses.NSSocialDataResponse
import com.nyotek.dot.admin.models.responses.NSSocialResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NSUserDetailViewModel @Inject constructor(
    private val repository: Repository,
    val languageConfig: NSLanguageConfig,
    colorResources: ColorResources,
    application: Application
) : BaseViewModel(repository, languageConfig.dataStorePreference, colorResources, application) {

    var isCreate: Boolean? = true
    var isUpdateSuccess: MutableLiveData<Boolean> = MutableLiveData()
    var imageUrl: String? = null

    fun getSocialInfo(callback: (NSSocialDataResponse?) -> Unit) = viewModelScope.launch {
        getSocialInfoApi(callback)
    }

    private suspend fun getSocialInfoApi(callback: (NSSocialDataResponse?) -> Unit) {
        showProgress()
        performApiCalls({ repository.remote.getSocialInfo() }) { response, isSuccess ->
            hideProgress()
            if (isSuccess) {
                val data = response[0] as NSSocialResponse?
                isCreate = data?.data == null
                callback.invoke(data?.data)
            }
        }
    }

    fun createSocialInfo(request: CreateSocialRequest, callback: (NSSocialDataResponse?) -> Unit) = viewModelScope.launch {
        createSocialInfoApi(request, callback)
    }

    private suspend fun createSocialInfoApi(request: CreateSocialRequest, callback: (NSSocialDataResponse?) -> Unit) {
        showProgress()
        performApiCalls({ repository.remote.createSocialInfo(request) }) { response, isSuccess ->
            hideProgress()
            if (isSuccess) {
                val data = response[0] as NSSocialResponse?
                isCreate = data?.data == null
                callback.invoke(data?.data)
            }
        }
    }

    fun updateFirstName(firstName: String) = viewModelScope.launch {
        updateFirstNameApi(firstName)
    }

    private suspend fun updateFirstNameApi(firstName: String) {
        val map: HashMap<String, String> = hashMapOf()
        map["first_name"] = firstName
        performApiCalls({ repository.remote.updateFirstName(map) }) { response, isSuccess ->
            if (isSuccess) {
                isUpdateSuccess.postValue(true)
            }
        }
    }

    fun updateLastName(lastName: String) = viewModelScope.launch {
        updateLastNameApi(lastName)
    }

    private suspend fun updateLastNameApi(lastName: String) {
        val map: HashMap<String, String> = hashMapOf()
        map["last_name"] = lastName
        performApiCalls({ repository.remote.updateLastName(map) }) { response, isSuccess ->
            if (isSuccess) {
                isUpdateSuccess.postValue(true)
            }
        }
    }

    fun updateProfilePic(profileImg: String?) = viewModelScope.launch {
        updateProfilePicApi(profileImg)
    }

    private suspend fun updateProfilePicApi(profileImg: String?) {
        if (profileImg?.isNotEmpty() == true) {
            showProgress()
            val map: HashMap<String, String> = hashMapOf()
            map["profile_pic_url"] = profileImg
            performApiCalls({ repository.remote.updateSocialProfileImage(map) }) { response, isSuccess ->
                hideProgress()
                if (isSuccess) {
                    isUpdateSuccess.postValue(true)
                }
            }
        }
    }

    fun updateDob(year: Int,month: Int, day: Int) = viewModelScope.launch {
        updateDobApi(year, month, day)
    }

    private suspend fun updateDobApi(year: Int,month: Int, day: Int) {
        val map: HashMap<String, Int> = hashMapOf()
        map["birth_year"] = year
        map["birth_month"] = month
        map["birth_day"] = day
        performApiCalls({ repository.remote.updateDob(map) }) { response, isSuccess ->
            if (isSuccess) {
                isUpdateSuccess.postValue(true)
            }
        }
    }

    fun updateBiography(bio: String) = viewModelScope.launch {
        updateBiographyApi(bio)
    }

    private suspend fun updateBiographyApi(bio: String) {
        val map: HashMap<String, String> = hashMapOf()
        map["biography"] = bio
        performApiCalls({ repository.remote.updateBiography(map) }) { response, isSuccess ->
            if (isSuccess) {
                isUpdateSuccess.postValue(true)
            }
        }
    }

}