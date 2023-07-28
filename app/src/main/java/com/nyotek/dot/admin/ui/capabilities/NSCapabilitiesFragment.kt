package com.nyotek.dot.admin.ui.capabilities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.NSAlertButtonClickEvent
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.callbacks.NSCapabilitiesCallback
import com.nyotek.dot.admin.common.callbacks.NSCapabilityCallback
import com.nyotek.dot.admin.common.callbacks.NSSuccessFailCallback
import com.nyotek.dot.admin.common.callbacks.NSSwitchCallback
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.buildAlertDialog
import com.nyotek.dot.admin.common.utils.gone
import com.nyotek.dot.admin.common.utils.setVisibility
import com.nyotek.dot.admin.common.utils.setupWithAdapterAndCustomLayoutManager
import com.nyotek.dot.admin.databinding.LayoutCreateCapabiltiesBinding
import com.nyotek.dot.admin.databinding.NsFragmentCapabilitiesBinding
import com.nyotek.dot.admin.repository.network.responses.CapabilitiesDataItem
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

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
        viewModel.getLocalLanguageList()
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
            getCapabilitiesList(
                isShowProgress,
                isCapabilityAvailableCheck = isCapabilityCheck,
                callback = object : NSCapabilityCallback {
                    override fun onCapability(capabilities: MutableList<CapabilitiesDataItem>) {
                        binding.srlRefresh.isRefreshing = false
                        setAdapter(capabilities)
                    }
                })
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
                    if (capabilitiesRecycleAdapter == null) {
                        capabilitiesRecycleAdapter =
                            NSCapabilitiesRecycleAdapter(
                                object : NSCapabilitiesCallback {
                                    override fun onItemSelect(
                                        model: CapabilitiesDataItem,
                                        isDelete: Boolean
                                    ) {
                                        adapterCapabilityItemSelect(model, isDelete)
                                    }
                                },
                                object : NSSwitchCallback {
                                    override fun switch(serviceId: String, isEnable: Boolean) {
                                        capabilityEnableDisable(serviceId, isEnable)
                                    }
                                })
                        setupWithAdapterAndCustomLayoutManager(
                            capabilitiesRecycleAdapter!!,
                            GridLayoutManager(activity, 2)
                        )
                        isNestedScrollingEnabled = false
                    }
                    capabilitiesRecycleAdapter?.setData(capabilities)
                }
            }
        }
    }

    /**
     * Adapter capability item select
     *
     * @param model capability Item
     * @param isDelete check click for delete or not
     */
    private fun adapterCapabilityItemSelect(model: CapabilitiesDataItem, isDelete: Boolean) {
        viewModel.apply {
            selectedCapabilities = model
            if (isDelete) {
                showCommonDialog(
                    title = "",
                    message = stringResource.doYouWantToDelete,
                    alertKey = NSConstants.KEY_ALERT_CAPABILITIES_DELETE,
                    positiveButton = stringResource.ok,
                    negativeButton = stringResource.cancel
                )
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPositiveButtonClickEvent(event: NSAlertButtonClickEvent) {
        viewModel.apply {
            if (event.buttonType == NSConstants.KEY_ALERT_BUTTON_POSITIVE && event.alertKey == NSConstants.KEY_ALERT_CAPABILITIES_DELETE) {
                selectedCapabilities?.apply {
                    if (id != null) {
                        capabilitiesDelete(id, true)
                    }
                }
            }
        }
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

                            layoutName.rvLanguageTitle.refreshAdapter()
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
                                    dataItem?.id ?: "",
                                    object : NSSuccessFailCallback {
                                        override fun onResponse(isSuccess: Boolean) {
                                            progress.gone()
                                            dialog.dismiss()
                                            if (isSuccess) {
                                                getCapabilityList(true, isCapabilityCheck = false)
                                            } else {
                                                isProgressShowing.value = false
                                            }
                                        }
                                    })
                            }
                        }
                    }
                }
            }
        }
    }
}