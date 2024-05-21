package com.nyotek.dot.admin.ui.fleets.vehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.BrandLogoHelper
import com.nyotek.dot.admin.common.MapBoxView
import com.nyotek.dot.admin.common.NSAddress
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.callbacks.NSFileUploadCallback
import com.nyotek.dot.admin.common.callbacks.NSVehicleEditCallback
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.buildAlertDialog
import com.nyotek.dot.admin.common.utils.gone
import com.nyotek.dot.admin.common.utils.setCoilCenter
import com.nyotek.dot.admin.common.utils.setupWithAdapter
import com.nyotek.dot.admin.common.utils.visible
import com.nyotek.dot.admin.databinding.LayoutCreateVehicleBinding
import com.nyotek.dot.admin.databinding.NsFragmentVehicleBinding
import com.nyotek.dot.admin.repository.network.responses.FleetDataItem
import com.nyotek.dot.admin.repository.network.responses.VehicleDataItem
import com.nyotek.dot.admin.ui.fleets.vehicle.detail.NSVehicleDetailFragment
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NSVehicleFragment : BaseViewModelFragment<NSVehicleViewModel, NsFragmentVehicleBinding>(), NSFileUploadCallback {

    override val viewModel: NSVehicleViewModel by lazy {
        ViewModelProvider(this)[NSVehicleViewModel::class.java]
    }

    private var vehicleRecycleAdapter: NSVehicleRecycleAdapter? = null
    private var mapBoxView: MapBoxView? = null
    private var isFragmentLoad = false
    private val brandLogoHelper: BrandLogoHelper = BrandLogoHelper(this, callback = this)

    companion object {
        fun newInstance(bundle: Bundle?) = NSVehicleFragment().apply {
            arguments = bundle
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentVehicleBinding {
        mapBoxView = MapBoxView(requireContext())
        return NsFragmentVehicleBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        mapBoxView?.initMapView(requireContext(), binding.mapFragmentVehicle, FleetDataItem())
    }

    override fun loadFragment(bundle: Bundle?) {
        super.loadFragment(bundle)
        arguments = bundle
        arguments?.let {
            with(viewModel) {
                if (!isFragmentLoad) {
                    //binding.clMap.visible()
                    isFragmentLoad = true
                    strVehicleDetail = it.getString(NSConstants.FLEET_DETAIL_KEY)
                    initCreateVendor()
                    viewCreated()
                    setListener()
                    getUserVehicle(true)
                }
            }
        }
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

    private fun initCreateVendor() {
        binding.apply {
            viewModel.apply {
                stringResource.apply {
                    srlRefresh.setSize(com.intuit.sdp.R.dimen._4sdp)
                    tvVehicleTitle.text = vehicle
                    tvCreateVehicle.text = createVehicle
                }
            }
        }
    }

    /**
     * View created
     */
    private fun viewCreated() {
        viewModel.apply {
            baseObserveViewModel(viewModel)
            observeViewModel()
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
        binding.apply {
            viewModel.apply {
                tvCreateVehicle.setOnClickListener {
                    showCreateVehicleDialog(true)
                }

                srlRefresh.setOnRefreshListener {
                    getUserVehicle(false)
                }
            }
        }
    }

    private fun getUserVehicle(isShowProgress: Boolean) {
        binding.apply {
            viewModel.apply {
                getVehicleDetail(isShowProgress) {
                    srlRefresh.isRefreshing = false
                    setVehicleAdapter(it)
                }
            }
        }
    }

    /**
     * Set profile adapter
     *
     */
    private fun setVehicleAdapter(branchList: MutableList<VehicleDataItem>) {
        with(binding) {
            with(viewModel) {
                with(rvVehicleList) {
                    vehicleRecycleAdapter = NSVehicleRecycleAdapter(activity, { response, position ->
                        editVehicleData(response, position)
                    }, { serviceId, isEnable ->
                        vehicleEnableDisable(serviceId, isEnable)
                    })
                    setupWithAdapter(vehicleRecycleAdapter!!)
                    vehicleRecycleAdapter?.setData(branchList)
                }
            }
        }
    }

    private fun editVehicleData(response: VehicleDataItem, position: Int) {
        viewModel.apply {
            val bundle = bundleOf(
                NSConstants.VEHICLE_DETAIL_KEY to Gson().toJson(response),
                NSConstants.FLEET_DETAIL_KEY to strVehicleDetail
            )
            fleetManagementFragmentChangeCallback?.setFragment(
                this@NSVehicleFragment.javaClass.simpleName,
                NSVehicleDetailFragment.newInstance(bundle, object : NSVehicleEditCallback {
                    override fun onVehicle(vehicleData: VehicleDataItem) {
                        vehicleRecycleAdapter?.updateSingleData(vehicleData, position)
                    }
                }),
                true, bundle
            )
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getCurrentLocation(event: NSAddress) {
        val address = event.addresses[0].getAddressLine(0).toString()
        if (address.isNotEmpty()) {
            event.locationResult.lastLocation?.apply {
                mapBoxView?.setCurrentLatLong(latitude, longitude)
            }
        }
    }

    private fun showCreateVehicleDialog(isCreate: Boolean, dataItem: VehicleDataItem? = null) {
        binding.apply {
            viewModel.apply {

                buildAlertDialog(
                    requireContext(),
                    LayoutCreateVehicleBinding::inflate
                ) { dialog, binding ->
                    binding.apply {
                        stringResource.apply {
                            tvCreateVehicleTitle.text = if (isCreate) createVehicle else updateVehicle
                            layoutRegistrationNo.tvCommonTitle.text = vehicleRegistrationNo
                            layoutManufacturer.tvCommonTitle.text = manufacturer
                            layoutManufacturerYear.tvCommonTitle.text = manufacturerYear
                            layoutModel.tvCommonTitle.text = model
                            layoutLoadCapacity.tvCommonTitle.text = loadCapacity
                            layoutCapability.tvCommonTitle.text = capabilities
                            layoutNotes.tvCommonTitle.text = additionalNote
                            tvCreate.text = if (isCreate) create else update
                            tvCancel.text = cancel
                        }

                        if (!isCreate) {
                            dataItem?.apply {
                                layoutRegistrationNo.edtValue.setText(registrationNo)
                                layoutManufacturer.edtValue.setText(manufacturer)
                                layoutManufacturerYear.edtValue.setText(manufacturingYear)
                                layoutNotes.edtValue.setText(additionalNote)
                                layoutModel.edtValue.setText(model)
                                layoutLoadCapacity.edtValue.setText(loadCapacity)
                                layoutLogo.ivBrandLogo.setCoilCenter(url = vehicleImg)
                            }
                        }

                        tvCancel.setOnClickListener {
                            dialog.dismiss()
                        }

                        layoutLogo.clBrandLogo.setOnClickListener {
                            brandLogoHelper.openImagePicker(activity, layoutLogo.ivBrandLogo, null, true,
                                isFill = true
                            )
                        }

                        var selectedCapabilities: MutableList<String> = arrayListOf()
                        getCapabilities(false, isCapabilityCheck = true, isShowError = false) {
                            NSUtilities.setCapability(activity, false, layoutCapability, it, dataItem) { capabilities ->
                                selectedCapabilities = capabilities
                            }
                        }

                        tvCreate.setOnClickListener {
                            val strRegistrationNo = layoutRegistrationNo.edtValue.text.toString().trim()
                            val strManufacture = layoutManufacturer.edtValue.text.toString().trim()
                            val strManufactureYear = layoutManufacturerYear.edtValue.text.toString().trim()
                            val strNotes = layoutNotes.edtValue.text.toString().trim()
                            val strModel = layoutModel.edtValue.text.toString().trim()
                            val strLoadCapacity = layoutLoadCapacity.edtValue.text.toString().trim()

                            stringResource.apply {
                                if (strRegistrationNo.isEmpty()) {
                                    showError(registrationNumberCanNotEmpty)
                                    return@setOnClickListener
                                } else if (strModel.isEmpty()) {
                                    showError(modelCanNotEmpty)
                                    return@setOnClickListener
                                } else if (strManufacture.isEmpty()) {
                                    showError(manufacturerCanNotEmpty)
                                    return@setOnClickListener
                                } else if (strManufactureYear.isEmpty()) {
                                    showError(manufacturerYearCanNotEmpty)
                                    return@setOnClickListener
                                } else if (strLoadCapacity.isEmpty()) {
                                    showError(loadCapacityCanNotEmpty)
                                    return@setOnClickListener
                                } else if (uploadFileUrl?.isEmpty() == true) {
                                    showError(logoCanNotEmpty)
                                    return@setOnClickListener
                                } else {
                                    val map: HashMap<String, String> = hashMapOf()
                                    map[NSConstants.REGISTRATION_NO] = strRegistrationNo
                                    map[NSConstants.MODEL] = strModel
                                    map[NSConstants.MANUFACTURE] = strManufacture
                                    map[NSConstants.MANUFACTURE_YEAR] = strManufactureYear
                                    map[NSConstants.LOAD_CAPACITY] = strLoadCapacity
                                    map[NSConstants.NOTES] = strNotes

                                    progress.visible()
                                    createVehicle(selectedCapabilities, map) { isSuccess ->
                                        progress.gone()
                                        dialog.dismiss()
                                        if (isSuccess) {
                                            getUserVehicle(true)
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

    override fun onFileUrl(url: String, width: Int, height: Int) {
        viewModel.uploadFileUrl = url
    }
}