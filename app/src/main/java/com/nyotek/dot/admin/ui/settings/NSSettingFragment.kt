package com.nyotek.dot.admin.ui.settings

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.franmontiel.localechanger.utils.ActivityRecreationHelper
import com.nyotek.dot.admin.BuildConfig
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.NSLanguageConfig
import com.nyotek.dot.admin.common.utils.setupWithAdapter
import com.nyotek.dot.admin.common.utils.switchActivity
import com.nyotek.dot.admin.databinding.NsFragmentSettingsBinding
import com.nyotek.dot.admin.repository.network.responses.NSSettingListResponse
import com.nyotek.dot.admin.ui.login.NSLoginActivity
import com.nyotek.dot.admin.ui.settings.profile.NSUserDetailFragment

class NSSettingFragment : BaseViewModelFragment<NSSettingViewModel, NsFragmentSettingsBinding>() {

    override val viewModel: NSSettingViewModel by lazy {
        ViewModelProvider(this)[NSSettingViewModel::class.java]
    }

    companion object {
        fun newInstance() = NSSettingFragment()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentSettingsBinding {
        return NsFragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun loadFragment() {
        super.loadFragment()

    }

    override fun setupViews() {
        super.setupViews()
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
                NSLanguageConfig.logout()
                switchActivity(
                    NSLoginActivity::class.java,
                    flags = intArrayOf(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
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
            stringResource.apply {
                setLayoutHeader(
                    layoutHomeHeader,
                    settings
                )
                ColorResources.setCardBackground(rvSettings, 14f, 1, ColorResources.getWhiteColor(), ColorResources.getBorderColor())
            }
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
        baseObserveViewModel(viewModel)
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
                    NSLanguageConfig.isLanguageRtl()) {
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
                    showDialogLanguageSelect()
                }

                contactUs -> {
                    showDialogCallEmailAction(BuildConfig.PHONE_NUMBER, BuildConfig.MAIL_ID)
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
        settingFragmentChangeCallback?.setFragment(
            this@NSSettingFragment.javaClass.simpleName,
            NSUserDetailFragment.newInstance(),
            true
        )
    }
}