package com.nyotek.dot.admin.ui.fleets.vehicle.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.nyotek.dot.admin.common.BrandLogoHelper
import com.nyotek.dot.admin.common.MapBoxView
import com.nyotek.dot.admin.common.NSAddress
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSFragment
import com.nyotek.dot.admin.common.OnTextUpdateHelper
import com.nyotek.dot.admin.common.callbacks.NSCapabilityCallback
import com.nyotek.dot.admin.common.callbacks.NSCapabilityListCallback
import com.nyotek.dot.admin.common.callbacks.NSFileUploadCallback
import com.nyotek.dot.admin.common.callbacks.NSItemSelectCallback
import com.nyotek.dot.admin.common.callbacks.NSOnTextChangeCallback
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.getLngValue
import com.nyotek.dot.admin.common.utils.visible
import com.nyotek.dot.admin.databinding.LayoutCreateVehicleBinding
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


class NSVehicleDetailFragment : NSFragment(), NSFileUploadCallback {
    private val vehicleDetailViewModel: NSVehicleDetailViewModel by lazy {
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
    private var _binding: NsFragmentVehicleDetailBinding? = null
    private val binding get() = _binding!!
    private var mapBoxView: MapBoxView? = null
    private var layoutCreateVehicle: LayoutCreateVehicleBinding? = null
    private var isFragmentLoad = false
    private val brandLogoHelper: BrandLogoHelper = BrandLogoHelper(this, callback = this)

    companion object {
        fun newInstance(bundle: Bundle?) = NSVehicleDetailFragment().apply {
            arguments = bundle
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = NsFragmentVehicleDetailBinding.inflate(inflater, container, false)
        mapBoxView = MapBoxView(requireContext())
        mapBoxView?.initMapView(requireContext(), binding.mapFragmentVehicle, FleetDataItem())
        binding.clMap.visible()
        viewCreated()

        return binding.root
    }

    fun loadFragment(bundle: Bundle?) {
        arguments = bundle
        arguments?.let {
            with(vehicleDetailViewModel) {
                strVehicleDetail = it.getString(NSConstants.VEHICLE_DETAIL_KEY)
                fleetDetail = it.getString(NSConstants.FLEET_DETAIL_KEY)
                getVehicleDetail()
                initCreateVendor()
                setListener()
            }
        }
    }

    fun resetFragment() {
        vehicleDetailViewModel.apply {
            strVehicleDetail = ""
            vehicleDataItem = null
            initCreateVendor(false)
        }
    }

    private fun initCreateVendor(isApiCall: Boolean = true) {
        binding.apply {
            vehicleDetailViewModel.apply {
                stringResource.apply {
                    layoutHomeHeader.tvHeaderTitle.text = vehicleDetails
                    tvVehicleTitle.text = vehicleDetails
                    tvVehicleActive.text = if (vehicleDataItem?.isActive == true) active else inActive
                    layoutManufacturer.tvCommonTitle.text = manufacturer
                    layoutManufacturerYear.tvCommonTitle.text = manufacturerYear
                    layoutLoadCapacity.tvCommonTitle.text = loadCapacity
                    layoutModel.tvCommonTitle.text = model
                    layoutRegistrationNo.tvCommonTitle.text = vehicleRegistrationNo
                    layoutCapability.tvCommonTitle.text = capability
                    layoutNotes.tvCommonTitle.text = additionalNote
                    spinner.tvCommonTitle.text = updateDriver
                    Glide.with(activity.applicationContext).load(vehicleDataItem?.vehicleImg).into(layoutLogo.ivBrandLogo)

                    layoutManufacturer.edtValue.setText(vehicleDataItem?.manufacturer)
                    layoutManufacturerYear.edtValue.setText(vehicleDataItem?.manufacturingYear)
                    layoutModel.edtValue.setText(vehicleDataItem?.model)
                    layoutRegistrationNo.edtValue.setText(vehicleDataItem?.registrationNo)
                    layoutLoadCapacity.edtValue.setText(vehicleDataItem?.loadCapacity)
                    layoutNotes.edtValue.setText(vehicleDataItem?.additionalNote)

                    switchService.isActivated = vehicleDataItem?.isActive == true

                    capabilitiesViewModel.getCapabilitiesList(false, isCapabilityAvailableCheck = true, isShowError = false, callback = object : NSCapabilityCallback {
                        override fun onCapability(capabilities: MutableList<CapabilitiesDataItem>) {
                            NSUtilities.setCapability(activity, true, layoutCapability, capabilities, vehicleDataItem, object :
                                NSCapabilityListCallback {
                                override fun onCapability(capabilities: MutableList<String>) {
                                    if (vehicleDataItem?.capabilities?.equals(capabilities) != true) {
                                        vehicleDataItem?.capabilities = capabilities
                                        vehicleDetailViewModel.updateCapability(capabilities)
                                    }
                                }
                            })
                        }
                    })

                    if (isApiCall) {
                        //Get Vehicle Detail
                        vehicleDetailViewModel.apply {
                            vehicleDataItem?.id?.let { getVehicleDetail(it, true) }
                        }
                    }
                }
            }
        }
    }

    private fun setVehicleDetail(response: VehicleDetailData) {
        binding.apply {
            vehicleDetailViewModel.apply {
                driverId = response.driverId
                employeeViewModel.getEmployeeWithRole(false, fleetModel?.serviceIds?: arrayListOf(), vehicleDataItem?.refId)
            }
        }
    }

    private fun setUpdateDriverList(employeeList: MutableList<EmployeeDataItem>) {
        binding.apply {
            val nameList : MutableList<String> = arrayListOf()
            val idList : MutableList<String> = arrayListOf()
            nameList.add(stringResource.selectDriver)
            idList.add("")
            nameList.addAll(employeeList.map { it.userId?:"" }.toMutableList())
            idList.addAll(employeeList.map { it.userId?:"" }.toMutableList())

            NSUtilities.setSpinner(activity, spinner.spinnerAppSelect, nameList, idList, object :
                NSItemSelectCallback {
                override fun onItemSelect(selectedId: String) {
                    if (selectedId != vehicleDetailViewModel.driverId) {
                        vehicleDetailViewModel.driverId = selectedId
                        vehicleDetailViewModel.assignVehicle(selectedId)
                    }
                }
            }, true)

            val spinnerPosition = idList.indexOf(vehicleDetailViewModel.driverId)
            if (spinnerPosition != -1) {
                spinner.spinnerAppSelect.setSelection(spinnerPosition)
            }

            val empResponse = employeeList.find { it.userId == vehicleDetailViewModel.driverId }
            tvUserTitle.text = vehicleDetailViewModel.driverId
            tvStatus.text = getLngValue(employeeViewModel.jobTitleMap[empResponse?.titleId]?.name)

        }
    }



    /**
     * View created
     */
    private fun viewCreated() {
        vehicleDetailViewModel.apply {
            baseObserveViewModel(vehicleDetailViewModel)
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
            vehicleDetailViewModel.apply {
                vehicleDataItem?.apply {
                    switchService.setOnClickListener {
                        vehicleDataItem?.isActive = !vehicleDataItem!!.isActive
                        tvVehicleActive.text = if (vehicleDataItem?.isActive == true) stringResource.active else stringResource.inActive
                        NSUtilities.switchEnableDisable(switchService, vehicleDataItem!!.isActive)
                        vehicleViewModel.vehicleEnableDisable(vehicleDataItem!!.id, vehicleDataItem!!.isActive, true)
                    }
                }

                layoutHomeHeader.ivBack.setOnClickListener {
                    onBackPress()
                }

                layoutLogo.clBrandLogo.setOnClickListener {
                    brandLogoHelper.openImagePicker(activity, layoutLogo.ivBrandLogo, null, true,
                        isFill = true
                    )
                }

                OnTextUpdateHelper(layoutNotes.edtValue, vehicleDataItem?.additionalNote?:"", object : NSOnTextChangeCallback {
                    override fun afterTextChanged(text: String) {
                        vehicleDataItem?.additionalNote = text
                        vehicleDetailViewModel.updateNotes(layoutNotes.edtValue.text.toString())
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

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(employeeViewModel) {
            isEmployeeListAvailable.observe(
                viewLifecycleOwner
            ) { list ->
                setUpdateDriverList(list)
            }
        }

        with(vehicleDetailViewModel) {
            isVehicleDetailAvailable.observe(
                viewLifecycleOwner
            ) { response ->
                setVehicleDetail(response)
            }
        }
    }

    override fun onFileUrl(url: String, width: Int, height: Int) {
        vehicleDetailViewModel.apply {
            updateVehicleImage(url)
        }
    }
}