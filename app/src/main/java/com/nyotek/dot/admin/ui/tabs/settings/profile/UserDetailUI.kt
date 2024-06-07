package com.nyotek.dot.admin.ui.tabs.settings.profile

import android.text.InputType
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.NsFragmentUserDetailBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDetailUI @Inject constructor(private val binding: NsFragmentUserDetailBinding, private val colorResources: ColorResources) {

    init {
        binding.apply {
            setTextViews()
        }
    }

    private fun setTextViews() {
        binding.apply {
            colorResources.apply {
                getStringResource().apply {
                    colorResources.setCardBackground(clUserDetail, 14f, 1, colorResources.getWhiteColor(), colorResources.getBorderColor())

                    layoutName.tvCommonTitle.text = firstName
                    layoutName.edtValue.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
                    layoutLastName.tvCommonTitle.text = lastName
                    layoutLastName.edtValue.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
                    layoutEmail.tvCommonTitle.text = emailAddress
                    layoutEmail.edtValue.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
                    layoutEmail.edtValue.isEnabled = false
                    layoutBio.tvCommonTitle.text = bio
                    layoutBio.edtValue.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                    layoutBirthday.tvCommonTitle.text = birthDate
                }
            }
        }
    }
}