package com.nyotek.dot.admin.ui.splash

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSUserManager
import com.nyotek.dot.admin.common.utils.setVisibility
import com.nyotek.dot.admin.common.utils.switchActivity
import com.nyotek.dot.admin.databinding.NsFragmentSplashBinding
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
        viewCreated()
    }

    override fun observeViewModel() {
        super.observeViewModel()
        with(viewModel) {
            with(binding) {
                isAppThemeDataAvailable.observe(
                    viewLifecycleOwner
                ) { isAppTheme ->
                    if (isAppTheme) {
                        getLanguageList()
                    } else {
                        showError(stringResource.somethingWentWrong)
                    }
                }

                isLanguageDataAvailable.observe(
                    viewLifecycleOwner
                ) { isLanguage ->
                    if (isLanguage) {
                        NSConstants.IS_LANGUAGE_UPDATE = true
                        checkLoginStatus()
                    }
                }

                isClProgressVisible.observe(
                    viewLifecycleOwner
                ) { isVisible ->
                    clProgress.setVisibility(isVisible)
                }
            }
        }
    }

    private fun viewCreated() {
        NSApplication.getInstance().setSelectedNavigationType(NSConstants.DASHBOARD_TAB)
        baseObserveViewModel(viewModel)
        observeViewModel()
        viewModel.getAppTheme()
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