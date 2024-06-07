package com.nyotek.dot.admin.ui.tabs.capabilities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.nyotek.dot.admin.base.BaseFragment
import com.nyotek.dot.admin.common.FilterHelper
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSUtilities
import com.nyotek.dot.admin.common.extension.buildAlertDialog
import com.nyotek.dot.admin.common.extension.gone
import com.nyotek.dot.admin.common.extension.isValidList
import com.nyotek.dot.admin.common.extension.setupWithAdapterAndCustomLayoutManager
import com.nyotek.dot.admin.databinding.LayoutCreateCapabiltiesBinding
import com.nyotek.dot.admin.databinding.NsFragmentCapabilitiesBinding
import com.nyotek.dot.admin.models.responses.ActiveInActiveFilter
import com.nyotek.dot.admin.models.responses.CapabilitiesDataItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CapabilitiesFragment : BaseFragment<NsFragmentCapabilitiesBinding>() {

    private val viewModel by viewModels<CapabilitiesViewModel>()
    private lateinit var themeUI: CapabilitiesUI
    private var capabilitiesRecycleAdapter: NSCapabilitiesRecycleAdapter? = null
    private var isLoadFragment: Boolean = false

    companion object {
        fun newInstance() = CapabilitiesFragment()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentCapabilitiesBinding {
        return NsFragmentCapabilitiesBinding.inflate(inflater, container, false)
    }

    override fun loadFragment() {
        super.loadFragment()
        if (!isLoadFragment) {
            isLoadFragment = true
            viewModel.colorResources.getStringResource().apply {
                setLayoutHeader(binding.layoutHomeHeader, capabilities, createCapabilities)
            }

            themeUI = CapabilitiesUI(binding, viewModel.colorResources, viewModel.languageConfig)
            viewModel.getLocalLanguages()
            viewModel.getCapabilities(true)
        }
    }

    override fun setListener() {
        super.setListener()
        binding.apply {
            srlRefresh.setOnRefreshListener {
                viewModel.getCapabilities(false, isApiDataCheck = true)
            }

            layoutHomeHeader.tvHeaderBtn.setOnClickListener {
                showCreateCapabilityDialog(isCreate = true, null)
            }
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        observeBaseViewModel(viewModel)
        observe()
    }

    private fun observe() {
        viewModel.apply {
            capabilitiesList.observe(
                viewLifecycleOwner
            ) {
                setAdapter(it)
            }

            refresh.observe(
                viewLifecycleOwner
            ) {
                binding.srlRefresh.isRefreshing = it
            }
        }
    }

    private fun setAdapter(capabilities: MutableList<CapabilitiesDataItem>) {
        viewModel.apply {
            binding.apply {
                rvCapabilitiesList.apply {
                    FilterHelper(activity, binding.rvCapabilityFilter, if (filterList.isEmpty()) FilterHelper.getCommonFilterLists() else filterList, colorResources) { _, list ->
                        viewModel.apply {
                            filterList = list
                            filterData(capabilities, filterList)
                        }
                    }

                    capabilitiesRecycleAdapter = NSCapabilitiesRecycleAdapter(themeUI, { model, isDelete ->
                         adapterCapabilityItemSelect(model, isDelete)
                    }, { serviceId, isEnable ->
                        capabilitiesEnableDisable(isEnable, serviceId)
                    })

                    setupWithAdapterAndCustomLayoutManager(
                        capabilitiesRecycleAdapter!!,
                        GridLayoutManager(activity, 2)
                    )
                    isNestedScrollingEnabled = false
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
        binding.srlRefresh.isRefreshing = false
    }

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
                                capabilitiesDelete(id)
                            }
                        }
                    }
                }
            } else {
                showCreateCapabilityDialog(isCreate = false, model)
            }
        }
    }

    private fun showCreateCapabilityDialog(isCreate: Boolean, dataItem: CapabilitiesDataItem?) {
        binding.apply {
            viewModel.apply {
                val labelMap = dataItem?.label ?: hashMapOf()
                val capabilityName = HashMap(labelMap)

                buildAlertDialog(
                    requireContext(),
                    LayoutCreateCapabiltiesBinding::inflate
                ) { dialog, bind ->
                    themeUI.setCapabilitiesCreateEditUI(bind, isCreate)
                    bind.apply {
                        stringResource.apply {
                            val layoutName = bind.layoutName
                            layoutName.edtValue.setText("")

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
                                createEditCapability(capabilityName, isCreate, dataItem?.id ?: "") { list ->
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