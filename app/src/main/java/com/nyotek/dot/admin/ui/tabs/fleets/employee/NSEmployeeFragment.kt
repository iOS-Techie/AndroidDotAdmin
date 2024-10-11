package com.nyotek.dot.admin.ui.tabs.fleets.employee

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.BaseFragment
import com.nyotek.dot.admin.common.NSAddress
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSPermissionEvent
import com.nyotek.dot.admin.common.NSPermissionHelper
import com.nyotek.dot.admin.common.NSRequestCodes
import com.nyotek.dot.admin.common.NSUtilities
import com.nyotek.dot.admin.common.extension.addOnTextChangedListener
import com.nyotek.dot.admin.common.extension.buildAlertDialog
import com.nyotek.dot.admin.common.extension.getLngValue
import com.nyotek.dot.admin.common.extension.invisible
import com.nyotek.dot.admin.common.extension.isValidInput
import com.nyotek.dot.admin.common.extension.isValidList
import com.nyotek.dot.admin.common.extension.setPlaceholderAdapter
import com.nyotek.dot.admin.common.extension.setupWithAdapter
import com.nyotek.dot.admin.common.extension.visible
import com.nyotek.dot.admin.common.map.MapBoxView
import com.nyotek.dot.admin.databinding.LayoutInviteEmployeeBinding
import com.nyotek.dot.admin.databinding.LayoutInviteUserItemBinding
import com.nyotek.dot.admin.databinding.NsFragmentEmployeeBinding
import com.nyotek.dot.admin.location.NSLocationManager
import com.nyotek.dot.admin.models.responses.EmployeeDataItem
import com.nyotek.dot.admin.models.responses.FleetDataItem
import com.nyotek.dot.admin.models.responses.SpinnerData
import com.nyotek.dot.admin.ui.common.NSUserViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


@AndroidEntryPoint
class NSEmployeeFragment : BaseFragment<NsFragmentEmployeeBinding>() {

    private val viewModel by viewModels<NSEmployeeViewModel>()
    private lateinit var themeUI: EmployeeUI
    private var isEmployeeMapLoad: Boolean = false

    private val userManagementViewModel: NSUserViewModel by lazy {
        ViewModelProvider(this)[NSUserViewModel::class.java]
    }

    private var userAdapter: NSEmployeeUserSearchRecycleAdapter? = null
    private var isFragmentLoad = false
    private var addEmployeeDialog: AlertDialog? = null
    private var empAdapter: NSEmployeeRecycleAdapter? = null
    private var mapBoxView: MapBoxView? = null
    private var addEmployeeList: MutableList<String> = arrayListOf()
    var util: PhoneNumberUtil? = null
    
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
        container: ViewGroup?,
    ): NsFragmentEmployeeBinding {
        mapBoxView = MapBoxView(requireContext(), viewModel.colorResources, viewModel.languageConfig, false)
        return NsFragmentEmployeeBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        themeUI = EmployeeUI(binding, viewModel)
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
//                    userAdapter?.setData(searchUserList)
                }

                isFleetDetailAvailable.observe(
                    viewLifecycleOwner
                ) { fleetDataItem ->
                    fleetData = fleetDataItem
                    if (!isEmployeeMapLoad) {
                        isEmployeeMapLoad = true
                        mapBoxView?.initMapView(
                            requireContext(),
                            binding.mapFragmentEmployee,
                            fleetData
                        )
                    } else {
                        mapBoxView?.updateMapData(
                            requireContext(),
                            fleetData
                        )
                    }
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
            if (util == null) {
                util = PhoneNumberUtil.getInstance()
            }
            
            initCreateVendor()
            getCurrentLocation()
        }
    }

    private fun initCreateVendor() {
        binding.apply {
            viewModel.apply {
                stringResource.apply {
                    tvEmployeeTitle.text = employees
                    tvAddEmployee.text = add
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
                    getJobTitleList(false)
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
                        mapBoxView?.goToMapPositionFromDriveId(vendorId)
                    })
                    setupWithAdapter(empAdapter!!)
                    isNestedScrollingEnabled = false
                    empAdapter?.setRole(viewModel.jobTitleList)
                    
                    val mList: MutableList<EmployeeDataItem> = arrayListOf()
                    mList.addAll(list.filter { !it.isDeleted }.sortedBy { it.userId })
                    EmployeeHelper.setEmployeeList(mList)
                    empAdapter?.setData(EmployeeHelper.getEmployeeList())
                    
                    moveCameraOnFirstUser()
                }
            }
        }
    }
    
    private fun moveCameraOnFirstUser() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (EmployeeHelper.getEmployeeList().isValidList()) {
                if (!EmployeeHelper.getEmployeeList()[0].vendorId.isNullOrEmpty()) {
                    mapBoxView?.goToMapPositionFromDriveId(EmployeeHelper.getEmployeeList()[0].userId ?: "")
                }
            }
        }, 1000)
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
                            val addUserWithPlus = "+ $adduser"
                            tvAddEmployee.text = addUserWithPlus
                            tvSendInvite.text = invite
                            tvCancelApp.text = cancel
                            tvInviteEmployeeTitle.text = addEmployee
                            tvRoleNameTitle.text = employeeRole
                            tvUserDetailTitle.text = userDetails
                        }
                        
                        addEmployeeList.clear()
                        addEmployeeList.add("")
                        
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
                        
                        addLayouts(binding, true, addEmployeeList.size - 1)
                        
                        tvAddEmployee.setOnClickListener {
                            if (addEmployeeList.isEmpty()) {
                                addEmployeeList.add("")
                                addLayouts(binding, true, addEmployeeList.size - 1)
                            } else if (addEmployeeList.any { it.length < 12 || !it.contains("+")}) {
                                showError(colorResources.getStringResource().enterValidPhoneNumber)
                            } else {
                                if (isValidPhoneNumbers()) {
                                    addEmployeeList.add("")
                                    addLayouts(binding, false, addEmployeeList.size - 1)
                                } else {
                                    showError(colorResources.getStringResource().enterValidPhoneNumber)
                                }
                            }
                        }
                        
                        ivCloseEmployee.setOnClickListener {
                            addEmployeeDialog?.dismiss()
                        }

                        tvCancelApp.setOnClickListener {
                            addEmployeeDialog?.dismiss()
                        }

                        tvSendInvite.setOnClickListener {
                            val vendorIdValue = vendorId
                           
                            if (selectedTitleId?.isNotEmpty() == true) {
                                if (addEmployeeList.isNotEmpty()) {
                                    if (isValidPhoneNumbers()) {
                                        NSUtilities.showProgressBar(
                                            progress,
                                            addEmployeeDialog,
                                            colorResources
                                        )
                                        employeeAdd(
                                            vendorIdValue ?: "",
                                            addEmployeeList,
                                            selectedTitleId ?: ""
                                        ) { isShowError ->
                                            if (isShowError) {
                                                showError(colorResources.getStringResource().enterValidPhoneNumber)
                                            }
                                            NSUtilities.hideProgressBar(
                                                progress,
                                                addEmployeeDialog,
                                                colorResources
                                            )
                                        }
                                    } else {
                                        showError(colorResources.getStringResource().enterValidPhoneNumber)
                                    }
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
    
    private fun isValidPhoneNumbers(): Boolean {
        var isAllValid = true
        for (data in addEmployeeList) {
            if (isValidInput(data)) {
                val lastPart = data.takeLast(10)
                val firstPart = data.take(data.length - 10)
                
                val phoneNumber = PhoneNumber().setCountryCode(
                    firstPart.replace("+", "").toInt()
                ).setNationalNumber(lastPart.toLong())
                if (phoneNumber == null || util?.isValidNumber(phoneNumber) == false) {
                    isAllValid = false
                }
            } else {
                isAllValid = false
            }
        }
        
        return isAllValid
    }
    
    private fun addLayouts(inviteEmployeeBinding: LayoutInviteEmployeeBinding, isRemoveAllViews: Boolean, index: Int) {
        inviteEmployeeBinding.apply {
            if (isRemoveAllViews) {
                llEmployeeView.removeAllViews()
            }
            
            val itemView = LayoutInflater.from(activity).inflate(R.layout.layout_invite_user_item, llEmployeeView, false)
            val bind = LayoutInviteUserItemBinding.bind(itemView)
            themeUI.setAddEmployeeAdapter(bind)
            bind.layoutUser.edtValue.inputType = InputType.TYPE_CLASS_PHONE
            bind.layoutUser.edtValue.imeOptions = EditorInfo.IME_ACTION_DONE
            
            // Set the click listener for the remove button
            bind.ivDeleteEmployee.setOnClickListener {
                llEmployeeView.removeView(itemView)
                addEmployeeList.removeAt(index)
            }
            
            bind.layoutUser.edtValue.addOnTextChangedListener(
                onTextChanged = { text, _, _, _ ->
                    val string: String = text.toString()
                    addEmployeeList[index] = string
                }
            )
            
            llEmployeeView.addView(itemView)
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

    override fun onStart() {
        super.onStart()
        binding.mapFragmentEmployee.visible()
    }
}