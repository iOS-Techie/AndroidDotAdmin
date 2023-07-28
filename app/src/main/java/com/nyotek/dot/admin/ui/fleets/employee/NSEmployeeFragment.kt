package com.nyotek.dot.admin.ui.fleets.employee

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.MapBoxView
import com.nyotek.dot.admin.common.NSAddress
import com.nyotek.dot.admin.common.NSAlertButtonClickEvent
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSPermissionEvent
import com.nyotek.dot.admin.common.NSRequestCodes
import com.nyotek.dot.admin.common.callbacks.NSEmployeeCallback
import com.nyotek.dot.admin.common.callbacks.NSEmployeeSwitchEnableDisableCallback
import com.nyotek.dot.admin.common.callbacks.NSSpinnerSelectCallback
import com.nyotek.dot.admin.common.callbacks.NSUserClickCallback
import com.nyotek.dot.admin.common.callbacks.NSVehicleSelectCallback
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.addOnTextChangedListener
import com.nyotek.dot.admin.common.utils.buildAlertDialog
import com.nyotek.dot.admin.common.utils.getLngValue
import com.nyotek.dot.admin.common.utils.notifyAdapter
import com.nyotek.dot.admin.common.utils.setVisibility
import com.nyotek.dot.admin.common.utils.setupWithAdapter
import com.nyotek.dot.admin.common.utils.visible
import com.nyotek.dot.admin.databinding.LayoutInviteEmployeeBinding
import com.nyotek.dot.admin.databinding.LayoutSpinnerItemBinding
import com.nyotek.dot.admin.databinding.LayoutSpinnerItemDropDownBinding
import com.nyotek.dot.admin.databinding.LayoutUpdateEmployeeBinding
import com.nyotek.dot.admin.databinding.NsFragmentEmployeeBinding
import com.nyotek.dot.admin.repository.network.requests.NSEmployeeEditRequest
import com.nyotek.dot.admin.repository.network.responses.EmployeeDataItem
import com.nyotek.dot.admin.repository.network.responses.FleetDataItem
import com.nyotek.dot.admin.repository.network.responses.JobListDataItem
import com.nyotek.dot.admin.ui.common.NSUserViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NSEmployeeFragment : BaseViewModelFragment<NSEmployeeViewModel, NsFragmentEmployeeBinding>() {

    override val viewModel: NSEmployeeViewModel by lazy {
        ViewModelProvider(this)[NSEmployeeViewModel::class.java]
    }

    private val userManagementViewModel: NSUserViewModel by lazy {
        ViewModelProvider(this)[NSUserViewModel::class.java]
    }

    private var userSearchAdapter: NSEmployeeUserSearchRecycleAdapter? = null
    private var isFragmentLoad = false
    private var addEmployeeDialog: AlertDialog? = null
    private var empAdapter: NSEmployeeRecycleAdapter? = null
    private var mapBoxView: MapBoxView? = null


    companion object {
        private var fleetData: FleetDataItem? = null
        fun newInstance(bundle: Bundle?, list: FleetDataItem?) = NSEmployeeFragment().apply {
            arguments = bundle
            fleetData = list
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentEmployeeBinding {
        mapBoxView = MapBoxView(requireContext())
        return NsFragmentEmployeeBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        isFragmentLoad = false
        mapBoxView?.initMapView(requireContext(), binding.mapFragmentEmployee, fleetData)
        initUI()
        viewCreated()
        setListener()
    }

    override fun observeViewModel() {
        super.observeViewModel()
        with(viewModel) {
            with(binding) {
                isSwipeRefresh.observe(
                    viewLifecycleOwner
                ) { isSwipe ->
                    if (isSwipe) {
                        srlRefresh.isRefreshing = false
                    }
                }

                isEmployeeListAvailable.observe(
                    viewLifecycleOwner
                ) { list ->
                    addEmployeeDialog?.dismiss()
                    if (addEmployeeDialog != null) {
                        NSUtilities.hideProgressBar(null, addEmployeeDialog)
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
            baseObserveViewModel(viewModel)
            observeViewModel()
            binding.clMap.visible()
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
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
                    empAdapter =
                        NSEmployeeRecycleAdapter(object : NSEmployeeCallback {
                            override fun onClick(
                                employeeData: EmployeeDataItem,
                                isDelete: Boolean
                            ) {
                                selectedEmployeeData = employeeData
                                employeeEditDelete(employeeData, !isDelete)
                            }
                        }, object :
                            NSEmployeeSwitchEnableDisableCallback {
                            override fun switch(
                                vendorId: String,
                                userId: String,
                                isEnable: Boolean
                            ) {
                                employeeSwitch(
                                    vendorId,
                                    userId,
                                    isEnable
                                )
                            }
                        }, object : NSVehicleSelectCallback {
                            override fun onItemSelect(vendorId: String) {
                                mapBoxView?.goToMapPosition(vendorId)
                            }
                        })
                    setupWithAdapter(empAdapter!!)
                    isNestedScrollingEnabled = false
                    empAdapter?.setJob(viewModel.jobTitleMap)
                    empAdapter?.setData(list)
                }
            }
        }
    }

    private fun employeeSwitch(vendorId: String, userId: String, isEnable: Boolean) {
        viewModel.apply {
            employeeEnableDisable(vendorId, userId, isEnable, isShowProgress = true)
        }
    }

    private fun employeeEditDelete(employeeData: EmployeeDataItem, isEdit: Boolean) {
        if (isEdit) {
            showEditEmployeeDialog(employeeData)
        } else {
            showCommonDialog(
                title = "",
                message = stringResource.doYouWantToDelete,
                alertKey = NSConstants.KEY_ALERT_EMPLOYEE_DELETE,
                positiveButton = stringResource.ok,
                negativeButton = stringResource.cancel
            )
        }
    }

    private fun showEditEmployeeDialog(employeeData: EmployeeDataItem) {
        binding.apply {
            viewModel.apply {

                buildAlertDialog(
                    requireContext(),
                    LayoutUpdateEmployeeBinding::inflate
                ) { dialog, binding ->
                    binding.apply {
                        stringResource.apply {
                            tvNameTitle.text = editEmployee
                            tvSubmitApp.text = save
                            tvCancelApp.text = cancel
                            tvThemeNameTitle.text = employeeRole
                        }

                        var spinnerTitleId: String? = null
                        setSpinner(
                            spinnerTheme,
                            jobTitleList,
                            employeeData,
                            callback = object : NSSpinnerSelectCallback {
                                override fun onItemSelect(item: String) {
                                    spinnerTitleId = item
                                }
                            })

                        ivCloseAppCreate.setOnClickListener {
                            dialog.dismiss()
                        }

                        tvCancelApp.setOnClickListener {
                            dialog.dismiss()
                        }

                        tvSubmitApp.setOnClickListener {
                            val employeeEditRequest = NSEmployeeEditRequest(
                                employeeData.vendorId,
                                employeeData.userId,
                                spinnerTitleId
                            )

                            if (employeeEditRequest.titleId?.isEmpty() == true) {
                                showError(stringResource.pleaseSelectEmployeeRole)
                                return@setOnClickListener
                            }
                            if (employeeEditRequest.userId?.isEmpty() == true) {
                                showError(stringResource.pleaseSelectUser)
                                return@setOnClickListener
                            }
                            dialog.dismiss()
                            employeeEdit(true, employeeEditRequest)
                        }
                    }
                }
            }
        }
    }

    private fun setSpinner(
        spinnerEmployee: Spinner,
        jobTitleList: MutableList<JobListDataItem>,
        employeeData: EmployeeDataItem?,
        isInvite: Boolean = false,
        callback: NSSpinnerSelectCallback
    ) {
        viewModel.apply {
            val adapter: ArrayAdapter<JobListDataItem> =
                object : ArrayAdapter<JobListDataItem>(
                    activity,
                    R.layout.layout_spinner_item,
                    android.R.id.text1,
                    jobTitleList
                ) {
                    override fun getDropDownView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        val view = super.getDropDownView(position, convertView, parent)
                        val bind = LayoutSpinnerItemDropDownBinding.bind(view)
                        bind.text1.text = getLngValue(jobTitleList[position].name)
                        if (isInvite) {
                            if (position == 0) {
                                bind.text1.visibility = View.GONE
                            } else {
                                bind.text1.visibility = View.VISIBLE
                            }
                        }
                        return view
                    }

                    override fun getCount(): Int {
                        return jobTitleList.ifEmpty { arrayListOf() }.size
                    }

                    override fun getView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        val view = super.getView(position, convertView, parent)
                        val bind = LayoutSpinnerItemBinding.bind(view)
                        if (isInvite && position == 0) {
                            bind.text1.setTextColor(ColorResources.getGrayColor())
                            bind.text1.text = stringResource.selectEmployeeRole
                        } else {
                            bind.text1.setTextColor(ColorResources.getPrimaryColor())
                            bind.text1.text = getLngValue(jobTitleList[position].name)
                        }
                        return view
                    }
                }

            adapter.setDropDownViewResource(R.layout.layout_spinner_item_drop_down)
            spinnerEmployee.adapter = adapter
            val themeCallback = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    callback.onItemSelect(jobTitleList[position].id ?: "")
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
            spinnerEmployee.onItemSelectedListener = themeCallback

            if (employeeData != null) {
                var position: Int = -1
                jobTitleList.forEachIndexed { index, s ->
                    if (s.id == employeeData.titleId) {
                        position = index
                    }
                }

                val spinnerPosition: Int = position
                spinnerEmployee.setSelection(spinnerPosition)
            }
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
                            tvInviteEmployeeTitle.text = inviteEmployee
                            layoutUser.tvCommonTitle.text = user
                            layoutUser.edtValue.hint = searchUser
                            tvRoleNameTitle.text = employeeRole
                        }

                        val jobList: MutableList<JobListDataItem> = arrayListOf()

                        val name: HashMap<String, String> = hashMapOf()
                        name[""] = stringResource.selectEmployeeRole
                        val jobTitleModel = JobListDataItem(name = name)
                        jobList.add(jobTitleModel)
                        jobList.addAll(jobTitleList)

                        var selectedTitleId: String? = null
                        setSpinner(spinnerRole, jobList, null, true, object : NSSpinnerSelectCallback {
                            override fun onItemSelect(item: String) {
                                selectedTitleId = item
                            }
                        })

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
                            val userId: String = searchUserList.find { it.isEmployeeSelected }?.id?:""

                            if (selectedTitleId?.isNotEmpty() == true) {
                                if (userId.isNotEmpty()) {
                                    NSUtilities.showProgressBar(progress, addEmployeeDialog)
                                    employeeAdd(
                                        vendorIdValue ?: "",
                                        userId,
                                        selectedTitleId ?: "",
                                        true
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
                userSearchAdapter =
                    NSEmployeeUserSearchRecycleAdapter(object : NSUserClickCallback {
                        override fun onUserSelect() {
                            notifyAdapter(userSearchAdapter!!)
                        }
                    })
                setupWithAdapter(userSearchAdapter!!)
                isNestedScrollingEnabled = false
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPositiveButtonClickEvent(event: NSAlertButtonClickEvent) {
        viewModel.apply {
            if (event.buttonType == NSConstants.KEY_ALERT_BUTTON_POSITIVE && event.alertKey == NSConstants.KEY_ALERT_EMPLOYEE_DELETE) {
                selectedEmployeeData?.apply {
                    if (userId != null) {
                        employeeDelete(vendorId!!, userId, true)
                    }
                }
            }
        }
    }

    /**
     * Open order route to display destination location in map
     */
    private fun getCurrentLocation() {
        if (NSApplication.getInstance().getPermissionHelper()
                .isLocationPermissionEnable(activity, NSRequestCodes.REQUEST_LOCATION_CODE)
        ) {
            NSApplication.getInstance().getLocationManager()
                .requestLocation(Looper.getMainLooper(), true)
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
                    Toast.makeText(requireActivity(), "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }
}