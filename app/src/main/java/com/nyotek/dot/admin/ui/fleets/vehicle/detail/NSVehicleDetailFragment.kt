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
import com.nyotek.dot.admin.common.callbacks.NSFileUploadCallback
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.getLngValue
import com.nyotek.dot.admin.common.utils.gone
import com.nyotek.dot.admin.common.utils.setPlaceholderAdapter
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
import com.nyotek.dot.admin.common.utils.setVisibility
import com.nyotek.dot.admin.common.utils.setVisibilityIn
import com.nyotek.dot.admin.common.utils.status
import com.nyotek.dot.admin.common.utils.switchEnableDisable
import com.nyotek.dot.admin.common.utils.visible
import com.nyotek.dot.admin.databinding.NsFragmentVehicleDetailBinding
import com.nyotek.dot.admin.repository.network.responses.CapabilitiesDataItem
import com.nyotek.dot.admin.repository.network.responses.EmployeeDataItem
import com.nyotek.dot.admin.repository.network.responses.FleetDataItem
import com.nyotek.dot.admin.repository.network.responses.SpinnerData
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
                employeeList = list
                setUpdateDriverList(list)
            }
        }

        with(viewModel) {
            isVehicleDetailAvailable.observe(
                viewLifecycleOwner
            ) { response ->
                setVehicleDetail(response)
            }

            isVehicleAssign.observe(
                viewLifecycleOwner
            ) {
                if (it) {
                    setUpdateDriverList(employeeViewModel.employeeList)
                    //vehicleViewModel.getDriverVehicleDetail(employeeDataItem?.vehicleId )
                }
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
                    tvVehicleActive.status(vehicleDataItem?.isActive == true)
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
                    clVehicleItem.gone()
                    layoutManufacturer.edtValue.setText(vehicleDataItem?.manufacturer)
                    layoutManufacturerYear.edtValue.setText(vehicleDataItem?.manufacturingYear)
                    layoutModel.edtValue.setText(vehicleDataItem?.model)
                    layoutRegistrationNo.edtValue.setText(vehicleDataItem?.registrationNo)
                    layoutLoadCapacity.edtValue.setText(vehicleDataItem?.loadCapacity)
                    layoutNotes.edtValue.setText(vehicleDataItem?.additionalNote)

                    switchService.isActivated = vehicleDataItem?.isActive == true

                    capabilitiesViewModel.getCapabilities(
                        false,
                        isCapabilityCheck = true,
                        isShowError = false
                    ) {
                        setCapabilityList(it)
                    }

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
                    vehicleDataItem
                ) {
                    if (vehicleDataItem?.capabilities?.equals(capabilities) != true) {
                        vehicleDataItem?.capabilities = it
                        viewModel.updateCapability(it)
                    }
                }
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
            val nameList: MutableList<String> = employeeList.map { it.userId ?: "" }.toMutableList()
            val idList: MutableList<String> = employeeList.map { it.userId ?: "" }.toMutableList()
            val spinnerList = SpinnerData(idList, nameList)
            spinner.spinnerAppSelect.setPlaceholderAdapter(
                spinnerList,
                activity,
                viewModel.driverId,
                isHideFirstPosition = true,
                placeholderName = stringResource.selectDriver
            ) { selectedId ->
                if (selectedId != viewModel.driverId && selectedId?.isNotEmpty() == true) {
                    viewModel.driverId = selectedId
                    viewModel.assignVehicle(selectedId)
                }
            }

            val empResponse = employeeList.find { it.userId == viewModel.driverId }
            tvUserTitle.text = viewModel.driverId ?: ""
            tvStatus.text = getLngValue(employeeViewModel.jobTitleMap[empResponse?.titleId]?.name)

            val spinnerPosition = idList.indexOf(viewModel.driverId)
            val isVisible = spinnerPosition != -1
            clVehicleItem.setVisibility(isVisible)
            viewLineTextSub.setVisibilityIn(isVisible)
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

                ivDelete.setSafeOnClickListener {
                    showCommonDialog(
                        title = "",
                        message = stringResource.doYouWantToDelete,
                        alertKey = NSConstants.KEY_ALERT_EMPLOYEE_VEHICLE_DELETE_DETAIL,
                        positiveButton = stringResource.ok,
                        negativeButton = stringResource.cancel
                    ) { isCancel ->
                        if (!isCancel) {
                            driverId?.let {
                                assignVehicle(
                                    it, capabilities = arrayListOf()
                                )
                            }
                        }
                    }
                }

                OnTextUpdateHelper(
                    layoutNotes.edtValue,
                    vehicleDataItem?.additionalNote ?: "") {
                    vehicleDataItem?.additionalNote = it
                    viewModel.updateNotes(layoutNotes.edtValue.text.toString())
                }
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