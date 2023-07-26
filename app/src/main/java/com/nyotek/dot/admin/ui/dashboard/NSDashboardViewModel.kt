package com.nyotek.dot.admin.ui.dashboard

import android.app.Application
import androidx.fragment.app.Fragment
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.repository.NSUserRepository
import com.nyotek.dot.admin.repository.network.responses.NSNavigationResponse
import com.nyotek.dot.admin.repository.network.responses.NSUserDetailResponse
import com.nyotek.dot.admin.ui.dashboard.tabs.ServicesTabFragment
import com.nyotek.dot.admin.ui.dashboard.tabs.CapabilitiesTabFragment
import com.nyotek.dot.admin.ui.dashboard.tabs.DashboardMainTabFragment
import com.nyotek.dot.admin.ui.dashboard.tabs.FleetTabFragment

class NSDashboardViewModel(application: Application) : NSViewModel(application) {
    var navItemList: MutableList<NSNavigationResponse> = arrayListOf()
    var isUserAvailableAvailable = NSSingleLiveEvent<NSUserDetailResponse>()
    val mFragmentList: MutableList<Fragment> = ArrayList()
    var selectedPage = 0

    /**
     * Set side navigation item
     *
     */
    fun setNavigationItem() {
        with(stringResource) {
            val selectedInstance = NSApplication.getInstance()
            if (selectedInstance.getSelectedNavigationType() == null) {
                selectedInstance.setSelectedNavigationType(NSConstants.DASHBOARD_TAB)
            }
            navItemList.clear()
            navItemList.add(NSNavigationResponse(R.drawable.ic_dashboard, R.drawable.ic_dashboard_white, dashboard, NSConstants.DASHBOARD_TAB, DashboardMainTabFragment.newInstance()))
            navItemList.add(NSNavigationResponse(R.drawable.ic_capability, R.drawable.ic_capability_white, capabilities, NSConstants.CAPABILITIES_TAB, CapabilitiesTabFragment.newInstance()))
            navItemList.add(NSNavigationResponse(R.drawable.ic_services, R.drawable.ic_services_white, services, NSConstants.SERVICE_TAB, ServicesTabFragment.newInstance()))
            navItemList.add(NSNavigationResponse(R.drawable.ic_fleets, R.drawable.ic_fleets_white, fleets, NSConstants.FLEETS_TAB, FleetTabFragment.newInstance()))
           setFragmentList()
        }
    }

    /**
     * Set fragment list
     *
     */
    private fun setFragmentList() {
        mFragmentList.clear()
        for (item in navItemList) {
            item.fragment?.let { mFragmentList.add(it) }
        }
    }

    /**
     * To initiate user detail process
     *
     */
    fun getUserMainDetail() {
        isProgressShowing.value = true
        NSUserRepository.getUserDetail(this)
    }

    override fun apiResponse(data: Any) {
        if (data is NSUserDetailResponse) {
            isProgressShowing.value = false
            isUserAvailableAvailable.postValue(data)
        }
    }
}