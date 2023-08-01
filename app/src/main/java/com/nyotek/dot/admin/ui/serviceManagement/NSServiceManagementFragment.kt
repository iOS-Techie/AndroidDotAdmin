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
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListData
import com.nyotek.dot.admin.ui.capabilities.NSCapabilitiesViewModel
import com.nyotek.dot.admin.ui.fleets.NSFleetViewModel

class NSServiceManagementFragment :
    BaseViewModelFragment<NSServiceManagementViewModel, NsFragmentServiceManagementBinding>() {

    private var serviceRecycleAdapter: NSServiceManagementRecycleAdapter? = null
    private var isFragmentLoad = false

    override val viewModel: NSServiceManagementViewModel by lazy {
        ViewModelProvider(this)[NSServiceManagementViewModel::class.java]
    }

    private val capabilitiesViewModel: NSCapabilitiesViewModel by lazy {
        ViewModelProvider(this)[NSCapabilitiesViewModel::class.java]
    }

    private val fleetViewModel: NSFleetViewModel by lazy {
        ViewModelProvider(this)[NSFleetViewModel::class.java]
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
        baseObserveViewModel(capabilitiesViewModel)
        observeViewModel()
        setServiceManagementAdapter()
        viewModel.setCapabilityModel(capabilitiesViewModel)
        viewModel.setFleetModel(fleetViewModel)
        FilterHelper(activity, binding.rvServiceFilter) { _, list ->
            viewModel.apply {
                selectedFilterList = list
                filterData(list)
            }
        }
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

                isServiceListAvailable.observe(
                    viewLifecycleOwner
                ) {
                    this@NSServiceManagementFragment.viewModel.isProgressShowing.value = false
                    srlRefresh.isRefreshing = false
                    filterData(selectedFilterList)
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
        viewModel.getServiceList(!isFragmentLoad)
        isFragmentLoad = true
    }

    /**
     * Set listener
     */
    private fun setListener() {
        viewModel.apply {
            with(binding) {
                srlRefresh.setOnRefreshListener {
                    this@NSServiceManagementFragment.viewModel.getServiceList(false)
                }

                layoutHomeHeader.apply {
                    ivClearData.setSafeOnClickListener {
                        etSearch.setText("")
                    }

                    tvHeaderBtn.setSafeOnClickListener {
                        showCreateServiceDialog()
                    }

                    etSearch.addOnTextChangedListener(
                        onTextChanged = { text, _, _, _ ->
                            ivClearData.setVisibility(text.toString().isNotEmpty())
                            filterData(selectedFilterList, text.toString())
                        }
                    )
                }
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
                                    createdServiceName = layoutServiceName.edtValue.text.toString()
                                    createdServiceDescription = etServiceDescription.text.toString()
                                    dialog.dismiss()
                                    createService(true)
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
                        createdServiceName = null
                        createdServiceDescription = null
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
                            viewModel, { serviceId, capabilityId, fleets, isDirectFleet, isFleetUpdate ->
                                selectedFleets = fleets
                                selectedServiceId = serviceId
                                isFleetNeedToUpdate = isFleetUpdate
                                if (isDirectFleet && isFleetUpdate) {
                                    serviceFleetsUpdate(serviceId, fleets, true)
                                } else {
                                    serviceCapabilityUpdate(serviceId, capabilityId, true)
                                }
                            }, { serviceId, isEnable ->
                                serviceEnableDisable(serviceId, isEnable, true)
                            })
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

    private fun filterData(
        filterList: MutableList<ActiveInActiveFilter>,
        searchText: String = binding.layoutHomeHeader.etSearch.text.toString()
    ) {
        with(viewModel) {
            serviceRecycleAdapter?.apply {
                val filterTypes = getFilterSelectedTypes(filterList)

                if (filterTypes.isNotEmpty()) {

                    val filter = serviceItemList.filter { filterTypes.contains(if (it.isActive) NSConstants.ACTIVE else NSConstants.IN_ACTIVE) } as MutableList<NSGetServiceListData>
                    setAdapterData(searchText, if (searchText.isEmpty()) filter else filter.filter {
                        it.name
                            ?.lowercase()?.contains(searchText.lowercase()) == true
                    } as MutableList<NSGetServiceListData>)

                } else {

                    setAdapterData(searchText, if (searchText.isEmpty()) serviceItemList else serviceItemList.filter {
                        it.name?.lowercase()?.contains(searchText.lowercase()) == true
                    } as MutableList<NSGetServiceListData>)

                }
            }
        }
    }

    private fun setAdapterData(search: String, serviceItemList: MutableList<NSGetServiceListData>) {
        serviceRecycleAdapter?.apply {
            if (search.isEmpty()) {
                setSubList(viewModel.capabilityItemList, viewModel.fleetItemList)
            }
            setData(serviceItemList)
        }
    }
}