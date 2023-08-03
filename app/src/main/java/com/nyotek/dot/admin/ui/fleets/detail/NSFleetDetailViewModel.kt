package com.nyotek.dot.admin.ui.fleets.detail

import android.app.Application
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.nyotek.dot.admin.common.NSDateTimeHelper
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.repository.NSAddressRepository
import com.nyotek.dot.admin.repository.NSFleetRepository
import com.nyotek.dot.admin.repository.network.requests.NSFleetLogoScaleRequest
import com.nyotek.dot.admin.repository.network.requests.NSFleetLogoUpdateRequest
import com.nyotek.dot.admin.repository.network.responses.AddressData
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.FleetSingleResponse
import com.nyotek.dot.admin.repository.network.responses.GetAddressResponse
import com.nyotek.dot.admin.repository.network.responses.NSFleetBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSUpdateFleetLogoResponse

class NSFleetDetailViewModel(application: Application) : NSViewModel(application) {
    var fleetModel: FleetData? = null

    var urlToUpload: String = ""
    var fleetLogoUpdateRequest: NSFleetLogoUpdateRequest = NSFleetLogoUpdateRequest()

    private var isName = false
    private var isSlogan = false
    private var isUrl = false
    private var isTags = false
    var updatePosition = 0
    var isProgressVisible = false

    var isAllDataUpdateAvailable = NSSingleLiveEvent<Boolean>()

    val mFragmentList: MutableList<Fragment> = ArrayList()
    val mFragmentTitleList: MutableList<String> = ArrayList()

    fun getFleetDetail(fleetDetail: String?, callback: ((FleetData?) -> Unit)) {
        if (!fleetDetail.isNullOrEmpty()) {
            fleetModel = Gson().fromJson(fleetDetail, FleetData::class.java)
            fleetLogoUpdateRequest.vendorId = fleetModel?.vendorId
            if (!fleetModel?.vendorId.isNullOrEmpty()) {
                showProgress()
                callCommonApi({ obj ->
                    NSFleetRepository.getFleetDetail(fleetModel?.vendorId?:"", obj)
                }, { data, isSuccess ->
                    if (data is FleetSingleResponse) {
                        if (isSuccess) {
                            setFleetDetail(data.data, callback)
                        } else {
                            hideProgress()
                        }
                    } else {
                        hideProgress()
                    }
                })

            }
        }
    }

    private fun setFleetDetail(fleetData: FleetData?, callback: ((FleetData?) -> Unit)) {
        fleetModel = fleetData
        if (fleetModel?.addressId?.isNotEmpty() == true) {
            getAddressDetail(callback)
        } else {
            hideProgress()
            callback.invoke(fleetData)
        }
    }

    private fun getAddressDetail(callback: ((FleetData?) -> Unit)) {
        callCommonApi({ obj ->
            NSAddressRepository.getAddress(fleetModel!!.addressId!!, obj)
        }, { data, isSuccess ->
            if (data is GetAddressResponse) {
                if (isSuccess) {
                    fleetModel?.addressModel = data.data
                    callback.invoke(fleetModel)
                } else {
                    hideProgress()
                }
            } else {
                hideProgress()
            }
        })
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

    fun fleetEnableDisable(fleetId: String?, isEnable: Boolean) {
        callCommonApi({ obj ->
            if (fleetId != null) {
                NSFleetRepository.enableDisableFleet(fleetId, isEnable, obj)
            }
        }, { _, _ ->

        })
    }

    override fun apiResponse(data: Any) {
        when(data) {
            is NSUpdateFleetLogoResponse -> {
                isProgressVisible = false
                isProgressShowing.value = false
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
            }
        }
    }
}