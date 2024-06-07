package com.nyotek.dot.admin.ui.login

import com.nyotek.dot.admin.common.extension.getColorWithAlpha
import com.nyotek.dot.admin.common.extension.getRadius
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.FragmentLoginBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginUI @Inject constructor(private val binding: FragmentLoginBinding, private val colorResources: ColorResources) {

    init {
        binding.apply {
            colorResources.setBackground(clRight, colorResources.getWhiteColor())
            colorResources.setCardBackground(
                etEmailPhone,
                getRadius(6f),
                0,
                getColorWithAlpha(colorResources.getPrimaryColor(), 5f),
                getColorWithAlpha(colorResources.getPrimaryColor(), 5f)
            )
            colorResources.setCardBackground(
                etPassword,
                getRadius(6f),
                0,
                getColorWithAlpha(colorResources.getPrimaryColor(), 5f),
                getColorWithAlpha(colorResources.getPrimaryColor(), 5f)
            )
            colorResources.setBackground(viewLine, colorResources.getPrimaryColor())
            etEmailPhone.setHintTextColor(colorResources.getPrimaryLightColor())
            etPassword.setHintTextColor(colorResources.getPrimaryLightColor())
            tvCharactersMsg.setTextColor(colorResources.getSecondaryGrayColor())

            setTextViews()
        }
    }

    private fun setTextViews() {
        binding.apply {
            colorResources.apply {
                getStringResource().apply {
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
    }
}