package com.nyotek.dot.admin.ui.fleets.map

import android.app.Application
import android.location.Address
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.repository.NSAddressRepository
import com.nyotek.dot.admin.repository.NSFleetRepository
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

    private fun editAddress(callback: ((AddressData?) -> Unit)) {
        if (currentAddressData != null) {
            currentAddressData?.apply {

                callCommonApi({ obj ->
                    NSAddressRepository.editAddress(getSelectedEditAddressRequest(), obj)
                }, { _, _ ->
                    hideProgress()
                    selectedVendorId = ""
                    callback.invoke(selectedAddressModel)
                })
            }
        } else {
            callback.invoke(selectedAddressModel)
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

    fun createOrEditAddress(callback: ((AddressData?) -> Unit)) {
        showProgress()
        if (selectedAddressId.isNotEmpty()) {
            editAddress(callback)
        } else {
            createVendorAddress(callback)
        }
    }

    fun branchEditAddress(callback: ((AddressData?) -> Unit)) {
        if (selectedAddressId.isNotEmpty()) {
            getSelectedEditAddressRequest()
        } else {
            getSelectedCreateAddressRequest()
        }
        callback.invoke(selectedAddressModel)
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

    private fun createVendorAddress(callback: ((AddressData?) -> Unit)) {

        callCommonApi({ obj ->
            NSAddressRepository.createFleetAddress(getSelectedCreateAddressRequest(), obj)
        }, { data, isSuccess ->
            hideProgress()
            if (!isSuccess) {
                callback.invoke(AddressData())
            } else if (data is NSCreateFleetAddressResponse) {
                if (selectedAddressId.isEmpty()) {
                    selectedAddressId = data.data?.id ?: ""
                }
                callback.invoke(data.data)
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
    fun getFleetLocations(
        vendorId: String?,
        isShowProgress: Boolean,
        callback: ((FleetDataItem?) -> Unit?)
    ) {
        if (isShowProgress) showProgress()

        callCommonApi({ obj ->
            NSFleetRepository.getFleetLocations(vendorId!!, obj)
        }, { data, _ ->
            hideProgress()
            if (data is FleetLocationResponse) {
                callback.invoke(data.fleetDataItem)
            }
        })

    }

    override fun apiResponse(data: Any) {

    }
}