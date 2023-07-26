package com.nyotek.dot.admin.common.callbacks

import com.nyotek.dot.admin.repository.network.responses.AddressData

/**
 * The interface to listen the click on side navigation item
 */
interface NSOnAddressSelectCallback {

    /**
     * Invoked when the side navigation item click
     */
    fun onItemSelect(addressData: AddressData?, isFromEditBranch: Boolean)
}