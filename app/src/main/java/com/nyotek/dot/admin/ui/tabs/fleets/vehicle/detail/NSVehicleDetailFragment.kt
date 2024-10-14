package com.nyotek.dot.admin.ui.tabs.fleets.vehicle.detail

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nyotek.dot.admin.base.BaseFragment
import com.nyotek.dot.admin.common.BrandLogoHelper
import com.nyotek.dot.admin.common.NSAddress
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSUtilities
import com.nyotek.dot.admin.common.OnTextUpdateHelper
import com.nyotek.dot.admin.common.callbacks.NSFileUploadCallback
import com.nyotek.dot.admin.common.callbacks.NSVehicleEditCallback
import com.nyotek.dot.admin.common.event.EventHelper
import com.nyotek.dot.admin.common.extension.getLngValue
import com.nyotek.dot.admin.common.extension.invisible
import com.nyotek.dot.admin.common.extension.isValidList
import com.nyotek.dot.admin.common.extension.rotation
import com.nyotek.dot.admin.common.extension.setCoil
import com.nyotek.dot.admin.common.extension.setPlaceholderAdapter
import com.nyotek.dot.admin.common.extension.setSafeOnClickListener
import com.nyotek.dot.admin.common.extension.setVisibility
import com.nyotek.dot.admin.common.extension.setVisibilityIn
import com.nyotek.dot.admin.common.extension.status
import com.nyotek.dot.admin.common.extension.switchEnableDisable
import com.nyotek.dot.admin.common.extension.visible
import com.nyotek.dot.admin.common.map.MapBoxView
import com.nyotek.dot.admin.databinding.NsFragmentVehicleDetailBinding
import com.nyotek.dot.admin.models.responses.CapabilitiesDataItem
import com.nyotek.dot.admin.models.responses.EmployeeDataItem
import com.nyotek.dot.admin.models.responses.FleetDataItem
import com.nyotek.dot.admin.models.responses.SpinnerData
import com.nyotek.dot.admin.models.responses.VehicleDataItem
import com.nyotek.dot.admin.models.responses.VehicleDetailData
import com.nyotek.dot.admin.ui.tabs.fleets.employee.NSEmployeeViewModel
import com.nyotek.dot.admin.ui.tabs.fleets.vehicle.VehicleHelper
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class NSVehicleDetailFragment : BaseFragment<NsFragmentVehicleDetailBinding>(),
    NSFileUploadCallback {

    private val viewModel by viewModels<VehicleDetailViewModel>()
    private val employeeViewModel by viewModels<NSEmployeeViewModel>()
    private var mapBoxView: MapBoxView? = null
    private val brandLogoHelper: BrandLogoHelper = BrandLogoHelper(this, callback = this)
    private lateinit var themeUI: VehicleDetailUI
    private var isDriverMapLoad: Boolean = false
    private val eventViewModel = EventHelper.getEventViewModel()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackPressedHandler()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentVehicleDetailBinding {
        mapBoxView = MapBoxView(requireContext(), viewModel.colorResources, viewModel.languageConfig, false)
        return NsFragmentVehicleDetailBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        themeUI = VehicleDetailUI(activity, binding, viewModel.colorResources)
        binding.clMap.visible()

        arguments?.let {
            viewModel.apply {
                getVehicleDetail(it.getString(NSConstants.VEHICLE_DETAIL_KEY), it.getString(NSConstants.FLEET_DETAIL_KEY))
                initVehicleDetail()
                setListener()
            }
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        observeBaseViewModel(viewModel)
        observeBaseViewModel(employeeViewModel)
        
        with(employeeViewModel) {
            isEmployeeListAvailable.observe(
                viewLifecycleOwner
            ) { list ->
                employeeList = list
                setUpdateDriverList(list)
            }
            
            eventViewModel.refreshEvent.observe(viewLifecycleOwner) {
                mapBoxView?.refreshMapView(5, binding.mapFragmentVehicle)
            }
        }
    }

    private fun initVehicleDetail(isApiCall: Boolean = true) {
        binding.apply {
            viewModel.apply {
                tvVehicleActive.status(vehicleDataItem?.isActive == true)
                layoutLogo.ivBrandLogo.setCoil(url = vehicleDataItem?.vehicleImg, NSConstants.FILL, 0f)
                layoutManufacturer.edtValue.setText(vehicleDataItem?.manufacturer)
                layoutManufacturerYear.edtValue.setText(vehicleDataItem?.manufacturingYear)
                layoutModel.edtValue.setText(vehicleDataItem?.model)
                layoutRegistrationNo.edtValue.setText(vehicleDataItem?.registrationNo)
                layoutLoadCapacity.edtValue.setText(vehicleDataItem?.loadCapacity)
                layoutNotes.edtValue.setText(vehicleDataItem?.additionalNote)

                switchService.switchEnableDisable(vehicleDataItem?.isActive == true)
                switchService.rotation(viewModel.languageConfig.isLanguageRtl())
                layoutLogo.tvEditTitle.text = colorResources.getStringResource().selectImage
                
                getCapabilities(true, isApiDataCheck = false) {
                    setCapabilityList(it)
                }

                if (isApiCall) {
                    //Get Vehicle Detail
                    vehicleDataItem?.id?.let { getVehicleDetail(it, true) { vehicleData ->
                        setVehicleDetail(vehicleData)
                    } }
                }
            }
        }
    }

    private fun setCapabilityList(capabilityList: MutableList<CapabilitiesDataItem>) {
        binding.apply {
            viewModel.apply {
                val tempList = capabilityList.filter { it.isActive }
                val activeCapabilities = if (tempList.isValidList()) tempList.toMutableList() else arrayListOf()
                NSUtilities.setCapability(
                    activity,
                    viewModel, true, isShowActiveDot = false,
                    layoutCapability,
                    activeCapabilities,
                    vehicleDataItem
                ) {
                    updateCapabilityParameter(it, activeCapabilities, object : NSVehicleEditCallback {
                        override fun onVehicle(vehicleData: VehicleDataItem) {

                        }
                    })
                }
            }
        }
    }

    private fun setVehicleDetail(response: VehicleDetailData) {
        binding.apply {
            viewModel.apply {
                driverId = response.driverId
                //Get Employee List with Role
                employeeViewModel.getEmployeeWithRole(false, vehicleDataItem?.refId)
            }
        }
    }

    override fun setListener() {
        super.setListener()
        binding.apply {
            viewModel.apply {
                vehicleDataItem?.apply {
                    switchService.setOnClickListener {
                        isActive = !isActive
                        tvVehicleActive.status(isActive)
                        switchService.switchEnableDisable(isActive)
                        VehicleHelper.updateVehicleItem(vehicleDataItem?.id, vehicleDataItem)
                        //vehicleDataItem?.let { it1 -> vehCallback?.onVehicle(it1) }
                        vehicleEnableDisable(id, isActive)
                    }
                }

                layoutHomeHeader.ivBack.setOnClickListener {
                    binding.mapFragmentVehicle.invisible()
                    findNavController().popBackStack()
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
                        VehicleHelper.updateVehicleItem(vehicleDataItem?.id, vehicleDataItem)
                       // vehicleDataItem?.let { it1 -> vehCallback?.onVehicle(it1) }
                    }
                }
            }
        }
    }

    private fun setUpdateDriverList(employeeList: MutableList<EmployeeDataItem>) {
        binding.apply {
            val nameList: MutableList<String> = employeeList.map { it.userId ?: "" }.toMutableList()
            var idList: MutableList<String> = employeeList.map { it.userId ?: "" }.toMutableList()

            val spinnerList = SpinnerData(idList, nameList)
            if (spinner.spinnerAppSelect.adapter == null) {
                spinner.spinnerAppSelect.setPlaceholderAdapter(
                    spinnerList,
                    activity, viewModel.colorResources,
                    "",
                    isHideFirstPosition = true,
                    placeholderName = stringResource.selectDriver
                ) { selectedId ->
                    if (selectedId != viewModel.driverId && selectedId?.isNotEmpty() == true) {
                        viewModel.apply {
                            viewModel.driverId = selectedId
                            assignVehicleToDriver(
                                false,
                                vehicleDataItem?.capabilities ?: arrayListOf()
                            )
                        }
                    }
                }
            }
            Handler(Looper.getMainLooper()).post {
                tvUserTitle.visible()
                tvUserTitle.setBackgroundColor(Color.WHITE)
            }
            val empResponse = employeeList.find { it.userId == viewModel.driverId }
            tvUserTitle.text = viewModel.driverId ?: ""
            tvStatus.text = getLngValue(viewModel.colorResources.themeHelper.getJobRolesTypes().find { it.id == empResponse?.titleId }?.name)

            idList = employeeList.map { it.userId ?: "" }.toMutableList()
            val spinnerPosition = idList.indexOf(viewModel.driverId)
            val isVisible = spinnerPosition != -1
            clVehicleItem.setVisibility(isVisible)
            //viewLineTextSub.setVisibilityIn(isVisible)

            if (viewModel.driverId?.isNotEmpty() == true && idList.isValidList() && spinnerPosition >= 0) {
                employeeViewModel.getDriverLocation(viewModel.driverId) {
                    if (!isDriverMapLoad) {
                        isDriverMapLoad = true
                        mapBoxView?.clearMap()
                        mapBoxView?.initMapView(requireContext(), binding.mapFragmentVehicle, it, key = 5)
                        mapBoxView?.goToMapPositionFromDriveId(viewModel.driverId!!)
                    } else {
                        mapBoxView?.goToMapPositionFromDriveId(viewModel.driverId!!)
                    }
                }
            } else {
                mapBoxView?.initMapView(requireContext(), binding.mapFragmentVehicle, FleetDataItem(), key = 5)
            }
        }
    }

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
            VehicleHelper.updateVehicleItem(vehicleDataItem?.id, vehicleDataItem)
            //vehicleDataItem?.let { it1 -> vehCallback?.onVehicle(it1) }
            updateVehicleImage(url)
        }
    }

    private fun setBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.mapFragmentVehicle.invisible()
                findNavController().popBackStack()
            }
        })
    }
}