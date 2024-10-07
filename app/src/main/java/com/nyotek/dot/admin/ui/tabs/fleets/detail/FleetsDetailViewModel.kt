package com.nyotek.dot.admin.ui.tabs.fleets.detail

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.nyotek.dot.admin.base.BaseViewModel
import com.nyotek.dot.admin.common.NSDateTimeHelper
import com.nyotek.dot.admin.common.NSLanguageConfig
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.data.Repository
import com.nyotek.dot.admin.models.requests.NSAddressRequest
import com.nyotek.dot.admin.models.requests.NSFleetLogoScaleRequest
import com.nyotek.dot.admin.models.requests.NSFleetLogoUpdateRequest
import com.nyotek.dot.admin.models.requests.NSFleetNameUpdateRequest
import com.nyotek.dot.admin.models.requests.NSFleetRequest
import com.nyotek.dot.admin.models.requests.NSFleetServiceIdsUpdateRequest
import com.nyotek.dot.admin.models.requests.NSFleetSloganUpdateRequest
import com.nyotek.dot.admin.models.requests.NSFleetUpdateTagsRequest
import com.nyotek.dot.admin.models.requests.NSFleetUrlUpdateRequest
import com.nyotek.dot.admin.models.responses.FleetData
import com.nyotek.dot.admin.models.responses.FleetSingleResponse
import com.nyotek.dot.admin.models.responses.GetAddressResponse
import com.nyotek.dot.admin.models.responses.NSFleetBlankDataResponse
import com.nyotek.dot.admin.models.responses.NSUpdateFleetLogoResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FleetsDetailViewModel @Inject constructor(
    private val repository: Repository,
    val languageConfig: NSLanguageConfig,
    colorResources: ColorResources,
    application: Application
) : BaseViewModel(repository, languageConfig.dataStorePreference, colorResources, application) {

    var fleetModel: FleetData? = null
    var oldFleetModel: FleetData? = null

    var urlToUpload: String = ""
    var fleetLogoUpdateRequest: NSFleetLogoUpdateRequest = NSFleetLogoUpdateRequest()

    private var isName = false
    private var isSlogan = false
    private var isUrl = false
    private var isTags = false
    var isProgressVisible = false

    var isAllDataUpdateAvailable = NSSingleLiveEvent<Boolean>()
    var fleetDataObserve = NSSingleLiveEvent<FleetData>()

    val mFragmentList: MutableList<Fragment> = ArrayList()
    val mFragmentTitleList: MutableList<String> = ArrayList()

    fun getFleetDetail(fleetDetail: String?) {
        if (!fleetDetail.isNullOrEmpty()) {
            fleetModel = Gson().fromJson(fleetDetail, FleetData::class.java)
            oldFleetModel = fleetModel?.copy()
            fleetLogoUpdateRequest.vendorId = fleetModel?.vendorId
            if (!fleetModel?.vendorId.isNullOrEmpty()) {
                showProgress()
                getBaseServiceList(false) {
                    getFleetDetailData(fleetModel?.vendorId)
                }
            }
        }
    }

    private fun getFleetDetailData(vendorId: String?) = viewModelScope.launch {
        getFleetDetailApi(vendorId)
    }

    private suspend fun getFleetDetailApi(vendorId: String?) {
        showProgress()
        performApiCalls({ repository.remote.getFleetDetails(NSFleetRequest(vendorId))}
        ) { response, isSuccess ->
            if (isSuccess) {
                val res = response[0] as FleetSingleResponse?
                if (res is FleetSingleResponse) {
                    setFleetDetail(res.data)
                } else {
                    hideProgress()
                }
            } else {
                hideProgress()
            }
        }
    }

    private fun setFleetDetail(fleetData: FleetData?) {
        fleetModel = fleetData
        if (fleetModel?.addressId?.isNotEmpty() == true) {
            getAddressDetail()
        } else {
            hideProgress()
            fleetDataObserve.postValue(fleetData?:FleetData())
        }
    }

    private fun getAddressDetail() = viewModelScope.launch {
        getAddressDetailApi()
    }

    private suspend fun getAddressDetailApi() {
        performApiCalls({ repository.remote.getAddress(NSAddressRequest(fleetModel!!.addressId!!))}
        ) { response, isSuccess ->
            hideProgress()
            if (isSuccess) {
                val res = response[0] as GetAddressResponse?
                if (res is GetAddressResponse) {
                    fleetModel?.addressModel = res.data
                    fleetDataObserve.postValue(fleetModel ?: FleetData())
                }
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

    fun updateName() = viewModelScope.launch {
        updateNameApi()
    }

    private suspend fun updateNameApi() {
        if (fleetModel?.name?.isNotEmpty() == true && oldFleetModel?.name?.equals(fleetModel?.name) == false) {
            fleetModel?.vendorId?.let {
                performApiCalls({ repository.remote.updateFleetName(NSFleetNameUpdateRequest(it, fleetModel?.name?: hashMapOf()))}
                ) { response, isSuccess ->
                    if (isSuccess) {
                        oldFleetModel?.name = fleetModel?.name!!
                        getApiResponse(response[0] as NSFleetBlankDataResponse?)
                    } else {
                        hideProgress()
                    }
                }
            }
        } else {
            hideProgress()
        }
    }

    fun updateSlogan() = viewModelScope.launch {
        updateSloganApi()
    }

    private suspend fun updateSloganApi() {
        if (fleetModel?.slogan?.isNotEmpty() == true && oldFleetModel?.slogan?.equals(fleetModel?.slogan) == false) {
            fleetModel?.vendorId?.let {
                performApiCalls({ repository.remote.updateFleetSlogan(NSFleetSloganUpdateRequest(it, fleetModel?.slogan?: hashMapOf()))}
                ) { response, isSuccess ->
                    if (isSuccess) {
                        oldFleetModel?.slogan = fleetModel?.slogan!!
                        getApiResponse(response[0] as NSFleetBlankDataResponse?)
                    } else {
                        hideProgress()
                    }
                }
            }
        } else {
            hideProgress()
        }
    }

    fun updateUrl() = viewModelScope.launch {
        updateUrlApi()
    }

    private suspend fun updateUrlApi() {
        if (oldFleetModel?.url?.equals(fleetModel?.url) == false) {
            fleetModel?.vendorId?.let {
                performApiCalls({
                    repository.remote.updateFleetUrl(NSFleetUrlUpdateRequest(it, fleetModel?.url))
                }
                ) { response, isSuccess ->
                    if (isSuccess) {
                        oldFleetModel?.url = fleetModel?.url!!
                        getApiResponse(response[0] as NSFleetBlankDataResponse?)
                    } else {
                        hideProgress()
                    }
                }
            }
        }
    }

    fun updateTags() = viewModelScope.launch {
        updateTagsApi()
    }

    private suspend fun updateTagsApi() {
        if (oldFleetModel?.tags?.equals(fleetModel?.tags) == false) {
            fleetModel?.vendorId?.let {
                performApiCalls({
                    repository.remote.updateFleetTags(NSFleetUpdateTagsRequest(it, fleetModel?.tags ?: arrayListOf()))
                }
                ) { response, isSuccess ->
                    if (isSuccess) {
                        oldFleetModel?.tags = fleetModel?.tags
                        getApiResponse(response[0] as NSFleetBlankDataResponse?)
                    } else {
                        hideProgress()
                    }
                }
            }
        }
    }

    fun updateServiceIds() = viewModelScope.launch {
        isProgressVisible = true
        updateServiceIdsApi()
    }

    private suspend fun updateServiceIdsApi() {
        if (oldFleetModel?.serviceIds?.equals(fleetModel?.serviceIds) == false) {
            fleetModel?.vendorId?.let {
                showProgress()
                performApiCalls({
                    repository.remote.updateFleetServiceIds(
                        NSFleetServiceIdsUpdateRequest(it, fleetModel?.serviceIds ?: arrayListOf())
                    )
                }
                ) { response, isSuccess ->
                    if (isSuccess) {
                        oldFleetModel?.serviceIds = fleetModel?.serviceIds?: arrayListOf()
                        getApiResponse(response[0] as NSFleetBlankDataResponse?)
                    } else {
                        hideProgress()
                    }
                }
            }
        } else {
            getApiResponse(null)
        }
    }

    fun getCreatedDate(date: String?): String {
        return colorResources.getStringResource().createdDate + " : " + NSDateTimeHelper.getServiceDateView(date)
    }

    fun updateFleetLogo() = viewModelScope.launch {
        updateFleetLogoApi()
    }

    private suspend fun updateFleetLogoApi() {
        showProgress()
        performApiCalls({ repository.remote.updateFleetLogo(fleetLogoUpdateRequest)}
        ) { response, isSuccess ->
            if (isSuccess) {
                val res = response[0] as NSUpdateFleetLogoResponse?
                if (res is NSUpdateFleetLogoResponse) {
                    hideProgress()
                    isProgressVisible = false
                } else {
                    hideProgress()
                }
            } else {
                hideProgress()
            }
        }
    }

    fun updateFleetLogoScale(logoScale: String) = viewModelScope.launch {
        updateFleetLogoScaleApi(logoScale)
    }

    private suspend fun updateFleetLogoScaleApi(logoScale: String) {
        showProgress()
        performApiCalls({ repository.remote.updateFleetScale(NSFleetLogoScaleRequest(fleetModel?.vendorId, logoScale))}
        ) { _, _ ->
            hideProgress()
        }
    }

    fun fleetEnableDisable(fleetId: String?, isEnable: Boolean) = viewModelScope.launch {
        fleetEnableDisableApi(fleetId, isEnable)
    }

    private suspend fun fleetEnableDisableApi(fleetId: String?, isEnable: Boolean) {
        performApiCalls({
            if (isEnable) {
                repository.remote.enableFleet(NSFleetRequest(fleetId))
            } else {
                repository.remote.disableFleet(NSFleetRequest(fleetId))
            }}
        ) {_, _ -> }
    }

    private fun getApiResponse(data: Any?) {
        if (oldFleetModel?.name?.equals(fleetModel?.name) == false) {
            updateName()
        } else if (oldFleetModel?.slogan?.equals(fleetModel?.slogan) == false) {
            updateSlogan()
        } else if (oldFleetModel?.url?.equals(fleetModel?.url) == false) {
            updateUrl()
        } else if (oldFleetModel?.tags?.equals(fleetModel?.tags) == false) {
            updateTags()
        } else if (oldFleetModel?.serviceIds?.equals(fleetModel?.serviceIds) == false) {
            updateServiceIds()
        } else {
            hideProgress()
            if (isProgressVisible) {
                isAllDataUpdateAvailable.value = true
            }
        }
        
        /*if (data == null) {
            hideProgress()
        } else {
            when (data) {
                is NSFleetBlankDataResponse? -> {
                    if ((checkFocus() == 4 || checkFocus() == 3) && isProgressVisible) {
                        isProgressVisible = false
                        hideProgress()
                        //isProgressShowing.value = false
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

                else -> {
                    hideProgress()
                }
            }
        }*/
    }
}