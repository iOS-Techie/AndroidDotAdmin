package com.nyotek.dot.admin.ui.fleets.vehicle.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.BrandLogoHelper
import com.nyotek.dot.admin.common.MapBoxView
import com.nyotek.dot.admin.common.NSAddress
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.OnTextUpdateHelper
import com.nyotek.dot.admin.common.callbacks.NSFileUploadCallback
import com.nyotek.dot.admin.common.callbacks.NSVehicleEditCallback
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.getLngValue
import com.nyotek.dot.admin.common.utils.glideCenter
import com.nyotek.dot.admin.common.utils.gone
import com.nyotek.dot.admin.common.utils.isValidList
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
import com.nyotek.dot.admin.ui.fleets.employee.NSEmployeeViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NSVehicleDetailFragment :
    BaseViewModelFragment<NSVehicleDetailViewModel, NsFragmentVehicleDetailBinding>(),
    NSFileUploadCallback {

    override val viewModel: NSVehicleDetailViewModel by lazy {
        ViewModelProvider(this)[NSVehicleDetailViewModel::class.java]
    }

    private val employeeViewModel: NSEmployeeViewModel by lazy {
        ViewModelProvider(this)[NSEmployeeViewModel::class.java]
    }

    private var mapBoxView: MapBoxView? = null
    private val brandLogoHelper: BrandLogoHelper = BrandLogoHelper(this, callback = this)

    companion object {

        private var vehCallback: NSVehicleEditCallback? = null
        fun newInstance(bundle: Bundle?, callback: NSVehicleEditCallback? = null) = NSVehicleDetailFragment().apply {
            arguments = bundle
            vehCallback = callback
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
                getVehicleDetail(it.getString(NSConstants.VEHICLE_DETAIL_KEY), it.getString(NSConstants.FLEET_DETAIL_KEY))
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
    }

    fun resetFragment() {
        viewModel.apply {
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
                    layoutLogo.ivBrandLogo.glideCenter(url = vehicleDataItem?.vehicleImg)
                    clVehicleItem.gone()
                    layoutManufacturer.edtValue.setText(vehicleDataItem?.manufacturer)
                    layoutManufacturer.edtValue.isEnabled = false
                    layoutManufacturerYear.edtValue.setText(vehicleDataItem?.manufacturingYear)
                    layoutManufacturerYear.edtValue.isEnabled = false
                    layoutModel.edtValue.setText(vehicleDataItem?.model)
                    layoutModel.edtValue.isEnabled = false
                    layoutRegistrationNo.edtValue.setText(vehicleDataItem?.registrationNo)
                    layoutRegistrationNo.edtValue.isEnabled = false
                    layoutLoadCapacity.edtValue.setText(vehicleDataItem?.loadCapacity)
                    layoutLoadCapacity.edtValue.isEnabled = false
                    layoutNotes.edtValue.setText(vehicleDataItem?.additionalNote)

                    switchService.isActivated = vehicleDataItem?.isActive == true

                    //Get Capability List
                    getCapabilitiesList(
                        isShowError = false,
                        isCapabilityCheck = true
                    ) {
                        setCapabilityList(it)
                    }

                    if (isApiCall) {
                        //Get Vehicle Detail
                        viewModel.apply {
                            vehicleDataItem?.id?.let { getVehicleDetail(it, true) { vehicleData ->
                                setVehicleDetail(vehicleData)
                            } }
                        }
                    }
                }
            }
        }
    }

    private fun setCapabilityList(capabilityList: MutableList<CapabilitiesDataItem>) {
        binding.apply {
            viewModel.apply {
                NSUtilities.setCapability(
                    activity,
                    true,
                    layoutCapability,
                    capabilityList,
                    vehicleDataItem
                ) {
                    updateCapabilityParameter(it, capabilityList, vehCallback)
                }
            }
        }
    }

    private fun setVehicleDetail(response: VehicleDetailData) {
        binding.apply {
            viewModel.apply {
                driverId = response.driverId
                //Get Employee List with Role
                employeeViewModel.getEmployeeWithRole(
                    false,
                    fleetModel?.serviceIds ?: arrayListOf(),
                    vehicleDataItem?.refId
                )
            }
        }
    }

    /**
     * View created
     */
    private fun viewCreated() {
        viewModel.apply {
            baseObserveViewModel(viewModel)
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
                        vehicleDataItem?.let { it1 -> vehCallback?.onVehicle(it1) }
                        vehicleEnableDisable(id, isActive)
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
                            deleteVehicleToDriver()
                            //assignVehicleToDriver(true, arrayListOf())
                        }
                    }
                }

                OnTextUpdateHelper(
                    layoutNotes.edtValue,
                    vehicleDataItem?.additionalNote ?: "") {
                    val enteredNotes: String = layoutNotes.edtValue.text.toString()
                    val additionNotes: String = vehicleDataItem?.additionalNote?:""

                    if (additionNotes != enteredNotes) {
                        viewModel.updateNotes(enteredNotes)
                        vehicleDataItem?.additionalNote = it
                        vehicleDataItem?.let { it1 -> vehCallback?.onVehicle(it1) }
                    }
                }
            }
        }
    }

    /***
     * Set Update Driver Spinner
     */
    private fun setUpdateDriverList(employeeList: MutableList<EmployeeDataItem>) {
        binding.apply {
            val nameList: MutableList<String> = employeeList.map { it.userId ?: "" }.toMutableList()
            var idList: MutableList<String> = employeeList.map { it.userId ?: "" }.toMutableList()

            val spinnerList = SpinnerData(idList, nameList)
            spinner.spinnerAppSelect.setPlaceholderAdapter(
                spinnerList,
                activity,
                viewModel.driverId,
                isHideFirstPosition = true,
                placeholderName = stringResource.selectDriver
            ) { selectedId ->
                if (selectedId != viewModel.driverId && selectedId?.isNotEmpty() == true) {
                    viewModel.apply {
                        viewModel.driverId = selectedId
                        assignVehicleToDriver(false, vehicleDataItem?.capabilities?: arrayListOf())
                    }
                }
            }

            val empResponse = employeeList.find { it.userId == viewModel.driverId }
            tvUserTitle.text = viewModel.driverId ?: ""
            tvStatus.text = getLngValue(employeeViewModel.jobTitleMap[empResponse?.titleId]?.name)

            idList = employeeList.map { it.userId ?: "" }.toMutableList()
            val spinnerPosition = idList.indexOf(viewModel.driverId)
            val isVisible = spinnerPosition != -1
            clVehicleItem.setVisibility(isVisible)
            viewLineTextSub.setVisibilityIn(isVisible)

            if (viewModel.driverId?.isNotEmpty() == true && idList.isValidList() && spinnerPosition >= 0) {
                employeeViewModel.getDriverLocation(viewModel.driverId) {
                    mapBoxView?.initMapView(requireContext(), binding.mapFragmentVehicle, it)
                }
            } else {
                mapBoxView?.initMapView(requireContext(), binding.mapFragmentVehicle, FleetDataItem())
            }
        }
    }

    /***
     * Assign Vehicle To Selected Driver
     */
    private fun assignVehicleToDriver(isFromDelete: Boolean, capabilities: MutableList<String>) {
        viewModel.apply {
            driverId?.let {
                assignVehicle(isFromDelete,
                    it, capabilities = capabilities, vehicleId = if (isFromDelete) "" else vehicleDataItem?.id
                ) {
                    if (isFromDelete) {
                        viewModel.driverId = ""
                    }
                    setUpdateDriverList(employeeViewModel.employeeList)
                }
            }
        }
    }

    private fun deleteVehicleToDriver() {
        viewModel.apply {
            driverId?.let {
                deleteVehicle(it
                ) {
                    viewModel.driverId = ""
                    setUpdateDriverList(employeeViewModel.employeeList)
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
            vehicleDataItem?.vehicleImg = url
            vehicleDataItem?.let { it1 -> vehCallback?.onVehicle(it1) }
            updateVehicleImage(url)
        }
    }
}