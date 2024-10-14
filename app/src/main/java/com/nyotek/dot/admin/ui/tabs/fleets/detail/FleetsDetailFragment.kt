package com.nyotek.dot.admin.ui.tabs.fleets.detail

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.nyotek.dot.admin.base.BaseFragment
import com.nyotek.dot.admin.common.BrandLogoHelper
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSServiceConfig
import com.nyotek.dot.admin.common.NSUtilities
import com.nyotek.dot.admin.common.NSViewPagerAdapter
import com.nyotek.dot.admin.common.callbacks.NSFileUploadCallback
import com.nyotek.dot.admin.common.extension.getTagLists
import com.nyotek.dot.admin.common.extension.gone
import com.nyotek.dot.admin.common.extension.invisible
import com.nyotek.dot.admin.common.extension.navigateSafeNew
import com.nyotek.dot.admin.common.extension.rotation
import com.nyotek.dot.admin.common.extension.setSafeOnClickListener
import com.nyotek.dot.admin.common.extension.showToast
import com.nyotek.dot.admin.common.extension.status
import com.nyotek.dot.admin.common.extension.switchEnableDisable
import com.nyotek.dot.admin.common.extension.visible
import com.nyotek.dot.admin.common.utils.NSAddressConfig
import com.nyotek.dot.admin.databinding.NsFragmentFleetDetailBinding
import com.nyotek.dot.admin.models.responses.AddressData
import com.nyotek.dot.admin.models.responses.FleetData
import com.nyotek.dot.admin.ui.tabs.fleets.employee.EmployeeFragmentDirections
import com.nyotek.dot.admin.ui.tabs.fleets.employee.NSEmployeeFragment
import com.nyotek.dot.admin.ui.tabs.fleets.map.NSMapViewModel
import com.nyotek.dot.admin.ui.tabs.fleets.vehicle.VehicleFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FleetsDetailFragment : BaseFragment<NsFragmentFleetDetailBinding>(), NSFileUploadCallback {

    private val viewModel by viewModels<FleetsDetailViewModel>()
    private val mapViewModel by viewModels<NSMapViewModel>()
    private val brandLogoHelper: BrandLogoHelper = BrandLogoHelper(this, callback = this)
    private lateinit var themeUI: FleetDetailUI
    private var pager: NSViewPagerAdapter? = null
    private var employeeFragment: NSEmployeeFragment? = null
    private var vehicleFragment: VehicleFragment? = null
    private var serviceHorizontalAdapter: NSFleetServiceListRecycleAdapter? = null
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackPressedHandler()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentFleetDetailBinding {
        return NsFragmentFleetDetailBinding.inflate(inflater, container, false)
    }

    override fun observeViewModel() {
        super.observeViewModel()
        observeBaseViewModel(mapViewModel)
        observeBaseViewModel(viewModel)

        viewModel.apply {
            isAllDataUpdateAvailable.observe(
                viewLifecycleOwner
            ) { isAllDataUpdate ->
                if (isAllDataUpdate) {
                    showToast(requireContext(), stringResource.updatedSuccessfully)
                }
            }

            fleetDataObserve.observe(
                viewLifecycleOwner
            ) { data ->
                if (pager == null) {
                    setFleetDetailFromJson(data)
                } else {
                    updateTabFragmentData()
                }
            }
        }
    }

    override fun setupViews() {
        super.setupViews()
        themeUI = FleetDetailUI(binding, viewModel.colorResources)
        if (pager == null) {
            arguments?.let {
                viewModel.getFleetDetail(it.getString(NSConstants.FLEET_DETAIL_KEY))
            }
        } else {
            updateTabFragmentData()
        }
        setFleetDetailBox()
        initUI()
        setListener()

    }

    private fun initUI() {
        binding.apply {
            stringResource.apply {
                setLayoutHeader(layoutHomeHeader, fleetDetail, isBack = true)
                brandLogoHelper.initView(activity, ivBrandLogo, tvSizeTitle)
                switchService.rotation(viewModel.languageConfig.isLanguageRtl())
            }
        }
    }

    private fun setFleetDetailBox() {
        binding.apply {
            viewModel.apply {
                serviceHorizontalAdapter = NSServiceConfig.setFleetDetail(
                    requireActivity(),
                    layoutName,
                    layoutUrl,
                    layoutAddress,
                    layoutSlogan,
                    layoutTags,
                    tvFill,
                    tvFit,
                    tvEditTitle,
                    rlBrandLogo,
                    rvServiceList, colorResources, viewModel
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

    private fun pageChangeListener() {
        binding.apply {
            viewModel.apply {
                fleetPager.registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        binding.fleetPager.visible()
                        val fragment = mFragmentList[position]
                        if (fragment is VehicleFragment) {
                            fragment.loadFragment(arguments)
                        }
                    }
                })
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
                    
                    /*val baseServiceList = viewModel.colorResources.themeHelper.getServiceResponse()?.data?: arrayListOf()
                    baseServiceList.forEach { service ->
                        if (fleetModel.serviceIds.contains(service.serviceId)) {
                            service.isSelected = true
                        }
                    }
                    serviceHorizontalAdapter?.setData(baseServiceList)*/
                    tvEditTitle.text = stringResource.selectImage

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
                   /* layoutTags.edtValue.apply {
                        gravity = Gravity.START
                        hint = stringResource.enterTag
                        setText(tags.getTags())
                    }*/

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
                        if (pager == null) {
                            setFragmentList()
                        } else {
                            updateTabFragmentData()
                        }
                    }
                }
            }
        }
    }

    override fun setListener() {
        super.setListener()
        with(binding) {
            with(viewModel) {

                layoutHomeHeader.ivBack.setSafeOnClickListener {
                    findNavController().popBackStack()
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
                //setEditTextFocusChange(layoutTags.edtValue, 3)

                tvSave.setSafeOnClickListener {
                    /*val list = serviceHorizontalAdapter?.getData()?.filter { it.isSelected }?.mapNotNull { it.serviceId }?: arrayListOf()
                    val serviceList: List<String> = list
                    fleetModel?.serviceIds?.clear()
                    fleetModel?.serviceIds?.addAll(serviceList)*/
                    
                    updateServiceIds()
                }
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

    private fun setEditTextFocusChange(editText: EditText, position: Int) {
        viewModel.apply {
            editText.onFocusChangeListener =
                View.OnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        focusChangeOnUpdateData(position)
                    }
                    manageFocus(name = hasFocus)
                }
        }
    }

    private fun focusChangeOnUpdateData(position: Int) {
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

    private fun setFragmentList() {
        viewModel.apply {
            stringResource.apply {
                mFragmentList.clear()
                mFragmentTitleList.clear()
                employeeFragment = NSEmployeeFragment.newInstance(arguments) {
                    findNavController().navigateSafeNew(EmployeeFragmentDirections.actionFleetDetailToDriverDetail(it))
                }
                mFragmentList.add(employeeFragment!!)
                vehicleFragment = VehicleFragment.newInstance(arguments) {
                    findNavController().navigateSafeNew(EmployeeFragmentDirections.actionFleetDetailToVehicleDetail(it))
                }
                mFragmentList.add(vehicleFragment!!)
                mFragmentTitleList.add(employee)
                mFragmentTitleList.add(vehicle)
            }
            binding.fleetPager.invisible()
            setupViewPager(requireActivity(), binding.fleetPager)
            pageChangeListener()
        }
    }

    private fun setupViewPager(activity: FragmentActivity, viewPager: ViewPager2) {
        try {
            viewModel.apply {
                pager = NSViewPagerAdapter(activity)
                pager?.setFragment(mFragmentList)
                binding.fleetPager.adapter = pager
                binding.fleetPager.isUserInputEnabled = false
                binding.fleetPager.offscreenPageLimit = mFragmentList.size
                binding.fleetPager.isSaveEnabled = false
                TabLayoutMediator(
                    binding.tabService, viewPager
                ) { tab, position -> tab.text = mFragmentTitleList[position] }.attach()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateTabFragmentData() {
        if (binding.fleetPager.currentItem == 0) {
            if (employeeFragment != null) {
                employeeFragment?.updateChangedDetail()
            }
        } else {
            if (vehicleFragment != null) {
                vehicleFragment?.updateChangedDetail()
            }
        }
    }

    override fun onFileUrl(url: String, width: Int, height: Int) {
        viewModel.apply {
            fleetLogoUpdateRequest.logoHeight = height
            fleetLogoUpdateRequest.logoWidth = width
            fleetLogoUpdateRequest.logo = url
            fleetModel?.logo = url
            urlToUpload = url
            binding.tvEditTitle.text = if (url.isEmpty()) stringResource.selectImage else stringResource.edit
            updateFleetLogo()
        }
    }

    private fun setBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })
    }
}