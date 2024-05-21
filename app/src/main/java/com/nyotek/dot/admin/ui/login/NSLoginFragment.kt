package com.nyotek.dot.admin.ui.login

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.callbacks.NSLocalJsonCallback
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.getColorWithAlpha
import com.nyotek.dot.admin.common.utils.getRadius
import com.nyotek.dot.admin.common.utils.switchActivity
import com.nyotek.dot.admin.databinding.NsFragmentLoginBinding
import com.nyotek.dot.admin.repository.network.responses.NSLanguageStringResponse
import com.nyotek.dot.admin.ui.dashboard.NSDashboardActivity

class NSLoginFragment : BaseViewModelFragment<NSLoginViewModel, NsFragmentLoginBinding>() {

    override val viewModel: NSLoginViewModel by lazy {
        ViewModelProvider(this)[NSLoginViewModel::class.java]
    }

    companion object {
        fun newInstance() = NSLoginFragment()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentLoginBinding {
        return NsFragmentLoginBinding.inflate(inflater, container, false)
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
            isLoginSuccess.observe(
                viewLifecycleOwner
            ) { isLogin ->
                if (isLogin) {
                    switchActivity(
                        NSDashboardActivity::class.java,
                        flags = intArrayOf(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    )
                }
            }
        }
    }

    /**
     * Init ui
     *
     */
    private fun initUI() {
        binding.apply {
            stringResource.apply {
                ColorResources.setBackground(clRight, ColorResources.getWhiteColor())
                ColorResources.setCardBackground(etEmailPhone, getRadius(6f), 0, getColorWithAlpha(ColorResources.getPrimaryColor(), 5f), getColorWithAlpha(ColorResources.getPrimaryColor(), 5f))
                ColorResources.setCardBackground(etPassword, getRadius(6f), 0, getColorWithAlpha(ColorResources.getPrimaryColor(), 5f), getColorWithAlpha(ColorResources.getPrimaryColor(), 5f))
                ColorResources.setBackground(viewLine, ColorResources.getPrimaryColor())
                etEmailPhone.setHintTextColor(ColorResources.getPrimaryLightColor())
                etPassword.setHintTextColor(ColorResources.getPrimaryLightColor())
                tvCharactersMsg.setTextColor(ColorResources.getSecondaryGrayColor())
                etEmailPhone.hint = typeYourEmailPhone
                tvEmailPhoneTitle.text = emailPhoneNumber
                tvCharactersMsg.text = mustBe8Char
                tvPasswordTitle.text = password
                etPassword.hint = typeYourPassword
                btnLogin.text = logIn
                tvWelcomeBack.text = welcomeBack
                tvSubTitle.text = letsBuildSomething
            }
        }
    }

    /**
     * View created
     */
    private fun viewCreated() {
        baseObserveViewModel(viewModel)
        observeViewModel()

        /*with(loginViewModel) {
            strEmail = "adilkolkar@nyotek.com"
            strPassword = "adk@1234"
            login()
        }*/
    }

    val obj: (Boolean, Boolean, Boolean) -> Unit = { isThemeAvailable, isLocalLng, isShowLanguageSelectDialog ->
        if (isThemeAvailable) {
            NSConstants.IS_LANGUAGE_UPDATE = true
            switchActivity(
                NSDashboardActivity::class.java,
                flags = intArrayOf(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        } else if (isLocalLng) {
            getLocalLng()
        } else if (isShowLanguageSelectDialog) {
            showDialogLanguageSelect(true)
        }
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

    /**
     * Set listener
     */
    private fun setListener() {
        with(binding) {
            with(viewModel) {
                btnLogin.setOnClickListener {
                    strEmail = etEmailPhone.text.toString()
                    strPassword = etPassword.text.toString()
                    login(obj)
                 }
            }
        }
    }
}
