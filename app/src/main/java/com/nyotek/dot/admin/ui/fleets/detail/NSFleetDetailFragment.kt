package com.nyotek.dot.admin.ui.fleets.detail

import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.tabs.TabLayoutMediator
import com.nyotek.dot.admin.common.BrandLogoHelper
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSDateTimeHelper
import com.nyotek.dot.admin.common.NSFragment
import com.nyotek.dot.admin.common.NSServiceConfig
import com.nyotek.dot.admin.common.NSViewPagerAdapter
import com.nyotek.dot.admin.common.callbacks.NSFileUploadCallback
import com.nyotek.dot.admin.common.callbacks.NSLocalLanguageListCallback
import com.nyotek.dot.admin.common.callbacks.NSOnAddressSelectCallback
import com.nyotek.dot.admin.common.utils.NSAddressConfig
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.gone
import com.nyotek.dot.admin.common.utils.invisible
import com.nyotek.dot.admin.common.utils.visible
import com.nyotek.dot.admin.databinding.NsFragmentFleetDetailBinding
import com.nyotek.dot.admin.repository.network.requests.NSCreateFleetAddressRequest
import com.nyotek.dot.admin.repository.network.requests.NSFleetLogoUpdateRequest
import com.nyotek.dot.admin.repository.network.responses.AddressData
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.FleetDataItem
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListData
import com.nyotek.dot.admin.ui.fleets.NSFleetViewModel
import com.nyotek.dot.admin.ui.fleets.vehicle.NSVehicleFragment
import com.nyotek.dot.admin.ui.fleets.employee.NSEmployeeFragment
import com.nyotek.dot.admin.ui.fleets.map.NSMapViewModel


class NSFleetDetailFragment : NSFragment(), NSFileUploadCallback {
    private val fleetDetailViewModel: NSFleetDetailViewModel by lazy {
        ViewModelProvider(this)[NSFleetDetailViewModel::class.java]
    }
    private val mapViewModel: NSMapViewModel by lazy {
        ViewModelProvider(this)[NSMapViewModel::class.java]
    }
    private val fleetViewModel: NSFleetViewModel by lazy {
        ViewModelProvider(this)[NSFleetViewModel::class.java]
    }
    private var _binding: NsFragmentFleetDetailBinding? = null
    private val fleetBinding get() = _binding!!
    private var selectedLatLng: LatLng? = null
    private var serviceHorizontalAdapter: NSFleetServiceListRecycleAdapter? = null
    private var isFragmentAdded: Boolean = false
    private val brandLogoHelper: BrandLogoHelper = BrandLogoHelper(this, callback = this)

    companion object {
        fun newInstance(bundle: Bundle?) = NSFleetDetailFragment().apply {
            arguments = bundle
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentFleetDetailBinding.inflate(inflater, container, false)
        baseObserveViewModel(mapViewModel)
        baseObserveViewModel(fleetDetailViewModel)
        observeViewModel()
        setFleetTopBox()
        pageChangeListener()
        return fleetBinding.root
    }

    fun loadFragment(bundle: Bundle?) {
        removeAllViews()
        arguments = bundle
        arguments?.let {
            with(fleetDetailViewModel) {
                getFleetDetail(it.getString(NSConstants.FLEET_DETAIL_KEY))
            }
        }
        initUI()
        viewCreated()
        setListener()
    }

    private fun initUI() {
        fleetBinding.apply {
            stringResource.apply {
                setLayoutHeader(
                    layoutHomeHeader,
                    fleetDetail,
                    isProfile = false,
                    isSearch = false,
                    isBack = true
                )
                tvCreateFleetTitle.text = fleets
                tvSave.text = save
                tvCancel.text = cancel
                brandLogoHelper.initView(activity, ivBrandLogo, tvSizeTitle)
            }
        }
    }

    private fun removeAllViews() {
        fleetBinding.apply {
            fleetDetailViewModel.apply {
                isFragmentAdded = false
                tvSizeTitle.gone()
                NSApplication.getInstance().removeAllMapLocalLanguage()
                ivBrandLogo.setImageResource(0)
                layoutName.edtValue.setText("")
                layoutSlogan.edtValue.setText("")
                layoutTags.edtValue.setText("")
                layoutTags.edtValue.clearTag()
                layoutUrl.edtValue.setText("")
                layoutAddress.edtValue.text = ""
                cbFill.isChecked = true
                cbFit.isChecked = false
                serviceHorizontalAdapter?.clearData()
                fleetModel = null
                selectedFleetId = ""
                isEnableFleet = false
                addressDetailModel = null
                urlToUpload = ""
                fleetLogoUpdateRequest = NSFleetLogoUpdateRequest()
                createAddressRequest = NSCreateFleetAddressRequest()
                tabService.removeAllTabs()
                fleetPager.invisible()
                manageFocus()
            }
        }
    }

    private fun pageChangeListener() {
        fleetBinding.apply {
            fleetDetailViewModel.apply {
                fleetPager.registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        fleetBinding.fleetPager.visible()
                        if (mFragmentList[position] is NSVehicleFragment) {
                            (mFragmentList[position] as NSVehicleFragment).loadFragment(arguments)
                        }
                    }
                })
            }
        }
    }

    /**
     * View created
     */
    private fun viewCreated() {
        NSUtilities.clearFilter(NSApplication.getInstance().getFilterOrderTypes())
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(fleetBinding) {
            with(fleetDetailViewModel) {

                layoutHomeHeader.ivBack.setOnClickListener {
                    onBackPress()
                }

                clCheckFill.setOnClickListener {
                    fleetModel?.logoScale = NSConstants.FILL
                    cbFit.isChecked = false
                    cbFill.isChecked = true
                    updateFleetLogoScale(NSConstants.FILL)
                    brandLogoHelper.setBrandLogo(false, fleetModel?.logo ?: "", fleetModel?.logoScale.equals(NSConstants.FILL))
                }

                clCheckFit.setOnClickListener {
                    fleetModel?.logoScale = NSConstants.FIT
                    cbFit.isChecked = true
                    cbFill.isChecked = false
                    updateFleetLogoScale(NSConstants.FIT)
                    brandLogoHelper.setBrandLogo(false, fleetModel?.logo ?: "", fleetModel?.logoScale.equals(NSConstants.FILL))
                }

                layoutAddress.edtValue.setOnClickListener {
                    NSAddressConfig.showAddressDialog(requireActivity(), mapViewModel, false, fleetBinding.viewAddress, fleetDetailViewModel.addressDetailModel?: AddressData(), fleetDetailViewModel.fleetModel?.vendorId?:"", fleetDetailViewModel.fleetModel?.addressId?:"", fleetDetailViewModel.fleetModel?.serviceIds?: arrayListOf(), object : NSOnAddressSelectCallback {
                        override fun onItemSelect(
                            addressData: AddressData?,
                            isFromEditBranch: Boolean
                        ) {
                            fleetDetailViewModel.addressDetailModel = addressData
                            fleetBinding.layoutAddress.edtValue.text = addressData?.addr1
                        }
                    })
                }

                rlSelectAddress.setOnClickListener {
                    rlSelectAddress.gone()
                }

                layoutName.edtValue.onFocusChangeListener =
                    View.OnFocusChangeListener { _, hasFocus ->
                        if (!hasFocus) {
                            updatePosition = 0
                            updateData(updatePosition)
                        }
                        manageFocus(name = hasFocus)
                    }

                layoutSlogan.edtValue.onFocusChangeListener =
                    View.OnFocusChangeListener { _, hasFocus ->
                        if (!hasFocus) {
                            updatePosition = 1
                            updateData(updatePosition)
                        }
                        manageFocus(slogan = hasFocus)
                    }

                layoutUrl.edtValue.onFocusChangeListener =
                    View.OnFocusChangeListener { _, hasFocus ->
                        if (!hasFocus) {
                            updatePosition = 2
                            updateData(updatePosition)
                        }
                        manageFocus(slogan = hasFocus)
                    }

                layoutTags.edtValue.onFocusChangeListener =
                    View.OnFocusChangeListener { _, hasFocus ->
                        if (!hasFocus) {
                            updatePosition = 3
                            updateData(updatePosition)
                        }
                        manageFocus(tags = hasFocus)
                    }

                tvSave.setOnClickListener {
                    isProgressVisible = true
                    isProgressShowing.value = true
                    updateServiceIds()
                }
            }
        }
    }

    private fun setFleetTopBox() {
        fleetBinding.apply {
            fleetDetailViewModel.apply {
                serviceHorizontalAdapter = NSServiceConfig.setFleetDetail(requireActivity(), false, layoutName, layoutUrl, layoutAddress, layoutSlogan, layoutTags, tvFill, tvFit, tvEditTitle, rlBrandLogo, rvServiceList)

                rlBrandLogo.setOnClickListener {
                    brandLogoHelper.openImagePicker(activity, ivBrandLogo, tvSizeTitle, true, fleetModel?.logoScale.equals(NSConstants.FILL))
                }
            }
        }
    }

    private fun updateData(position: Int) {
        when (position) {
            0 -> {
                fleetDetailViewModel.updateName()
            }
            1 -> {
                fleetDetailViewModel.updateSlogan()
            }
            2 -> {
                fleetDetailViewModel.updateUrl()
            }
            3 -> {
                val tags = fleetBinding.layoutTags.edtValue.text.toString()
                val list: List<String> = tags.split(" ")
                fleetDetailViewModel.fleetModel?.tags = list
                fleetDetailViewModel.updateTags()
            }
        }
    }

    private fun setFleetDetailFromJson(fleetModel: FleetData?) {
        fleetBinding.apply {
            fleetModel?.apply {
                val createdDate =
                    stringResource.createdDate + " : " + NSDateTimeHelper.getServiceDateView(created)
                tvFleetCreatedDate.text = createdDate
                tvFleetActive.text =
                    if (isActive) stringResource.active else stringResource.inActive
                NSUtilities.switchEnableDisable(switchService, isActive)

                brandLogoHelper.setBrandLogo(false, logo ?: "", logoScale.equals(NSConstants.FILL))

                cbFill.isChecked = logoScale.equals(NSConstants.FILL)
                cbFit.isChecked = !logoScale.equals(NSConstants.FILL)


                layoutTags.edtValue.gravity = Gravity.START

                var tagsList = ""
                for (tagsItem in tags?: arrayListOf()) {
                    tagsList += "$tagsItem "
                }

                layoutTags.edtValue.hint = stringResource.enterTag
                layoutTags.edtValue.setText(tagsList)

                NSUtilities.setLanguageText(layoutUrl.edtValue, fleetDetailViewModel.fleetModel, true)
                layoutUrl.edtValue.setText(url)
                NSUtilities.setLanguageText(layoutName.edtValue, layoutName.rvLanguageTitle, name)
                NSUtilities.setLanguageText(
                    layoutSlogan.edtValue,
                    layoutSlogan.rvLanguageTitle,
                    slogan
                )

                switchService.setOnClickListener {
                    NSUtilities.switchEnableDisable(switchService, !isActive)
                    fleetDetailViewModel.selectedFleetId = vendorId
                    fleetDetailViewModel.isEnableFleet = !isActive
                    fleetViewModel.fleetEnableDisable(vendorId, !isActive, true)
                    isActive = !isActive
                    tvFleetActive.text = if (isActive) stringResource.active else stringResource.inActive
                }

                layoutAddress.edtValue.ellipsize = TextUtils.TruncateAt.END


                fleetDetailViewModel.apply {
                    layoutAddress.edtValue.text = addressDetailModel?.addr1 ?: ""
                    selectedLatLng =
                        LatLng(addressDetailModel?.lat ?: 0.0, addressDetailModel?.lng ?: 0.0)
                    //getServiceList(true)
                    mapViewModel.getFleetLocations(fleetModel.vendorId,true)
                }
            }
        }
    }

    private fun setFragmentList(fleetData: FleetDataItem?) {
        fleetDetailViewModel.apply {
            stringResource.apply {
                mFragmentList.clear()
                mFragmentTitleList.clear()
                mFragmentList.add(NSEmployeeFragment.newInstance(arguments, fleetData))
                mFragmentList.add(NSVehicleFragment.newInstance(arguments))
                mFragmentTitleList.add(employee)
                mFragmentTitleList.add(vehicle)
            }
            fleetBinding.fleetPager.invisible()
            setupViewPager(requireActivity(), fleetBinding.fleetPager)
        }
    }

    private fun setupViewPager(activity: FragmentActivity, viewPager: ViewPager2) {
        try {
            fleetDetailViewModel.apply {
                val adapter = NSViewPagerAdapter(activity)
                adapter.setFragment(mFragmentList)
                viewPager.adapter = adapter
                viewPager.isUserInputEnabled = false
                viewPager.offscreenPageLimit = mFragmentList.size
                TabLayoutMediator(fleetBinding.tabService, viewPager
                ) { tab, position -> tab.text = mFragmentTitleList[position] }.attach()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Set user list data
     *
     * @param serviceList when data available it's true
     */
    private fun setServiceList(serviceList: MutableList<NSGetServiceListData>) {
        NSServiceConfig.getListFromLocal(fleetDetailViewModel.fleetModel?.serviceIds?: arrayListOf(), object : NSLocalLanguageListCallback {
            override fun onItemGet() {
                fleetBinding.layoutName.rvLanguageTitle.refreshAdapter()
                fleetBinding.layoutSlogan.rvLanguageTitle.refreshAdapter()
            }
        })
        serviceHorizontalAdapter?.setFleetData(serviceList, fleetDetailViewModel.fleetModel)
        serviceHorizontalAdapter?.setData(serviceList)
        /*if (!isFragmentAdded) {
            isFragmentAdded = true
            setFragmentList()
        }*/
    }

    /**
     * Set fleet location list data
     *
     * @param fleetData when data available it's true
     */
    private fun setFleetLocationList(fleetData: FleetDataItem?) {
        if (!isFragmentAdded) {
            isFragmentAdded = true
            setFragmentList(fleetData)
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(fleetDetailViewModel) {
            with(fleetBinding) {

                isFleetDataAvailable.observe(
                    viewLifecycleOwner
                ) { fleetData ->
                    setFleetDetailFromJson(fleetData)
                }

                isServiceListAvailable.observe(
                    viewLifecycleOwner
                ) { serviceList ->
                    setServiceList(serviceList)
                }

                mapViewModel.isFleetLocationListAvailable.observe(
                    viewLifecycleOwner
                ) { fleetData ->
                    setFleetLocationList(fleetData)
                }

                isAllDataUpdateAvailable.observe(
                    viewLifecycleOwner
                ) { isAllDataUpdate ->
                    if (isAllDataUpdate) {
                        Toast.makeText(
                            requireContext(),
                            stringResource.updatedSuccessfully,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onFileUrl(url: String, width: Int, height: Int) {
        fleetDetailViewModel.apply {
            fleetLogoUpdateRequest.logoHeight = height
            fleetLogoUpdateRequest.logoWidth = width
            fleetLogoUpdateRequest.logo = url
            fleetModel?.logo = url
            urlToUpload = url
            updateFleetLogo()
        }
    }
}