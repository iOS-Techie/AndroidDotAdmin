package com.nyotek.dot.admin.ui.tabs.fleets.employee

import android.R
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.nyotek.dot.admin.base.BaseFragment
import com.nyotek.dot.admin.common.NSAddress
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSOnCheckDetailScreen
import com.nyotek.dot.admin.common.NSOnMapResetEvent
import com.nyotek.dot.admin.common.NSPermissionEvent
import com.nyotek.dot.admin.common.NSPermissionHelper
import com.nyotek.dot.admin.common.NSRequestCodes
import com.nyotek.dot.admin.common.NSUtilities
import com.nyotek.dot.admin.common.extension.addOnTextChangedListener
import com.nyotek.dot.admin.common.extension.buildAlertDialog
import com.nyotek.dot.admin.common.extension.getLngValue
import com.nyotek.dot.admin.common.extension.invisible
import com.nyotek.dot.admin.common.extension.navigateSafeNew
import com.nyotek.dot.admin.common.extension.notifyAdapter
import com.nyotek.dot.admin.common.extension.setPlaceholderAdapter
import com.nyotek.dot.admin.common.extension.setVisibility
import com.nyotek.dot.admin.common.extension.setupWithAdapter
import com.nyotek.dot.admin.common.extension.visible
import com.nyotek.dot.admin.common.map.MapBoxView
import com.nyotek.dot.admin.databinding.LayoutInviteEmployeeBinding
import com.nyotek.dot.admin.databinding.NsFragmentEmployeeBinding
import com.nyotek.dot.admin.location.NSLocationManager
import com.nyotek.dot.admin.models.responses.EmployeeDataItem
import com.nyotek.dot.admin.models.responses.FleetDataItem
import com.nyotek.dot.admin.models.responses.SpinnerData
import com.nyotek.dot.admin.ui.common.NSUserViewModel
import com.nyotek.dot.admin.ui.tabs.fleets.vehicle.VehicleHelper
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


@AndroidEntryPoint
class NSEmployeeFragment : BaseFragment<NsFragmentEmployeeBinding>() {

    private val viewModel by viewModels<NSEmployeeViewModel>()
    private lateinit var themeUI: EmployeeUI

    private val userManagementViewModel: NSUserViewModel by lazy {
        ViewModelProvider(this)[NSUserViewModel::class.java]
    }

    private var userSearchAdapter: NSEmployeeUserSearchRecycleAdapter? = null
    private var isFragmentLoad = false
    private var addEmployeeDialog: AlertDialog? = null
    private var empAdapter: NSEmployeeRecycleAdapter? = null
    private var mapBoxView: MapBoxView? = null

    @Inject
    lateinit var locationManager: NSLocationManager

    @Inject
    lateinit var permissionHelper: NSPermissionHelper

    private var callback: ((Bundle?) -> Unit)? = null

    companion object {
        private var fleetData: FleetDataItem? = null
        fun newInstance(bundle: Bundle?, callback: ((Bundle?) -> Unit)?) = NSEmployeeFragment().apply {
            arguments = bundle
            this.callback = callback
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentEmployeeBinding {
        mapBoxView = MapBoxView(requireContext(), viewModel.colorResources, viewModel.languageConfig)
        return NsFragmentEmployeeBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        themeUI = EmployeeUI(binding, viewModel.colorResources, viewModel.languageConfig)
        isFragmentLoad = false

        initUI()
        viewCreated()
        setListener()
    }

    fun updateChangedDetail() {
        viewModel.apply {
            if (selectedPosition != -1) {
                val item = EmployeeHelper.getEmployeeList()[selectedPosition]
                empAdapter?.updateSingleData(item, selectedPosition)
                selectedPosition = -1
                binding.mapFragmentEmployee.visible()
            }
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        with(viewModel) {
            with(binding) {
                isEmployeeListAvailable.observe(
                    viewLifecycleOwner
                ) { list ->
                    addEmployeeDialog?.dismiss()
                    if (addEmployeeDialog != null) {
                        NSUtilities.hideProgressBar(null, addEmployeeDialog, colorResources)
                    }
                    srlRefresh.isRefreshing = false
                    setEmployeeAdapter(list)
                }

                userManagementViewModel.isSearchUserListCall.observe(
                    viewLifecycleOwner
                ) { searchList ->
                    searchUserList = searchList
                    userSearchAdapter?.setData(searchUserList)
                }

                isFleetDetailAvailable.observe(
                    viewLifecycleOwner
                ) { fleetDataItem ->
                    fleetData = fleetDataItem
                    mapBoxView?.initMapView(requireContext(), binding.mapFragmentEmployee, fleetData)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            with(viewModel) {
                if (!isFragmentLoad) {
                    isFragmentLoad = true
                    strVendorDetail = it.getString(NSConstants.FLEET_DETAIL_KEY)
                    getVendorDetail()
                }
            }
        }
    }

    private fun initUI() {
        binding.apply {
            initCreateVendor()
            getCurrentLocation()
        }
    }

    private fun initCreateVendor() {
        binding.apply {
            viewModel.apply {
                stringResource.apply {
                    tvEmployeeTitle.text = employees
                    tvAddEmployee.text = addEmployee
                }
            }
        }
    }

    /**
     * View created
     */
    private fun viewCreated() {
        viewModel.apply {
            observeBaseViewModel(viewModel)
            observeViewModel()
            binding.clMap.visible()
        }
    }

    override fun setListener() {
        super.setListener()
        binding.apply {
            viewModel.apply {
                srlRefresh.setOnRefreshListener {
                    getJobTitleList(false, vendorModel?.serviceIds ?: arrayListOf())
                }

                tvAddEmployee.setOnClickListener {
                    showAddEmployeeDialog()
                }
            }
        }
    }

    /**
     * Set employee adapter
     *
     */
    private fun setEmployeeAdapter(list: MutableList<EmployeeDataItem>) {
        viewModel.apply {
            with(binding) {
                with(rvEmployeeList) {
                    empAdapter = NSEmployeeRecycleAdapter(themeUI, { model, isDelete, position ->
                        employeeEditDelete(model, !isDelete, position)
                    }, { vendorId, userId, isEnable ->
                        employeeSwitch(vendorId, userId, isEnable)
                    }, { vendorId ->
                        mapBoxView?.goToMapPosition(vendorId)
                    })
                    setupWithAdapter(empAdapter!!)
                    isNestedScrollingEnabled = false
                    empAdapter?.setJob(viewModel.jobTitleMap)
                    EmployeeHelper.setEmployeeList(list)
                    empAdapter?.setData(EmployeeHelper.getEmployeeList())
                }
            }
        }
    }

    private fun employeeSwitch(vendorId: String, userId: String, isEnable: Boolean) {
        viewModel.apply {
            employeeEnableDisable(vendorId, userId, isEnable)
        }
    }

    private fun employeeEditDelete(employeeData: EmployeeDataItem, isEdit: Boolean, position: Int) {
        if (isEdit) {
            editEmployeeData(employeeData, position)
        } else {
            showCommonDialog(
                title = "",
                message = stringResource.doYouWantToDelete,
                positiveButton = stringResource.ok,
                negativeButton = stringResource.cancel
            ) {
                if (!it) {
                    employeeData.apply {
                        if (userId != null) {
                            viewModel.employeeDelete(vendorId!!, userId)
                        }
                    }
                }
            }
        }
    }

    private fun editEmployeeData(response: EmployeeDataItem, position: Int) {
        viewModel.apply {
            selectedPosition = position
           // mapBoxView?.clearMap()
            //binding.mapFragmentEmployee.removeAllViews()
            viewModel.isDetailScreenOpen = true
            val bundle = bundleOf(
                NSConstants.DRIVER_DETAIL_KEY to Gson().toJson(response),
                NSConstants.FLEET_DETAIL_KEY to strVendorDetail,
                NSConstants.Job_TITLE_LIST_KEY to Gson().toJson(jobTitleList)
            )
            binding.mapFragmentEmployee.invisible()
            callback?.invoke(bundle)
            //findNavController().navigateSafeNew(EmployeeFragmentDirections.actionFleetDetailToDriverDetail(bundle))


            /*fleetManagementFragmentChangeCallback?.setFragment(
                this@NSEmployeeFragment.javaClass.simpleName,
                NSDriverDetailFragment.newInstance(bundle, object : NSEmployeeEditCallback {
                    override fun onEmployee(empDataItem: EmployeeDataItem) {
                        empAdapter?.updateSingleData(empDataItem, position)
                    }
                }) {
                    mapBoxView?.clearMap()
                    mapBoxView?.initMapView(
                        requireContext(),
                        binding.mapFragmentEmployee,
                        fleetData
                    )
                },
                true, bundle
            )*/
        }
    }

    private fun showAddEmployeeDialog() {
        binding.apply {

            buildAlertDialog(
                requireContext(),
                LayoutInviteEmployeeBinding::inflate
            ) { dialog, binding ->
                addEmployeeDialog = dialog

                binding.apply {
                    viewModel.apply {
                        stringResource.apply {
                            layoutUser.edtValue.setText("")
                            tvSendInvite.text = add
                            tvCancelApp.text = cancel
                            tvInviteEmployeeTitle.text = addEmployee
                            layoutUser.tvCommonTitle.text = user
                            layoutUser.edtValue.hint = searchUser
                            tvRoleNameTitle.text = employeeRole
                        }

                        var selectedTitleId: String? = null
                        val nameList: MutableList<String> = jobTitleList.map { getLngValue(it.name) }.toMutableList()
                        val idList: MutableList<String> = jobTitleList.map { it.id ?: "" }.toMutableList()
                        val spinnerList = SpinnerData(idList, nameList)
                        spinnerRole.setPlaceholderAdapter(
                            spinnerList,
                            activity,
                            colorResources, "",
                            isHideFirstPosition = true,
                            placeholderName = stringResource.selectEmployeeRole
                        ) { selectedId ->
                            if (selectedId != selectedTitleId) {
                                selectedTitleId = selectedId
                            }
                        }

                        setUserManagementAdapter(binding)

                        layoutUser.edtValue.addOnTextChangedListener(
                            afterTextChanged = { s ->
                                clUserList.setVisibility(s.toString().isNotEmpty())
                                if ((s ?: "").isEmpty()) {
                                    userSearchAdapter?.setData(arrayListOf())
                                } else {
                                    userManagementViewModel.search(s.toString(), false)
                                }
                            }
                        )

                        ivCloseEmployee.setOnClickListener {
                            addEmployeeDialog?.dismiss()
                            layoutUser.edtValue.setText("")
                        }

                        tvCancelApp.setOnClickListener {
                            addEmployeeDialog?.dismiss()
                            layoutUser.edtValue.setText("")
                        }

                        tvSendInvite.setOnClickListener {
                            val vendorIdValue = vendorId
                            val userId: String =
                                searchUserList.find { it.isEmployeeSelected }?.id ?: ""

                            if (selectedTitleId?.isNotEmpty() == true) {
                                if (userId.isNotEmpty()) {
                                    NSUtilities.showProgressBar(progress, addEmployeeDialog, colorResources)
                                    employeeAdd(
                                        vendorIdValue ?: "",
                                        userId,
                                        selectedTitleId ?: ""
                                    )
                                } else {
                                    showError(stringResource.pleaseSelectUser)
                                }
                            } else {
                                showError(stringResource.pleaseSelectEmployeeRole)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Set employee user search adapter
     *
     */
    private fun setUserManagementAdapter(inviteEmployeeBinding: LayoutInviteEmployeeBinding) {
        with(inviteEmployeeBinding) {
            with(rvUserList) {
                userSearchAdapter = NSEmployeeUserSearchRecycleAdapter {
                        notifyAdapter(userSearchAdapter!!)
                    }
                setupWithAdapter(userSearchAdapter!!)
                isNestedScrollingEnabled = false
            }
        }
    }

    /**
     * Open order route to display destination location in map
     */
    private fun getCurrentLocation() {
        if (permissionHelper.isLocationPermissionEnable(activity, NSRequestCodes.REQUEST_LOCATION_CODE)) {
            locationManager.requestLocation(Looper.getMainLooper(), true)
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
    fun onPermissionEvent(event: NSPermissionEvent) {
        when (event.requestCode) {
            NSRequestCodes.REQUEST_LOCATION_CODE -> {
                if (event.grantResults.isNotEmpty() && event.grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation()
                } else {
                    Toast.makeText(requireActivity(), stringResource.allowLocationPermission, Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun checkDetailScreen(@Suppress("UNUSED_PARAMETER") event: NSOnCheckDetailScreen) {
        viewModel.isDetailScreenOpen = false
        mapBoxView?.clearMap()
        mapBoxView?.initMapView(
            requireContext(),
            binding.mapFragmentEmployee,
            fleetData
        )
    }

    override fun onStart() {
        super.onStart()
        binding.mapFragmentEmployee.visible()
    }
}