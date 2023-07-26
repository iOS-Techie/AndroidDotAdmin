package com.nyotek.dot.admin.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.franmontiel.localechanger.utils.ActivityRecreationHelper
import com.nyotek.dot.admin.BuildConfig
import com.nyotek.dot.admin.common.NSAlertButtonClickEvent
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSFragment
import com.nyotek.dot.admin.common.NSLog
import com.nyotek.dot.admin.common.callbacks.NSLogoSelectCallback
import com.nyotek.dot.admin.common.callbacks.NSSettingSelectCallback
import com.nyotek.dot.admin.common.utils.NSLanguageConfig
import com.nyotek.dot.admin.common.utils.switchActivity
import com.nyotek.dot.admin.databinding.NsFragmentSettingsBinding
import com.nyotek.dot.admin.repository.network.responses.NSCommonResponse
import com.nyotek.dot.admin.ui.login.NSLoginActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NSSettingFragment : NSFragment() {
    private val settingModel: NSSettingViewModel by lazy {
        ViewModelProvider(this)[NSSettingViewModel::class.java]
    }
    private var _binding: NsFragmentSettingsBinding? = null
    private val profileBinding get() = _binding!!
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
            with(settingModel) {
                strUserDetail = it.getString(NSConstants.USER_DETAIL_KEY)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentSettingsBinding.inflate(inflater, container, false)
        initUI()
        viewCreated()
        setListener()
        return profileBinding.root
    }

    private fun initUI() {
        profileBinding.apply {
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
        with(profileBinding) {
            settingModel.getJsonUserDetail(activity)
            baseObserveViewModel(settingModel)
            observeViewModel()
            setProfileAdapter()
        }
    }

    private fun setListener() {
        with(profileBinding) {
            with(settingModel) {
                tvBackSettings.setOnClickListener {
                    onBackPress()
                }
            }
        }
    }

    /**
     * Set profile adapter
     *
     */
    private fun setProfileAdapter() {
        with(profileBinding) {
            with(settingModel) {
                getProfileListData(activity)
                rvSettings.layoutManager = LinearLayoutManager(activity)
                settingAdapter =
                    NSSettingRecycleAdapter(
                        profileItemList,
                        isLanguageSelected(),
                        object : NSSettingSelectCallback {
                            override fun onPosition(title: String) {
                                onClickProfile(title)
                            }
                        })
                rvSettings.adapter = settingAdapter
                rvSettings.isNestedScrollingEnabled = false
            }
        }
    }

    /**
     * Set profile adapter
     *
     */
    private fun setMainUserAdapter() {
        with(profileBinding) {
            with(settingModel) {
                val layoutManager = GridLayoutManager(activity, 2)
                rvSettingsUsers.layoutManager = layoutManager
                settingRecycleAdapter =
                    NSSettingsUserRecycleAdapter(activity, object : NSLogoSelectCallback {
                        override fun onItemSelect(model: NSCommonResponse, position: Int) {
                        }
                    })

                rvSettingsUsers.adapter = settingRecycleAdapter
                settingRecycleAdapter?.updateData(settingUserList)
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
                        yes
                    )
                }
                else -> {}
            }
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(settingModel) {

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

            isSettingUserAvailable.observe(
                viewLifecycleOwner
            ) { isSettingUsers ->
                if (isSettingUsers) {
                    setMainUserAdapter()
                }
            }
        }
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