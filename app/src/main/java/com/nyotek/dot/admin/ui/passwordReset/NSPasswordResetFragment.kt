package com.nyotek.dot.admin.ui.passwordReset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.nyotek.dot.admin.common.NSFragment
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.getColorWithAlpha
import com.nyotek.dot.admin.common.utils.getRadius
import com.nyotek.dot.admin.databinding.NsFragmentPasswordResetBinding

class NSPasswordResetFragment : NSFragment() {
    private val resetViewModel: NSPasswordResetViewModel by lazy {
        ViewModelProvider(this)[NSPasswordResetViewModel::class.java]
    }
    private var _binding: NsFragmentPasswordResetBinding? = null
    private val resetBinding get() = _binding!!

    companion object {
        fun newInstance() = NSPasswordResetFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = NsFragmentPasswordResetBinding.inflate(inflater, container, false)
        initUI()
        viewCreated()
        setListener()
        return resetBinding.root
    }

    /**
     * Init ui
     *
     */
    private fun initUI() {
        resetBinding.apply {
            stringResource.apply {
                ColorResources.setBackground(clRight, ColorResources.getWhiteColor())
                ColorResources.setCardBackground(etEmailPhone, getRadius(6f), 0, getColorWithAlpha(ColorResources.getPrimaryColor(), 5f), getColorWithAlpha(ColorResources.getPrimaryColor(), 5f))
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
        baseObserveViewModel(resetViewModel)
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
        with(resetBinding) {
            with(resetViewModel) {
                btnReset.setOnClickListener {
                    strEmail = etEmailPhone.text.toString()

                 }
            }
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(resetViewModel) {
            /*isLoginSuccess.observe(
                viewLifecycleOwner
            ) { isLogin ->
                if (isLogin) {
                    switchActivity(
                        NSDashboardActivity::class.java,
                        flags = intArrayOf(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    )
                }
            }*/
        }
    }
}
