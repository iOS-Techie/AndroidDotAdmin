package com.nyotek.dot.admin.ui.passwordReset

import android.app.Application
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.repository.network.responses.NSUserResponse

/**
 * The view model class for login. It handles the business logic to communicate with the model for the login and provides the data to the observing UI component.
 */
class NSPasswordResetViewModel(application: Application) : NSViewModel(application) {
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
            /*isProgressShowing.value = true
            NSUserRepository.loginWithEmailPassword(strEmail, strPassword, this)*/
        }
    }

    override fun apiResponse(data: Any) {
        if (data is NSUserResponse) {
            isProgressShowing.value = false
            isLoginSuccess.value = true
        }
    }
}