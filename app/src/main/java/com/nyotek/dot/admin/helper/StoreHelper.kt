package com.nyotek.dot.admin.helper

import com.nyotek.dot.admin.models.responses.BootStrapData
import com.nyotek.dot.admin.models.responses.LanguageSelectModel
import com.nyotek.dot.admin.models.responses.NSGetThemeData
import com.nyotek.dot.admin.models.responses.VendorDetailResponse

object StoreHelper {
    private var vendorMap: HashMap<String, VendorDetailResponse> = hashMapOf()
    private var localLanguageList: MutableList<LanguageSelectModel> = arrayListOf()
    private var themeModel: NSGetThemeData? = null
    private var stringResource: HashMap<String, String> = hashMapOf()
    private lateinit var resourceStr: String
    private var bootStrapData: BootStrapData? = null
    private var filterList: MutableList<String> = arrayListOf()

    fun setVendorMap(vendorId: String, vendorData: VendorDetailResponse) {
        vendorMap[vendorId] = vendorData
    }

    fun getVendorMap(vendorId: String): VendorDetailResponse? {
        return vendorMap[vendorId]
    }

    fun setLocalLanguage(list: MutableList<LanguageSelectModel>) {
        localLanguageList = list
    }

    fun getLocalLanguages(): MutableList<LanguageSelectModel> {
        return localLanguageList
    }
    
    fun setFilterList(list: MutableList<String>) {
        filterList = list
    }
    
    fun getFilterList(): MutableList<String> {
        return filterList
    }
    
    fun isTagAvailable(tagToFind: String?): Boolean {
        return filterList.any { vendor ->
            vendor.contains(tagToFind?:"")
        }
    }
    
    fun clearFilter() {
        filterList.clear()
    }

    fun getThemeModel(): NSGetThemeData = if (themeModel != null) {
        themeModel?: ColorHelper.getThemeData()
    } else {
        ColorHelper.getThemeData()
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

    fun getResourceStr(): String = resourceStr

    fun setResourceStr(resourcesStr: String) {
        resourceStr = resourcesStr
    }

    fun setBootStrapData(bootStrapData: BootStrapData?) {
        this.bootStrapData = bootStrapData
    }

    fun getBootStrapData(): BootStrapData? {
        return bootStrapData
    }

}