package com.nyotek.dot.admin.ui.fleets.employee.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.MapBoxView
import com.nyotek.dot.admin.common.NSAddress
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.callbacks.NSDialogClickCallback
import com.nyotek.dot.admin.common.callbacks.NSFileUploadCallback
import com.nyotek.dot.admin.common.utils.getLngValue
import com.nyotek.dot.admin.common.utils.getMapValue
import com.nyotek.dot.admin.common.utils.glide
import com.nyotek.dot.admin.common.utils.gone
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
import com.nyotek.dot.admin.ui.capabilities.NSCapabilitiesViewModel
import com.nyotek.dot.admin.ui.fleets.employee.NSEmployeeViewModel
import com.nyotek.dot.admin.ui.fleets.vehicle.NSVehicleViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NSDriverDetailFragment :
    BaseViewModelFragment<NSDriverDetailViewModel, NsFragmentDriverDetailBinding>(),
    NSFileUploadCallback {

    override val viewModel: NSDriverDetailViewModel by lazy {
        ViewModelProvider(this)[NSDriverDetailViewModel::class.java]
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

    companion object {
        fun newInstance(bundle: Bundle?) = NSDriverDetailFragment().apply {
            arguments = bundle
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
        binding.clMap.visible()
        baseObserveViewModel(viewModel)
        baseObserveViewModel(vehicleViewModel)
        baseObserveViewModel(employeeViewModel)
        baseObserveViewModel(capabilitiesViewModel)
        viewCreated()
    }

    override fun loadFragment(bundle: Bundle?) {
        super.loadFragment(bundle)
        arguments = bundle
        arguments?.let {
            with(viewModel) {
                strVehicleDetail = it.getString(NSConstants.DRIVER_DETAIL_KEY)
                fleetDetail = it.getString(NSConstants.FLEET_DETAIL_KEY)

                employeeViewModel.apply {
                    strJobTitle = it.getString(NSConstants.Job_TITLE_LIST_KEY)
                    getJobTitleListFromString()
                }

                getVehicleDetail()
                initDriverDetail()
                setListener()
            }
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        with(vehicleViewModel) {
            isVehicleListAvailable.observe(
                viewLifecycleOwner
            ) { response ->
                setVehicleList(response)
            }

            isVehicleDetailAvailable.observe(
                viewLifecycleOwner
            ) { response ->
                setDriverVehicleDetail(response)
            }
        }

        viewModel.apply {
            isVehicleAssign.observe(
                viewLifecycleOwner
            ) {
                if (it) {
                    vehicleViewModel.getDriverVehicleDetail(employeeDataItem?.vehicleId )
                }
            }
        }

        employeeViewModel.apply {
            isDriverLocationAvailable.observe(viewLifecycleOwner) {
                mapBoxView?.initMapView(requireContext(), binding.mapFragmentDriver, it)
            }
        }
    }

    fun resetFragment() {
        viewModel.apply {
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
                    layoutFleet.edtValue.getMapValue(fleetModel?.name)
                    switchService.switchEnableDisable(employeeDataItem?.isActive == true)

                    setEmployeeRole(employeeViewModel.jobTitleList)

                    if (isApiCall) {
                        //Get Vehicle Detail
                        vehicleViewModel.apply {
                            getVehicleList(viewModel.fleetModel?.vendorId, true)
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

                        employeeViewModel.apply {
                            employeeEditRequest = NSEmployeeEditRequest(
                                fleetModel?.vendorId,
                                employeeDataItem?.userId,
                                selectedId
                            )
                            employeeEdit(true, employeeEditRequest)
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
                    vehicleViewModel.getAssignVehicleDriver(employeeDataItem?.userId,
                        it,true)
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
                    icDriverImg.glide(url = vehicleImg)
                    tvUserTitle.text = manufacturer?:""
                    tvStatus.text = model?:""
                    updateVehicle(vehicleData)
                }

                if (viewModel.employeeDataItem?.userId?.isNotEmpty() == true) {
                    employeeViewModel.getDriverLocation(viewModel.employeeDataItem?.userId!!)
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
                                assignVehicle(
                                    employeeDataItem?.userId!!, selectedId,
                                    capabilities
                                )
                            }
                        }
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
                employeeDataItem?.apply {
                    switchService.setSafeOnClickListener {
                        isActive = !isActive
                        tvVehicleActive.status(isActive)
                        switchService.switchEnableDisable(isActive)
                        employeeViewModel.employeeEnableDisable(fleetModel?.vendorId!!, employeeDataItem?.userId!!, isActive, isShowProgress = true)
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
                        negativeButton = stringResource.cancel, callback = object : NSDialogClickCallback {
                            override fun onDialog(isCancelClick: Boolean) {
                                if (!isCancelClick) {
                                    assignVehicle(
                                        viewModel.employeeDataItem?.userId!!, capabilities = arrayListOf())
                                }
                            }
                        }
                    )
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