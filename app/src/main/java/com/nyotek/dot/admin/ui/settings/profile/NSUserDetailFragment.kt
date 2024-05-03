package com.nyotek.dot.admin.ui.settings.profile

import android.app.Activity.RESULT_OK
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.BrandLogoHelper
import com.nyotek.dot.admin.common.NSUserManager
import com.nyotek.dot.admin.common.callbacks.NSFileUploadCallback
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.convertToInt
import com.nyotek.dot.admin.common.utils.gone
import com.nyotek.dot.admin.common.utils.onTextChanged
import com.nyotek.dot.admin.common.utils.setCoilCircle
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
import com.nyotek.dot.admin.common.utils.setVisibility
import com.nyotek.dot.admin.databinding.NsFragmentUserDetailBinding
import com.nyotek.dot.admin.repository.network.responses.NSSocialDataResponse

class NSUserDetailFragment : BaseViewModelFragment<NSUserDetailViewModel, NsFragmentUserDetailBinding>(),
        (Boolean) -> Unit {

    override val viewModel: NSUserDetailViewModel by lazy {
        ViewModelProvider(this)[NSUserDetailViewModel::class.java]
    }

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
            layoutHeader.apply {
                stringResource.apply {
                    ColorResources.setCardBackground(clUserDetail, 14f, 1, ColorResources.getWhiteColor(), ColorResources.getBorderColor())
                    setLayoutHeader(layoutHeader, profile, isBack = true)
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

    /**
     * View created
     */
    private fun viewCreated() {
        baseObserveViewModel(viewModel)
        observeViewModel()
    }

    override fun loadFragment() {
        super.loadFragment()
        //Clear Data
        binding.layoutEmail.edtValue.setText("")
        setUserDetail(NSSocialDataResponse())
        //Api calling
        viewModel.getSocialInfo(false) { response ->
            setUserDetail(response)
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(binding) {
            with(layoutHeader) {
                ivBack.setSafeOnClickListener {
                    onBackPress()
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

                var dayValue = ""
                var monthValue = ""
                var yearValue = ""

                binding.layoutBirthday.tvCommonTitle.setSafeOnClickListener {
                    NSUtilities.openDatePicker(activity, layoutBirthday.edtValue.text.toString()) { date, day, month, year ->
                        layoutBirthday.edtValue.text = date
                        dayValue = day
                        monthValue = month
                        yearValue = year

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
                val userDetail = NSUserManager.getUserDetail()
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