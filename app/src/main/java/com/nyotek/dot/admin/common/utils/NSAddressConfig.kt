package com.nyotek.dot.admin.common.utils

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.nyotek.dot.admin.common.callbacks.NSOnAddressSelectCallback
import com.nyotek.dot.admin.common.callbacks.NSVendorAddressSelectCallback
import com.nyotek.dot.admin.repository.network.responses.AddressData
import com.nyotek.dot.admin.ui.fleets.map.LocationPickerDialog
import com.nyotek.dot.admin.ui.fleets.map.NSMapViewModel


/**
 * The language class that handles tasks that are common throughout the application languages
 */
object NSAddressConfig {

    fun showAddressDialog(activity: FragmentActivity, mapViewModel: NSMapViewModel,
                          isFromEditBranch: Boolean = false,
                          view: View, addressData: AddressData,
                          vendorId: String,
                          addressId: String,
                          serviceIds: MutableList<String>,
                          callback: NSOnAddressSelectCallback) {

        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val alertDialogFragment = LocationPickerDialog.newInstance(isFromEditBranch, location, view.height, addressData, mapViewModel, vendorId, serviceIds, addressId, object :
            NSVendorAddressSelectCallback {
            override fun onItemSelect(addressData: AddressData?) {
                callback.onItemSelect(addressData, isFromEditBranch)
            }
        })
        if (alertDialogFragment.dialog == null || alertDialogFragment.dialog?.isShowing == false) {
            alertDialogFragment.show(activity.supportFragmentManager, "")
        }
    }
}