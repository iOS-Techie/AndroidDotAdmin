package com.nyotek.dot.admin.ui.tabs.fleets.map

import android.app.Application
import android.location.Address
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nyotek.dot.admin.base.BaseViewModel
import com.nyotek.dot.admin.common.NSLanguageConfig
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.data.Repository
import com.nyotek.dot.admin.models.requests.NSCreateFleetAddressRequest
import com.nyotek.dot.admin.models.requests.NSEditAddressRequest
import com.nyotek.dot.admin.models.responses.AddressData
import com.nyotek.dot.admin.models.responses.FleetDataItem
import com.nyotek.dot.admin.models.responses.FleetLocationResponse
import com.nyotek.dot.admin.models.responses.NSCreateFleetAddressResponse
import com.nyotek.dot.admin.models.responses.NSDispatchOrderListData
import com.nyotek.dot.admin.models.responses.NSDispatchOrderListResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NSMapViewModel @Inject constructor(
    private val repository: Repository,
    val languageConfig: NSLanguageConfig,
    colorResources: ColorResources,
    application: Application
) : BaseViewModel(repository, languageConfig.dataStorePreference, colorResources, application) {

    var selectedVendorId: String = ""
    var selectedServiceIdList: MutableList<String> = arrayListOf()
    var selectedAddressId: String = ""
    var currentAddressData: AddressData? = null
    var selectedAddressModel: AddressData? = null
    var tempFleetDataItem: FleetDataItem? = null
    var dispatchOrderItemObserve: MutableLiveData<MutableList<NSDispatchOrderListData>?> = MutableLiveData()
    var fleetDataItemObserve: MutableLiveData<FleetDataItem?> = MutableLiveData()

    private fun editAddress(callback: ((AddressData?) -> Unit)) = viewModelScope.launch {
        editAddressApi(callback)
    }

    private suspend fun editAddressApi(callback: ((AddressData?) -> Unit)) {
        if (currentAddressData != null) {
            currentAddressData?.apply {
                performApiCalls(
                    { repository.remote.editAddress(getSelectedEditAddressRequest())}
                ) { _, _ ->
                    hideProgress()
                    selectedVendorId = ""
                    callback.invoke(selectedAddressModel)
                }
            }
        } else {
            hideProgress()
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
            createFleetAddress(callback)
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

    private fun createFleetAddress(callback: ((AddressData?) -> Unit)) = viewModelScope.launch {
        createFleetAddressApi(callback)
    }

    private suspend fun createFleetAddressApi(callback: ((AddressData?) -> Unit)) {
        performApiCalls(
            { repository.remote.createFleetAddress(getSelectedCreateAddressRequest())}
        ) { response, isSuccess ->
            if (isSuccess) {
                val res = response[0] as NSCreateFleetAddressResponse?
                if (res is NSCreateFleetAddressResponse) {
                    if (selectedAddressId.isEmpty()) {
                        selectedAddressId = res.data?.id ?: ""
                    }
                    hideProgress()
                    callback.invoke(res.data)
                } else {
                    hideProgress()
                }
            } else {
                hideProgress()
            }
        }
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

    fun getFleetLocations(isShowProgress: Boolean, isFromFleetDetail: Boolean = false) = viewModelScope.launch {
        getFleetLocationsApi(isShowProgress, isFromFleetDetail)
    }

    private suspend fun getFleetLocationsApi(isShowProgress: Boolean, isFromFleetDetail: Boolean = false) {
        if (isShowProgress) showProgress()

        performApiCalls(
            { repository.remote.getFleetLocation()}
        ) { response, isSuccess ->
            if (isSuccess) {
                val res = response[0] as FleetLocationResponse?
                if (!isFromFleetDetail) {
                    hideProgress()
                }
                if (res is FleetLocationResponse) {
                    fleetDataItemObserve.postValue(res.fleetDataItem)
                }
            } else {
                hideProgress()
            }
        }
    }

    fun getDispatchDrivers(driverId: String?, isShowProgress: Boolean) = viewModelScope.launch {
        getDispatchDriversApi(driverId, isShowProgress)
    }

    private suspend fun getDispatchDriversApi(driverId: String?, isShowProgress: Boolean) {
        if (isShowProgress) showProgress()

        performApiCalls(
            { repository.remote.dispatchDrivers(driverId!!)}
        ) { response, isSuccess ->
            hideProgress()
            if (isSuccess) {
                val res = response[0] as NSDispatchOrderListResponse?
                if (res is NSDispatchOrderListResponse) {
                    dispatchOrderItemObserve.postValue(res.orderData)
                }
            }
        }
    }
}