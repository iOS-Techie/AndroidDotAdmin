package com.nyotek.dot.admin.ui.settings.profile

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.repository.NSSocialRepository
import com.nyotek.dot.admin.repository.network.requests.CreateSocialRequest
import com.nyotek.dot.admin.repository.network.responses.NSSocialDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSSocialResponse


/**
 * The view model class for notification. It handles the business logic to communicate with the model for the notification and provides the data to the observing UI component.
 */
class NSUserDetailViewModel(application: Application) : NSViewModel(application) {

    var isCreate: Boolean? = true
    var isUpdateSuccess: MutableLiveData<Boolean> = MutableLiveData()
    var imageUrl: String? = null

    fun getSocialInfo(isApiCall: Boolean = true, callback: (NSSocialDataResponse?) -> Unit) {
        showProgress()
        callCommonApi({ obj ->
            NSSocialRepository.getSocialInfo(isApiCall, obj)
        }, { data, _ ->
            hideProgress()
            if (data is NSSocialResponse?) {
                isCreate = data?.data == null
                callback.invoke(data?.data)
            }
        })
    }

    fun createSocialInfo(request: CreateSocialRequest, callback: (NSSocialDataResponse?) -> Unit) {
        showProgress()
        callCommonApi({ obj ->
            NSSocialRepository.createSocialInfo(request, obj)
        }, { data, _ ->
            hideProgress()
            if (data is NSSocialResponse?) {
                isCreate = data?.data == null
                callback.invoke(data?.data)
            }
        })
    }

    fun updateFirstName(firstName: String) {
        callCommonApi({ obj ->
            NSSocialRepository.updateSocialFirstName(firstName, obj)
        }, { _, _ ->
            isUpdateSuccess.postValue(true)
        })
    }

    fun updateLastName(lastName: String) {
        callCommonApi({ obj ->
            NSSocialRepository.updateSocialLastName(lastName, obj)
        }, { _, _ ->
            isUpdateSuccess.postValue(true)
        })
    }

    fun updateProfilePic(profileImg: String?) {
        if (profileImg?.isNotEmpty() == true) {
            showProgress()
            callCommonApi({ obj ->
                NSSocialRepository.updateSocialProfileImage(profileImg, obj)
            }, { _, _ ->
                hideProgress()
                isUpdateSuccess.postValue(true)
            })
        }
    }

    fun updateDob(year: Int,month: Int, day: Int) {
        callCommonApi({ obj ->
            NSSocialRepository.updateSocialDob(year, month, day, obj)
        }, { _, _ ->
            isUpdateSuccess.postValue(true)
        })
    }

    fun updateBiography(bio: String) {
        callCommonApi({ obj ->
            NSSocialRepository.updateSocialBiography(bio, obj)
        }, { _, _ ->
            isUpdateSuccess.postValue(true)
        })
    }

    override fun apiResponse(data: Any) {

    }
}