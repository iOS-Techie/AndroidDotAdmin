package com.nyotek.dot.admin.common.callbacks

import com.nyotek.dot.admin.repository.network.responses.LanguageSelectModel

/**
 * The interface to listen the click on language selection
 */
interface NSLanguageSubItemSelectCallback {

    /**
     * Invoked when the side navigation item click
     */
    fun onItemSelect(model: LanguageSelectModel)
}