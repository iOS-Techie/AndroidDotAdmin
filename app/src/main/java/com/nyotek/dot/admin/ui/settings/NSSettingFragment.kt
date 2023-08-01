package com.nyotek.dot.admin.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.franmontiel.localechanger.utils.ActivityRecreationHelper
import com.nyotek.dot.admin.BuildConfig
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.utils.NSLanguageConfig
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
import com.nyotek.dot.admin.common.utils.setupWithAdapter
import com.nyotek.dot.admin.common.utils.setupWithAdapterAndCustomLayoutManager
import com.nyotek.dot.admin.common.utils.switchActivity
import com.nyotek.dot.admin.databinding.NsFragmentSettingsBinding
import com.nyotek.dot.admin.ui.login.NSLoginActivity

class NSSettingFragment : BaseViewModelFragment<NSSettingViewModel, NsFragmentSettingsBinding>() {

    override val viewModel: NSSettingViewModel by lazy {
        ViewModelProvider(this)[NSSettingViewModel::class.java]
    }

    private var settingAdapter: NSSettingRecycleAdapter? = null
    private var settingRecycleAdapter: NSSettingsUserRecycleAdapter? = null

    companion object {
        fun newInstance(bundle: Bundle?) = NSSettingFragment().apply {
            arguments = bundle
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            with(viewModel) {
                strUserDetail = it.getString(NSConstants.USER_DETAIL_KEY)
            }
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentSettingsBinding {
        return NsFragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        initUI()
        viewCreated()
        setListener()
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

            isSettingUserAvailable.observe(
                viewLifecycleOwner
            ) { isSettingUsers ->
                if (isSettingUsers) {
                    setMainUserAdapter()
                }
            }
        }
    }

    private fun initUI() {
        binding.apply {
            stringResource.apply {
                tvSettingTitle.text = setting
                tvBackSettings.text = back
                tvSaveSettings.text = save
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
        viewModel.getJsonUserDetail(activity)
        baseObserveViewModel(viewModel)
        observeViewModel()
        setSettingAdapter()
    }

    private fun setListener() {
        with(binding) {
            tvBackSettings.setSafeOnClickListener {
                onBackPress()
            }
        }
    }

    /**
     * Set setting adapter
     *
     */
    private fun setSettingAdapter() {
        with(binding) {
            with(viewModel) {
                getProfileListData(activity)
                settingAdapter =
                    NSSettingRecycleAdapter(
                        isLanguageSelected()) {
                        onClickProfile(it)
                    }
                rvSettings.setupWithAdapter(settingAdapter!!)
                settingAdapter?.setItemSize(profileItemList.size)
                settingAdapter?.setData(profileItemList)
                rvSettings.isNestedScrollingEnabled = false
            }
        }
    }

    /**
     * Set setting user adapter
     *
     */
    private fun setMainUserAdapter() {
        with(binding) {
            with(viewModel) {
                settingRecycleAdapter = NSSettingsUserRecycleAdapter()
                rvSettingsUsers.setupWithAdapterAndCustomLayoutManager(
                    settingRecycleAdapter!!,
                    GridLayoutManager(activity, 2)
                )
                settingRecycleAdapter?.setData(settingUserList)
            }
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
                            viewModel.logout(true)
                        }
                    }
                }

                else -> {}
            }
        }
    }
}