package com.nyotek.dot.admin.common

import com.nyotek.dot.admin.common.extension.isValidList
import com.nyotek.dot.admin.models.responses.CapabilitiesDataItem
import com.nyotek.dot.admin.models.responses.JobListDataItem
import com.nyotek.dot.admin.models.responses.LanguageSelectModel
import com.nyotek.dot.admin.models.responses.NSGetServiceListResponse
import com.nyotek.dot.admin.models.responses.NSGetThemeData
import com.nyotek.dot.admin.models.responses.NSMainDetailUser
import com.nyotek.dot.admin.models.responses.ServiceCapabilitiesDataItem
import com.nyotek.dot.admin.models.responses.VendorDetailResponse

object NSThemeHelper{
    var IS_LANGUAGE_UPDATE = false
    var isLanguageChange = false
    var THEME_ID = ""
    var SERVICE_ID = ""
    var USER_DETAIL_SERVICE_ID = ""
    private lateinit var themeModel: NSGetThemeData
    private var stringResource: HashMap<String, String> = hashMapOf()
    private var localLanguageList: MutableList<LanguageSelectModel> = arrayListOf()
    private var localLanguages: MutableList<LanguageSelectModel> = arrayListOf()
    private var capabilities: MutableList<CapabilitiesDataItem> = arrayListOf()
    private var localMapLanguageList: HashMap<String, MutableList<LanguageSelectModel>> = hashMapOf()
    private var capabilityItemList: HashMap<String, ServiceCapabilitiesDataItem?> = hashMapOf()
    private var serviceResponse: NSGetServiceListResponse? = null
    private var vendorDetailResponse: HashMap<String, VendorDetailResponse?> = hashMapOf()
    private var jobTitleMap: HashMap<String, JobListDataItem> = hashMapOf()
    private var userDetail: NSMainDetailUser? = null

    fun getThemeModel(): NSGetThemeData {
        if (!::themeModel.isInitialized) {
            return NSGetThemeData()
        }
        return themeModel
    }

    fun setThemeModel(resources: NSGetThemeData) {
        themeModel = resources
    }

    fun getStringModel(): HashMap<String, String> {
        return stringResource
    }

    fun setStringModel(resources: HashMap<String, String>) {
        stringResource = resources
    }

    fun setLocalLanguageList(list: MutableList<LanguageSelectModel>) {
        localLanguageList = list
    }

    fun getLocalLanguageLists(): MutableList<LanguageSelectModel> {
        return localLanguageList
    }

    fun setFleetLanguageList(localLanguage: MutableList<LanguageSelectModel>, isSelect: Boolean) {
        if (isSelect) {
            if (localLanguage.isValidList()) {
                localLanguage[0].isSelected = true
            }
        }
        localLanguages = localLanguage
    }

    fun getFleetLanguageList(): MutableList<LanguageSelectModel> {
        return localLanguages
    }

    fun setCapabilities(list: MutableList<CapabilitiesDataItem>) {
        list.sortBy { it.id }
        capabilities.clear()
        capabilities.addAll(list)
    }

    fun getCapabilities(): MutableList<CapabilitiesDataItem> {
        return capabilities
    }

    fun setMapLocalLanguage(list: HashMap<String,MutableList<LanguageSelectModel>>) {
        localMapLanguageList = list
    }

    fun getMapLocalLanguages(): HashMap<String,MutableList<LanguageSelectModel>> {
        return localMapLanguageList
    }

    fun removeMapLocalLanguage(serviceId: String) {
        localMapLanguageList.remove(serviceId)
    }

    fun removeAllMapLocalLanguage() {
        localMapLanguageList.clear()
    }

    fun getCapabilityItemList(): HashMap<String, ServiceCapabilitiesDataItem?> = capabilityItemList

    fun setCapabilityItemList(serviceId: String, item: ServiceCapabilitiesDataItem?) {
        capabilityItemList[serviceId] = item
    }

    fun setServiceResponse(response: NSGetServiceListResponse?) {
        serviceResponse = response
    }

    fun getServiceResponse(): NSGetServiceListResponse? {
        return serviceResponse
    }

    fun setVendorDetailResponse(response: VendorDetailResponse?) {
        if (response?.vendorId != null) {
            vendorDetailResponse[response.vendorId] = response
        }
    }

    fun getVendorDetailResponse(vendorId: String): VendorDetailResponse? {
        return vendorDetailResponse[vendorId]
    }

    fun getJobRolesTypes(): HashMap<String, JobListDataItem> = jobTitleMap

    fun setJobRoleType(filter: HashMap<String, JobListDataItem>) {
        jobTitleMap = filter
    }

    fun setUserDetail(user: NSMainDetailUser?) {
        userDetail = user
    }

    fun getUserDetail(): NSMainDetailUser? {
        return userDetail
    }
}