package com.nyotek.dot.admin.common.keys

class HeaderKey {
    companion object {
        const val KEY_CONTENT_TYPE = "content-type"
        const val KEY_ACCEPT = "Accept"
        const val ACCEPT_VALUE = "*/*"
        const val KEY_LOCALE = "X-Locale"
        const val KEY_SELECTED_LANGUAGE = "X-Selected-Lang"
        const val KEY_SERVICE_ID = "X-SERVICE-ID"
        const val KEY_APP_ID = "X-APP-ID"
        const val KEY_TIME_ZONE = "x-client-tz"
        const val KEY_DEVICE_ID = "X-Device-Id"
        const val KEY_BuildVersion = "X-BuildVersion"
        const val MULTIPART_JSON = "multipart/form-data"
        const val APPLICATION_JSON = "application/json"
        const val AUTHORISATION_KEY = "Authorization"
        const val BEARER = "Bearer "
        const val TIMEOUT: Long = 30
    }
}