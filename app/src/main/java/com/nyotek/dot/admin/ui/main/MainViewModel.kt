package com.nyotek.dot.admin.ui.main

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.BaseViewModel
import com.nyotek.dot.admin.common.NSLanguageConfig
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.data.Repository
import com.nyotek.dot.admin.models.responses.NSNavigationResponse
import com.nyotek.dot.admin.models.responses.NSUserDetailResponse
import com.nyotek.dot.admin.ui.tabs.capabilities.CapabilitiesFragment
import com.nyotek.dot.admin.ui.tabs.dashboard.DashboardFragment
import com.nyotek.dot.admin.ui.tabs.dispatch.DispatchTabFragment
import com.nyotek.dot.admin.ui.tabs.fleets.FleetsTabFragment
import com.nyotek.dot.admin.ui.tabs.services.ServicesFragment
import com.nyotek.dot.admin.ui.tabs.settings.SettingsTabFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    val languageConfig: NSLanguageConfig,
    colorResources: ColorResources,
    application: Application
) : BaseViewModel(repository, languageConfig.dataStorePreference, colorResources, application) {

    var navItemList: MutableList<NSNavigationResponse> = arrayListOf()
    val mFragmentList: MutableList<Fragment> = ArrayList()
    var isUserAvailableAvailable = NSSingleLiveEvent<NSUserDetailResponse?>()

    fun setNavigationItem() {
        with(colorResources.getStringResource()) {
            navItemList.clear()
            navItemList.add(NSNavigationResponse(R.id.dashboard,R.drawable.ic_dashboard, R.drawable.ic_dashboard_white, dashboard, fragment = DashboardFragment.newInstance()))
            navItemList.add(NSNavigationResponse(R.id.capabilities,R.drawable.ic_capability, R.drawable.ic_capability_white, capabilities, fragment = CapabilitiesFragment.newInstance()))
            navItemList.add(NSNavigationResponse(R.id.services, R.drawable.ic_services, R.drawable.ic_services_white, services, fragment = ServicesFragment.newInstance()))
            navItemList.add(NSNavigationResponse(R.id.fleets, R.drawable.ic_fleets, R.drawable.ic_fleets_white, fleets, fragment = FleetsTabFragment.newInstance()))
            navItemList.add(NSNavigationResponse(R.id.dispatch, R.drawable.ic_dispatch_truck, R.drawable.ic_dispatch_truck_white, dispatch, fragment = DispatchTabFragment.newInstance()))
            navItemList.add(NSNavigationResponse(R.id.settings, R.drawable.ic_settings, R.drawable.ic_settings_s, settings, fragment = SettingsTabFragment.newInstance()))
            setFragmentList()
        }
    }

    private fun setFragmentList() {
        mFragmentList.clear()
        for (item in navItemList) {
            item.fragment?.let { mFragmentList.add(it) }
        }
    }

    fun getUserMainDetail() = viewModelScope.launch {
        getUserMainDetailApi()
    }

    private suspend fun getUserMainDetailApi() {
        showProgress()
        performApiCalls(
            { repository.remote.userDetail() }
        ) {response, isSuccess ->
            hideProgress()
            if (isSuccess) {
                val data = response[0] as NSUserDetailResponse?
                if (data != null) {
                    colorResources.themeHelper.setUserDetail(data.data)
                    isUserAvailableAvailable.postValue(data)
                }
            }
        }
    }
}