package com.nyotek.dot.admin.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.nyotek.dot.admin.common.*
import com.nyotek.dot.admin.common.callbacks.NSBackClickCallback
import com.nyotek.dot.admin.common.callbacks.NSSideNavigationSelectCallback
import com.nyotek.dot.admin.common.utils.NSLanguageConfig
import com.nyotek.dot.admin.common.utils.linear
import com.nyotek.dot.admin.common.utils.notifyAdapter
import com.nyotek.dot.admin.common.utils.switchActivity
import com.nyotek.dot.admin.databinding.NsFragmentDashboardBinding
import com.nyotek.dot.admin.repository.network.responses.NSNavigationResponse
import com.nyotek.dot.admin.repository.network.responses.NSUserDetailResponse
import com.nyotek.dot.admin.ui.dashboard.tabs.CapabilitiesTabFragment
import com.nyotek.dot.admin.ui.dashboard.tabs.DashboardMainTabFragment
import com.nyotek.dot.admin.ui.dashboard.tabs.FleetTabFragment
import com.nyotek.dot.admin.ui.dashboard.tabs.ServicesTabFragment
import com.nyotek.dot.admin.ui.login.NSLoginActivity
import com.nyotek.dot.admin.ui.settings.NSSettingViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NSDashboardFragment : NSFragment(), NSBackClickCallback {
    private val dashboardViewModel: NSDashboardViewModel by lazy {
        ViewModelProvider(this)[NSDashboardViewModel::class.java]
    }
    private val settingModel: NSSettingViewModel by lazy {
        ViewModelProvider(this)[NSSettingViewModel::class.java]
    }
    private var _binding: NsFragmentDashboardBinding? = null
    private val dashboardBinding get() = _binding!!
    private var navigationAdapter: NSSideNavigationRecycleAdapter? = null

    companion object {
        fun newInstance() = NSDashboardFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentDashboardBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return dashboardBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        baseObserveViewModel(dashboardViewModel)
        baseObserveViewModel(settingModel)
        observeViewModel()
        setNavigationAdapter()
        dashboardViewModel.apply {
            dashboardBinding.apply {
                setDashboard()
                tvUserTitle.text = stringResource.logout
            }
        }
        //dashboardViewModel.getUserMainDetail()
    }

    private fun setListener() {
        dashboardBinding.apply {
            dashboardViewModel.apply {
                clLogout.setOnClickListener {
                    stringResource.apply {
                        showLogoutDialog(
                            logout,
                            logoutMessage,
                            no,
                            yes
                        )
                    }
                }
            }
        }
    }

    /**
     * Set dashboard first fragment
     *
     */
    private fun setDashboard() {
        NSApplication.getInstance().setSelectedNavigationType(NSConstants.DASHBOARD_TAB)
    }

    /**
     * Set navigation adapter
     *
     */
    private fun setNavigationAdapter() {
        with(dashboardBinding) {
            with(dashboardViewModel) {
                with(rvNavList) {
                    setNavigationItem()
                    linear(activity)
                    navigationAdapter =
                        NSSideNavigationRecycleAdapter(isLanguageSelected(), object : NSSideNavigationSelectCallback {
                            override fun onItemSelect(
                                navResponse: NSNavigationResponse,
                                position: Int
                            ) {
                                navResponse.apply {
                                    NSApplication.getInstance().setSelectedNavigationType(type)
                                    dashboardBinding.dashboardPager.setCurrentItem(
                                        position,
                                        false
                                    )
                                }
                                notifyAdapter(navigationAdapter!!)
                            }
                        })
                    adapter = navigationAdapter
                    navigationAdapter?.setData(navItemList)
                    isNestedScrollingEnabled = false
                    setupViewPager(requireActivity(), dashboardBinding.dashboardPager)
                }
            }
        }
    }

    // Add Fragments to Tabs
    private fun setupViewPager(activity: FragmentActivity, viewPager: ViewPager2) {
        try {
            var isVendorAdded = false
            var isServiceManagerAdded = false
            val adapter = NSViewPagerAdapter(activity)
            adapter.setFragment(dashboardViewModel.mFragmentList)
            viewPager.adapter = adapter
            viewPager.isUserInputEnabled = false
            viewPager.offscreenPageLimit = dashboardViewModel.mFragmentList.size
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    dashboardViewModel.selectedPage = position
                    if (dashboardViewModel.mFragmentList[position] is CapabilitiesTabFragment) {
                        NSApplication.getInstance().setSelectedNavigationType(NSConstants.CAPABILITIES_TAB)
                        (dashboardViewModel.mFragmentList[position] as CapabilitiesTabFragment).setFragment()
                    } else if (dashboardViewModel.mFragmentList[position] is FleetTabFragment && !isVendorAdded) {
                        isVendorAdded = true
                        (dashboardViewModel.mFragmentList[position] as FleetTabFragment).setFragment()
                        NSApplication.getInstance().setSelectedNavigationType(NSConstants.FLEETS_TAB)
                    } else if (dashboardViewModel.mFragmentList[position] is ServicesTabFragment && !isServiceManagerAdded) {
                        isServiceManagerAdded = true
                        NSApplication.getInstance().setSelectedNavigationType(NSConstants.SERVICE_TAB)
                        (dashboardViewModel.mFragmentList[position] as ServicesTabFragment).setFragment()
                    } else if (dashboardViewModel.mFragmentList[position] is DashboardMainTabFragment) {
                        NSApplication.getInstance().setSelectedNavigationType(NSConstants.DASHBOARD_TAB)
                        //(dashboardViewModel.mFragmentList[position] as DashboardMainTabFragment).setFragment()
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Set user detail
     *
     * @param userDetail
     */
    private fun setUserDetail(userDetail: NSUserDetailResponse) {
        dashboardBinding.apply {
            setDashboard()
            tvUserTitle.text = userDetail.data?.username

           /* clUserName.setOnClickListener {
                switchActivity(
                    NSSettingActivity::class.java,
                    bundleOf(NSConstants.USER_DETAIL_KEY to Gson().toJson(userDetail))
                )
            }*/
        }
    }

    private fun observeViewModel() {
        with(dashboardViewModel) {
            isUserAvailableAvailable.observe(
                viewLifecycleOwner
            ) { userData ->
                setUserDetail(userData)
            }
        }

        settingModel.apply {
            isLogout.observe(
                viewLifecycleOwner
            ) { isLogout ->
                NSLog.d(tagLog, "observeViewModel: $isLogout")
                NSLanguageConfig.logout()
                switchActivity(
                    NSLoginActivity::class.java,
                    flags = intArrayOf(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBackPress(@Suppress("UNUSED_PARAMETER") event: NSOnBackPressEvent) {
        when (NSApplication.getInstance().getSelectedNavigationType()) {
            NSConstants.CAPABILITIES_TAB -> {
                setFragmentBack()
            }
            NSConstants.FLEETS_TAB -> {
                setFragmentBack()
            }
            NSConstants.SERVICE_TAB -> {
                setFragmentBack()
            }
        }
    }

    /**
     * Set fragment back
     *
     */
    private fun setFragmentBack() {
        dashboardViewModel.apply {
            if (mFragmentList[dashboardBinding.dashboardPager.currentItem] is ServicesTabFragment) {
                (mFragmentList[dashboardBinding.dashboardPager.currentItem] as ServicesTabFragment).onBackClick(this@NSDashboardFragment)
            } else if (mFragmentList[dashboardBinding.dashboardPager.currentItem] is FleetTabFragment) {
                (mFragmentList[dashboardBinding.dashboardPager.currentItem] as FleetTabFragment).onBackClick(this@NSDashboardFragment)
            } else if (mFragmentList[dashboardBinding.dashboardPager.currentItem] is CapabilitiesTabFragment) {
                (mFragmentList[dashboardBinding.dashboardPager.currentItem] as CapabilitiesTabFragment).onBackClick(this@NSDashboardFragment)
            }
        }
    }

    override fun onBack() {
        EventBus.getDefault().post(NSOnBackPressReceiveEvent())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPositiveButtonClickEvent(event: NSAlertButtonClickEvent) {
        if (event.buttonType == NSConstants.KEY_ALERT_BUTTON_NEGATIVE && event.alertKey == NSConstants.LOGOUT_CLICK) {
            with(settingModel) {
                logout(true)
            }
        }
    }
}