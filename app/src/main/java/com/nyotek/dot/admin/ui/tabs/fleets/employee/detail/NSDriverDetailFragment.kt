package com.nyotek.dot.admin.ui.tabs.fleets.employee.detail

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
import com.nyotek.dot.admin.common.NSAddress
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.event.EventHelper
import com.nyotek.dot.admin.common.extension.getLngValue
import com.nyotek.dot.admin.common.extension.getMapValue
import com.nyotek.dot.admin.common.extension.getSpinnerData
import com.nyotek.dot.admin.common.extension.gone
import com.nyotek.dot.admin.common.extension.setCoil
import com.nyotek.dot.admin.common.extension.setCoilCircle
import com.nyotek.dot.admin.common.extension.setPlaceholderAdapter
import com.nyotek.dot.admin.common.extension.setSafeOnClickListener
import com.nyotek.dot.admin.common.extension.setVisibility
import com.nyotek.dot.admin.common.extension.setVisibilityIn
import com.nyotek.dot.admin.common.extension.status
import com.nyotek.dot.admin.common.extension.switchEnableDisable
import com.nyotek.dot.admin.common.extension.visible
import com.nyotek.dot.admin.common.map.MapBoxView
import com.nyotek.dot.admin.databinding.NsFragmentDriverDetailBinding
import com.nyotek.dot.admin.models.requests.NSEmployeeEditRequest
import com.nyotek.dot.admin.models.responses.JobListDataItem
import com.nyotek.dot.admin.models.responses.SpinnerData
import com.nyotek.dot.admin.models.responses.VehicleData
import com.nyotek.dot.admin.models.responses.VehicleDataItem
import com.nyotek.dot.admin.ui.tabs.fleets.employee.EmployeeHelper
import com.nyotek.dot.admin.ui.tabs.fleets.employee.NSEmployeeViewModel
import com.nyotek.dot.admin.ui.tabs.fleets.vehicle.VehicleViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class NSDriverDetailFragment : BaseFragment<NsFragmentDriverDetailBinding>() {

    private val viewModel by viewModels<NSEmployeeViewModel>()
    private val vehicleViewModel by viewModels<VehicleViewModel>()
    private lateinit var themeUI: DriverDetailUI
    private var mapBoxView: MapBoxView? = null
    private var isDriverMapLoad: Boolean = false
    val eventViewModel = EventHelper.getEventViewModel()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackPressedHandler()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentDriverDetailBinding {
        mapBoxView = MapBoxView(requireContext(), viewModel.colorResources, viewModel.languageConfig, false)
        return NsFragmentDriverDetailBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        binding.clMapDriver.visible()
        themeUI = DriverDetailUI(binding, viewModel.colorResources, viewModel.languageConfig)
        observeBaseViewModel(viewModel)
        observeBaseViewModel(vehicleViewModel)

        arguments?.let {
            with(viewModel) {
                strVehicleDetail = it.getString(NSConstants.DRIVER_DETAIL_KEY)
                fleetDetail = it.getString(NSConstants.FLEET_DETAIL_KEY)

                getJobTitleListFromString(it.getString(NSConstants.Job_TITLE_LIST_KEY))
                getDriverDetail()
                initDriverDetail()
                setListener()
            }
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        binding.apply {
            viewModel.apply {
                vehicleDataObserve.observe(
                    viewLifecycleOwner
                ) {
                    setDriverVehicleDetail(it)
                }
                
                eventViewModel.refreshEvent.observe(viewLifecycleOwner) {
                    mapBoxView?.refreshMapView(3, binding.mapFragmentDriver)
                }
            }
        }
    }

    override fun setListener() {
        super.setListener()
        binding.apply {
            viewModel.apply {
                employeeDataItem?.apply {
                    switchService.setSafeOnClickListener {
                        isActive = !isActive
                        tvVehicleActive.status(isActive)
                        switchService.switchEnableDisable(isActive)
                        EmployeeHelper.updateEmployeeItem(employeeDataItem?.userId, this)
                        //empCallback?.onEmployee(this)
                        employeeEnableDisable(
                            fleetModel?.vendorId!!,
                            employeeDataItem?.userId!!,
                            isActive
                        )
                    }
                }

                layoutHomeHeader.ivBack.setSafeOnClickListener {
                    mapFragmentDriver.gone()
                    findNavController().popBackStack()
                }

                ivDelete.setSafeOnClickListener {
                    showCommonDialog(
                        title = "",
                        message = stringResource.doYouWantToDelete,
                        positiveButton = stringResource.ok,
                        negativeButton = stringResource.cancel) {
                        if (!it) {
                            assignVehicleToDriver(capabilities = arrayListOf(), isDelete = true)
                        }
                    }
                }
            }
        }
    }

    private fun initDriverDetail(isApiCall: Boolean = true) {
        binding.apply {
            viewModel.apply {
                stringResource.apply {
                    tvVehicleActive.status(employeeDataItem?.isActive == true)
                    layoutName.edtValue.setText(employeeDataItem?.titleId)
                    layoutFleet.edtValue.getMapValue(fleetModel?.name)
                    layoutFleet.edtValue.isEnabled = false
                    switchService.switchEnableDisable(employeeDataItem?.isActive == true)

                    setEmployeeRole(viewModel.jobTitleList)

                    if (isApiCall) {
                        //Get Vehicle Detail
                        vehicleViewModel.apply {
                            getVehicleList(true, viewModel.fleetModel?.vendorId?:"", arrayListOf(), isFromDriverDetail = true) {
                                setVehicleList(it)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setEmployeeRole(list: MutableList<JobListDataItem>) {
        binding.apply {
            viewModel.apply {
                var spinnerTitleId: String? = employeeDataItem?.titleId
                val titleList = list.map { getLngValue(it.name) } as MutableList<String>
                val idList = list.map { it.id!! } as MutableList<String>
                val spinnerList = SpinnerData(idList, titleList)

                spinnerRole.spinnerAppSelect.setPlaceholderAdapter(spinnerList, activity, colorResources, spinnerTitleId, isHideFirstPosition = true, placeholderName = stringResource.selectEmployeeRole) { selectedId ->
                    if (spinnerTitleId != selectedId) {
                        spinnerTitleId = selectedId

                        viewModel.apply {
                            employeeEditRequest = NSEmployeeEditRequest(
                                fleetModel?.vendorId,
                                employeeDataItem?.userId,
                                selectedId
                            )
                            employeeDataItem?.titleId = spinnerTitleId
                            EmployeeHelper.updateEmployeeItem(employeeDataItem?.userId, employeeDataItem)
                            //empCallback?.onEmployee(employeeDataItem!!)
                            employeeEdit(employeeEditRequest)
                        }
                    }
                }
            }
        }
    }

    private fun setVehicleList(list: MutableList<VehicleDataItem>) {
        binding.apply {
            viewModel.apply {
                vehicleDataList = list
                fleetModel?.vendorId?.let {
                    getAssignVehicleDriver(employeeDataItem?.userId, it,true)
                }
            }
        }
    }

    private fun setDriverVehicleDetail(vehicleData: VehicleData?) {
        binding.apply {
            viewModel.apply {

                val isVisible = vehicleData?.manufacturer?.isNotEmpty() == true
                //viewLineTextSub.setVisibilityIn(isVisible)
                clDriverItem.setVisibility(isVisible)
                Handler(Looper.getMainLooper()).post {
                    tvUserTitle.visible()
                    tvUserTitle.setBackgroundColor(Color.WHITE)
                }

                vehicleData?.apply {
                    icDriverImg.setCoil(url = vehicleImg)
                    tvUserTitle.text = manufacturer?:""
                    tvStatus.text = model?:""
                    updateVehicle(vehicleData)
                }

                if (employeeDataItem?.userId?.isNotEmpty() == true) {
                    getDriverLocation(employeeDataItem?.userId!!) {
                        Handler(Looper.getMainLooper()).post {
                            if (!isDriverMapLoad) {
                                isDriverMapLoad = true
                                mapBoxView?.initMapView(requireContext(), binding.mapFragmentDriver, it, key = 3)
                                Handler(Looper.getMainLooper()).postDelayed({
                                    mapBoxView?.goToMapPositionFromDriveId(employeeDataItem?.userId!!)
                                }, 200)
                            } else {
                                mapBoxView?.goToDispatchMapPosition(it?.features)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateVehicle(vehicleData: VehicleData?) {
        binding.apply {
            viewModel.apply {
                vehicleData?.apply {
                    var spinnerTitleId: String? = id
                    val titleList = vehicleDataList.map { "${it.manufacturer} ${it.model}" } as MutableList<String>
                    val idList = vehicleDataList.map { it.id!! } as MutableList<String>
                    
                    val strings = viewModel.colorResources.getStringResource()
                    val assignVehicleTitle = "${strings.assign} ${strings.vehicle}"
                    val noAssignedVehicleAvailable = "${strings.no} ${strings.assigned} ${strings.vehicle}"
                    val spinnerList = getSpinnerData(idList, titleList, noAssignedVehicleAvailable)
                    
                    spinner.spinnerAppSelect.setPlaceholderAdapter(spinnerList, activity, colorResources, id, isHideFirstPosition = true, placeholderName = assignVehicleTitle) { selectedId ->
                        if (spinnerTitleId != selectedId && selectedId?.isNotEmpty() == true) {
                            spinnerTitleId = selectedId
                            if (spinnerTitleId?.isNotEmpty() == true) {
                                employeeDataItem?.vehicleId = selectedId
                                EmployeeHelper.updateEmployeeItem(employeeDataItem?.userId, employeeDataItem)
                                //empCallback?.onEmployee(employeeDataItem!!)
                                assignVehicleToDriver(selectedId, capabilities, false)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun assignVehicleToDriver(selectedId: String = "", capabilities: MutableList<String>, isDelete: Boolean) {
        viewModel.apply {
            assignVehicle(isDelete,
                employeeDataItem?.userId!!, selectedId,
                capabilities
            ) {
                if (it) {
                    getDriverVehicleDetail(isDelete, employeeDataItem?.vehicleId)
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

    private fun setBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.mapFragmentDriver.gone()
                findNavController().popBackStack()
            }
        })
    }
}