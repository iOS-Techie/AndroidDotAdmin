package com.nyotek.dot.admin.ui.splash

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.BaseFragment
import com.nyotek.dot.admin.common.NSThemeHelper
import com.nyotek.dot.admin.common.NSUtilities
import com.nyotek.dot.admin.common.callbacks.NSLocalJsonCallback
import com.nyotek.dot.admin.common.extension.observeOnce
import com.nyotek.dot.admin.common.extension.switchActivity
import com.nyotek.dot.admin.databinding.FragmentSplashBinding
import com.nyotek.dot.admin.models.responses.NSLanguageStringResponse
import com.nyotek.dot.admin.ui.login.LoginActivity
import com.nyotek.dot.admin.ui.main.MainActivity
import com.nyotek.dot.admin.ui.main.selectedDrawerId
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    private val viewModel by viewModels<SplashViewModel>()
    private val minSplashTime = 1000L

    companion object {
        fun newInstance() = SplashFragment()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSplashBinding {
        return FragmentSplashBinding.inflate(inflater, container, false)
    }

    override fun observeViewModel() {
        super.observeViewModel()
        observeBaseViewModel(viewModel)
        observe()
    }

    val obj: (Boolean, Boolean, Boolean) -> Unit = { isThemeAvailable, isLocalLng, isShowLanguageSelectDialog ->
        if (isThemeAvailable) {
            NSThemeHelper.IS_LANGUAGE_UPDATE = true
            checkLoginStatus()
        } else if (isLocalLng) {
            getLocalLng()
        } else if (isShowLanguageSelectDialog) {
            showDialogLanguageSelect(true, viewModel.colorResources, viewModel.languageConfig)
        }
    }

    override fun setupViews() {
        super.setupViews()
        //This is for Dashboard default tab selection when language change
        selectedDrawerId = R.id.dashboard
        viewModel.getAppThemeAndChangeLocaleFromSelection(requireContext(), obj)
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

    private fun observe() {
        viewModel.allDataLoaded.observeOnce(viewLifecycleOwner) { isNext ->
            if (isNext) {
                checkLoginStatus()
            }
        }
    }

    private fun checkLoginStatus() {
        viewModel.apply {
            hideProgress()
            Handler(Looper.getMainLooper()).postDelayed({
                switchActivity(
                    if (!dataStoreRepository.isUserLoggedIn) LoginActivity::class.java else MainActivity::class.java,
                    flags = intArrayOf(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
                finish()
            }, minSplashTime)
        }
    }
}