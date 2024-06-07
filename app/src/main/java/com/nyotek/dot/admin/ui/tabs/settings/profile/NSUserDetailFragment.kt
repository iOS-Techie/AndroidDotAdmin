package com.nyotek.dot.admin.ui.tabs.settings.profile

import android.app.Activity.RESULT_OK
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.BaseFragment
import com.nyotek.dot.admin.common.BrandLogoHelper
import com.nyotek.dot.admin.common.NSUtilities
import com.nyotek.dot.admin.common.callbacks.NSFileUploadCallback
import com.nyotek.dot.admin.common.extension.convertToInt
import com.nyotek.dot.admin.common.extension.gone
import com.nyotek.dot.admin.common.extension.onTextChanged
import com.nyotek.dot.admin.common.extension.setCoilCircle
import com.nyotek.dot.admin.common.extension.setSafeOnClickListener
import com.nyotek.dot.admin.common.extension.setVisibility
import com.nyotek.dot.admin.databinding.NsFragmentUserDetailBinding
import com.nyotek.dot.admin.models.responses.NSSocialDataResponse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NSUserDetailFragment : BaseFragment<NsFragmentUserDetailBinding>(),
        (Boolean) -> Unit {

    private val viewModel by viewModels<NSUserDetailViewModel>()
    private lateinit var themeUI: UserDetailUI

    companion object {
        fun newInstance() = NSUserDetailFragment()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentUserDetailBinding {
        return NsFragmentUserDetailBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        themeUI = UserDetailUI(binding, viewModel.colorResources)
        initUI()
        viewCreated()
        setListener()
    }

    override fun observeViewModel() {
        super.observeViewModel()
        viewModel.apply {
            isUpdateSuccess.observe(viewLifecycleOwner) {
                updatedValues(it)
            }
        }
    }

    private fun initUI() {
        with(binding) {
            setLayoutHeader(layoutHeader, viewModel.colorResources.getStringResource().profile, isBack = true)
            viewModel.getSocialInfo { response ->
                setUserDetail(response)
            }
        }
    }

    /**
     * View created
     */
    private fun viewCreated() {
        observeBaseViewModel(viewModel)
        observeViewModel()
    }

    /**
     * Set listener
     */
    override fun setListener() {
        super.setListener()
        binding.apply {
            layoutHeader.apply {
                ivBack.setSafeOnClickListener {
                    findNavController().popBackStack()
                }

                val brandLogoHelper = BrandLogoHelper(
                    this@NSUserDetailFragment,
                    callback = object : NSFileUploadCallback {
                        override fun onFileUrl(url: String, width: Int, height: Int) {
                            viewModel.apply {
                                imageUrl = url
                                if (isCreate == false) {
                                    viewModel.updateProfilePic(imageUrl)
                                }
                            }
                        }
                    })

                ivProfileLogo.setSafeOnClickListener {
                    brandLogoHelper.openImagePicker(
                        activity,
                        ivProfileLogo,
                        null,
                        isNeedUpload = true,
                        true
                    )
                }

                binding.layoutBirthday.tvCommonTitle.setSafeOnClickListener {
                    NSUtilities.openDatePicker(activity, viewModel.colorResources, layoutBirthday.edtValue.text.toString()) { date, day, month, year ->
                        layoutBirthday.edtValue.text = date

                        if (viewModel.isCreate == false) {
                            viewModel.updateDob(year.convertToInt(), month.convertToInt(), day.convertToInt())
                        }
                    }
                }

                if (viewModel.isCreate == false) {
                    layoutName.edtValue.onTextChanged { text ->
                        run {
                            viewModel.updateFirstName(text)
                        }
                    }

                    layoutLastName.edtValue.onTextChanged { text ->
                        run {
                            viewModel.updateLastName(text)
                        }
                    }

                    layoutBio.edtValue.onTextChanged { text ->
                        run {
                            viewModel.updateBiography(text)
                        }
                    }
                }
            }
        }
    }

    private fun setUserDetail(model: NSSocialDataResponse?) {
        binding.apply {
            if (viewModel.isCreate == true) {
                ivProfileLogo.setImageResource(R.drawable.ic_profile_plus)
                clProfileEditIco.gone()
            }
            model?.apply {
                val userDetail = viewModel.colorResources.themeHelper.getUserDetail()
                layoutEmail.edtValue.setText(userDetail?.email)
                layoutName.edtValue.setText(firstName)
                layoutLastName.edtValue.setText(lastName)
                if (birthYear != null) {
                    val date =
                        "${NSUtilities.getDateZero(birthYear)}-${NSUtilities.getDateZero(birthMonth)}-${
                            NSUtilities.getDateZero(birthDay)
                        }"
                    layoutBirthday.edtValue.text = date
                } else {
                    layoutBirthday.edtValue.text = ""
                }
                layoutBio.edtValue.setText(biography)
                ivProfileLogo.setCoilCircle(profilePicUrl)
                clProfileEditIco.setVisibility(!profilePicUrl.isNullOrEmpty())
            }
        }
    }

    private fun updatedValues(isUpdate: Boolean) {
        if (isUpdate) {
            requireActivity().setResult(RESULT_OK)
        }
    }

    override fun invoke(isShowProgress: Boolean) {
        viewModel.apply {
            if (isShowProgress) {
                showProgress()
            } else {
                hideProgress()
            }
        }
    }
}