package com.nyotek.dot.admin.ui.fleets.vehicle.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.BrandLogoHelper
import com.nyotek.dot.admin.common.MapBoxView
import com.nyotek.dot.admin.common.NSAddress
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.OnTextUpdateHelper
import com.nyotek.dot.admin.common.callbacks.NSCapabilityCallback
import com.nyotek.dot.admin.common.callbacks.NSCapabilityListCallback
import com.nyotek.dot.admin.common.callbacks.NSFileUploadCallback
import com.nyotek.dot.admin.common.callbacks.NSItemSelectCallback
import com.nyotek.dot.admin.common.callbacks.NSOnTextChangeCallback
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.getLngValue
import com.nyotek.dot.admin.common.utils.status
import com.nyotek.dot.admin.common.utils.switchEnableDisable
import com.nyotek.dot.admin.common.utils.visible
import com.nyotek.dot.admin.databinding.NsFragmentVehicleDetailBinding
import com.nyotek.dot.admin.repository.network.responses.CapabilitiesDataItem
import com.nyotek.dot.admin.repository.network.responses.EmployeeDataItem
import com.nyotek.dot.admin.repository.network.responses.FleetDataItem
import com.nyotek.dot.admin.repository.network.responses.VehicleDetailData
import com.nyotek.dot.admin.ui.capabilities.NSCapabilitiesViewModel
import com.nyotek.dot.admin.ui.fleets.employee.NSEmployeeViewModel
import com.nyotek.dot.admin.ui.fleets.vehicle.NSVehicleViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NSVehicleDetailFragment :
    BaseViewModelFragment<NSVehicleDetailViewModel, NsFragmentVehicleDetailBinding>(),
    NSFileUploadCallback {

    override val viewModel: NSVehicleDetailViewModel by lazy {
        ViewModelProvider(this)[NSVehicleDetailViewModel::class.java]
    }

    private val vehicleViewModel: NSVehicleViewModel by lazy {
        ViewModelProvider(this)[NSVehicleViewModel::class.java]
    }
    private val employeeViewModel: NSEmployeeViewModel by lazy {
        ViewModelProvider(this)[NSEmployeeViewModel::class.java]
    }
    private val capabilitiesViewModel: NSCapabilitiesViewModel by lazy {
        ViewModelProvider(this)[NSCapabilitiesViewModel::class.java]
    }

    private var mapBoxView: MapBoxView? = null
    private val brandLogoHelper: BrandLogoHelper = BrandLogoHelper(this, callback = this)

    companion object {
        fun newInstance(bundle: Bundle?) = NSVehicleDetailFragment().apply {
            arguments = bundle
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentVehicleDetailBinding {
        mapBoxView = MapBoxView(requireContext())
        return NsFragmentVehicleDetailBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        mapBoxView?.initMapView(requireContext(), binding.mapFragmentVehicle, FleetDataItem())
        binding.clMap.visible()
        viewCreated()
    }

    override fun loadFragment(bundle: Bundle?) {
        super.loadFragment(bundle)
        arguments = bundle
        arguments?.let {
            with(viewModel) {
                strVehicleDetail = it.getString(NSConstants.VEHICLE_DETAIL_KEY)
                fleetDetail = it.getString(NSConstants.FLEET_DETAIL_KEY)
                getVehicleDetail()
                initCreateVendor()
                setListener()
            }
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        with(employeeViewModel) {
            isEmployeeListAvailable.observe(
                viewLifecycleOwner
            ) { list ->
                setUpdateDriverList(list)
            }
        }

        with(viewModel) {
            isVehicleDetailAvailable.observe(
                viewLifecycleOwner
            ) { response ->
                setVehicleDetail(response)
            }
        }
    }

    fun resetFragment() {
        viewModel.apply {
            strVehicleDetail = ""
            vehicleDataItem = null
            initCreateVendor(false)
        }
    }

    private fun initCreateVendor(isApiCall: Boolean = true) {
        binding.apply {
            viewModel.apply {
                stringResource.apply {
                    layoutHomeHeader.tvHeaderTitle.text = vehicleDetails
                    tvVehicleTitle.text = vehicleDetails
                    tvVehicleActive.text =
                        if (vehicleDataItem?.isActive == true) active else inActive
                    layoutManufacturer.tvCommonTitle.text = manufacturer
                    layoutManufacturerYear.tvCommonTitle.text = manufacturerYear
                    layoutLoadCapacity.tvCommonTitle.text = loadCapacity
                    layoutModel.tvCommonTitle.text = model
                    layoutRegistrationNo.tvCommonTitle.text = vehicleRegistrationNo
                    layoutCapability.tvCommonTitle.text = capability
                    layoutNotes.tvCommonTitle.text = additionalNote
                    spinner.tvCommonTitle.text = updateDriver
                    Glide.with(activity.applicationContext).load(vehicleDataItem?.vehicleImg)
                        .into(layoutLogo.ivBrandLogo)

                    layoutManufacturer.edtValue.setText(vehicleDataItem?.manufacturer)
                    layoutManufacturerYear.edtValue.setText(vehicleDataItem?.manufacturingYear)
                    layoutModel.edtValue.setText(vehicleDataItem?.model)
                    layoutRegistrationNo.edtValue.setText(vehicleDataItem?.registrationNo)
                    layoutLoadCapacity.edtValue.setText(vehicleDataItem?.loadCapacity)
                    layoutNotes.edtValue.setText(vehicleDataItem?.additionalNote)

                    switchService.isActivated = vehicleDataItem?.isActive == true

                    capabilitiesViewModel.getCapabilitiesList(
                        false,
                        isCapabilityAvailableCheck = true,
                        isShowError = false,
                        callback = object : NSCapabilityCallback {
                            override fun onCapability(capabilities: MutableList<CapabilitiesDataItem>) {
                                setCapabilityList(capabilities)
                            }
                        })

                    if (isApiCall) {
                        //Get Vehicle Detail
                        viewModel.apply {
                            vehicleDataItem?.id?.let { getVehicleDetail(it, true) }
                        }
                    }
                }
            }
        }
    }

    private fun setCapabilityList(capabilities: MutableList<CapabilitiesDataItem>) {
        binding.apply {
            viewModel.apply {
                NSUtilities.setCapability(
                    activity,
                    true,
                    layoutCapability,
                    capabilities,
                    vehicleDataItem,
                    object :
                        NSCapabilityListCallback {
                        override fun onCapability(capabilities: MutableList<String>) {
                            if (vehicleDataItem?.capabilities?.equals(capabilities) != true) {
                                vehicleDataItem?.capabilities = capabilities
                                viewModel.updateCapability(capabilities)
                            }
                        }
                    })
            }
        }
    }

    private fun setVehicleDetail(response: VehicleDetailData) {
        binding.apply {
            viewModel.apply {
                driverId = response.driverId
                employeeViewModel.getEmployeeWithRole(
                    false,
                    fleetModel?.serviceIds ?: arrayListOf(),
                    vehicleDataItem?.refId
                )
            }
        }
    }

    private fun setUpdateDriverList(employeeList: MutableList<EmployeeDataItem>) {
        binding.apply {
            val nameList: MutableList<String> = arrayListOf()
            val idList: MutableList<String> = arrayListOf()
            nameList.add(stringResource.selectDriver)
            idList.add("")
            nameList.addAll(employeeList.map { it.userId ?: "" }.toMutableList())
            idList.addAll(employeeList.map { it.userId ?: "" }.toMutableList())

            NSUtilities.setSpinner(activity, spinner.spinnerAppSelect, nameList, idList, object :
                NSItemSelectCallback {
                override fun onItemSelect(selectedId: String) {
                    if (selectedId != viewModel.driverId) {
                        viewModel.driverId = selectedId
                        viewModel.assignVehicle(selectedId)
                    }
                }
            }, true)

            val spinnerPosition = idList.indexOf(viewModel.driverId)
            if (spinnerPosition != -1) {
                spinner.spinnerAppSelect.setSelection(spinnerPosition)
            }

            val empResponse = employeeList.find { it.userId == viewModel.driverId }
            tvUserTitle.text = viewModel.driverId
            tvStatus.text = getLngValue(employeeViewModel.jobTitleMap[empResponse?.titleId]?.name)

        }
    }


    /**
     * View created
     */
    private fun viewCreated() {
        viewModel.apply {
            baseObserveViewModel(viewModel)
            baseObserveViewModel(capabilitiesViewModel)
            observeViewModel()
            // getVendorList(true)
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
        binding.apply {
            viewModel.apply {
                vehicleDataItem?.apply {
                    switchService.setOnClickListener {
                        isActive = !isActive
                        tvVehicleActive.status(isActive)
                        switchService.switchEnableDisable(isActive)
                        vehicleViewModel.vehicleEnableDisable(
                            id,
                            isActive,
                            true
                        )
                    }
                }

                layoutHomeHeader.ivBack.setOnClickListener {
                    onBackPress()
                }

                layoutLogo.clBrandLogo.setOnClickListener {
                    brandLogoHelper.openImagePicker(
                        activity, layoutLogo.ivBrandLogo, null, true,
                        isFill = true
                    )
                }

                OnTextUpdateHelper(
                    layoutNotes.edtValue,
                    vehicleDataItem?.additionalNote ?: "",
                    object : NSOnTextChangeCallback {
                        override fun afterTextChanged(text: String) {
                            vehicleDataItem?.additionalNote = text
                            viewModel.updateNotes(layoutNotes.edtValue.text.toString())
                        }
                    })
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getCurrentLocation(event: NSAddress) {
        val address = event.addresses[0].getAddressLine(0).toString()
        if (address.isNotEmpty()) {
            event.locationResult.lastLocation?.apply {
                mapBoxView?.setCurrentLatLong(latitude, longitude)
            }
        }
    }

    override fun onFileUrl(url: String, width: Int, height: Int) {
        viewModel.apply {
            updateVehicleImage(url)
        }
    }
}