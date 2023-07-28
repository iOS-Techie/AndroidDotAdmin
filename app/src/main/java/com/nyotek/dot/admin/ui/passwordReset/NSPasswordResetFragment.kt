package com.nyotek.dot.admin.ui.passwordReset

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.getColorWithAlpha
import com.nyotek.dot.admin.common.utils.getRadius
import com.nyotek.dot.admin.databinding.NsFragmentPasswordResetBinding

class NSPasswordResetFragment :
    BaseViewModelFragment<NSPasswordResetViewModel, NsFragmentPasswordResetBinding>() {

    override val viewModel: NSPasswordResetViewModel by lazy {
        ViewModelProvider(this)[NSPasswordResetViewModel::class.java]
    }

    companion object {
        fun newInstance() = NSPasswordResetFragment()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentPasswordResetBinding {
        return NsFragmentPasswordResetBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        initUI()
        viewCreated()
        setListener()
    }

    override fun observeViewModel() {
        super.observeViewModel()

    }

    /**
     * Init ui
     *
     */
    private fun initUI() {
        binding.apply {
            stringResource.apply {
                ColorResources.setBackground(clRight, ColorResources.getWhiteColor())
                ColorResources.setCardBackground(
                    etEmailPhone,
                    getRadius(6f),
                    0,
                    getColorWithAlpha(ColorResources.getPrimaryColor(), 5f),
                    getColorWithAlpha(ColorResources.getPrimaryColor(), 5f)
                )
                ColorResources.setBackground(viewLine, ColorResources.getPrimaryColor())
                etEmailPhone.setHintTextColor(ColorResources.getPrimaryLightColor())
                etEmailPhone.hint = typeYourEmailPhone
                tvEmailPhoneTitle.text = emailPhoneNumber
                btnReset.text = sendMeTheLink
                tvWelcomeBack.text = passwordReset
                tvSubTitle.text = enterYourEmailWeSendReset
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
                btnReset.setOnClickListener {
                    strEmail = etEmailPhone.text.toString()

                }
            }
        }
    }
}
