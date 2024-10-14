package com.nyotek.dot.admin.ui.tabs.fleets

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nyotek.dot.admin.base.BaseViewModel
import com.nyotek.dot.admin.common.NSLanguageConfig
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.data.Repository
import com.nyotek.dot.admin.models.requests.NSCreateCompanyRequest
import com.nyotek.dot.admin.models.requests.NSFleetRequest
import com.nyotek.dot.admin.models.responses.ActiveInActiveFilter
import com.nyotek.dot.admin.models.responses.FleetData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FleetsViewModel @Inject constructor(
    private val repository: Repository,
    val languageConfig: NSLanguageConfig,
    colorResources: ColorResources,
    application: Application
) : BaseViewModel(repository, languageConfig.dataStorePreference, colorResources, application) {

    var createCompanyRequest: NSCreateCompanyRequest = NSCreateCompanyRequest()
    var urlToUpload: String = ""
    var filterList: MutableList<ActiveInActiveFilter> = arrayListOf()
    var fleetList: MutableLiveData<MutableList<FleetData>> = MutableLiveData()

    fun getFleetList(isShowProgress: Boolean) {
        if (isShowProgress) showProgress()
        getBaseServiceList(!isShowProgress) {
            getFleetList { fleets ->
                getLocalLanguages {
                    hideProgress()
                    fleetList.postValue(fleets)
                }
            }
        }
    }

    fun fleetEnableDisable(vendorId: String?, isEnable: Boolean) = viewModelScope.launch {
        fleetEnableDisableApi(vendorId, isEnable)
    }

    private suspend fun fleetEnableDisableApi(vendorId: String?, isEnable: Boolean) {
        performApiCalls(
            {
                if (isEnable) {
                    repository.remote.enableFleet(NSFleetRequest(vendorId))
                } else {
                    repository.remote.disableFleet(NSFleetRequest(vendorId))
                }}
        ) {_, _ ->}
    }

    fun createFleet() = viewModelScope.launch {
        createFleetApi()
    }

    private suspend fun createFleetApi() {
        showProgress()
        performApiCalls({ repository.remote.createFleet(createCompanyRequest)
            }) { _, isSuccess ->
            if (isSuccess) {
                createCompanyRequest = NSCreateCompanyRequest()
                urlToUpload = ""
                getFleetList(true)
            } else {
                hideProgress()
            }
        }
    }
}