package com.nyotek.dot.admin.repository

import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.repository.network.responses.StringResourceResponse

open class BaseRepository {
    var stringResource = StringResourceResponse()
    val apiManager by lazy { NSApplication.getInstance().getApiManager() }
}