package com.nyotek.dot.admin.repository

import com.nyotek.dot.admin.common.NSApplication

open class BaseRepository {
    var stringResource = NSApplication.getInstance().getStringModel()
    val apiManager by lazy { NSApplication.getInstance().getApiManager() }
}