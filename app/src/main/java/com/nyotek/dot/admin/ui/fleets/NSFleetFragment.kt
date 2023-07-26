package com.nyotek.dot.admin.ui.fleets

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.BrandLogoHelper
import com.nyotek.dot.admin.common.FilterHelper
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSFragment
import com.nyotek.dot.admin.common.NSServiceConfig
import com.nyotek.dot.admin.common.callbacks.NSFileUploadCallback
import com.nyotek.dot.admin.common.callbacks.NSSwitchEnableDisableCallback
import com.nyotek.dot.admin.common.callbacks.NSFleetDetailCallback
import com.nyotek.dot.admin.common.callbacks.NSFleetFilterCallback
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.getLngValue
import com.nyotek.dot.admin.common.utils.setVisibility
import com.nyotek.dot.admin.databinding.LayoutCreateFleetBinding
import com.nyotek.dot.admin.databinding.NsFragmentFleetsBinding
import com.nyotek.dot.admin.repository.network.requests.NSCreateCompanyRequest
import com.nyotek.dot.admin.repository.network.responses.ActiveInActiveFilter
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.ui.fleets.detail.NSFleetDetailFragment
import com.nyotek.dot.admin.ui.fleets.detail.NSFleetServiceListRecycleAdapter

class NSFleetFragment : NSFragment(), NSFileUploadCallback, NSFleetFilterCallback {
    private val fleetViewModel: NSFleetViewModel by lazy {
        ViewModelProvider(this)[NSFleetViewModel::class.java]
    }
    private var _binding: NsFragmentFleetsBinding? = null
    private val fleetBinding get() = _binding!!
    private var fleetRecycleAdapter: NSFleetManagementRecycleAdapter? = null
    private var serviceHorizontalAdapter: NSFleetServiceListRecycleAdapter? = null
    var ivBrandLogoImage: ImageView? = null
    var checkLogoFillScale: CheckBox? = null
    var checkLogoFitScale: CheckBox? = null
    var tvSizeTitleImage: TextView? = null
    private var isFragmentLoad = false
    private val brandLogoHelper: BrandLogoHelper = BrandLogoHelper(this, callback = this)

    companion object {
        fun newInstance() = NSFleetFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentFleetsBinding.inflate(inflater, container, false)
        baseObserveViewModel(fleetViewModel)
        observeViewModel()
        setFleetManagementAdapter()
        FilterHelper(activity, fleetBinding.rvFleetsFilter, this)
        return fleetBinding.root
    }

    fun loadFragment() {
        initUI()
        viewCreated()
        setListener()
    }

    private fun initUI() {
        fleetBinding.apply {
            stringResource.apply {
                setLayoutHeader(
                    layoutHomeHeader,
                    fleetManagement,
                    createFleet,
                    isProfile = false,
                    isSearch = true,
                    isBack = false
                )
            }
        }
    }


    /**
     * View created
     */
    private fun viewCreated() {
        fleetViewModel.apply {
            getFleetList(!isFragmentLoad)
            if (!isFragmentLoad) {
                isFragmentLoad = true
            }
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
        fleetBinding.apply {
            fleetViewModel.apply {
                layoutHomeHeader.apply {
                    ivClearData.setOnClickListener {
                        etSearch.setText("")
                    }

                    srlRefresh.setOnRefreshListener {
                        getFleetList(false)
                    }

                    tvHeaderBtn.setOnClickListener {
                        showFleetCreateDialog()
                    }

                    etSearch.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {

                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            ivClearData.setVisibility(s.toString().isNotEmpty())
                            filterData(selectedFilterList, s.toString())
                        }

                        override fun afterTextChanged(s: Editable?) {

                        }
                    })
                }
            }
        }
    }

    /**
     * Set profile adapter
     *
     */
    private fun setFleetManagementAdapter() {
        with(fleetBinding) {
            with(fleetViewModel) {
                with(rvFleetsList) {
                    layoutManager = GridLayoutManager(activity, 5)
                    fleetRecycleAdapter =
                        NSFleetManagementRecycleAdapter(
                            requireActivity(),
                            isLanguageSelected(),
                            object :
                                NSFleetDetailCallback {
                                override fun onItemSelect(model: FleetData) {
                                    fleetManagementFragmentChangeCallback?.setFragment(
                                        this@NSFleetFragment.javaClass.simpleName,
                                        NSFleetDetailFragment.newInstance(
                                            bundleOf(
                                                NSConstants.FLEET_DETAIL_KEY to Gson().toJson(
                                                    model
                                                )
                                            )
                                        ),
                                        true, bundleOf(
                                            NSConstants.FLEET_DETAIL_KEY to Gson().toJson(
                                                model
                                            )
                                        )
                                    )
                                }
                            },
                            object : NSSwitchEnableDisableCallback {
                                override fun switch(serviceId: String, isEnable: Boolean) {
                                    fleetEnableDisable(serviceId, isEnable, true)
                                }

                            })
                    adapter = fleetRecycleAdapter
                    isNestedScrollingEnabled = false
                }
            }
        }
    }

    private fun filterData(filterList: MutableList<ActiveInActiveFilter>, searchText: String = fleetBinding.layoutHomeHeader.etSearch.text.toString()) {
        with(fleetViewModel) {
            val filterTypes = getFilterSelectedTypes(filterList)
            if (filterTypes.isNotEmpty()) {
                if (searchText.isEmpty()) {
                    fleetRecycleAdapter?.updateData(fleetItemList.filter { filterTypes.contains(if (it.isActive) NSConstants.ACTIVE else NSConstants.IN_ACTIVE) } as MutableList<FleetData>)
                } else {
                    fleetRecycleAdapter?.updateData(fleetItemList.filter { filterTypes.contains(if (it.isActive) NSConstants.ACTIVE else NSConstants.IN_ACTIVE) && getLngValue(it.name).lowercase().contains(searchText.lowercase())} as MutableList<FleetData>)
                }
            } else {
                if (searchText.isEmpty()) {
                    fleetRecycleAdapter?.updateData(fleetItemList)
                } else {
                    fleetRecycleAdapter?.updateData(fleetItemList.filter { getLngValue(it.name).lowercase().contains(searchText.lowercase())} as MutableList<FleetData>)
                }
            }
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(fleetViewModel) {
            with(fleetBinding) {
                isSwipeRefresh.observe(
                    viewLifecycleOwner
                ) { isSwipe ->
                    if (isSwipe) {
                        srlRefresh.isRefreshing = false
                    }
                }

                isFleetListAvailable.observe(
                    viewLifecycleOwner
                ) {
                    srlRefresh.isRefreshing = false
                    filterData(selectedFilterList)
                }
            }
        }
    }

    private fun showFleetCreateDialog() {
        fleetBinding.apply {
            fleetViewModel.apply {

                val builder = AlertDialog.Builder(requireActivity())
                val view: View =
                    requireActivity().layoutInflater.inflate(R.layout.layout_create_fleet, null)
                builder.setView(view)
                builder.setCancelable(false)
                val layoutCreateFleet = LayoutCreateFleetBinding.bind(view)
                val dialog = builder.create()
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                layoutCreateFleet.apply {
                    stringResource.apply {
                        tvCreateFleetTitle.text = createFleet
                        tvFleetActive.text = inActive
                        tvSave.text = create
                        tvCancel.text = cancel
                    }

                    ivBrandLogoImage = ivBrandLogo
                    checkLogoFillScale = cbFill
                    checkLogoFitScale = cbFit
                    tvSizeTitleImage = tvSizeTitle

                    NSApplication.getInstance().removeAllMapLocalLanguage()
                    serviceHorizontalAdapter = NSServiceConfig.setFleetDetail(requireActivity(), true, layoutName, layoutUrl, null, layoutSlogan, layoutTags, tvFill, tvFit, tvEditTitle, rlBrandLogo, rvServiceList)
                    brandLogoHelper.initView(activity, ivBrandLogo, tvSizeTitle)
                    rlBrandLogo.setOnClickListener {
                        brandLogoHelper.openImagePicker(activity, ivBrandLogo, tvSizeTitle, true, checkLogoFillScale?.isChecked == true)
                    }


                    var isActiveFleet = false
                    switchService.setOnClickListener {
                        isActiveFleet = !isActiveFleet
                        createCompanyRequest.isActive = isActiveFleet
                        NSUtilities.switchEnableDisable(switchService, isActiveFleet)
                        tvFleetActive.text = if (isActiveFleet) stringResource.active else stringResource.inActive
                    }

                    NSUtilities.setLanguageText(layoutName.edtValue, layoutName.rvLanguageTitle, createCompanyRequest.name)
                    NSUtilities.setLanguageText(layoutSlogan.edtValue, layoutSlogan.rvLanguageTitle, createCompanyRequest.slogan)

                    cbFill.isChecked = true
                    createCompanyRequest.logoScale = NSConstants.FILL

                    clCheckFill.setOnClickListener {
                        cbFit.isChecked = false
                        cbFill.isChecked = true
                        createCompanyRequest.logoScale = NSConstants.FILL
                        brandLogoHelper.setBrandLogo(false, createCompanyRequest.logo ?: "", checkLogoFillScale?.isChecked == true)
                    }

                    clCheckFit.setOnClickListener {
                        createCompanyRequest.logoScale = NSConstants.FIT
                        cbFit.isChecked = true
                        cbFill.isChecked = false
                        brandLogoHelper.setBrandLogo(false, createCompanyRequest.logo ?: "", checkLogoFillScale?.isChecked == true)
                    }

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
                            createFleet()
                        }
                    }

                    //getServiceList(true)

                    isServiceListAvailable.observe(
                        viewLifecycleOwner
                    ) { isUserDetail ->
                        setServiceListForCreateFleet(isUserDetail)
                    }
                }

                if (!dialog.isShowing) {
                    dialog.show()
                }
            }
        }
    }

    private fun setServiceListForCreateFleet(isUserDetail: Boolean) {
        with(fleetViewModel) {
            if (isUserDetail) {
                serviceHorizontalAdapter?.updateData2(serviceItemList, fleetViewModel.createCompanyRequest)
            }
        }
    }

    override fun onFileUrl(url: String, width: Int, height: Int) {
        fleetViewModel.apply {
            urlToUpload = url
            createCompanyRequest.logo = url
            createCompanyRequest.logoHeight = height
            createCompanyRequest.logoWidth = width
        }
    }

    override fun onFilterSelect(
        model: ActiveInActiveFilter,
        list: MutableList<ActiveInActiveFilter>
    ) {
        fleetViewModel.apply {
            selectedFilterList = list
            filterData(list)
        }
    }
}