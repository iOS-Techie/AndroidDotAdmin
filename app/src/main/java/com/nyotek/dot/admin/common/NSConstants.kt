package com.nyotek.dot.admin.common


/**
 * Class that contains constants that used across modules
 */
class NSConstants {
    companion object {
        const val DEVICE_ID_STORE = "device_id_store"
        const val DEVICE_ID_STORE_VALUE = "device_id_store_value"
        const val KEY_ALERT_EMPLOYEE_DELETE = "alertButtonEmployeeDelete"
        const val KEY_ALERT_EMPLOYEE_VEHICLE_DELETE = "alertButtonEmployeeVehicleDelete"
        const val KEY_ALERT_EMPLOYEE_VEHICLE_DELETE_DETAIL = "alertButtonEmployeeVehicleDeleteDetail"
        const val KEY_ALERT_CAPABILITIES_DELETE = "alertButtonCapabilitiesDelete"
        const val UNKNOWN_HOST_EXCEPTION = "Unable to reach server"
        const val KEY_ALERT_BUTTON_POSITIVE = "alertButtonPositive"
        const val KEY_ALERT_BUTTON_NEGATIVE = "alertButtonNegative"
        const val ERROR = "error"
        const val SESSION_EXPIRED = "session_expired"
        const val SESSION_EXPIRED_ERROR = "session expired"
        const val REFRESH_TOKEN_ENABLE = "refresh_token_enable"
        const val POSITIVE_CLICK = "positive_button_click"
        const val LOGOUT_CLICK = "logout_button_click"
        var SERVICE_ID = ""
        var USER_DETAIL_SERVICE_ID = ""
        var THEME_ID = ""

        //Navigation type
        const val CAPABILITIES_TAB = "capabilities_tab"
        const val FLEETS_TAB = "fleets_tab"
        const val DASHBOARD_TAB = "dashboard_tab"
        const val SERVICE_TAB = "service_tab"
        const val DISPATCH_TAB = "dispatch_tab"

        //Keys
        const val USER_DETAIL_KEY = "user_detail_key"
        const val FLEET_DETAIL_KEY = "fleet_detail_key"
        const val DISPATCH_DETAIL_KEY = "dispatch_detail_key"
        const val Job_TITLE_LIST_KEY = "job_title_list_key"
        const val VEHICLE_DETAIL_KEY = "vendor_detail_key"
        const val DRIVER_DETAIL_KEY = "driver_detail_key"
        const val VENDOR_SERVICE_ID_KEY = "vendor_service_id_key"
        var IS_LANGUAGE_UPDATE = false

        const val FILL = "fill"
        const val FIT = "fit"
        const val ACTIVE = "active"
        const val ALL = "all"
        const val IN_ACTIVE = "in_active"
        const val LAYER_ID = "layer_id"
        const val SOURCE_ID = "source_id"
        const val ICON_ID = "icon_id"
        const val MODEL = "model"
        const val REGISTRATION_NO = "registration_no"
        const val MANUFACTURE = "manufacture"
        const val LOAD_CAPACITY = "load_capacity"
        const val NOTES = "notes"
        const val MANUFACTURE_YEAR = "manufacture_year"
        var isFragmentDispatchLoad: Boolean = false

    }
}