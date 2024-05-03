package com.nyotek.dot.admin.ui.splash

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.apiRefresh.NyoTokenRefresher
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSUserManager
import com.nyotek.dot.admin.common.callbacks.NSLocalJsonCallback
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.setVisibility
import com.nyotek.dot.admin.common.utils.switchActivity
import com.nyotek.dot.admin.databinding.NsFragmentSplashBinding
import com.nyotek.dot.admin.repository.network.responses.NSLanguageStringResponse
import com.nyotek.dot.admin.ui.dashboard.NSDashboardActivity
import com.nyotek.dot.admin.ui.login.NSLoginActivity

class NSSplashFragment : BaseViewModelFragment<NSSplashViewModel, NsFragmentSplashBinding>() {

    private val minSplashTime = 1000L

    override val viewModel: NSSplashViewModel by lazy {
        ViewModelProvider(this)[NSSplashViewModel::class.java]
    }

    companion object {
        fun newInstance() = NSSplashFragment()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentSplashBinding {
        return NsFragmentSplashBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        NyoTokenRefresher.refreshIfNeeded()
        viewCreated()
    }

    override fun observeViewModel() {
        super.observeViewModel()
        with(viewModel) {
            with(binding) {

                isClProgressVisible.observe(
                    viewLifecycleOwner
                ) { isVisible ->
                    clProgress.setVisibility(isVisible)
                }
            }
        }
    }

    val obj: (Boolean, Boolean, Boolean) -> Unit = { isThemeAvailable, isLocalLng, isShowLanguageSelectDialog ->
        if (isThemeAvailable) {
            NSConstants.IS_LANGUAGE_UPDATE = true
            checkLoginStatus()
        } else if (isLocalLng) {
            getLocalLng()
        } else if (isShowLanguageSelectDialog) {
            showDialogLanguageSelect(true)
        }
    }

    private fun viewCreated() {
        NSApplication.getInstance().setSelectedNavigationType(NSConstants.DASHBOARD_TAB)
        baseObserveViewModel(viewModel)
        observeViewModel()
        viewModel.getAppThemeAndChangeLocaleFromSelection(false, obj)

        /*viewModel.getAppTheme {
            if (it) {
                NSConstants.IS_LANGUAGE_UPDATE = true
                checkLoginStatus()
            }
        }*/
    }

    private fun getLocalLng() {
        viewModel.apply {
            NSUtilities.getLocalJsonRowData(requireActivity(), R.raw.local_json, object :
                NSLocalJsonCallback {
                override fun onLocal(json: NSLanguageStringResponse) {
                    setLanguageJsonData(json, obj)
                }
            })
        }
    }

    private fun checkLoginStatus() {
        Handler(Looper.getMainLooper()).postDelayed({
            switchActivity(
                if (!NSUserManager.isUserLoggedIn) NSLoginActivity::class.java else NSDashboardActivity::class.java,
                flags = intArrayOf(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
            finish()
        }, minSplashTime)
    }
}