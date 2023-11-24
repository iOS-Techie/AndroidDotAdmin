package com.nyotek.dot.admin.repository.network.error

import com.google.gson.Gson
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSConstants.Companion.SESSION_EXPIRED_ERROR
import com.nyotek.dot.admin.common.NSLogoutEvent
import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.callbacks.NSTokenRefreshCallback
import com.nyotek.dot.admin.repository.network.responses.NSErrorResponse
import com.nyotek.dot.admin.repository.network.responses.StringResourceResponse
import org.greenrobot.eventbus.EventBus
import retrofit2.Response

/**
 * The class to process errors thrown by the web service and give the appropriate error message.
 */
class NSApiErrorHandler {
    companion object {
        private lateinit var errorMessageList: MutableList<Any>
        const val ERROR_SERVICE_CAPABILITIES_LIST = "error_service_capabilities_list"
        const val ERROR_CAPABILITIES_LIST = "error_capabilities_list"
        const val ERROR_USER_DETAIL = "error_user_detail"
        const val ERROR_LOGIN_WITH_EMAIL_PASSWORD = "error_login_with_email_password"
        const val ERROR_REFRESH_TOKEN = "error_refresh_token"
        const val ERROR_NOTIFICATION_REGISTER = "error_notification_register_data"
        const val ERROR_NOTIFICATION_DEREGISTER = "error_notification_de_register_data"
        const val ERROR_NOTIFICATION_FETCH = "error_notification_fetch_data"
        const val ERROR_NOTIFICATION_SETTING_DATA = "error_notification_setting_data"
        const val ERROR_LOGOUT = "error_logout"
        const val ERROR_WALLET_DATA = "error_wallet_data"
        const val ERROR_WALLET_BALANCE_DATA = "error_wallet_balance_data"
        const val ERROR_WALLET_ADD_DEDUCT_DATA = "error_wallet_add_deduct_data"
        const val ERROR_WALLET_TRANSACTION_DATA = "error_wallet_transaction_data"
        const val ERROR_ORDER_TIMELINE = "error_order_data_time_line"
        const val ERROR_APP_THEME = "error_app_theme_data"
        const val ERROR_CREATE_APP = "error_create_app"
        const val ERROR_UPLOAD_FILE = "error_upload_file"
        const val ERROR_LANGUAGE_STRINGS = "error_language_strings"
        const val ERROR_LANGUAGE_CREATE_STRINGS = "error_language_create_strings"
        const val ERROR_VENDOR_ENABLE_DISABLE = "error_vendor_enable_disable_data"
        const val ERROR_VEHICLE_LIST = "error_vehicle_list"
        const val ERROR_UPDATE_NAME = "error_vendor_name_update"
        const val ERROR_UPDATE_SLOGAN = "error_vendor_slogan_update"
        const val ERROR_UPDATE_URL = "error_vendor_url_update"
        const val ERROR_UPDATE_SERVICE_IDS = "error_vendor_service_ids_update"
        const val ERROR_UPDATE_VENDOR_TAGS = "error_vendor_tags_update"
        const val ERROR_GET_ADDRESS = "error_get_address"
        const val REQUEST_CREATE_VENDOR = "error_create_company"
        const val REQUEST_CREATE_VENDOR_ADDRESS = "error_create_vendor_address"
        const val REQUEST_VENDOR_DETAIL = "error_vendor_detail"
        const val ERROR_GET_APP_LIST = "error_get_app_list"
        const val ERROR_GET_APP_BY_SERVICE_LIST = "error_get_app_by_service_list"
        const val ERROR_GET_SERVICE_LIST = "error_get_service_list"
        const val ERROR_LOCAL_LANGUAGE = "error_local_language"
        const val ERROR_GET_LANGUAGE_STRING = "error_get_language_string"
        const val ERROR_SEARCH_USERNAME = "error_search_user_name"
        const val ERROR_SEARCH_MOBILE = "error_search_mobile"
        const val ERROR_JOB_TITLE = "error_job_title"
        const val ERROR_EMPLOYEE_LIST = "error_employee_list"
        const val ERROR_EMPLOYEE_ENABLE_DISABLE = "error_employee_enable_disable_data"
        const val ERROR_EMPLOYEE_DELETE = "error_employee_delete_data"
        const val ERROR_EMPLOYEE_ADD = "error_employee_add_data"
        const val ERROR_EMPLOYEE_EDIT = "error_employee_edit_data"
        const val ERROR_FLEET_LOCATION = "error_fleet_locations"
        const val ERROR_CAPABILITIES_ENABLE_DISABLE = "error_vendor_enable_disable_data"
        const val ERROR_CAPABILITY_DELETE = "error_capability_delete_data"
        const val ERROR_CAPABILITY_SERVICE_UPDATE = "error_capability_service_update_data"
        const val ERROR_CREATE_EDIT_CAPABILITY = "error_create_edit_capability"
        const val ERROR_CREATE_VEHICLE_DATA = "error_create_vehicle_data"
        const val ERROR_VEHICLE_ENABLE_DISABLE = "error_vehicle_enable_disable_data"
        const val ERROR_VEHICLE_UPDATE_IMAGE = "error_vehicle_update_image"
        const val ERROR_VEHICLE_UPDATE_NOTES = "error_vehicle_update_notes"
        const val ERROR_VEHICLE_UPDATE_CAPABILITY = "error_vehicle_update_capability"
        const val ERROR_VEHICLE_DETAIL = "error_vehicle_detail"
        const val ERROR_DRIVER_VEHICLE_DETAIL = "error_driver_vehicle_detail"
        const val ERROR_ASSIGN_VEHICLE_DETAIL = "error_assign_vehicle_detail"
        const val ERROR_DELETE_VEHICLE_DETAIL = "error_delete_vehicle_detail"
        const val ERROR_SERVICE_FLEET_UPDATE = "error_update_fleets_update"
        const val ERROR_ASSIGN_VEHICLE_DRIVER = "error_assign_vehicle_driver"
        const val ERROR_DRIVER_LOCATION = "error_driver_locations"
        const val ERROR_DRIVER_DISPATCH = "error_driver_dispatch"
        const val ERROR_DISPATCH_FROM_SERVICE = "error_dispatch_from_service"
        const val ERROR_DISPATCH_DETAIL = "error_dispatch_detail"
        const val ERROR_DISPATCH_ORDER_STATUS = "error_dispatch_order_status"
        const val ERROR_DISPATCH_LOCATION_HISTORY = "error_dispatch_location_history"

        /**
         * To get the error messages from API endpoints
         *
         * @param rawErrorResponse The error response to parse and get the actual message
         * @return The error message
         */
        fun getApiErrorMessage(
            rawErrorResponse: Response<*>,
            viewModelCallback: NSGenericViewModelCallback,
            tokenRefreshCallback: NSTokenRefreshCallback
        ) {
            val stringResource = StringResourceResponse()
            errorMessageList = mutableListOf()

            var errorMessage: String = stringResource.dataFailed
            var isSessionTimeOut = false
            val isErrorNotNull = rawErrorResponse.errorBody() != null
            var isTokenExpire = false
            if (isErrorNotNull) {
                var strError = rawErrorResponse.errorBody()!!.string()
                val isError = strError.isNotEmpty()
                if (isError) {
                    if (strError.startsWith("{") && strError.contains(NSConstants.ERROR)) {
                        val errorModel = Gson().fromJson(strError, NSErrorResponse::class.java)
                        if (errorModel != null) {
                            strError = errorModel.error!!
                        }
                    }
                    if (strError.contains(SESSION_EXPIRED_ERROR) || errorMessage.contains(SESSION_EXPIRED_ERROR)) {
                        isSessionTimeOut = true
                    } else {
                        errorMessage = strError
                    }
                }
            }

            if (isSessionTimeOut) {
                EventBus.getDefault().post(NSLogoutEvent())
            } else {
                when (val responseErrorCode = rawErrorResponse.code()) {
                    in 400..429 -> {
                        if (responseErrorCode == 401) {
                            isTokenExpire = true
                            tokenRefreshCallback.onTokenRefresh()
                            //viewModelCallback.onFailure(REFRESH_TOKEN_ENABLE)
                        } else if (responseErrorCode != 404){
                            errorMessageList.clear()
                            if (errorMessage.isNotEmpty()) {
                                errorMessageList.add(errorMessage)
                            }
                        }
                    }
                    in 500..503 -> {
                        errorMessageList.clear()
                        if (errorMessage.isNotEmpty()) {
                            errorMessageList.add(errorMessage)
                        }
                    }
                    else -> {
                        errorMessageList.clear()
                        if (rawErrorResponse.body() != null && !rawErrorResponse.message()
                                .isNullOrEmpty()
                        ) {
                            if (errorMessage.isNotEmpty()) {
                                errorMessageList.add(errorMessage)
                            }
                        } else if (rawErrorResponse.body() == null && rawErrorResponse.errorBody() != null) {
                            if (errorMessage.isNotEmpty()) {
                                errorMessageList.add(errorMessage)
                            }
                        } else {
                            errorMessageList.add(stringResource.dataFailed)
                        }
                    }
                }
            }

            if (!isTokenExpire) {
                viewModelCallback.onError(errorMessageList)
            }
        }
    }
}