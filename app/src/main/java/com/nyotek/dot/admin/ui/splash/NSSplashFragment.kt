package com.nyotek.dot.admin.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSFragment
import com.nyotek.dot.admin.common.NSUserManager
import com.nyotek.dot.admin.common.utils.setVisibility
import com.nyotek.dot.admin.common.utils.switchActivity
import com.nyotek.dot.admin.databinding.NsFragmentSplashBinding
import com.nyotek.dot.admin.ui.dashboard.NSDashboardActivity
import com.nyotek.dot.admin.ui.login.NSLoginActivity

class NSSplashFragment : NSFragment() {
    private val splashModel: NSSplashViewModel by lazy {
        ViewModelProvider(this)[NSSplashViewModel::class.java]
    }
    private var _binding: NsFragmentSplashBinding? = null
    private val splashBinding get() = _binding!!
    private val minSplashTime = 1000L

    companion object {
        fun newInstance() = NSSplashFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = NsFragmentSplashBinding.inflate(inflater, container, false)
        viewCreated()
        return splashBinding.root
    }

    private fun viewCreated() {
        NSApplication.getInstance().setSelectedNavigationType(NSConstants.DASHBOARD_TAB)
        baseObserveViewModel(splashModel)
        observeViewModel()
        splashModel.getAppTheme()
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

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(splashModel) {
            with(splashBinding) {
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
}