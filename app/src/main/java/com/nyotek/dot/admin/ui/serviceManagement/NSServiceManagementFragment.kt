package com.nyotek.dot.admin.ui.serviceManagement

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.FilterHelper
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.utils.addOnTextChangedListener
import com.nyotek.dot.admin.common.utils.buildAlertDialog
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
import com.nyotek.dot.admin.common.utils.setVisibility
import com.nyotek.dot.admin.common.utils.setupWithAdapterAndCustomLayoutManager
import com.nyotek.dot.admin.databinding.LayoutCreateServiceBinding
import com.nyotek.dot.admin.databinding.NsFragmentServiceManagementBinding
import com.nyotek.dot.admin.repository.network.responses.ActiveInActiveFilter
import com.nyotek.dot.admin.repository.network.responses.CapabilitiesDataItem
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListData

class NSServiceManagementFragment :
    BaseViewModelFragment<NSServiceManagementViewModel, NsFragmentServiceManagementBinding>() {

    private var serviceRecycleAdapter: NSServiceManagementRecycleAdapter? = null
    private var isFragmentLoad = false

    override val viewModel: NSServiceManagementViewModel by lazy {
        ViewModelProvider(this)[NSServiceManagementViewModel::class.java]
    }

    companion object {
        fun newInstance() = NSServiceManagementFragment()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentServiceManagementBinding {
        return NsFragmentServiceManagementBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        baseObserveViewModel(viewModel)
        observeViewModel()
        setServiceManagementAdapter()
    }

    override fun loadFragment() {
        super.loadFragment()
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
            }
        }
    }

    private fun initUI() {
        binding.apply {
            stringResource.apply {
                setLayoutHeader(
                    layoutHomeHeader,
                    serviceManagement,
                    isSearch = true
                )
            }
        }
    }

    /**
     * View created
     */
    private fun viewCreated() {
        serviceApiCall(!isFragmentLoad)
        isFragmentLoad = true
    }

    private fun serviceApiCall(isShowProgress: Boolean) {
        viewModel.getServiceListApi(isShowProgress) { serviceList, fleetList, capabilities ->
            setServiceData(serviceList, fleetList, capabilities)
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
        viewModel.apply {
            with(binding) {
                srlRefresh.setOnRefreshListener {
                    serviceApiCall(false)
                }

                layoutHomeHeader.apply {
                    ivClearData.setSafeOnClickListener {
                        etSearch.setText("")
                    }

                    tvHeaderBtn.setSafeOnClickListener {
                        showCreateServiceDialog()
                    }
                }
            }
        }
    }

    private fun setServiceData(serviceList: MutableList<NSGetServiceListData>, fleetList: MutableList<FleetData>, capabilities: MutableList<CapabilitiesDataItem>) {
        binding.apply {
            viewModel.apply {
                srlRefresh.isRefreshing = false

                FilterHelper(activity, binding.rvServiceFilter, if (filterList.isEmpty()) FilterHelper.getCommonFilterLists() else filterList) { _, list ->
                    viewModel.apply {
                        filterList = list
                        filterData(serviceList, fleetList, capabilities, filterList)
                    }
                }

                layoutHomeHeader.etSearch.addOnTextChangedListener(
                    onTextChanged = { text, _, _, _ ->
                        layoutHomeHeader.ivClearData.setVisibility(text.toString().isNotEmpty())
                        filterData(serviceList, fleetList, capabilities, filterList, text.toString())
                    }
                )

                filterData(serviceList, fleetList, capabilities, filterList)
            }
        }
    }

    private fun showCreateServiceDialog() {
        binding.apply {
            viewModel.apply {

                buildAlertDialog(
                    requireContext(),
                    LayoutCreateServiceBinding::inflate
                ) { dialog, binding ->
                    binding.apply {
                        stringResource.apply {
                            tvServiceSubmit.text = create
                            tvCancelService.text = cancel
                            layoutServiceName.tvCommonTitle.text = name
                            tvDescriptionTitle.text = description
                            tvCreateServiceTitle.text = createService

                            ivCloseServiceCreate.setSafeOnClickListener {
                                dialog.dismiss()
                                cancelCreateService(binding)
                            }

                            tvCancelService.setSafeOnClickListener {
                                dialog.dismiss()
                                cancelCreateService(binding)
                            }

                            tvServiceSubmit.setSafeOnClickListener {
                                if (layoutServiceName.edtValue.text.toString().isEmpty()) {
                                    showError(pleaseEnterName)
                                    return@setSafeOnClickListener
                                } else if (etServiceDescription.text.toString().isEmpty()) {
                                    showError(pleaseEnterDescription)
                                    return@setSafeOnClickListener
                                } else {
                                    val name = layoutServiceName.edtValue.text.toString()
                                    val desc = etServiceDescription.text.toString()
                                    dialog.dismiss()
                                    createService(name, desc,true)
                                }
                                cancelCreateService(binding)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Cancel create service
     *
     */
    private fun cancelCreateService(layoutCreateService: LayoutCreateServiceBinding) {
        viewModel.apply {
            binding.apply {
                layoutHomeHeader.apply {
                    layoutCreateService.apply {
                        layoutServiceName.edtValue.setText("")
                        etServiceDescription.setText("")
                    }
                }
            }
        }
    }

    /**
     * Set service adapter
     *
     */
    private fun setServiceManagementAdapter() {
        with(binding) {
            with(viewModel) {
                with(rvServiceList) {
                    serviceRecycleAdapter =
                        NSServiceManagementRecycleAdapter(
                            activity,
                            viewModel, { serviceId, capabilityId ->
                                serviceCapabilityUpdate(serviceId, capabilityId)
                            }, { serviceId, fleets ->
                                serviceFleetsUpdate(serviceId, fleets)
                            }) { serviceId, isEnable ->
                            //Service Enable Disable
                            serviceEnableDisable(serviceId, isEnable)
                        }
                    setupWithAdapterAndCustomLayoutManager(
                        serviceRecycleAdapter!!,
                        GridLayoutManager(activity, 3)
                    )
                    adapter = serviceRecycleAdapter
                    isNestedScrollingEnabled = true
                }
            }
        }
    }

    private fun filterData(serviceList: MutableList<NSGetServiceListData>, fleetList: MutableList<FleetData>, capabilities: MutableList<CapabilitiesDataItem>,
        filterList: MutableList<ActiveInActiveFilter>,
        searchText: String = binding.layoutHomeHeader.etSearch.text.toString()
    ) {
        with(viewModel) {
            serviceRecycleAdapter?.apply {
                val filterTypes = getFilterSelectedTypes(filterList)

                if (filterTypes.isNotEmpty()) {

                    val filter = serviceList.filter { filterTypes.contains(if (it.isActive) NSConstants.ACTIVE else NSConstants.IN_ACTIVE) } as MutableList<NSGetServiceListData>
                    setAdapterData(searchText, if (searchText.isEmpty()) filter else filter.filter {
                        it.name
                            ?.lowercase()?.contains(searchText.lowercase()) == true
                    } as MutableList<NSGetServiceListData>, fleetList, capabilities)

                } else {

                    setAdapterData(searchText, if (searchText.isEmpty()) serviceList else serviceList.filter {
                        it.name?.lowercase()?.contains(searchText.lowercase()) == true
                    } as MutableList<NSGetServiceListData>, fleetList, capabilities)

                }
            }
        }
    }

    private fun setAdapterData(search: String, serviceItemList: MutableList<NSGetServiceListData>, fleetList: MutableList<FleetData>, capabilities: MutableList<CapabilitiesDataItem>) {
        serviceRecycleAdapter?.apply {
            if (search.isEmpty()) {
                setSubList(capabilities, fleetList)
            }
            setData(serviceItemList)
        }
    }
}