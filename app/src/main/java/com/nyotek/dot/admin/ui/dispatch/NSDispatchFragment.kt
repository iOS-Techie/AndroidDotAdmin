package com.nyotek.dot.admin.ui.dispatch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.BrandLogoHelper
import com.nyotek.dot.admin.common.DispatchSpinnerAdapter
import com.nyotek.dot.admin.common.FilterHelper
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSServiceConfig
import com.nyotek.dot.admin.common.callbacks.NSFileUploadCallback
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.addOnTextChangedListener
import com.nyotek.dot.admin.common.utils.buildAlertDialog
import com.nyotek.dot.admin.common.utils.getLngValue
import com.nyotek.dot.admin.common.utils.gone
import com.nyotek.dot.admin.common.utils.setVisibility
import com.nyotek.dot.admin.common.utils.setupWithAdapterAndCustomLayoutManager
import com.nyotek.dot.admin.common.utils.status
import com.nyotek.dot.admin.common.utils.visible
import com.nyotek.dot.admin.databinding.LayoutCreateFleetBinding
import com.nyotek.dot.admin.databinding.NsFragmentDispatchBinding
import com.nyotek.dot.admin.repository.network.requests.NSCreateCompanyRequest
import com.nyotek.dot.admin.repository.network.responses.ActiveInActiveFilter
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.NSDispatchOrderListData
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListData
import com.nyotek.dot.admin.ui.dispatch.detail.NSDispatchDetailFragment
import com.nyotek.dot.admin.ui.fleets.detail.NSFleetDetailFragment

class NSDispatchFragment : BaseViewModelFragment<NSDispatchViewModel, NsFragmentDispatchBinding>(),
    NSFileUploadCallback {

    //Get Service list
    override val viewModel: NSDispatchViewModel by lazy {
        ViewModelProvider(this)[NSDispatchViewModel::class.java]
    }

    private var dispatchRecycleAdapter: NSDispatchManagementRecycleAdapter? = null
    private var isFragmentLoad = false
    private val brandLogoHelper: BrandLogoHelper = BrandLogoHelper(this, callback = this)

    companion object {
        fun newInstance() = NSDispatchFragment()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentDispatchBinding {
        return NsFragmentDispatchBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        baseObserveViewModel(viewModel)
        observeViewModel()
        setFleetManagementAdapter()
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
                    dispatchManagement,
                    isSearch = true
                )
            }
        }
    }

    /**
     * View created
     */
    private fun viewCreated() {
        viewModel.apply {
            binding.layoutSpinner.clDispatchBorderBg.gone()
            setDispatchServiceFilter(!isFragmentLoad)
            //getFleetFromApi(!isFragmentLoad)
            isFragmentLoad = true
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
        binding.apply {
            viewModel.apply {
                layoutHomeHeader.apply {
                    ivClearData.setOnClickListener {
                        etSearch.setText("")
                    }

                    srlRefresh.setOnRefreshListener {
                        callDispatchFromService(selectedServiceId, false)
                    }

                    tvHeaderBtn.setOnClickListener {
                        showFleetCreateDialog()
                    }
                }
            }
        }
    }

    private fun setDispatchServiceFilter(isShowProgress: Boolean) {
        binding.apply {
            viewModel.apply {
                getServiceListApi(isShowProgress) {
                    layoutSpinner.clDispatchBorderBg.visible()
                    val adapter = DispatchSpinnerAdapter(requireContext(), it)
                    layoutSpinner.spinnerDispatchSelect.adapter = adapter
                    layoutSpinner.spinnerDispatchSelect.onItemSelectedListener = object : OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                            val model: NSGetServiceListData = it[position]
                            callDispatchFromService(model.serviceId, isShowProgress)
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {

                        }

                    }
                }
            }
        }
    }

    private fun callDispatchFromService(serviceId: String?, isShowProgress: Boolean) {
        viewModel.apply {
            binding.apply {
                selectedServiceId = serviceId
                if (serviceId?.isNotEmpty() == true) {
                    getDispatchFromService(serviceId, isShowProgress) { item ->
                        binding.srlRefresh.isRefreshing = false
                        setDispatchData(item)
                    }
                } else {
                    binding.srlRefresh.isRefreshing = false
                }
            }
        }
    }

    private fun setDispatchData(dispatchList: MutableList<NSDispatchOrderListData>) {
        binding.apply {
            viewModel.apply {

                FilterHelper(activity, binding.rvDispatchFilter, if (filterList.isEmpty()) FilterHelper.getDispatchFilterLists() else filterList) { _, list ->
                    viewModel.apply {
                        filterList = list
                        setFilterData(dispatchList, list)
                    }
                }

                layoutHomeHeader.etSearch.addOnTextChangedListener(
                    onTextChanged = { text, _, _, _ ->
                        layoutHomeHeader.ivClearData.setVisibility(text.toString().isNotEmpty())
                        setFilterData(dispatchList, filterList, text.toString())
                    }
                )

                setFilterData(dispatchList, filterList)
            }
        }
    }

    /**
     * Set fleet adapter
     *
     */
    private fun setFleetManagementAdapter() {
        with(binding) {
            with(viewModel) {
                with(rvFleetsList) {
                    dispatchRecycleAdapter = NSDispatchManagementRecycleAdapter(activity) {
                        openDispatchDetail(it)
                    }

                    setupWithAdapterAndCustomLayoutManager(dispatchRecycleAdapter!!, GridLayoutManager(activity, 3))
                }
            }
        }
    }

    private fun openDispatchDetail(model: NSDispatchOrderListData) {
        val gson = Gson().toJson(model)
        val bundle = bundleOf(NSConstants.DISPATCH_DETAIL_KEY to gson)
        dispatchManagementFragmentChangeCallback?.setFragment(
            this@NSDispatchFragment.javaClass.simpleName,
            NSDispatchDetailFragment.newInstance(bundle),
            true, bundle
        )
    }

    private fun setFilterData(fleetList: MutableList<NSDispatchOrderListData>,
        filterList: MutableList<ActiveInActiveFilter>,
        searchText: String = binding.layoutHomeHeader.etSearch.text.toString()
    ) {
        with(viewModel) {
            val filterTypes = getTypesFilterSelected(filterList)

            if (filterTypes.isNotEmpty()) {
                val filter = fleetList.filter { it.status.any { statusFilter -> filterTypes.contains(NSUtilities.capitalizeFirstLetter(statusFilter.status.replace("_", " "))) }} as MutableList<NSDispatchOrderListData>
                setAdapterData(if (searchText.isEmpty()) filter else filter.filter {
                    (it.userMetadata?.userName?.lowercase()?:"").contains(searchText.lowercase()) or
                            (it.userMetadata?.userPhone?.lowercase()?:"").contains(searchText.lowercase())
                } as MutableList<NSDispatchOrderListData>)

            } else {

                setAdapterData(if (searchText.isEmpty()) fleetList else fleetList.filter { (it.userMetadata?.userName?.lowercase()?:"").contains(searchText.lowercase()) or (it.userMetadata?.userPhone?.lowercase()?:"").contains(searchText.lowercase())
                } as MutableList<NSDispatchOrderListData>)

            }
        }
    }

    private fun setAdapterData(serviceItemList: MutableList<NSDispatchOrderListData>) {
        dispatchRecycleAdapter?.apply {
            setData(serviceItemList)
        }
    }

    private fun showFleetCreateDialog() {
        binding.apply {
            viewModel.apply {

                buildAlertDialog(
                    requireContext(),
                    LayoutCreateFleetBinding::inflate
                ) { dialog, binding ->

                    binding.apply {

                        stringResource.apply {
                            tvCreateFleetTitle.text = createFleet
                            tvFleetActive.text = inActive
                            tvSave.text = create
                            tvCancel.text = cancel
                        }

                        brandLogoHelper.initView(activity, ivBrandLogo, tvSizeTitle)
                        rlBrandLogo.setOnClickListener {
                            brandLogoHelper.openImagePicker(
                                activity,
                                ivBrandLogo,
                                tvSizeTitle,
                                true,
                                cbFill.isChecked
                            )
                        }

                        NSServiceConfig.setFleetDetail(requireActivity(), true, layoutName, layoutUrl, null, layoutSlogan, layoutTags, tvFill, tvFit, tvEditTitle, rlBrandLogo, rvServiceList)

                        var isActiveFleet = false
                        switchService.setOnClickListener {
                            isActiveFleet = !isActiveFleet
                            NSUtilities.switchEnableDisable(switchService, isActiveFleet)
                            tvFleetActive.status(isActiveFleet)
                        }

                        val name: HashMap<String, String> = hashMapOf()
                        NSUtilities.setLanguageText(
                            layoutName.edtValue,
                            layoutName.rvLanguageTitle,
                            name
                        )

                        val slogan: HashMap<String, String> = hashMapOf()
                        NSUtilities.setLanguageText(
                            layoutSlogan.edtValue,
                            layoutSlogan.rvLanguageTitle,
                            slogan
                        )

                        cbFill.isChecked = true
                        brandLogoHelper.logoFillFit(cbFill, clCheckFill, cbFit, clCheckFit, createCompanyRequest.logo)

                        ivClose.setOnClickListener {
                            createCompanyRequest = NSCreateCompanyRequest()
                            dialog.dismiss()
                        }

                        tvCancel.setOnClickListener {
                            createCompanyRequest = NSCreateCompanyRequest()
                            dialog.dismiss()
                        }

                        tvSave.setOnClickListener {
                            dialog.dismiss()
                            val tags = layoutTags.edtValue.text.toString()
                            val list: List<String> = tags.split(" ")
                            createCompanyRequest.tags = list

                            val url = layoutUrl.edtValue.text.toString()
                            createCompanyRequest.url = url

                            if (layoutName.edtValue.toString().isEmpty()) {
                                showError(stringResource.pleaseEnterName)
                                return@setOnClickListener
                            } else if (layoutSlogan.edtValue.toString().isEmpty()) {
                                showError(stringResource.pleaseEnterName)
                                return@setOnClickListener
                            } else if (createCompanyRequest.logo?.isEmpty() == true) {
                                showError(stringResource.logoCanNotEmpty)
                                return@setOnClickListener
                            } else {

                                createCompanyRequest.serviceIds.add(NSConstants.SERVICE_ID)
                                createCompanyRequest.logoScale = if (cbFill.isChecked) NSConstants.FILL else NSConstants.FIT
                                createCompanyRequest.isActive = isActiveFleet
                                createCompanyRequest.name = name
                                createCompanyRequest.slogan = slogan

                                /*createFleet {
                                    setDispatchData(it)
                                }*/
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onFileUrl(url: String, width: Int, height: Int) {
        viewModel.apply {
            urlToUpload = url
            createCompanyRequest.logo = url
            createCompanyRequest.logoHeight = height
            createCompanyRequest.logoWidth = width
        }
    }
}