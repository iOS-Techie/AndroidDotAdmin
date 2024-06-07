package com.nyotek.dot.admin.ui.tabs.dispatch

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.nyotek.dot.admin.base.BaseFragment
import com.nyotek.dot.admin.common.DispatchSpinnerAdapter
import com.nyotek.dot.admin.common.FilterHelper
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.extension.addOnTextChangedListener
import com.nyotek.dot.admin.common.extension.getLngValue
import com.nyotek.dot.admin.common.extension.gone
import com.nyotek.dot.admin.common.extension.navigateSafeNew
import com.nyotek.dot.admin.common.extension.setGlideWithHolder
import com.nyotek.dot.admin.common.extension.setVisibility
import com.nyotek.dot.admin.common.extension.setupWithAdapterAndCustomLayoutManager
import com.nyotek.dot.admin.common.extension.visible
import com.nyotek.dot.admin.databinding.NsFragmentDispatchBinding
import com.nyotek.dot.admin.models.responses.ActiveInActiveFilter
import com.nyotek.dot.admin.models.responses.AllDispatchListResponse
import com.nyotek.dot.admin.models.responses.NSDispatchOrderListData
import com.nyotek.dot.admin.models.responses.NSGetServiceListData
import com.nyotek.dot.admin.ui.tabs.fleets.vehicle.VehicleHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DispatchFragment : BaseFragment<NsFragmentDispatchBinding>() {

    private val viewModel by viewModels<DispatchViewModel>()
    private lateinit var themeUI: DispatchUI
    private var dispatchRecycleAdapter: NSDispatchManagementRecycleAdapter? = null

    private var dispatchSpinnerAdapter: DispatchSpinnerAdapter? = null

    companion object {
        fun newInstance() = DispatchFragment()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentDispatchBinding {
        return NsFragmentDispatchBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        themeUI = DispatchUI(binding, viewModel.colorResources)
        binding.apply {
            viewModel.colorResources.getStringResource().apply {
                setLayoutHeader(layoutHomeHeader, dispatchManagement, isSearch = true)
                binding.layoutSpinner.clDispatchBorderBg.gone()
            }
        }

        if (dispatchRecycleAdapter == null || DispatchHelper.isCancelledStatus()) {
            if (DispatchHelper.isCancelledStatus()) {
                DispatchHelper.setCancelled(false)
                Handler(Looper.getMainLooper()).postDelayed({
                    viewModel.getDispatchList(true)
                }, 300)
            } else {
                viewModel.getDispatchList(true)
            }
        } else {
            updateChangedDetail()
        }
    }

    private fun updateChangedDetail() {
        viewModel.apply {
            if (selectedPosition != -1) {
                val item = DispatchHelper.getDispatchList()[selectedPosition]
                dispatchRecycleAdapter?.updateSingleData(item, selectedPosition)
                selectedPosition = -1
            }
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        observeBaseViewModel(viewModel)
        observe()
    }

    override fun setListener() {
        super.setListener()
        binding.apply {
            srlRefresh.setOnRefreshListener {
                viewModel.getDispatchList(false, viewModel.selectedServiceId?:"")
            }

            layoutHomeHeader.apply {
                ivClearData.setOnClickListener {
                    etSearch.setText("")
                }
            }
        }
    }

    private fun observe() {
        viewModel.apply {
            dispatchListObserve.observe(
                viewLifecycleOwner
            ) {
                setDispatchApiResponseData(it)
            }

            refresh.observe(
                viewLifecycleOwner
            ) {
                binding.srlRefresh.isRefreshing = it
            }
        }
    }

    private fun setDispatchApiResponseData(response: AllDispatchListResponse?) {
        binding.apply {
            viewModel.apply {
                val serviceList = response?.serviceList ?: arrayListOf()
                val dispatchList = response?.dispatchList ?: arrayListOf()

                layoutSpinner.clDispatchBorderBg.visible()
                if (dispatchSpinnerAdapter == null) {
                    dispatchSpinnerAdapter = DispatchSpinnerAdapter(requireContext(), serviceList)
                    layoutSpinner.spinnerDispatchSelect.adapter = dispatchSpinnerAdapter
                    layoutSpinner.spinnerDispatchSelect.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            position: Int,
                            p3: Long
                        ) {
                            val model: NSGetServiceListData = serviceList[position]
                            selectedServiceLogo = model.logoUrl
                            selectedServiceId = model.serviceId?:""

                            if (!isApiCalled) {
                                isApiCalled = true
                                setDispatchData(dispatchList)
                            } else {
                                viewModel.getDispatchList(true, selectedServiceId ?: "")
                                //getDispatchListData(true, isSwipeRefresh = true, selectedServiceId)
                            }
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {

                        }
                    }
                } else {
                    setDispatchData(dispatchList)
                }
            }
        }
    }

    private fun setDispatchData(dispatchList: MutableList<NSDispatchOrderListData>) {
        binding.apply {
            viewModel.apply {
                srlRefresh.isRefreshing = false

                FilterHelper(activity, binding.rvDispatchFilter, if (filterList.isEmpty()) FilterHelper.getDispatchFilterLists() else filterList, colorResources) { _, list ->
                    viewModel.apply {
                        filterList = list
                        setFilterData(dispatchList, filterList)
                    }
                }

                layoutHomeHeader.etSearch.addOnTextChangedListener(
                    onTextChanged = { text, _, _, _ ->
                        layoutHomeHeader.ivClearData.setVisibility(text.toString().isNotEmpty())
                        setFilterData(dispatchList, filterList, text.toString())
                    }
                )

                rvFleetsList.apply {
                    dispatchRecycleAdapter =
                        NSDispatchManagementRecycleAdapter(themeUI,{ vendorId, vendorIcon, vendorName ->
                            getVendorInfo(vendorId) {
                                vendorIcon.setGlideWithHolder(it?.logo, it?.logoScale, 200)
                                vendorName.text = getLngValue(it?.name)
                            }
                        }){ dispatch, position ->
                            openDispatchDetail(dispatch, position)
                        }
                    setupWithAdapterAndCustomLayoutManager(
                        dispatchRecycleAdapter!!,
                        GridLayoutManager(activity, 3)
                    )
                    adapter = dispatchRecycleAdapter
                    rvFleetsList.setHasFixedSize(true)
                    isNestedScrollingEnabled = true
                }

                setFilterData(dispatchList, filterList)
            }
        }
    }

    private fun setFilterData(fleetList: MutableList<NSDispatchOrderListData>,
                              filterList: MutableList<ActiveInActiveFilter>,
                              searchText: String = binding.layoutHomeHeader.etSearch.text.toString()
    ) {
        viewModel.apply {
            viewModelScope.launch {
                val filterTypes = getTypesFilterSelectedAsync(filterList)

                if (filterTypes.isNotEmpty()) {
                    val filter = filterDataConcurrently(fleetList, filterTypes)
                    setAdapterData(if (searchText.isEmpty()) filter else filterDataConcurrently(filter, searchText))
                } else {
                    setAdapterData(if (searchText.isEmpty()) fleetList else filterDataConcurrently(fleetList, searchText))
                }
            }
        }
    }

    private fun setAdapterData(dispatchList: MutableList<NSDispatchOrderListData>) {
        dispatchRecycleAdapter?.apply {
            DispatchHelper.setDispatchList(dispatchList)
            setData(DispatchHelper.getDispatchList())
        }
        viewModel.hideProgress()
        binding.srlRefresh.isRefreshing = false
    }

    private fun openDispatchDetail(model: NSDispatchOrderListData, position: Int) {
        viewModel.selectedPosition = position
        val gson = Gson().toJson(model)
        val bundle = bundleOf(NSConstants.DISPATCH_DETAIL_KEY to gson, NSConstants.VENDOR_SERVICE_ID_KEY to viewModel.selectedServiceId)
        findNavController().navigateSafeNew(DispatchFragmentDirections.actionDispatchToDispatchDetail(bundle))
    }
}