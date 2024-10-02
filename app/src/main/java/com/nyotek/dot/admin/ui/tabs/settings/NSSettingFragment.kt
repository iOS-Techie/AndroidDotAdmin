package com.nyotek.dot.admin.ui.tabs.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.franmontiel.localechanger.utils.ActivityRecreationHelper
import com.nyotek.dot.admin.BuildConfig
import com.nyotek.dot.admin.base.BaseFragment
import com.nyotek.dot.admin.common.extension.navigateSafeNew
import com.nyotek.dot.admin.common.extension.setupWithAdapter
import com.nyotek.dot.admin.databinding.NsFragmentSettingsBinding
import com.nyotek.dot.admin.models.responses.NSSettingListResponse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NSSettingFragment : BaseFragment<NsFragmentSettingsBinding>() {

    private val viewModel by viewModels<NSSettingViewModel>()
    private lateinit var themeUI: SettingUI

    companion object {
        fun newInstance() = NSSettingFragment()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentSettingsBinding {
        return NsFragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        themeUI = SettingUI(binding, viewModel.colorResources)
        initUI()
        viewCreated()
        viewModel.getProfileListData()
    }

    override fun observeViewModel() {
        super.observeViewModel()
        with(viewModel) {

            isLogout.observe(
                viewLifecycleOwner
            ) {
                logoutFinal()
            }

            settingObserve.observe(
                viewLifecycleOwner
            ) {
                setSettingAdapter(it)
            }
        }
    }

    private fun initUI() {
        binding.apply {
            setLayoutHeader(
                layoutHomeHeader,
                viewModel.colorResources.getStringResource().settings
            )
        }
    }

    override fun onResume() {
        super.onResume()
        ActivityRecreationHelper.onResume(activity)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityRecreationHelper.onDestroy(activity)
    }

    /**
     * View created
     *
     */
    private fun viewCreated() {
        observeBaseViewModel(viewModel)
        observeViewModel()
    }

    /**
     * Set setting adapter
     *
     */
    private fun setSettingAdapter(list: MutableList<NSSettingListResponse>) {
        with(binding) {
            val settingAdapter =
                NSSettingRecycleAdapter(
                    viewModel.languageConfig.isLanguageRtl()) {
                    onClickProfile(it)
                }
            rvSettings.setupWithAdapter(settingAdapter)
            settingAdapter.setData(list)
            rvSettings.isNestedScrollingEnabled = false
        }
    }

    /**
     * On click profile
     *
     * @param title adapter position title
     */
    private fun onClickProfile(title: String) {
        with(stringResource) {
            when (title) {
                profile -> {
                    openUserDetail()
                }
                selectLanguage -> {
                    showDialogLanguageSelect(colorResources = viewModel.colorResources, languageConfig = viewModel.languageConfig, themeHelper = viewModel.themeHelper) {}
                }

                contactUs -> {
                    showDialogCallEmailAction(BuildConfig.PHONE_NUMBER, BuildConfig.MAIL_ID, viewModel.colorResources)
                }

                logout -> {
                    showLogoutDialog(
                        logout,
                        logoutMessage,
                        no,
                        yes) {
                        if (it) {
                            viewModel.logout()
                        }
                    }
                }

                else -> {}
            }
        }
    }

    private fun openUserDetail() {
        findNavController().navigateSafeNew(SettingFragmentDirections.actionSettingsToUserDetail(null))
    }
}