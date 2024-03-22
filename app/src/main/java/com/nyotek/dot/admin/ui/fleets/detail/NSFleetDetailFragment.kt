package com.nyotek.dot.admin.ui.fleets.detail

import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.BrandLogoHelper
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSServiceConfig
import com.nyotek.dot.admin.common.callbacks.NSFileUploadCallback
import com.nyotek.dot.admin.common.utils.NSAddressConfig
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.getTagLists
import com.nyotek.dot.admin.common.utils.getTags
import com.nyotek.dot.admin.common.utils.gone
import com.nyotek.dot.admin.common.utils.invisible
import com.nyotek.dot.admin.common.utils.setPager
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
import com.nyotek.dot.admin.common.utils.status
import com.nyotek.dot.admin.common.utils.switchEnableDisable
import com.nyotek.dot.admin.common.utils.visible
import com.nyotek.dot.admin.databinding.NsFragmentFleetDetailBinding
import com.nyotek.dot.admin.repository.network.requests.NSFleetLogoUpdateRequest
import com.nyotek.dot.admin.repository.network.responses.AddressData
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.FleetDataItem
import com.nyotek.dot.admin.ui.fleets.employee.NSEmployeeFragment
import com.nyotek.dot.admin.ui.fleets.map.NSMapViewModel
import com.nyotek.dot.admin.ui.fleets.vehicle.NSVehicleFragment


class NSFleetDetailFragment :
    BaseViewModelFragment<NSFleetDetailViewModel, NsFragmentFleetDetailBinding>(),
    NSFileUploadCallback {

    override val viewModel: NSFleetDetailViewModel by lazy {
        ViewModelProvider(this)[NSFleetDetailViewModel::class.java]
    }

    private val mapViewModel: NSMapViewModel by lazy {
        ViewModelProvider(this)[NSMapViewModel::class.java]
    }

    private var isFragmentAdded: Boolean = false
    private val brandLogoHelper: BrandLogoHelper = BrandLogoHelper(this, callback = this)

    companion object {
        fun newInstance(bundle: Bundle?) = NSFleetDetailFragment().apply {
            arguments = bundle
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentFleetDetailBinding {
        return NsFragmentFleetDetailBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        baseObserveViewModel(mapViewModel)
        baseObserveViewModel(viewModel)
        observeViewModel()
        setFleetDetailBox()
        pageChangeListener()
    }

    override fun loadFragment(bundle: Bundle?) {
        super.loadFragment(bundle)
        removeAllViews()
        arguments = bundle
        arguments?.let {
            with(viewModel) {
                getFleetDetail(it.getString(NSConstants.FLEET_DETAIL_KEY)) {
                    setFleetDetailFromJson(it)
                }
            }
        }
        initUI()
        setListener()
    }

    override fun observeViewModel() {
        super.observeViewModel()
        with(viewModel) {
            isAllDataUpdateAvailable.observe(
                viewLifecycleOwner
            ) { isAllDataUpdate ->
                if (isAllDataUpdate) {
                    Toast.makeText(
                        requireContext(),
                        stringResource.updatedSuccessfully,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun initUI() {
        binding.apply {
            stringResource.apply {
                setLayoutHeader(
                    layoutHomeHeader,
                    fleetDetail,
                    isBack = true
                )
                tvCreateFleetTitle.text = fleet
                tvSave.text = save
                tvCancel.text = cancel
                brandLogoHelper.initView(activity, ivBrandLogo, tvSizeTitle)
            }
        }
    }

    private fun removeAllViews() {
        binding.apply {
            viewModel.apply {
                isFragmentAdded = false
                tvSizeTitle.gone()
                NSApplication.getInstance().removeAllMapLocalLanguage()
                ivBrandLogo.setImageResource(0)
                layoutName.edtValue.setText("")
                layoutSlogan.edtValue.setText("")
                layoutTags.edtValue.setText("")
                layoutTags.edtValue.clearTag()
                layoutUrl.edtValue.setText("")
                layoutAddress.edtValue.text = ""
                cbFill.isChecked = true
                cbFit.isChecked = false
                fleetModel = null
                urlToUpload = ""
                fleetLogoUpdateRequest = NSFleetLogoUpdateRequest()
                tabService.removeAllTabs()
                fleetPager.invisible()
                manageFocus()
            }
        }
    }

    private fun pageChangeListener() {
        binding.apply {
            viewModel.apply {
                fleetPager.registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        binding.fleetPager.visible()
                        val fragment = mFragmentList[position]
                        if (fragment is NSVehicleFragment) {
                            fragment.loadFragment(arguments)
                        }
                    }
                })
            }
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(binding) {
            with(viewModel) {

                layoutHomeHeader.ivBack.setSafeOnClickListener {
                    onBackPress()
                }

                clCheckFill.setSafeOnClickListener {
                    setFillFit(true)
                }

                clCheckFit.setSafeOnClickListener {
                    setFillFit(false)
                }

                layoutAddress.edtValue.setSafeOnClickListener {
                    setAddress()
                }

                rlSelectAddress.setSafeOnClickListener {
                    rlSelectAddress.gone()
                }

                setEditTextFocusChange(layoutName.edtValue, 0)
                setEditTextFocusChange(layoutSlogan.edtValue, 1)
                setEditTextFocusChange(layoutUrl.edtValue, 2)
                setEditTextFocusChange(layoutTags.edtValue, 3)

                tvSave.setSafeOnClickListener {
                    updateServiceIds()
                }
            }
        }
    }

    private fun setEditTextFocusChange(editText: EditText, position: Int) {
        viewModel.apply {
            editText.onFocusChangeListener =
                View.OnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        updatePosition = position
                        updateData(updatePosition)
                    }
                    manageFocus(name = hasFocus)
                }
        }
    }
    private fun setFillFit(isFill: Boolean) {
        binding.apply {
            viewModel.apply {
                val status = if (isFill) NSConstants.FILL else NSConstants.FIT
                fleetModel?.logoScale = status
                cbFit.isChecked = !isFill
                cbFill.isChecked = isFill
                updateFleetLogoScale(status)
                brandLogoHelper.setBrandLogo(
                    false,
                    fleetModel?.logo ?: "",
                    status == NSConstants.FILL
                )
            }
        }
    }

    private fun setAddress() {
        binding.apply {
            viewModel.apply {
                fleetModel?.apply {
                    NSAddressConfig.showAddressDialog(
                        requireActivity(),
                        mapViewModel,
                        false,
                        binding.viewAddress,
                        addressModel ?: AddressData(),
                        vendorId ?: "",
                        addressId ?: "",
                        serviceIds) { addressData, _ ->
                        addressModel = addressData
                        layoutAddress.edtValue.text = addressData?.addr1
                    }
                }
            }
        }
    }

    private fun setFleetDetailBox() {
        binding.apply {
            viewModel.apply {
                NSServiceConfig.setFleetDetail(
                    requireActivity(),
                    false,
                    layoutName,
                    layoutUrl,
                    layoutAddress,
                    layoutSlogan,
                    layoutTags,
                    tvFill,
                    tvFit,
                    tvEditTitle,
                    rlBrandLogo,
                    rvServiceList
                )

                rlBrandLogo.setOnClickListener {
                    brandLogoHelper.openImagePicker(
                        activity,
                        ivBrandLogo,
                        tvSizeTitle,
                        true,
                        fleetModel?.logoScale.equals(NSConstants.FILL)
                    )
                }
            }
        }
    }

    private fun updateData(position: Int) {
        when (position) {
            0 -> {
                viewModel.updateName()
            }

            1 -> {
                viewModel.updateSlogan()
            }

            2 -> {
                viewModel.updateUrl()
            }

            3 -> {
                val tags = binding.layoutTags.edtValue.text.toString()
                viewModel.fleetModel?.tags = tags.getTagLists()
                viewModel.updateTags()
            }
        }
    }

    private fun setFleetDetailFromJson(fleetModel: FleetData?) {
        viewModel.apply {
            binding.apply {
                fleetModel?.apply {
                    tvFleetCreatedDate.text = getCreatedDate(created)
                    tvFleetActive.status(isActive)
                    switchService.switchEnableDisable(isActive)

                    brandLogoHelper.setBrandLogo(
                        false,
                        logo ?: "",
                        logoScale.equals(NSConstants.FILL)
                    )

                    //Logo Fill Fit
                    val scale = logoScale.equals(NSConstants.FILL)
                    cbFill.isChecked = scale
                    cbFit.isChecked = !scale


                    //Tags
                    layoutTags.edtValue.gravity = Gravity.START
                    layoutTags.edtValue.hint = stringResource.enterTag
                    layoutTags.edtValue.setText(tags.getTags())

                    NSUtilities.setLanguageText(layoutUrl.edtValue, viewModel.fleetModel, true)
                    layoutUrl.edtValue.setText(url)
                    NSUtilities.setLanguageText(
                        layoutName.edtValue,
                        layoutName.rvLanguageTitle,
                        name
                    )
                    NSUtilities.setLanguageText(
                        layoutSlogan.edtValue,
                        layoutSlogan.rvLanguageTitle,
                        slogan
                    )

                    switchService.setOnClickListener {
                        isActive = !isActive
                        switchService.switchEnableDisable(isActive)
                        fleetEnableDisable(vendorId, isActive)
                        tvFleetActive.status(isActive)
                    }

                    layoutAddress.edtValue.ellipsize = TextUtils.TruncateAt.END

                    viewModel.apply {
                        layoutAddress.edtValue.text = addressModel?.addr1 ?: ""
                        //getServiceList(true)
                        setFleetLocationList()
                    }
                }
            }
        }
    }

    private fun setFragmentList() {
        viewModel.apply {
            stringResource.apply {
                mFragmentList.clear()
                mFragmentTitleList.clear()
                mFragmentList.add(NSEmployeeFragment.newInstance(arguments))
                mFragmentList.add(NSVehicleFragment.newInstance(arguments))
                mFragmentTitleList.add(employee)
                mFragmentTitleList.add(vehicle)
            }
            binding.fleetPager.invisible()
            setupViewPager(requireActivity(), binding.fleetPager)
        }
    }

    private fun setupViewPager(activity: FragmentActivity, viewPager: ViewPager2) {
        try {
            viewModel.apply {
                binding.fleetPager.setPager(activity, mFragmentList)
                TabLayoutMediator(
                    binding.tabService, viewPager
                ) { tab, position -> tab.text = mFragmentTitleList[position] }.attach()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Set fleet location list data
     *
     * @param fleetData when data available it's true
     */
    private fun setFleetLocationList() {
        if (!isFragmentAdded) {
            isFragmentAdded = true
            setFragmentList()
        }
    }

    override fun onFileUrl(url: String, width: Int, height: Int) {
        viewModel.apply {
            fleetLogoUpdateRequest.logoHeight = height
            fleetLogoUpdateRequest.logoWidth = width
            fleetLogoUpdateRequest.logo = url
            fleetModel?.logo = url
            urlToUpload = url
            updateFleetLogo()
        }
    }
}