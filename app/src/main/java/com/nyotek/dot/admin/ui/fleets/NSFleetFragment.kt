package com.nyotek.dot.admin.ui.fleets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.BrandLogoHelper
import com.nyotek.dot.admin.common.FilterHelper
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSServiceConfig
import com.nyotek.dot.admin.common.callbacks.NSFileUploadCallback
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.addOnTextChangedListener
import com.nyotek.dot.admin.common.utils.buildAlertDialog
import com.nyotek.dot.admin.common.utils.getLngValue
import com.nyotek.dot.admin.common.utils.setVisibility
import com.nyotek.dot.admin.common.utils.setupWithAdapterAndCustomLayoutManager
import com.nyotek.dot.admin.common.utils.status
import com.nyotek.dot.admin.databinding.LayoutCreateFleetBinding
import com.nyotek.dot.admin.databinding.NsFragmentFleetsBinding
import com.nyotek.dot.admin.repository.network.requests.NSCreateCompanyRequest
import com.nyotek.dot.admin.repository.network.responses.ActiveInActiveFilter
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.ui.fleets.detail.NSFleetDetailFragment

class NSFleetFragment : BaseViewModelFragment<NSFleetViewModel, NsFragmentFleetsBinding>(),
    NSFileUploadCallback {

    override val viewModel: NSFleetViewModel by lazy {
        ViewModelProvider(this)[NSFleetViewModel::class.java]
    }

    private var fleetRecycleAdapter: NSFleetManagementRecycleAdapter? = null
    private var isFragmentLoad = false
    private val brandLogoHelper: BrandLogoHelper = BrandLogoHelper(this, callback = this)

    companion object {
        fun newInstance() = NSFleetFragment()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentFleetsBinding {
        return NsFragmentFleetsBinding.inflate(inflater, container, false)
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
                    fleetManagement,
                    createFleet,
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
            getFleetFromApi(!isFragmentLoad)
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
                        getFleetFromApi(false)
                    }

                    tvHeaderBtn.setOnClickListener {
                        showFleetCreateDialog()
                    }
                }
            }
        }
    }

    private fun getFleetFromApi(isShowProgress: Boolean) {
        viewModel.apply {
            getFleetList(isShowProgress) {
                binding.srlRefresh.isRefreshing = false
                setFleetData(it)
            }
        }
    }

    private fun setFleetData(fleetList: MutableList<FleetData>) {
        binding.apply {
            viewModel.apply {
                var filterList: MutableList<ActiveInActiveFilter> = arrayListOf()

                FilterHelper(activity, binding.rvFleetsFilter) { _, list ->
                    viewModel.apply {
                        filterList = list
                        setFilterData(fleetList, list)
                    }
                }

                layoutHomeHeader.etSearch.addOnTextChangedListener(
                    onTextChanged = { text, _, _, _ ->
                        layoutHomeHeader.ivClearData.setVisibility(text.toString().isNotEmpty())
                        setFilterData(fleetList, filterList, text.toString())
                    }
                )

                setFilterData(fleetList, filterList)
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
                    fleetRecycleAdapter = NSFleetManagementRecycleAdapter({ model ->
                        openFleetDetail(model)
                    }, {serviceId, isEnable ->
                        fleetEnableDisable(serviceId, isEnable)
                    })

                    setupWithAdapterAndCustomLayoutManager(fleetRecycleAdapter!!, GridLayoutManager(activity, 5))
                }
            }
        }
    }

    private fun openFleetDetail(model: FleetData) {
        val gson = Gson().toJson(model)
        val bundle = bundleOf(NSConstants.FLEET_DETAIL_KEY to gson)
        fleetManagementFragmentChangeCallback?.setFragment(
            this@NSFleetFragment.javaClass.simpleName,
            NSFleetDetailFragment.newInstance(bundle),
            true, bundle
        )
    }

    private fun setFilterData(fleetList: MutableList<FleetData>,
        filterList: MutableList<ActiveInActiveFilter>,
        searchText: String = binding.layoutHomeHeader.etSearch.text.toString()
    ) {
        with(viewModel) {
            val filterTypes = getFilterSelectedTypes(filterList)
            if (filterTypes.isNotEmpty()) {

                val filter = fleetList.filter { filterTypes.contains(if (it.isActive) NSConstants.ACTIVE else NSConstants.IN_ACTIVE) } as MutableList<FleetData>
                setAdapterData(if (searchText.isEmpty()) filter else filter.filter { getLngValue(
                    it.name
                ).lowercase().contains(searchText.lowercase()) } as MutableList<FleetData>)

            } else {

                setAdapterData(if (searchText.isEmpty()) fleetList else fleetList.filter {
                    getLngValue(it.name).lowercase().contains(searchText.lowercase())
                } as MutableList<FleetData>)

            }
        }
    }

    private fun setAdapterData(serviceItemList: MutableList<FleetData>) {
        fleetRecycleAdapter?.apply {
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

                                createCompanyRequest.logoScale = if (cbFill.isChecked) NSConstants.FILL else NSConstants.FIT
                                createCompanyRequest.isActive = isActiveFleet
                                createCompanyRequest.name = name
                                createCompanyRequest.slogan = slogan

                                createFleet {
                                    setFleetData(it)
                                }
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