package com.nyotek.dot.admin.ui.fleets.map

import android.app.Application
import android.location.Address
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.common.callbacks.NSVendorAddressSelectCallback
import com.nyotek.dot.admin.repository.NSAddressRepository
import com.nyotek.dot.admin.repository.NSFleetRepository
import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.requests.NSCreateFleetAddressRequest
import com.nyotek.dot.admin.repository.network.requests.NSEditAddressRequest
import com.nyotek.dot.admin.repository.network.responses.AddressData
import com.nyotek.dot.admin.repository.network.responses.FleetDataItem
import com.nyotek.dot.admin.repository.network.responses.FleetLocationResponse
import com.nyotek.dot.admin.repository.network.responses.NSCreateFleetAddressResponse

class NSMapViewModel(application: Application) : NSViewModel(application) {

    var selectedVendorId: String = ""
    var selectedServiceIdList: MutableList<String> = arrayListOf()
    var selectedAddressId: String = ""
    var currentAddressData: AddressData? = null
    var selectedAddressModel: AddressData? = null
    var isFleetLocationListAvailable = NSSingleLiveEvent<FleetDataItem?>()

    private fun editAddress(callback: NSVendorAddressSelectCallback) {
        if (currentAddressData != null) {
            currentAddressData?.apply {
                isProgressShowing.value = true

                NSAddressRepository.editAddress(
                    getSelectedEditAddressRequest(),
                    object : NSGenericViewModelCallback {
                        override fun <T> onSuccess(data: T) {
                            isProgressShowing.value = false
                            selectedVendorId = ""
                            callback.onItemSelect(selectedAddressModel)
                        }

                        override fun onError(errors: List<Any>) {
                            handleError(errors)
                        }

                        override fun onFailure(failureMessage: String?) {
                            handleFailure(failureMessage)
                        }

                        override fun <T> onNoNetwork(localData: T) {
                            handleNoNetwork()
                        }

                    })
            }
        } else {
            callback.onItemSelect(selectedAddressModel)
        }
    }

    private fun getSelectedEditAddressRequest(): NSEditAddressRequest {
        val editAddressRequest = NSEditAddressRequest(
            selectedAddressId,
            currentAddressData?.lat ?: 0.0,
            currentAddressData?.lng ?: 0.0,
            currentAddressData?.addr1.toString(),
            "",
            currentAddressData?.city,
            currentAddressData?.state,
            currentAddressData?.country,
            currentAddressData?.zip
        )

        setAddressDetailModel(currentAddressData)
        return editAddressRequest
    }

    private fun setAddressDetailModel(addressData: AddressData?) {
        selectedAddressModel = addressData
    }

    fun createOrEditAddress(callback: NSVendorAddressSelectCallback) {
        isProgressShowing.value = true
        if (selectedAddressId.isNotEmpty()) {
            editAddress(callback)
        } else {
            createVendorAddress(callback)
        }
    }

    fun branchEditAddress(callback: NSVendorAddressSelectCallback) {
        if (selectedAddressId.isNotEmpty()) {
            getSelectedEditAddressRequest()
        } else {
            getSelectedCreateAddressRequest()
        }
        callback.onItemSelect(selectedAddressModel)
    }

    fun branchAddressCreateEdit(callback: NSVendorAddressSelectCallback) {
        isProgressShowing.value = true
        if (selectedAddressId.isNotEmpty()) {
            editAddress(callback)
        } else {
            createVendorAddress(callback)
        }
    }

    private fun getSelectedCreateAddressRequest(): NSCreateFleetAddressRequest {
        val createAddressRequest = NSCreateFleetAddressRequest()
        createAddressRequest.apply {
            serviceIds = selectedServiceIdList
            refId = selectedVendorId
            lat = currentAddressData?.lat
            lng = currentAddressData?.lng
            addr1 = currentAddressData?.addr1.toString()
            addr2 = ""
            city = currentAddressData?.city
            state = currentAddressData?.state
            country = currentAddressData?.country
            zip = currentAddressData?.zip
        }

        setAddressDetailModel(currentAddressData)
        return createAddressRequest
    }

    private fun createVendorAddress(callback: NSVendorAddressSelectCallback){
        NSAddressRepository.createFleetAddress(getSelectedCreateAddressRequest(),object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                if (data is NSCreateFleetAddressResponse) {
                    if (selectedAddressId.isEmpty()) {
                        selectedAddressId = data.data?.id ?: ""
                    }
                    isProgressShowing.value = false
                    callback.onItemSelect(data.data)
                }
            }

            override fun onError(errors: List<Any>) {
                callback.onItemSelect(AddressData())
                handleError(errors)
            }

            override fun onFailure(failureMessage: String?) {
                callback.onItemSelect(AddressData())
                handleFailure(failureMessage)
            }

            override fun <T> onNoNetwork(localData: T) {
                handleNoNetwork()
            }

        })
    }

    fun getSelectedAddress(addressDetail: Address): AddressData {
        val address = AddressData()
        addressDetail.apply {
            address.addr1 = getAddressLine(0).toString()
            address.addr2 = ""
            address.lat = latitude
            address.lng = longitude
            address.city = locality
            address.state = adminArea
            address.country = countryName
            address.zip = postalCode
        }
        currentAddressData = address
        return address
    }

    /**
     * Get fleet locations
     *
     * @param isShowProgress
     */
    fun getFleetLocations(vendorId: String?, isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        NSFleetRepository.getFleetLocations(vendorId!!, this)
    }

    override fun apiResponse(data: Any) {
        when(data) {
            is FleetLocationResponse -> {
                isProgressShowing.value = false
                isFleetLocationListAvailable.value =  data.fleetDataItem
            }
        }
    }
}