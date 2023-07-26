package com.nyotek.dot.admin.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.nyotek.dot.admin.common.NSFragment
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.getColorWithAlpha
import com.nyotek.dot.admin.common.utils.getRadius
import com.nyotek.dot.admin.common.utils.switchActivity
import com.nyotek.dot.admin.databinding.NsFragmentLoginBinding
import com.nyotek.dot.admin.ui.dashboard.NSDashboardActivity

class NSLoginFragment : NSFragment() {
    private val loginViewModel: NSLoginViewModel by lazy {
        ViewModelProvider(this)[NSLoginViewModel::class.java]
    }
    private var _binding: NsFragmentLoginBinding? = null
    private val loginBinding get() = _binding!!

    companion object {
        fun newInstance() = NSLoginFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = NsFragmentLoginBinding.inflate(inflater, container, false)
        initUI()
        viewCreated()
        setListener()
        return loginBinding.root
    }

    /**
     * Init ui
     *
     */
    private fun initUI() {
        loginBinding.apply {
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
        baseObserveViewModel(loginViewModel)
        observeViewModel()

        /*with(loginViewModel) {
            strEmail = "adilkolkar@nyotek.com"
            strPassword = "adk@1234"
            login()
        }*/
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(loginBinding) {
            with(loginViewModel) {
                btnLogin.setOnClickListener {
                    strEmail = etEmailPhone.text.toString()
                    strPassword = etPassword.text.toString()
                    login()
                 }
            }
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(loginViewModel) {
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
}
