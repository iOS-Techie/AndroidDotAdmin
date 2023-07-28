package com.nyotek.dot.admin.ui.signup

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
import com.nyotek.dot.admin.common.utils.switchActivity
import com.nyotek.dot.admin.databinding.NsFragmentSignUpBinding
import com.nyotek.dot.admin.ui.dashboard.NSDashboardActivity
import com.nyotek.dot.admin.ui.login.NSLoginActivity

class NSSignUpFragment : BaseViewModelFragment<NSSignUpViewModel, NsFragmentSignUpBinding>() {

    override val viewModel: NSSignUpViewModel by lazy {
        ViewModelProvider(this)[NSSignUpViewModel::class.java]
    }

    companion object {
        fun newInstance() = NSSignUpFragment()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentSignUpBinding {
        return NsFragmentSignUpBinding.inflate(inflater, container, false)
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

    private fun initUI() {
        binding.apply {
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
        baseObserveViewModel(viewModel)
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
        with(binding) {
            with(viewModel) {
                btnLogin.setSafeOnClickListener {
                    strEmail = etEmail.text.toString()
                    strPassword = etPassword.text.toString()
                    login()
                 }

                clBottomLogin.setSafeOnClickListener {
                    switchActivity(
                        NSLoginActivity::class.java
                    )
                    finish()
                }
            }
        }
    }
}
