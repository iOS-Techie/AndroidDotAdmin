package com.nyotek.dot.admin.ui.fleets.employee.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.MapBoxView
import com.nyotek.dot.admin.common.NSAddress
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSOnMapResetEvent
import com.nyotek.dot.admin.common.callbacks.NSEmployeeEditCallback
import com.nyotek.dot.admin.common.utils.getLngValue
import com.nyotek.dot.admin.common.utils.getMapValue
import com.nyotek.dot.admin.common.utils.gone
import com.nyotek.dot.admin.common.utils.setGlideWithPlaceHolder
import com.nyotek.dot.admin.common.utils.setPlaceholderAdapter
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
import com.nyotek.dot.admin.common.utils.setVisibility
import com.nyotek.dot.admin.common.utils.setVisibilityIn
import com.nyotek.dot.admin.common.utils.status
import com.nyotek.dot.admin.common.utils.switchEnableDisable
import com.nyotek.dot.admin.common.utils.visible
import com.nyotek.dot.admin.databinding.NsFragmentDriverDetailBinding
import com.nyotek.dot.admin.repository.network.requests.NSEmployeeEditRequest
import com.nyotek.dot.admin.repository.network.responses.JobListDataItem
import com.nyotek.dot.admin.repository.network.responses.SpinnerData
import com.nyotek.dot.admin.repository.network.responses.VehicleData
import com.nyotek.dot.admin.repository.network.responses.VehicleDataItem
import com.nyotek.dot.admin.ui.fleets.employee.NSEmployeeFragment
import com.nyotek.dot.admin.ui.fleets.employee.NSEmployeeViewModel
import com.nyotek.dot.admin.ui.fleets.vehicle.NSVehicleViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NSDriverDetailFragment :
    BaseViewModelFragment<NSEmployeeViewModel, NsFragmentDriverDetailBinding>() {

    override val viewModel: NSEmployeeViewModel by lazy {
        ViewModelProvider(this)[NSEmployeeViewModel::class.java]
    }

    private val vehicleViewModel: NSVehicleViewModel by lazy {
        ViewModelProvider(this)[NSVehicleViewModel::class.java]
    }

    private var mapBoxView: MapBoxView? = null

    companion object {

        private var empCallback: NSEmployeeEditCallback? = null
        private var onMapUpdate: ((Boolean) -> Unit)? = null
        fun newInstance(bundle: Bundle?, callback: NSEmployeeEditCallback? = null, onMapUpdateCallback: ((Boolean) -> Unit)? = null) = NSDriverDetailFragment().apply {
            arguments = bundle
            empCallback = callback
            onMapUpdate = onMapUpdateCallback
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentDriverDetailBinding {
        mapBoxView = MapBoxView(requireContext())
        return NsFragmentDriverDetailBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        binding.clMapDriver.visible()
        baseObserveViewModel(viewModel)
        baseObserveViewModel(vehicleViewModel)
        viewCreated()
    }

    override fun loadFragment(bundle: Bundle?) {
        super.loadFragment(bundle)
        arguments = bundle
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

    fun resetFragment() {
        viewModel.apply {
            mapBoxView?.clearMap()
            binding.mapFragmentDriver.removeAllViews()
            strVehicleDetail = ""
            employeeDataItem = null
            initDriverDetail(false)
            setDriverVehicleDetail(VehicleData())
        }
    }

    private fun initDriverDetail(isApiCall: Boolean = true) {
        binding.apply {
            viewModel.apply {
                stringResource.apply {
                    layoutHomeHeader.tvHeaderTitle.text = driverDetail
                    tvVehicleTitle.text = driverDetail
                    tvVehicleActive.status(employeeDataItem?.isActive == true)
                    layoutName.tvCommonTitle.text = name
                    layoutFleet.tvCommonTitle.text = fleet
                    spinner.tvCommonTitle.text = updateVehicle
                    spinnerRole.tvCommonTitle.text = employeeRole
                    clDriverItem.gone()
                    layoutName.edtValue.setText(employeeDataItem?.titleId)
                    layoutName.edtValue.isEnabled = false
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

    //Employee Role Update
    private fun setEmployeeRole(list: MutableList<JobListDataItem>) {
        binding.apply {
            viewModel.apply {
                var spinnerTitleId: String? = employeeDataItem?.titleId
                val titleList = list.map { getLngValue(it.name) } as MutableList<String>
                val idList = list.map { it.id!! } as MutableList<String>
                val spinnerList = SpinnerData(idList, titleList)

                spinnerRole.spinnerAppSelect.setPlaceholderAdapter(spinnerList, activity, spinnerTitleId, isHideFirstPosition = true, placeholderName = stringResource.selectEmployeeRole) { selectedId ->
                    if (spinnerTitleId != selectedId) {
                        spinnerTitleId = selectedId

                        viewModel.apply {
                            employeeEditRequest = NSEmployeeEditRequest(
                                fleetModel?.vendorId,
                                employeeDataItem?.userId,
                                selectedId
                            )
                            employeeDataItem?.titleId = spinnerTitleId
                            empCallback?.onEmployee(employeeDataItem!!)
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
                    getAssignVehicleDriver(employeeDataItem?.userId,
                        it,true) { vehicleData ->
                        setDriverVehicleDetail(vehicleData)
                    }
                }
            }
        }
    }

    private fun setDriverVehicleDetail(vehicleData: VehicleData?) {
        binding.apply {
            viewModel.apply {

                val isVisible = vehicleData?.manufacturer?.isNotEmpty() == true
                viewLineTextSub.setVisibilityIn(isVisible)
                clDriverItem.setVisibility(isVisible)

                vehicleData?.apply {
                    icDriverImg.setGlideWithPlaceHolder(activity, url = vehicleImg)
                    tvUserTitle.text = manufacturer?:""
                    tvStatus.text = model?:""
                    updateVehicle(vehicleData)
                }

                if (employeeDataItem?.userId?.isNotEmpty() == true) {
                    getDriverLocation(employeeDataItem?.userId!!) {
                        mapBoxView?.initMapView(requireContext(), binding.mapFragmentDriver, it)
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
                    val spinnerList = SpinnerData(idList, titleList)

                    spinner.spinnerAppSelect.setPlaceholderAdapter(spinnerList, activity, id, isHideFirstPosition = true, placeholderName = stringResource.selectVehicle) { selectedId ->
                        if (spinnerTitleId != selectedId && selectedId?.isNotEmpty() == true) {
                            spinnerTitleId = selectedId
                            if (spinnerTitleId?.isNotEmpty() == true) {
                                employeeDataItem?.vehicleId = selectedId
                                empCallback?.onEmployee(employeeDataItem!!)
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
                    getDriverVehicleDetail(isDelete, employeeDataItem?.vehicleId) { vehicleData ->
                        setDriverVehicleDetail(if (isDelete) VehicleData() else vehicleData)
                    }
                }
            }
        }
    }

    /**
     * View created
     */
    private fun viewCreated() {
        viewModel.apply {
            baseObserveViewModel(viewModel)
            observeViewModel()
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
        binding.apply {
            viewModel.apply {
                employeeDataItem?.apply {
                    switchService.setSafeOnClickListener {
                        isActive = !isActive
                        tvVehicleActive.status(isActive)
                        switchService.switchEnableDisable(isActive)
                        empCallback?.onEmployee(this)
                        employeeEnableDisable(
                            fleetModel?.vendorId!!,
                            employeeDataItem?.userId!!,
                            isActive
                        )
                    }
                }

                layoutHomeHeader.ivBack.setSafeOnClickListener {
                    onBackPress()
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getCurrentLocation(event: NSAddress) {
        val address = event.addresses[0].getAddressLine(0).toString()
        if (address.isNotEmpty()) {
            event.locationResult.lastLocation?.apply {
                mapBoxView?.setCurrentLatLong(latitude, longitude)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMapReset(event: NSOnMapResetEvent) {
        if (viewModel.isMapReset) {
            viewModel.isMapReset = false
            onMapUpdate?.invoke(true)
            binding.mapFragmentDriver.removeAllViews()
            mapBoxView?.clearMap()
            mapBoxView?.initMapView(requireContext(), binding.mapFragmentDriver,
                viewModel.driverDetailFleetData
            )

        }
        if (event.isReset) {
            viewModel.isMapReset = event.isReset
            binding.mapFragmentDriver.removeAllViews()
            mapBoxView?.clearMap()
        }
    }
}