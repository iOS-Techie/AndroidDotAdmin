package com.nyotek.dot.admin.ui.tabs.fleets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.nyotek.dot.admin.base.BaseFragment
import com.nyotek.dot.admin.common.BrandLogoHelper
import com.nyotek.dot.admin.common.FilterHelper
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSServiceConfig
import com.nyotek.dot.admin.common.NSThemeHelper
import com.nyotek.dot.admin.common.NSUtilities
import com.nyotek.dot.admin.common.callbacks.NSFileUploadCallback
import com.nyotek.dot.admin.common.extension.addOnTextChangedListener
import com.nyotek.dot.admin.common.extension.buildAlertDialog
import com.nyotek.dot.admin.common.extension.getLngValue
import com.nyotek.dot.admin.common.extension.getTagLists
import com.nyotek.dot.admin.common.extension.navigateSafeNew
import com.nyotek.dot.admin.common.extension.rotation
import com.nyotek.dot.admin.common.extension.setPlaceholderAdapter
import com.nyotek.dot.admin.common.extension.setVisibility
import com.nyotek.dot.admin.common.extension.setupWithAdapterAndCustomLayoutManager
import com.nyotek.dot.admin.common.extension.status
import com.nyotek.dot.admin.common.extension.switchEnableDisable
import com.nyotek.dot.admin.databinding.LayoutCreateFleetBinding
import com.nyotek.dot.admin.databinding.NsFragmentFleetsBinding
import com.nyotek.dot.admin.models.requests.NSCreateCompanyRequest
import com.nyotek.dot.admin.models.responses.ActiveInActiveFilter
import com.nyotek.dot.admin.models.responses.FleetData
import com.nyotek.dot.admin.models.responses.RegionDataItem
import com.nyotek.dot.admin.models.responses.SpinnerData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FleetsFragment : BaseFragment<NsFragmentFleetsBinding>(), NSFileUploadCallback {

    private val viewModel by viewModels<FleetsViewModel>()
    private var fleetRecycleAdapter: NSFleetManagementRecycleAdapter? = null
    private val brandLogoHelper: BrandLogoHelper = BrandLogoHelper(this, callback = this)

    companion object {
        fun newInstance() = FleetsFragment()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentFleetsBinding {
        return NsFragmentFleetsBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        binding.apply {
            viewModel.colorResources.getStringResource().apply {
                setLayoutHeader(layoutHomeHeader, fleetManagement, createFleet, isSearch = true)
            }
        }
        setFleetManagementAdapter()
        getFleetFromApi(true)
    }

    override fun observeViewModel() {
        super.observeViewModel()
        observeBaseViewModel(viewModel)
        binding.apply {
            viewModel.apply {
                fleetList.observe(
                    viewLifecycleOwner
                ) {
                    binding.srlRefresh.isRefreshing = false
                    setFleetData(it)
                }
            }
        }
    }

    override fun setListener() {
        super.setListener()
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

    private fun setFleetManagementAdapter() {
        with(binding) {
            with(viewModel) {
                with(rvFleetsList) {
                    fleetRecycleAdapter = NSFleetManagementRecycleAdapter(languageConfig, { model ->
                        openFleetDetail(model)
                    }, {serviceId, isEnable ->
                        fleetEnableDisable(serviceId, isEnable)
                    })

                    setupWithAdapterAndCustomLayoutManager(fleetRecycleAdapter!!, GridLayoutManager(activity, 5))
                }
            }
        }
    }

    private fun getFleetFromApi(isShowProgress: Boolean) {
        viewModel.apply {
            getFleetList(isShowProgress)
        }
    }

    private fun setFleetData(fleetList: MutableList<FleetData>) {
        binding.apply {
            viewModel.apply {

                FilterHelper(activity, binding.rvFleetsFilter, if (filterList.isEmpty()) FilterHelper.getCommonFilterLists() else filterList, colorResources) { _, list ->
                    viewModel.apply {
                        filterList = list
                        setFilterData(fleetList, filterList)
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
        viewModel.hideProgress()
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
                            layoutRegion.tvCommonTitle.text = selectRegion
                        }

                        switchService.rotation(languageConfig.isLanguageRtl())
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

                        NSServiceConfig.setFleetDetail(
                            requireActivity(),
                            layoutName,
                            layoutUrl,
                            null,
                            layoutSlogan,
                            layoutTags,
                            tvFill,
                            tvFit,
                            tvEditTitle,
                            rlBrandLogo,
                            rvServiceList,
                            colorResources,
                            viewModel
                        )

                        var isActiveFleet = false
                        switchService.setOnClickListener {
                            isActiveFleet = !isActiveFleet
                            switchService.switchEnableDisable(isActiveFleet)
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

                        var selectedISO2 = ""
                        viewModel.getRegion {
                            setRegion(binding, it) { iso2 ->
                                selectedISO2 = iso2
                            }
                        }

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
                            createCompanyRequest.tags = tags.getTagLists()

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
                            } else if (selectedISO2.isEmpty()) {
                                showError(stringResource.regionCanNotBeEmpty)
                                return@setOnClickListener
                            } else {

                                createCompanyRequest.serviceIds.add(NSThemeHelper.SERVICE_ID)
                                createCompanyRequest.logoScale = if (cbFill.isChecked) NSConstants.FILL else NSConstants.FIT
                                createCompanyRequest.isActive = isActiveFleet
                                createCompanyRequest.name = name
                                createCompanyRequest.slogan = slogan
                                createCompanyRequest.iso2 = selectedISO2

                                createFleet()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setRegion(bind: LayoutCreateFleetBinding, list: MutableList<RegionDataItem>, callback: (String) -> Unit) {
        binding.apply {
            viewModel.apply {
                var spinnerTitleId: String? = ""
                val titleList = list.map { getLngValue(it.countryName) } as MutableList<String>
                val idList = list.map { it.iso2!! } as MutableList<String>
                val spinnerList = SpinnerData(idList, titleList)

                bind.layoutRegion.spinnerAppSelect.setPlaceholderAdapter(spinnerList, activity, colorResources, spinnerTitleId, isHideFirstPosition = true, placeholderName = "") { selectedId ->
                    if (spinnerTitleId != selectedId) {
                        spinnerTitleId = selectedId
                        callback.invoke(selectedId?:"")
                    }
                }
            }
        }
    }

    private fun openFleetDetail(model: FleetData) {
        val gson = Gson().toJson(model)
        val bundle = bundleOf(NSConstants.FLEET_DETAIL_KEY to gson)
        findNavController().navigateSafeNew(FleetsFragmentDirections.actionFleetToFleetDetail(bundle))
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