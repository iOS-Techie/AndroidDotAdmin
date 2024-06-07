package com.nyotek.dot.admin.ui.tabs.services

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.nyotek.dot.admin.base.BaseFragment
import com.nyotek.dot.admin.common.FilterHelper
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.extension.addOnTextChangedListener
import com.nyotek.dot.admin.common.extension.setSafeOnClickListener
import com.nyotek.dot.admin.common.extension.setVisibility
import com.nyotek.dot.admin.common.extension.setupWithAdapterAndCustomLayoutManager
import com.nyotek.dot.admin.databinding.NsFragmentServiceManagementBinding
import com.nyotek.dot.admin.models.responses.ActiveInActiveFilter
import com.nyotek.dot.admin.models.responses.NSGetServiceListData
import com.nyotek.dot.admin.models.responses.ServiceMainModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ServicesFragment : BaseFragment<NsFragmentServiceManagementBinding>() {

    private val viewModel by viewModels<ServiceViewModel>()
    private var isLoadFragment: Boolean = false
    private lateinit var themeUI: ServiceUI
    private var serviceRecycleAdapter: NSServiceManagementRecycleAdapter? = null

    companion object {
        fun newInstance() = ServicesFragment()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentServiceManagementBinding {
        return NsFragmentServiceManagementBinding.inflate(inflater, container, false)
    }

    override fun loadFragment() {
        super.loadFragment()
        if (!isLoadFragment) {
            isLoadFragment = true
            viewModel.colorResources.getStringResource().apply {
                setLayoutHeader(binding.layoutHomeHeader, serviceManagement, isSearch = true)
            }

            viewModel.getServiceList(true)
        }
    }

    override fun setupViews() {
        super.setupViews()
        themeUI = ServiceUI(activity, binding, viewModel, viewModel.colorResources)
    }

    override fun observeViewModel() {
        super.observeViewModel()
        observeBaseViewModel(viewModel)
        observe()
    }

    override fun setListener() {
        super.setListener()
        binding.apply {
            viewModel.apply {
                srlRefresh.setOnRefreshListener {
                    viewModel.getServiceList(false)
                }

                layoutHomeHeader.apply {
                    ivClearData.setSafeOnClickListener {
                        etSearch.setText("")
                    }
                }
            }
        }
    }

    private fun observe() {
        viewModel.apply {
            serviceMainList.observe(
                viewLifecycleOwner
            ) {
                setServiceAdapterData(it)
            }

            refresh.observe(
                viewLifecycleOwner
            ) {
                binding.srlRefresh.isRefreshing = it
            }
        }
    }

    private fun setServiceAdapterData(serviceMainModel: ServiceMainModel) {
        binding.apply {
            viewModel.apply {
                srlRefresh.isRefreshing = false
                FilterHelper(activity, binding.rvServiceFilter, if (filterList.isEmpty()) FilterHelper.getCommonFilterLists() else filterList, colorResources) { _, list ->
                    viewModel.apply {
                        filterList = list
                        filterData(serviceMainModel, filterList)
                    }
                }

                layoutHomeHeader.etSearch.addOnTextChangedListener(
                    onTextChanged = { text, _, _, _ ->
                        layoutHomeHeader.ivClearData.setVisibility(text.toString().isNotEmpty())
                        filterData(serviceMainModel, filterList, text.toString())
                    }
                )

                rvServiceList.apply {
                    serviceRecycleAdapter =
                        NSServiceManagementRecycleAdapter(
                            themeUI,
                            serviceMainModel.capabilities,
                            serviceMainModel.fleetDataList,
                            { serviceId, capabilityId ->
                                serviceCapabilityUpdate(serviceId, capabilityId)
                            },
                            { serviceId, fleets ->
                                serviceFleetUpdate(serviceId, fleets)
                            }) { serviceId, isEnable ->
                            //Service Enable Disable
                            serviceEnableDisable(isEnable, serviceId)
                        }
                    setupWithAdapterAndCustomLayoutManager(
                        serviceRecycleAdapter!!,
                        GridLayoutManager(activity, 3)
                    )
                    adapter = serviceRecycleAdapter
                    isNestedScrollingEnabled = true
                }

                filterData(serviceMainModel, filterList)
            }
        }
    }

    private fun filterData(serviceMainModel: ServiceMainModel, filterList: MutableList<ActiveInActiveFilter>, searchText: String = binding.layoutHomeHeader.etSearch.text.toString()
    ) {
        with(viewModel) {
            serviceRecycleAdapter?.apply {
                val filterTypes = getFilterSelectedTypes(filterList)

                if (filterTypes.isNotEmpty()) {
                    val filter = serviceMainModel.serviceList?.filter { filterTypes.contains(if (it.isActive) NSConstants.ACTIVE else NSConstants.IN_ACTIVE) } as MutableList<NSGetServiceListData>
                    val searchFilter = filter.filter { it.name?.lowercase()?.contains(searchText.lowercase()) == true } as MutableList<NSGetServiceListData>
                    setAdapterData(searchText, if (searchText.isEmpty()) filter else searchFilter, serviceMainModel)
                } else {
                    val searchFilter = serviceMainModel.serviceList?.filter { it.name?.lowercase()?.contains(searchText.lowercase()) == true } as MutableList<NSGetServiceListData>
                    setAdapterData(searchText, if (searchText.isEmpty()) serviceMainModel.serviceList?: arrayListOf() else searchFilter, serviceMainModel)
                }
            }
        }
    }

    private fun setAdapterData(search: String, serviceItemList: MutableList<NSGetServiceListData>, serviceMainModel: ServiceMainModel) {
        serviceRecycleAdapter?.apply {
            /*if (search.isEmpty()) {
                setSubList(capabilities, fleetList)
            }*/
            setData(serviceItemList)
        }
    }
}