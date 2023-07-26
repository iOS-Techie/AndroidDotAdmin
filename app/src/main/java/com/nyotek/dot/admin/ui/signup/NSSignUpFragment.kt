package com.nyotek.dot.admin.ui.signup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.nyotek.dot.admin.common.NSFragment
import com.nyotek.dot.admin.common.utils.switchActivity
import com.nyotek.dot.admin.databinding.NsFragmentSignUpBinding
import com.nyotek.dot.admin.ui.dashboard.NSDashboardActivity
import com.nyotek.dot.admin.ui.login.NSLoginActivity

class NSSignUpFragment : NSFragment() {
    private val signUpViewModel: NSSignUpViewModel by lazy {
        ViewModelProvider(this)[NSSignUpViewModel::class.java]
    }
    private var _binding: NsFragmentSignUpBinding? = null
    private val signUpBinding get() = _binding!!

    companion object {
        fun newInstance() = NSSignUpFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = NsFragmentSignUpBinding.inflate(inflater, container, false)
        initUI()
        viewCreated()
        setListener()
        return signUpBinding.root
    }

    private fun initUI() {
        signUpBinding.apply {
            stringResource.apply {
                tvLoginTitle.text = signUp
                tvLoginSubTitle.text = enterYourCredentials
                etEmail.hint = email
                etUserName.hint = username
                etPassword.hint = password
                btnLogin.text = signUp
                tvNotYetRegister.text = youHaveAccount
                tvSignUp.text = logIn
            }
        }
    }

    /**
     * View created
     */
    private fun viewCreated() {
        baseObserveViewModel(signUpViewModel)
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
        with(signUpBinding) {
            with(signUpViewModel) {
                btnLogin.setOnClickListener {
                    strEmail = etEmail.text.toString()
                    strPassword = etPassword.text.toString()
                    login()
                 }

                clBottomLogin.setOnClickListener {
                    switchActivity(
                        NSLoginActivity::class.java
                    )
                    finish()
                }
            }
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(signUpViewModel) {
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
