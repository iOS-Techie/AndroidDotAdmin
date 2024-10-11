package com.nyotek.dot.admin.ui.tabs.fleets.vehicle

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.nyotek.dot.admin.base.BaseFragment
import com.nyotek.dot.admin.common.BrandLogoHelper
import com.nyotek.dot.admin.common.NSAddress
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSUtilities
import com.nyotek.dot.admin.common.callbacks.NSFileUploadCallback
import com.nyotek.dot.admin.common.extension.buildAlertDialog
import com.nyotek.dot.admin.common.extension.formatText
import com.nyotek.dot.admin.common.extension.gone
import com.nyotek.dot.admin.common.extension.isValidList
import com.nyotek.dot.admin.common.extension.setCoil
import com.nyotek.dot.admin.common.extension.setCoilCircle
import com.nyotek.dot.admin.common.extension.setupWithAdapter
import com.nyotek.dot.admin.common.extension.visible
import com.nyotek.dot.admin.common.map.MapBoxView
import com.nyotek.dot.admin.databinding.LayoutCreateVehicleBinding
import com.nyotek.dot.admin.databinding.NsFragmentVehicleBinding
import com.nyotek.dot.admin.models.responses.FleetDataItem
import com.nyotek.dot.admin.models.responses.VehicleDataItem
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class VehicleFragment : BaseFragment<NsFragmentVehicleBinding>(), NSFileUploadCallback {

    private val viewModel by viewModels<VehicleViewModel>()

    private var vehicleRecycleAdapter: NSVehicleRecycleAdapter? = null
    private var mapBoxView: MapBoxView? = null
    private var isFragmentLoad = false
    private val brandLogoHelper: BrandLogoHelper = BrandLogoHelper(this, callback = this)
    private lateinit var themeUI: VehicleUI
    private var callback: ((Bundle?) -> Unit)? = null

    companion object {
        fun newInstance(bundle: Bundle?, callback: ((Bundle?) -> Unit)?) = VehicleFragment().apply {
            arguments = bundle
            this.callback = callback
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentVehicleBinding {
        mapBoxView = MapBoxView(requireContext(), viewModel.colorResources, viewModel.languageConfig)
        return NsFragmentVehicleBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        mapBoxView?.initMapView(requireContext(), binding.mapFragmentVehicle, FleetDataItem())
    }

    override fun loadFragment(bundle: Bundle?) {
        super.loadFragment(bundle)
        themeUI = VehicleUI(binding, viewModel.colorResources, viewModel.languageConfig)
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

    fun updateChangedDetail() {
        viewModel.apply {
            if (selectedPosition != -1) {
                val item = VehicleHelper.getVehicleList()[selectedPosition]
                vehicleRecycleAdapter?.updateSingleData(item, selectedPosition)
                selectedPosition = -1
            }
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        with(viewModel) {
            with(binding) {

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

    private fun viewCreated() {
        viewModel.apply {
            observeBaseViewModel(viewModel)
            observeViewModel()
        }
    }

    override fun setListener() {
        super.setListener()
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

    private fun setVehicleAdapter(vehicleList: MutableList<VehicleDataItem>) {
        with(binding) {
            with(viewModel) {
                with(rvVehicleList) {
                    vehicleRecycleAdapter = NSVehicleRecycleAdapter(themeUI, { response, position ->
                        editVehicleData(response, position)
                    }, { serviceId, isEnable ->
                        vehicleEnableDisable(serviceId, isEnable)
                    })
                    setupWithAdapter(vehicleRecycleAdapter!!)
                    VehicleHelper.setVehicleList(vehicleList)
                    vehicleRecycleAdapter?.setData(VehicleHelper.getVehicleList())
                }
            }
        }
    }

    private fun editVehicleData(response: VehicleDataItem, position: Int) {
        viewModel.apply {
            selectedPosition = position
            val bundle = bundleOf(
                NSConstants.VEHICLE_DETAIL_KEY to Gson().toJson(response),
                NSConstants.FLEET_DETAIL_KEY to strVehicleDetail
            )
            callback?.invoke(bundle)
            /*fleetManagementFragmentChangeCallback?.setFragment(
                this@NSVehicleFragment.javaClass.simpleName,
                NSVehicleDetailFragment.newInstance(bundle, object : NSVehicleEditCallback {
                    override fun onVehicle(vehicleData: VehicleDataItem) {
                        vehicleRecycleAdapter?.updateSingleData(vehicleData, position)
                    }
                }),
                true, bundle
            )*/
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
                            layoutLogo.tvEditTitle.text = selectImage

                            layoutRegistrationNo.edtValue.formatText()
                            layoutRegistrationNo.edtValue.inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                            layoutNotes.edtValue.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                            layoutModel.edtValue.formatText(true)
                            layoutModel.edtValue.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                            layoutManufacturer.edtValue.formatText(true)
                            layoutManufacturer.edtValue.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                            layoutManufacturerYear.edtValue.inputType = InputType.TYPE_CLASS_NUMBER
                            layoutLoadCapacity.edtValue.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                        }

                        if (!isCreate) {
                            dataItem?.apply {
                                layoutRegistrationNo.edtValue.setText(registrationNo)
                                layoutManufacturer.edtValue.setText(manufacturer)
                                layoutManufacturerYear.edtValue.setText(manufacturingYear)
                                layoutNotes.edtValue.setText(additionalNote)
                                layoutModel.edtValue.setText(model)
                                layoutLoadCapacity.edtValue.setText(loadCapacity)
                                layoutLogo.ivBrandLogo.setCoil(url = vehicleImg)
                            }
                        }

                        tvCancel.setOnClickListener {
                            dialog.dismiss()
                        }
                        
                        brandLogoHelper.initView(activity, layoutLogo.ivBrandLogo, tvSizeTitle)
                        layoutLogo.clBrandLogo.setOnClickListener {
                            brandLogoHelper.openImagePicker(activity, layoutLogo.ivBrandLogo, null, true,
                                isFill = true
                            )
                        }

                        var selectedCapabilities: MutableList<String> = arrayListOf()
                        getCapabilities(false, null) {
                            val tempList = it.filter { it.isActive }
                            val activeCapabilities = if (tempList.isValidList()) tempList.toMutableList() else arrayListOf()
                            NSUtilities.setCapability(activity, viewModel, false, isShowActiveDot = false, layoutCapability, activeCapabilities, dataItem) { capabilities ->
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