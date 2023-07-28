package com.nyotek.dot.admin.ui.fleets.detail

import android.app.Application
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.nyotek.dot.admin.common.NSDateTimeHelper
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.repository.NSAddressRepository
import com.nyotek.dot.admin.repository.NSFleetRepository
import com.nyotek.dot.admin.repository.NSServiceRepository
import com.nyotek.dot.admin.repository.network.requests.NSCreateFleetAddressRequest
import com.nyotek.dot.admin.repository.network.requests.NSFleetLogoScaleRequest
import com.nyotek.dot.admin.repository.network.requests.NSFleetLogoUpdateRequest
import com.nyotek.dot.admin.repository.network.responses.AddressData
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.FleetSingleResponse
import com.nyotek.dot.admin.repository.network.responses.GetAddressResponse
import com.nyotek.dot.admin.repository.network.responses.NSCreateFleetAddressResponse
import com.nyotek.dot.admin.repository.network.responses.NSFleetBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListData
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListResponse
import com.nyotek.dot.admin.repository.network.responses.NSUpdateFleetLogoResponse

class NSFleetDetailViewModel(application: Application) : NSViewModel(application) {
    var isFleetDataAvailable = NSSingleLiveEvent<FleetData?>()
    var fleetModel: FleetData? = null
    var selectedFleetId: String? = null
    var isEnableFleet: Boolean = false
    var addressDetailModel: AddressData? = null

    var urlToUpload: String = ""
    var fleetLogoUpdateRequest: NSFleetLogoUpdateRequest = NSFleetLogoUpdateRequest()
    var createAddressRequest: NSCreateFleetAddressRequest = NSCreateFleetAddressRequest()

    private var isName = false
    private var isSlogan = false
    private var isUrl = false
    private var isTags = false
    var updatePosition = 0
    var isProgressVisible = false

    var isAllDataUpdateAvailable = NSSingleLiveEvent<Boolean>()

    val mFragmentList: MutableList<Fragment> = ArrayList()
    val mFragmentTitleList: MutableList<String> = ArrayList()

    fun getFleetDetail(fleetDetail: String?) {
        if (!fleetDetail.isNullOrEmpty()) {
            fleetModel = Gson().fromJson(fleetDetail, FleetData::class.java)
            fleetLogoUpdateRequest.vendorId = fleetModel?.vendorId
            if (!fleetModel?.vendorId.isNullOrEmpty()) {
                isProgressShowing.value = true
                NSFleetRepository.getFleetDetail(fleetModel?.vendorId?:"", this)
            }
        }
    }

    fun manageFocus(
        name: Boolean = false,
        slogan: Boolean = false,
        url: Boolean = false,
        tags: Boolean = false,
    ) {
        isName = name
        isSlogan = slogan
        isUrl = url
        isTags = tags
    }

    private fun checkFocus(): Int {
        when {
            isName -> {
                return 0
            }
            isSlogan -> {
                return 1
            }
            isUrl -> {
                return 2
            }
            isTags -> {
                return 3
            }
            else -> {
                return 4
            }
        }
    }

    fun updateName() {
        if (fleetModel?.name?.isNotEmpty() == true) {
            fleetModel?.vendorId?.let {
                NSFleetRepository.updateFleetName(
                    it,
                    fleetModel?.name ?: hashMapOf(),
                    this
                )
            }
        }
    }

    fun updateSlogan() {
        if (fleetModel?.slogan?.isNotEmpty() == true) {
            fleetModel?.vendorId?.let {
                NSFleetRepository.updateFleetrSlogan(
                    it,
                    fleetModel?.slogan ?: hashMapOf(),
                    this
                )
            }
        }
    }

    fun updateUrl() {
        fleetModel?.vendorId?.let {
            NSFleetRepository.updateFleetUrl(
                it,
                fleetModel?.url?:"",
                this
            )
        }
    }

    fun updateTags() {
        fleetModel?.vendorId?.let {
            NSFleetRepository.updateFleetTags(
                it,
                fleetModel?.tags?: arrayListOf(),
                this
            )
        }
    }

    fun updateServiceIds() {
        fleetModel?.vendorId?.let {
            isProgressVisible = true
            isProgressShowing.value = true
            NSFleetRepository.updateFleetServiceIds(
                it,
                fleetModel?.serviceIds?: arrayListOf(),
                this
            )
        }
    }

    fun getCreatedDate(date: String?): String {
        return stringResource.createdDate + " : " + NSDateTimeHelper.getServiceDateView(date)
    }

    fun updateFleetLogo() {
        NSFleetRepository.updateFleetLogo(fleetLogoUpdateRequest, this)
    }

    fun updateFleetLogoScale(logoScale: String) {
        NSFleetRepository.updateFleetLogoScale(NSFleetLogoScaleRequest(fleetModel?.vendorId, logoScale), this)
    }

    override fun apiResponse(data: Any) {
        when(data) {
            is FleetSingleResponse -> {
                fleetModel = data.data
                if (fleetModel?.addressId?.isNotEmpty() == true) {
                    NSAddressRepository.getAddress(fleetModel!!.addressId!!, this)
                } else {
                    isProgressShowing.value = false
                    isFleetDataAvailable.value = data.data
                }
            }
            is GetAddressResponse -> {
                //isProgressShowing.value = false
                addressDetailModel = data.data
                isFleetDataAvailable.value = fleetModel
            }
            is NSUpdateFleetLogoResponse -> {
                isProgressVisible = false
                isProgressShowing.value = false
            }
            is NSCreateFleetAddressResponse -> {
                if (fleetModel?.addressId?.isEmpty() == true) {
                    fleetModel?.addressId = data.data?.id
                }
                addressDetailModel = data.data
                isProgressVisible = false
                isProgressShowing.value = false
                isAllDataUpdateAvailable.value = true
            }
            is NSFleetBlankDataResponse -> {
                if ((checkFocus() == 4 || checkFocus() == 3) && isProgressVisible) {
                    isProgressVisible = false
                    isProgressShowing.value = false
                    isAllDataUpdateAvailable.value = true
                } else if (checkFocus() == 3) {
                    isTags = false
                } else if (checkFocus() == 2) {
                    isUrl = false
                    updateUrl()
                } else if (checkFocus() == 1) {
                    isSlogan = false
                    updateSlogan()
                } else if (checkFocus() == 0) {
                    isName = false
                    updateName()
                }
                selectedFleetId = ""
                isEnableFleet = false
            }
        }
    }
}