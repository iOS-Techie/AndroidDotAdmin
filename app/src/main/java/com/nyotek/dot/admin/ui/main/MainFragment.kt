package com.nyotek.dot.admin.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.BaseFragment
import com.nyotek.dot.admin.common.extension.navigateSafe
import com.nyotek.dot.admin.common.extension.notifyAdapter
import com.nyotek.dot.admin.common.extension.setPager
import com.nyotek.dot.admin.common.extension.setSafeOnClickListener
import com.nyotek.dot.admin.common.extension.setupWithAdapter
import com.nyotek.dot.admin.databinding.FragmentMainBinding
import com.nyotek.dot.admin.models.responses.NSUserDetailResponse
import com.nyotek.dot.admin.ui.tabs.capabilities.CapabilitiesFragment
import com.nyotek.dot.admin.ui.tabs.dispatch.DispatchTabFragment
import com.nyotek.dot.admin.ui.tabs.fleets.FleetsTabFragment
import com.nyotek.dot.admin.ui.tabs.services.ServicesFragment
import com.nyotek.dot.admin.ui.tabs.settings.SettingsTabFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>() {

    private val viewModel by viewModels<MainViewModel>()
    private var drawerSelectedItemId = R.id.dashboard
    private val drawerSelectedItemIdKey = "DRAWER_SELECTED_ITEM_ID_KEY"
    private var navigationAdapter: NSSideNavigationRecycleAdapter? = null
    private lateinit var themeUI: MainUI

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let {
            drawerSelectedItemId = it.getInt(drawerSelectedItemIdKey, drawerSelectedItemId)
        }
       // setBackPressedHandler()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        themeUI = MainUI(binding, viewModel.colorResources)
        setupBottomNavigationBar()
    }

    override fun observeViewModel() {
        super.observeViewModel()
        observeBaseViewModel(viewModel)
        observe()
    }

    private fun observe() {
        viewModel.apply {
            isUserAvailableAvailable.observe(
                viewLifecycleOwner
            ) { userData ->
                setUserDetail(userData)
            }
        }
    }

    private fun setUserDetail(userDetail: NSUserDetailResponse?) {
        binding.apply {
            tvUserTitle.text = userDetail?.data?.username
        }
    }

    private fun setupBottomNavigationBar() {
        val isEmpty = viewModel.languageConfig.getSelectedLanguage().isEmpty()
        if (isEmpty) {
            showDialogLanguageSelect(false, viewModel.colorResources, viewModel.languageConfig, viewModel.themeHelper){}
        } else {
            viewModel.getUserMainDetail()
            viewModel.setNavigationItem()
            setNavigationAdapter()
        }
    }

    private fun setNavigationAdapter() {
        binding.apply {
            viewModel.apply {
                rvNavList.apply {
                    setNavigationItem()
                    navigationAdapter =
                        NSSideNavigationRecycleAdapter(
                            viewModel.languageConfig.dataStorePreference.isLanguageRTL,
                            themeUI
                        ) { navResponse, position ->
                            binding.viewPager.setCurrentItem(position,false)
                            //binding.mainBottomNavigationView.selectedItemId = navResponse.id
                            selectedDrawerId =  navResponse.id
                            navigationAdapter?.let { notifyAdapter(it) }
                        }
                    setupWithAdapter(navigationAdapter!!)
                    navigationAdapter?.setData(navItemList)
                    setupViewPager(requireActivity(), binding.viewPager)
                }
            }
        }
    }

    private fun setupViewPager(activity: FragmentActivity, viewPager: ViewPager2) {
        viewModel.apply {
            viewPager.setPager(activity, mFragmentList) { position ->
                when (val fragment = mFragmentList[position]) {
                    is CapabilitiesFragment -> {
                        fragment.loadFragment()
                    }

                    is FleetsTabFragment -> {
                        fragment.loadFragment()
                    }

                    is ServicesFragment -> {
                        fragment.loadFragment()
                    }

                    is DispatchTabFragment -> {
                        fragment.loadFragment()
                    }

                    is SettingsTabFragment -> {
                        fragment.loadFragment()
                    }
                }
            }
        }
    }

    private fun setBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(drawerSelectedItemIdKey, drawerSelectedItemId)
        super.onSaveInstanceState(outState)
    }
}