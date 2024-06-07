package com.nyotek.dot.admin.common.utils

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.nyotek.dot.admin.models.responses.AddressData
import com.nyotek.dot.admin.ui.tabs.fleets.map.LocationPickerDialog
import com.nyotek.dot.admin.ui.tabs.fleets.map.NSMapViewModel

object NSAddressConfig {

    fun showAddressDialog(activity: FragmentActivity, mapViewModel: NSMapViewModel,
                          isFromEditBranch: Boolean = false,
                          view: View, addressData: AddressData,
                          vendorId: String,
                          addressId: String,
                          serviceIds: MutableList<String>,
                          callback: ((AddressData?, Boolean) -> Unit)) {

        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val alertDialogFragment = LocationPickerDialog.newInstance(isFromEditBranch, location, view.height, addressData, mapViewModel, vendorId, serviceIds, addressId) { selectedAddress ->
            callback.invoke(selectedAddress, isFromEditBranch)
        }
        if (alertDialogFragment.dialog == null || alertDialogFragment.dialog?.isShowing == false) {
            alertDialogFragment.show(activity.supportFragmentManager, "")
        }
    }
}