package com.nyotek.dot.admin.ui.dashboard

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.*
import com.nyotek.dot.admin.common.callbacks.NSBackClickCallback
import com.nyotek.dot.admin.common.utils.NSLanguageConfig
import com.nyotek.dot.admin.common.utils.notifyAdapter
import com.nyotek.dot.admin.common.utils.setPager
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
import com.nyotek.dot.admin.common.utils.setupWithAdapter
import com.nyotek.dot.admin.common.utils.switchActivity
import com.nyotek.dot.admin.databinding.NsFragmentDashboardBinding
import com.nyotek.dot.admin.repository.network.responses.NSNavigationResponse
import com.nyotek.dot.admin.repository.network.responses.NSUserDetailResponse
import com.nyotek.dot.admin.ui.dashboard.tabs.CapabilitiesTabFragment
import com.nyotek.dot.admin.ui.dashboard.tabs.DashboardMainTabFragment
import com.nyotek.dot.admin.ui.dashboard.tabs.DispatchTabFragment
import com.nyotek.dot.admin.ui.dashboard.tabs.FleetTabFragment
import com.nyotek.dot.admin.ui.dashboard.tabs.ServicesTabFragment
import com.nyotek.dot.admin.ui.login.NSLoginActivity
import com.nyotek.dot.admin.ui.settings.NSSettingViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NSDashboardFragment : BaseViewModelFragment<NSDashboardViewModel, NsFragmentDashboardBinding>(), NSBackClickCallback {

    private var navigationAdapter: NSSideNavigationRecycleAdapter? = null

    private val settingModel: NSSettingViewModel by lazy {
        ViewModelProvider(this)[NSSettingViewModel::class.java]
    }

    override val viewModel: NSDashboardViewModel by lazy {
        ViewModelProvider(this)[NSDashboardViewModel::class.java]
    }

    companion object {
        fun newInstance() = NSDashboardFragment()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentDashboardBinding {
        return NsFragmentDashboardBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        viewCreated()
        setListener()
    }

    override fun observeViewModel() {
        super.observeViewModel()
        viewModel.apply {
            isUserAvailableAvailable.observe(
                viewLifecycleOwner
            ) { userData ->
                setUserDetail(userData)
            }
        }

        settingModel.apply {
            isLogout.observe(
                viewLifecycleOwner
            ) {
                NSLanguageConfig.logout()
                switchActivity(
                    NSLoginActivity::class.java,
                    flags = intArrayOf(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
            }
        }
    }

    /**
     * View created
     */
    private fun viewCreated() {
        baseObserveViewModel(viewModel)
        baseObserveViewModel(settingModel)
        observeViewModel()
        setNavigationAdapter()
        setViewText()
    }

    private fun setViewText() {
        viewModel.apply {
            binding.apply {
                setDashboard()
                tvUserTitle.text = stringResource.logout
            }
        }
    }

    private fun setListener() {
        binding.apply {
            viewModel.apply {
                clLogout.setSafeOnClickListener {
                    stringResource.apply {
                        showLogoutDialog(
                            logout,
                            logoutMessage,
                            no,
                            yes) {
                            if (it) {
                                settingModel.logout()
                            }
                        }
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
        with(binding) {
            with(viewModel) {
                with(rvNavList) {
                    setNavigationItem()
                    navigationAdapter =
                        NSSideNavigationRecycleAdapter(isLanguageSelected()) { navResponse, position ->
                            sideNavigationItemClick(navResponse, position)
                        }
                    setupWithAdapter(navigationAdapter!!)
                    navigationAdapter?.setData(navItemList)
                    setupViewPager(requireActivity(), binding.dashboardPager)
                }
            }
        }
    }

    private fun sideNavigationItemClick(navResponse: NSNavigationResponse, position: Int) {
        navResponse.apply {
            NSApplication.getInstance().setSelectedNavigationType(type)
            binding.dashboardPager.setCurrentItem(
                position,
                false
            )
        }
        navigationAdapter?.let { notifyAdapter(it) }
    }

    // Add Fragments to Tabs
    private fun setupViewPager(activity: FragmentActivity, viewPager: ViewPager2) {
        try {

            viewModel.apply {
                viewPager.setPager(activity, mFragmentList) {position ->
                    selectedPage = position
                    val fragment = mFragmentList[position]
                    val instance = NSApplication.getInstance()

                    when (fragment) {
                        is CapabilitiesTabFragment -> {
                            instance.setSelectedNavigationType(NSConstants.CAPABILITIES_TAB)
                            fragment.setFragment()
                        }

                        is FleetTabFragment -> {
                            fragment.setFragment()
                            EventBus.getDefault().post(NSOnMapResetEvent(false))
                            instance.setSelectedNavigationType(NSConstants.FLEETS_TAB)
                        }

                        is DispatchTabFragment -> {
                            fragment.setFragment()
                            EventBus.getDefault().post(NSOnMapResetEvent(false))
                            instance.setSelectedNavigationType(NSConstants.DISPATCH_TAB)
                        }

                        is ServicesTabFragment -> {
                            instance.setSelectedNavigationType(NSConstants.SERVICE_TAB)
                            fragment.setFragment()
                        }

                        is DashboardMainTabFragment -> {
                            EventBus.getDefault().post(NSOnMapResetEvent(true))
                            instance.setSelectedNavigationType(NSConstants.DASHBOARD_TAB)
                        }
                    }
                }
            }

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
        binding.apply {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBackPress(@Suppress("UNUSED_PARAMETER") event: NSOnBackPressEvent) {
        when (NSApplication.getInstance().getSelectedNavigationType()) {
            NSConstants.CAPABILITIES_TAB -> setFragmentBack()
            NSConstants.FLEETS_TAB -> setFragmentBack()
            NSConstants.SERVICE_TAB -> setFragmentBack()
            NSConstants.DISPATCH_TAB -> setFragmentBack()
        }
    }

    /**
     * Set fragment back
     *
     */
    private fun setFragmentBack() {
        viewModel.apply {
            when (val fragment = mFragmentList[binding.dashboardPager.currentItem]) {
                is ServicesTabFragment -> {
                    fragment.onBackClick(this@NSDashboardFragment)
                }

                is FleetTabFragment -> {
                    fragment.onBackClick(this@NSDashboardFragment)
                }

                is CapabilitiesTabFragment -> {
                    fragment.onBackClick(this@NSDashboardFragment)
                }

                is DispatchTabFragment -> {
                    fragment.onBackClick(this@NSDashboardFragment)
                }
            }
        }
    }

    override fun onBack() {
        EventBus.getDefault().post(NSOnBackPressReceiveEvent())
    }
}