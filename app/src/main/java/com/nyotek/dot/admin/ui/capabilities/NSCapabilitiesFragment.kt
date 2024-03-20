package com.nyotek.dot.admin.ui.capabilities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.FilterHelper
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.buildAlertDialog
import com.nyotek.dot.admin.common.utils.gone
import com.nyotek.dot.admin.common.utils.isValidList
import com.nyotek.dot.admin.common.utils.setVisibility
import com.nyotek.dot.admin.common.utils.setupWithAdapterAndCustomLayoutManager
import com.nyotek.dot.admin.databinding.LayoutCreateCapabiltiesBinding
import com.nyotek.dot.admin.databinding.NsFragmentCapabilitiesBinding
import com.nyotek.dot.admin.repository.network.responses.ActiveInActiveFilter
import com.nyotek.dot.admin.repository.network.responses.CapabilitiesDataItem
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListData

class NSCapabilitiesFragment : BaseViewModelFragment<NSCapabilitiesViewModel, NsFragmentCapabilitiesBinding>() {

    private var capabilitiesRecycleAdapter: NSCapabilitiesRecycleAdapter? = null
    private var isFragmentAdd = false

    companion object {
        fun newInstance() = NSCapabilitiesFragment()
    }

    override val viewModel: NSCapabilitiesViewModel by lazy {
        ViewModelProvider(this)[NSCapabilitiesViewModel::class.java]
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentCapabilitiesBinding {
        return NsFragmentCapabilitiesBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        baseObserveViewModel(viewModel)
        observeViewModel()
    }

    override fun loadFragment() {
        super.loadFragment()
        initUI()
        viewCreated()
        setListener()
    }

    override fun observeViewModel() {
        super.observeViewModel()
        binding.apply {
            viewModel.apply {
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
                setLayoutHeader(layoutHomeHeader, capabilities, createCapabilities)
            }
        }
    }

    /**
     * View created
     */
    private fun viewCreated() {
        getCapabilityList(true)
        viewModel.getLocalLanguages(isSelect = true)
        isFragmentAdd = true
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(binding) {
            srlRefresh.setOnRefreshListener {
                getCapabilityList(false)
            }

            layoutHomeHeader.tvHeaderBtn.setOnClickListener {
                showCreateCapabilityDialog(isCreate = true, null)
            }
        }
    }

    /**
     * Get capability list
     *
     * @param isShowProgress
     */
    private fun getCapabilityList(isShowProgress: Boolean, isCapabilityCheck: Boolean = true) {
        viewModel.apply {
            getCapabilities(isShowProgress, isCapabilityCheck) {
                binding.srlRefresh.isRefreshing = false
                setAdapter(it)
            }
        }
    }

    /**
     * Set capabilities adapter
     *
     */
    private fun setAdapter(capabilities: MutableList<CapabilitiesDataItem>) {
        viewModel.apply {
            with(binding) {
                with(rvCapabilitiesList) {

                    FilterHelper(activity, binding.rvCapabilityFilter, if (filterList.isEmpty()) FilterHelper.getCommonFilterLists() else filterList) { _, list ->
                        viewModel.apply {
                            filterList = list
                            filterData(capabilities, filterList)
                        }
                    }


                    if (capabilitiesRecycleAdapter == null) {
                        capabilitiesRecycleAdapter = NSCapabilitiesRecycleAdapter({ model, isDelete ->
                            adapterCapabilityItemSelect(model, isDelete)
                        }, { serviceId, isEnable ->
                            capabilityEnableDisable(serviceId, isEnable)
                        })

                        setupWithAdapterAndCustomLayoutManager(
                            capabilitiesRecycleAdapter!!,
                            GridLayoutManager(activity, 2)
                        )
                        isNestedScrollingEnabled = false
                    }
                    filterData(capabilities, filterList)
                }
            }
        }
    }

    private fun filterData(capabilities: MutableList<CapabilitiesDataItem>, filterList: MutableList<ActiveInActiveFilter>) {
        with(viewModel) {
            capabilitiesRecycleAdapter?.apply {
                val filterTypes = getFilterSelectedTypes(filterList)

                if (filterTypes.isNotEmpty()) {
                    val filter = capabilities.filter { filterTypes.contains(if (it.isActive) NSConstants.ACTIVE else NSConstants.IN_ACTIVE) } as MutableList<CapabilitiesDataItem>
                    setAdapterData(filter)
                } else {
                    setAdapterData(capabilities)
                }
            }
        }
    }

    private fun setAdapterData(capabilities: MutableList<CapabilitiesDataItem>) {
        capabilitiesRecycleAdapter?.apply {
            setData(capabilities)
        }
        viewModel.hideProgress()
    }

    /**
     * Adapter capability item select
     *
     * @param model capability Item
     * @param isDelete check click for delete or not
     */
    private fun adapterCapabilityItemSelect(model: CapabilitiesDataItem, isDelete: Boolean) {
        viewModel.apply {
            if (isDelete) {
                showCommonDialog(
                    title = "",
                    message = stringResource.doYouWantToDelete,
                    positiveButton = stringResource.ok,
                    negativeButton = stringResource.cancel){
                    if (!it) {
                        model.apply {
                            if (id != null) {
                                capabilitiesDelete(id) { list ->
                                    setAdapter(list)
                                }
                            }
                        }
                    }
                }
            } else {
                showCreateCapabilityDialog(isCreate = false, model)
            }
        }
    }

    /**
     * Capability enable disable
     *
     * @param serviceId capability Id
     * @param isEnable switch Enable disable
     */
    private fun capabilityEnableDisable(serviceId: String, isEnable: Boolean) {
        viewModel.capabilityEnableDisable(
            serviceId,
            isEnable,
            false
        )
    }

    private fun showCreateCapabilityDialog(isCreate: Boolean, dataItem: CapabilitiesDataItem?) {
        binding.apply {
            viewModel.apply {
                val capabilityName = dataItem?.label ?: hashMapOf()

                buildAlertDialog(
                    requireContext(),
                    LayoutCreateCapabiltiesBinding::inflate
                ) { dialog, binding ->

                    binding.apply {
                        stringResource.apply {
                            val layoutName = binding.layoutName

                            tvCapabilityTitle.text =
                                if (isCreate) createCapabilities else updateCapabilities
                            viewLine.setVisibility(isCreate)
                            tvCapabilityActive.text = inActive
                            layoutName.tvCommonTitle.text = name
                            tvCancel.text = cancel
                            tvSave.text =
                                if (isCreate) create else update
                            layoutName.rvLanguageTitle.visibility = View.VISIBLE
                            layoutName.edtValue.setText("")

                            //layoutName.rvLanguageTitle.refreshAdapter()
                            NSUtilities.setLanguageText(
                                layoutName.edtValue,
                                layoutName.rvLanguageTitle,
                                capabilityName
                            )

                            tvCancel.setOnClickListener {
                                dialog.dismiss()
                            }

                            tvSave.setOnClickListener {
                                progress.visibility = View.VISIBLE
                                createEditCapability(
                                    capabilityName,
                                    isCreate,
                                    dataItem?.id ?: "") { list ->
                                    progress.gone()
                                    dialog.dismiss()
                                    if (list.isValidList()) {
                                        setAdapter(list)
                                    } else {
                                        viewModel.hideProgress()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}