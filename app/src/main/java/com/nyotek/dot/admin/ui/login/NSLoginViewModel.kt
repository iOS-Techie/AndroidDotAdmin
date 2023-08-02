package com.nyotek.dot.admin.ui.login

import android.app.Application
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.repository.NSUserRepository
import com.nyotek.dot.admin.repository.network.responses.NSUserResponse

/**
 * The view model class for login. It handles the business logic to communicate with the model for the login and provides the data to the observing UI component.
 */
class NSLoginViewModel(application: Application) : NSViewModel(application) {
    var strEmail: String? = null
    var strPassword: String? = null
    var isLoginSuccess = NSSingleLiveEvent<Boolean>()

    /**
     * To check all the mandatory fields are entered and valid
     *
     * @return status of all mandatory fields
     */
    private fun checkAllFieldsValid(): Boolean {
        var errorId: String? = null
        when {
            strEmail.isNullOrBlank() -> {
                errorId = stringResource.invalidEmailTitle
            }
            strPassword.isNullOrBlank() -> {
                errorId = stringResource.invalidPasswordTitle
            }
        }
        errorId?.let {
            validationErrorId.value = it
            return false
        }
        return true
    }

    /**
     * To initiate login process
     *
     */
    fun login() {
        if (checkAllFieldsValid()) {
            showProgress()
            callCommonApi({ obj ->
                NSUserRepository.loginWithEmailPassword(strEmail, strPassword, obj)
            }, { data, _ ->
                hideProgress()
                if (data is NSUserResponse) {
                    isLoginSuccess.value = true
                }
            })
        }
    }

    override fun apiResponse(data: Any) {

    }
}