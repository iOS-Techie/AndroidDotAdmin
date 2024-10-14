package com.nyotek.dot.admin.ui.tabs.fleets.employee

import com.nyotek.dot.admin.common.extension.rotation
import com.nyotek.dot.admin.common.extension.setCoilCircle
import com.nyotek.dot.admin.common.extension.setVisibility
import com.nyotek.dot.admin.common.extension.setVisibilityIn
import com.nyotek.dot.admin.databinding.LayoutEmployeeListBinding
import com.nyotek.dot.admin.databinding.LayoutInviteUserItemBinding
import com.nyotek.dot.admin.databinding.NsFragmentEmployeeBinding
import com.nyotek.dot.admin.models.responses.EmployeeDataItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmployeeUI @Inject constructor(private val binding: NsFragmentEmployeeBinding, private val viewModel: NSEmployeeViewModel) {

    init {
        binding.apply {
            setTextViews()
        }
    }

    private fun setTextViews() {
        binding.apply {
            viewModel.apply {
                colorResources.apply {
                
                }
            }
        }
    }

    fun setAdapter(bind: LayoutEmployeeListBinding, isEmployeeSelected: Boolean, response: EmployeeDataItem) {
        bind.apply {
            viewModel.apply {
                switchService.rotation(languageConfig.isLanguageRtl())
                colorResources.setBackground(clEmployeeItem, if (isEmployeeSelected) colorResources.getBackgroundColor() else colorResources.getWhiteColor())
                cardIcon.setCardBackgroundColor(colorResources.getGrayColor())
                
                if (!response.userId.isNullOrEmpty()) {
                    ivDelete.setVisibilityIn(colorResources.themeHelper.getUserDetail()?.id != response.userId)
                    viewModel.getUserDetail(response.userId) {
                        val model = it?.data?.social
                        val firstLastName = if (model != null) "${model.firstName} ${model.lastName}" else "-"
                        tvEmployeeTitle.text = firstLastName
                        tvDescription.text = if(it?.data?.mobile.isNullOrEmpty()) "" else it?.data?.mobile?:""
                        tvDescription.setVisibility(!it?.data?.mobile.isNullOrEmpty())
                        ivIcon.setCoilCircle(model?.profilePicUrl)
                    }
                }
            }
        }
    }
    
    fun setAddEmployeeAdapter(bind: LayoutInviteUserItemBinding) {
        bind.apply {
            viewModel.apply {
                colorResources.apply {
                    getStringResource().apply {
                        val userNumber = "$user $number"
                        layoutUser.tvCommonTitle.text = userNumber
                        layoutUser.edtValue.hint = searchByPhoneNumber
                    }
                }
            }
        }
    }
}