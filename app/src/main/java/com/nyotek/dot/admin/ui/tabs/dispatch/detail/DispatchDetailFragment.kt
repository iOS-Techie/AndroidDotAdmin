package com.nyotek.dot.admin.ui.tabs.dispatch.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nyotek.dot.admin.base.BaseFragment
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSOrderCancelEvent
import com.nyotek.dot.admin.common.adapter.DriverSpinnerAdapter
import com.nyotek.dot.admin.common.extension.getMapValue
import com.nyotek.dot.admin.common.extension.glideNormal
import com.nyotek.dot.admin.common.extension.gone
import com.nyotek.dot.admin.common.extension.invisible
import com.nyotek.dot.admin.common.extension.isValidList
import com.nyotek.dot.admin.common.extension.setGlideWithHolder
import com.nyotek.dot.admin.common.extension.setNormalCoil
import com.nyotek.dot.admin.common.extension.setSafeOnClickListener
import com.nyotek.dot.admin.common.extension.setTexts
import com.nyotek.dot.admin.common.extension.setVisibility
import com.nyotek.dot.admin.common.extension.setupWithAdapter
import com.nyotek.dot.admin.common.map.MapBoxView
import com.nyotek.dot.admin.databinding.LayoutDriverSpinnerItemViewBinding
import com.nyotek.dot.admin.databinding.NsFragmentDispatchDetailBinding
import com.nyotek.dot.admin.location.NSLocationManager
import com.nyotek.dot.admin.models.responses.DispatchData
import com.nyotek.dot.admin.models.responses.DispatchRequestItem
import com.nyotek.dot.admin.models.responses.DocumentDataItem
import com.nyotek.dot.admin.models.responses.NSDispatchDetailAllResponse
import com.nyotek.dot.admin.models.responses.Properties
import com.nyotek.dot.admin.models.responses.StatusItem
import com.nyotek.dot.admin.models.responses.UserMetaData
import com.nyotek.dot.admin.models.responses.VehicleData
import com.nyotek.dot.admin.models.responses.VendorDetailResponse
import com.nyotek.dot.admin.ui.tabs.dispatch.DispatchHelper
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class DispatchDetailFragment : BaseFragment<NsFragmentDispatchDetailBinding>() {

    private val viewModel by viewModels<DispatchDetailViewModel>()
    private var mapBoxView: MapBoxView? = null
    private lateinit var themeUI: DispatchDetailUI

    @Inject
    lateinit var locationManager: NSLocationManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackPressedHandler()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.selectedServiceId = arguments?.getString(NSConstants.VENDOR_SERVICE_ID_KEY)
        viewModel.getDispatchFromList(arguments?.getString(NSConstants.DISPATCH_DETAIL_KEY))
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentDispatchDetailBinding {
        mapBoxView = MapBoxView(activity, viewModel.colorResources, viewModel.languageConfig)
        return NsFragmentDispatchDetailBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        viewModel.apply {
            themeUI = DispatchDetailUI(activity, binding, viewModel.colorResources)
            binding.apply {
                viewModel.colorResources.getStringResource().apply {
                    setLayoutHeader(layoutHomeHeader, dispatchDetails, isSearch = false, isBack = true)
                }

                getServiceLogo(viewModel.selectedServiceId) {
                    ivBrandIcon.glideNormal(it) { isSuccess ->
                        ivBrandIcon.setVisibility(isSuccess)
                        ivBrandPlaceIcon.setVisibility(!isSuccess)
                    }
                }
            }
            getDispatchDetail()
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        observeBaseViewModel(viewModel)
        observe()
    }

    private fun observe() {
        viewModel.apply {
            dispatchListObserve.observe(
                viewLifecycleOwner
            ) {
                setDispatchDetailData(it)
            }

            updateOrderObserve.observe(
                viewLifecycleOwner
            ) {
                DispatchHelper.setCancelled(true)
                backPressEvent()
                EventBus.getDefault().post(NSOrderCancelEvent())
            }

            assignDriverObserve.observe(
                viewLifecycleOwner
            ) {driverId ->
                binding.tvAssignDriver.gone()
                dispatchSelectedData?.assignedDriverId = driverId
                updateDispatchLocal()
                DispatchHelper.setCancelled(true)
                getDispatchDetail()
            }
        }
    }

    private fun backPressEvent() {
        binding.mapFragmentEmployee.invisible()
        findNavController().popBackStack()
    }

    override fun setListener() {
        super.setListener()
        viewModel.apply {
            binding.apply {
                tvOrderCancel.setSafeOnClickListener {
                    updateOrderStatus(dispatchSelectedData?.dispatchId ?: "", NSConstants.ORDER_STATUS_CANCELLED)
                }

                layoutHomeHeader.ivBack.setSafeOnClickListener {
                    backPressEvent()
                }
            }
        }
    }

    private fun setDispatchDetailData(allModel: NSDispatchDetailAllResponse) {
        viewModel.currentMapFleetData = allModel.location?.fleetDataItem
        if (!viewModel.isMapReset) {
            viewModel.isMapReset = true
            mapBoxView?.clearMap()
            mapBoxView?.initMapView(
                requireContext(),
                binding.mapFragmentEmployee,
                viewModel.currentMapFleetData
            )
        }
        mapBoxView?.goToDispatchMapPosition(viewModel.currentMapFleetData?.features)
        setVehicleDetail(allModel.driverVehicleDetail?.data)
        setCustomerDetail(allModel.dispatchDetail?.data)
        setVendorDetail(allModel.dispatchDetail?.data, allModel.vendorDetail)
        setDispatchDetail(allModel.driverId, allModel.dispatchDetail?.data, allModel.driverListModel?.driverList?: arrayListOf())
        setDispatchRequestSent(allModel.dispatchRequest?.requestList?: arrayListOf())
        setDriverDetail(viewModel.getDriverDetail(allModel.driverDetail?.data))

    }

    private fun setVehicleDetail(vehicleData: VehicleData?) {
        binding.apply {
            viewModel.apply {
                layoutVehicle.apply {
                    vehicleData?.apply {
                        ivIcon.setNormalCoil(url = vehicleImg)
                        layoutName.tvDetail.text = vehicleData.manufacturer
                        layoutNumber.tvDetail.text = model
                        layoutEmail.tvDetail.text = registrationNo
                    }
                }
                layoutVehicleSecond.apply {
                    vehicleData?.apply {
                        layoutName.tvDetail.text = vehicleData.registrationNo
                        layoutNumber.tvDetail.text = manufacturingYear
                    }
                }
            }
        }
    }

    private fun setCustomerDetail(dispatchData: DispatchData?) {
        binding.apply {
            viewModel.apply {
                (dispatchSelectedData?.userMetadata?: UserMetaData()).apply {
                    layoutCustomer.apply {
                        layoutName.tvDetail.text = userName
                        layoutNumber.tvDetail.text = userPhone
                    }
                }
                layoutCustomer.layoutAddress.tvDetail.setTexts(dispatchData?.destination?.addressLine)

                clCustomerDetail.setVisibility(dispatchData != null || dispatchSelectedData?.userMetadata != null)
            }
        }
    }

    private fun setDriverDetail(documentData: DocumentDataItem?) {
        binding.apply {
            viewModel.apply {
                layoutDriver.apply {
                    layoutName.tvDetail.text = documentData?.refId
                }
                layoutDriverSecond.layoutName.tvDetail.text = documentData?.documentNumber
                //clDriverDetail.setVisibility(documentData != null)
                //clVehicleDetail.setVisibility(documentData != null)
            }
        }
    }

    private fun setDispatchDetail(driverId: String?, dispatchData: DispatchData?, list: MutableList<Properties>) {
        binding.apply {
            viewModel.apply {
                layoutOrder.apply {
                    layoutName.tvDetail.text = dispatchData?.rId
                    layoutNumber.tvDetail.text = dispatchData?.id
                    layoutEmail.tvDetail.text = dispatchData?.vendorSid
                    clOrderDetail.setVisibility(dispatchData != null)
                }
                layoutVendor.apply {
                    layoutAddress.tvDetail.setTexts(dispatchData?.pickup?.addressLine)
                    tvAddress.setTexts(dispatchData?.pickup?.addressLine)
                    tvDestinationAddress.setTexts(dispatchData?.destination?.addressLine)
                    locationManager.calculateDurationDistance(dispatchData?.pickup?.lat,dispatchData?.pickup?.lng, dispatchData?.destination?.lat,dispatchData?.destination?.lng) { _, distance ->
                        tvSpeed.text = distance.toInt().toString()
                    }

                    val statusAdapter = OrderStatusRecycleAdapter(themeUI)
                    rvOrderStatus.setupWithAdapter(statusAdapter)
                    val statusList: MutableList<StatusItem> = arrayListOf()
                    if (dispatchData?.status.isValidList()) {
                        //Display Order Cancel When Status is New
                        val currentStatus = dispatchData?.status?.first()?.status?.lowercase()
                        val isCanceled = currentStatus.equals(NSConstants.ORDER_STATUS_CANCELLED)
                        val isAssigned = currentStatus.equals(NSConstants.ORDER_STATUS_ASSIGNED)
                        val isNew = currentStatus.equals(NSConstants.ORDER_STATUS_NEW) || isAssigned
                        tvOrderCancel.setVisibility(isNew)

                        //When Status New or Assigned then Assigned Driver Button Visible

                        tvAssignDriver.setVisibility((isNew) && driverId.isNullOrEmpty() && !isCanceled)
                        if ((isNew) && driverId.isNullOrEmpty() && !isCanceled) {
                            setDriverList(list)
                        }

                        val updatedList = dispatchData?.status?.map { it.copy(isSelected = true) } as MutableList<StatusItem>
                        updatedList.sortBy { it.refId }
                        statusList.addAll(updatedList)
                        statusAdapter.setData(updatedList)
                    }
                }
            }
        }
    }

    private fun setVendorDetail(dispatchData: DispatchData?, vendorDetail: VendorDetailResponse?) {
        binding.apply {
            viewModel.apply {
                layoutVendor.apply {
                    layoutName.tvDetail.getMapValue(vendorDetail?.name)
                    layoutAddress.tvDetail.setTexts(dispatchData?.pickup?.addressLine)
                    ivIcon.setGlideWithHolder(vendorDetail?.logo, vendorDetail?.logoScale, 200, corners = 10)
                    clVendorDetail.setVisibility(dispatchData != null || vendorDetail != null)
                }
            }
        }
    }

    private fun setDispatchRequestSent(list: MutableList<DispatchRequestItem>) {
        binding.apply {
            viewModel.apply {
                val adapter = NSDispatchRequestSentRecycleAdapter(themeUI)
                rvDispatchRequestList.setupWithAdapter(adapter)
                adapter.setData(list)
                clDispatchRequestSent.setVisibility(list.isValidList())
            }
        }
    }

    private fun setDriverList(list: MutableList<Properties>) {
        binding.apply {
            viewModel.apply {
                var isSpinnerClick = false
                tvAssignDriver.setSafeOnClickListener {
                    isSpinnerClick = true
                    spinnerAssignDriver.performClick()
                }

                val adapter = DriverSpinnerAdapter(requireContext(), list, colorResources)
                spinnerAssignDriver.adapter = adapter
                spinnerAssignDriver.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                        if (isSpinnerClick) {
                            isSpinnerClick = false
                            val driverId = list[position].driverId
                            viewModel.assignDriver(dispatchSelectedData?.dispatchId?:"", driverId?:"")
                        }
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        isSpinnerClick = false
                    }
                }
                setDropdownWidthBasedOnWidestItem(list)
            }
        }
    }

    private fun setDropdownWidthBasedOnWidestItem(list: MutableList<Properties>) {
        var maxWidth = 0
        // Calculate the width of the widest item's text in the dropdown list
        for (item in list) {
            val textView = LayoutDriverSpinnerItemViewBinding.inflate(layoutInflater).tvSpinnerTitle
            textView.text = item.driverId
            textView.measure(0, 0)
            val textWidth = textView.measuredWidth + textView.paddingStart + textView.paddingEnd + textView.paddingTop + textView.paddingBottom
            maxWidth = maxOf(maxWidth, textWidth)
        }

        // Set the width of the dropdown popup
        binding.spinnerAssignDriver.dropDownWidth = maxWidth
    }

    private fun updateDispatchLocal() {
        DispatchHelper.updateDispatchItem(viewModel.dispatchSelectedData?.dispatchId, viewModel.dispatchSelectedData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPressEvent()
            }
        })
    }
}