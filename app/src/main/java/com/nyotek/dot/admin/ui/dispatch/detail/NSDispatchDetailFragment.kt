package com.nyotek.dot.admin.ui.dispatch.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.MapBoxView
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSOnMapResetEvent
import com.nyotek.dot.admin.common.NSOrderCancelEvent
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.getLngValue
import com.nyotek.dot.admin.common.utils.getMapValue
import com.nyotek.dot.admin.common.utils.glideNormal
import com.nyotek.dot.admin.common.utils.glideWithPlaceHolder
import com.nyotek.dot.admin.common.utils.gone
import com.nyotek.dot.admin.common.utils.invisible
import com.nyotek.dot.admin.common.utils.isValidList
import com.nyotek.dot.admin.common.utils.setGlideWithHolder
import com.nyotek.dot.admin.common.utils.setGlideWithPlaceHolder
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
import com.nyotek.dot.admin.common.utils.setTexts
import com.nyotek.dot.admin.common.utils.setVisibility
import com.nyotek.dot.admin.common.utils.setupWithAdapter
import com.nyotek.dot.admin.common.utils.visible
import com.nyotek.dot.admin.databinding.NsFragmentDispatchDetailBinding
import com.nyotek.dot.admin.repository.network.responses.DispatchData
import com.nyotek.dot.admin.repository.network.responses.DispatchRequestItem
import com.nyotek.dot.admin.repository.network.responses.DocumentDataItem
import com.nyotek.dot.admin.repository.network.responses.StatusItem
import com.nyotek.dot.admin.repository.network.responses.UserMetaData
import com.nyotek.dot.admin.repository.network.responses.VehicleData
import com.nyotek.dot.admin.repository.network.responses.VendorDetailResponse
import com.nyotek.dot.admin.ui.fleets.employee.detail.NSDriverDetailFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NSDispatchDetailFragment : BaseViewModelFragment<NSDispatchDetailViewModel, NsFragmentDispatchDetailBinding>() {

    //Get Service list
    override val viewModel: NSDispatchDetailViewModel by lazy {
        ViewModelProvider(this)[NSDispatchDetailViewModel::class.java]
    }

    private var isFragmentLoad = false
    private var mapBoxView: MapBoxView? = null
    private var inflater: LayoutInflater? = null
    private var container: ViewGroup? = null

    companion object {
        fun newInstance(bundle: Bundle?) = NSDispatchDetailFragment().apply {
            arguments = bundle
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentDispatchDetailBinding {
        this.inflater = inflater
        this.container = container
        mapBoxView = MapBoxView(requireContext())
        return NsFragmentDispatchDetailBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        baseObserveViewModel(viewModel)
        observeViewModel()
    }

    override fun loadFragment(bundle: Bundle?) {
        super.loadFragment(bundle)
        arguments = bundle
        clearData()
        initUI()
        viewCreated()
        setListener()

        viewModel.getDispatchDetail(bundle?.getString(NSConstants.DISPATCH_DETAIL_KEY)) { allModel ->
            viewModel.currentMapFleetData = allModel.location?.fleetDataItem
            mapBoxView?.clearMap()
            mapBoxView?.initMapView(
                requireContext(),
                binding.mapFragmentEmployee,
                viewModel.currentMapFleetData
            )
            mapBoxView?.goToDispatchMapPosition(viewModel.currentMapFleetData?.features)
            setDriverDetail(viewModel.getDriverDetail(allModel.driverDetail?.data))
            setVehicleDetail(allModel.driverVehicleDetail?.data)
            setCustomerDetail(allModel.dispatchDetail?.data)
            setVendorDetail(allModel.dispatchDetail?.data, allModel.vendorDetail)
            setDispatchDetail(allModel.dispatchDetail?.data)
            setDispatchRequestSent(allModel.dispatchRequest?.requestList?: arrayListOf())
            if(allModel.driverId?.isNotEmpty() == true) {
                binding.clDriverAndVehicle.visible()
            }
        }
    }

    private fun clearData() {
        viewModel.currentMapFleetData = null
        mapBoxView?.clearMap()
        setDriverDetail(DocumentDataItem())
        setVendorDetail(DispatchData(), VendorDetailResponse())
        setDispatchDetail(DispatchData())
        setCustomerDetail(DispatchData())
        setVehicleDetail(VehicleData())
        setDispatchRequestSent(arrayListOf())
    }

    override fun observeViewModel() {
        super.observeViewModel()
        with(viewModel) {
            with(binding) {

            }
        }
    }

    private fun initUI() {
        binding.apply {
            stringResource.apply {
                setLayoutHeader(
                    layoutHomeHeader,
                    dispatchManagement,
                    isSearch = false,
                    isBack = true
                )
                binding.clDriverAndVehicle.gone()
                tvStatus.text = orderStatus
                tvOrderCancel.text = cancelOrder
                tvOrderDetailTitle.text = orderDetails
                tvDriverDetailTitle.text = driverDetail
                tvVehicleDetailTitle.text = vehicleDetails
                tvCustomerDetailTitle.text = customerDetails
                tvVendorDetailTitle.text = vendorDetails
                tvRejectedDetailTitle.text = rejectedTripDriverList
                tvDispatchRequestSent.text = dispatchRequestSent
                tvUpdateStatus.text = update
                tvUpdateDriver.text = update
                tvTitleTrack.text = track
                binding.mapFragmentEmployee.removeAllViews()
              //  ColorResources.setBackground(binding.viewLine, ColorResources.getBorderColor())

                val serviceId = arguments?.getString(NSConstants.VENDOR_SERVICE_ID_KEY)
                viewModel.getServiceLogo(serviceId) {
                    ivBrandIcon.glideNormal(it) { isSuccess ->
                        ivBrandIcon.setVisibility(isSuccess)
                        ivBrandPlaceIcon.setVisibility(!isSuccess)
                    }
                }

                layoutOrder.apply {
                    layoutName.tvItemTitle.text = shortDispatchId
                    layoutNumber.tvItemTitle.text = dispatchId
                    layoutEmail.tvItemTitle.text = shortOrderId
                    rlAddress.gone()
                    rlName.visible()
                    rlNumber.visible()
                    rlEmail.visible()
                    viewLine.visible()
                }

                layoutDriver.apply {
                    val padding = requireContext().resources.getDimension(R.dimen.icon_padding).toInt()
                    ivIcon.setBackgroundResource(R.drawable.icon_bg)
                    ivIcon.setGlideWithPlaceHolder(activity, "", R.drawable.ic_driver_handle)
                    ivIcon.setPadding(padding, padding,padding,padding)
                    layoutName.tvItemTitle.text = name
                    layoutNumber.tvItemTitle.text = number
                    layoutEmail.tvItemTitle.text = emailAddress
                    rlAddress.gone()
                    rlName.visible()
                    viewLine.visible()
                }

                layoutDriverSecond.apply {
                    cardImg.invisible()
                    rlName.visible()
                    layoutName.tvItemTitle.text = licenseNumber
                    layoutNumber.tvItemTitle.text = age
                    rlNumber.gone()
                    rlEmail.invisible()
                    rlAddress.gone()
                }

                layoutVehicle.apply {
                    rlName.visible()
                    rlNumber.visible()
                    layoutName.tvItemTitle.text = name
                    layoutNumber.tvItemTitle.text = model
                    layoutEmail.tvItemTitle.text = loadCapacity
                    rlAddress.gone()
                    viewLine.visible()
                }

                layoutVehicleSecond.apply {
                    cardImg.invisible()
                    rlName.visible()
                    rlNumber.visible()
                    layoutName.tvItemTitle.text = registrationNo
                    layoutNumber.tvItemTitle.text = year
                    rlAddress.gone()
                }

                layoutCustomer.apply {
                    rlName.visible()
                    rlNumber.visible()
                    rlAddress.visible()
                    layoutName.tvItemTitle.text = name
                    layoutNumber.tvItemTitle.text = number
                    layoutAddress.tvItemTitle.text = address
                }

                layoutVendor.apply {
                    rlName.visible()
                    rlAddress.visible()
                    layoutName.tvItemTitle.text = name
                    layoutAddress.tvItemTitle.text = address
                }

                layoutRejectTitle.apply {
                    tvDriverName.text = driverName
                    tvVehicleName.text = vehicle
                    tvDate.text = date
                    tvTime.text = time
                }
            }
        }
    }

    /**
     * View created
     */
    private fun viewCreated() {
        viewModel.apply {
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
                    ivBack.setSafeOnClickListener {
                        onBackPress()
                    }
                }

                tvOrderCancel.setSafeOnClickListener {
                    updateOrderStatus(dispatchSelectedData?.dispatchId?:"", NSConstants.ORDER_STATUS_CANCELLED) {
                        onBackPress()
                        EventBus.getDefault().post(NSOrderCancelEvent())
                    }
                }
            }
        }
    }

    private fun setVehicleDetail(vehicleData: VehicleData?) {
        binding.apply {
            viewModel.apply {
                layoutVehicle.apply {
                    vehicleData?.apply {
                        ivIcon.glideWithPlaceHolder(url = vehicleImg)
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
            }
        }
    }

    private fun setDriverDetail(dispatchData: DocumentDataItem?) {
        binding.apply {
            viewModel.apply {
                layoutDriver.apply {
                    layoutName.tvDetail.text = dispatchData?.refId
                }
                layoutDriverSecond.layoutName.tvDetail.text = dispatchData?.documentNumber
            }
        }
    }

    private fun setDispatchDetail(dispatchData: DispatchData?) {
        binding.apply {
            viewModel.apply {
                layoutOrder.apply {
                    layoutName.tvDetail.text = dispatchData?.rId
                    layoutNumber.tvDetail.text = dispatchData?.id
                    layoutEmail.tvDetail.text = dispatchData?.vendorSid
                }
                layoutVendor.apply {
                    layoutAddress.tvDetail.setTexts(dispatchData?.pickup?.addressLine)
                    tvAddress.setTexts(dispatchData?.pickup?.addressLine)
                    tvDestinationAddress.setTexts(dispatchData?.destination?.addressLine)
                    NSApplication.getInstance().getLocationManager().calculateDurationDistance(dispatchData?.pickup?.lat,dispatchData?.pickup?.lng, dispatchData?.destination?.lat,dispatchData?.destination?.lng) { time, distance ->
                        tvSpeed.text = distance.toInt().toString()
                    }

                    val statusAdapter = OrderStatusRecycleAdapter()
                    rvOrderStatus.setupWithAdapter(statusAdapter)
                    val statusList: MutableList<StatusItem> = arrayListOf()
                    if (dispatchData?.status.isValidList()) {
                        //Display Order Cancel When Status is New
                        val isNew = dispatchData?.status?.first()?.status?.lowercase().equals(NSConstants.ORDER_STATUS_NEW)
                        tvOrderCancel.setVisibility(isNew)


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
                }
            }
        }
    }

    private fun setDispatchRequestSent(list: MutableList<DispatchRequestItem>) {
        binding.apply {
            viewModel.apply {
                val adapter = NSDispatchRequestSentRecycleAdapter()
                rvDispatchRequestList.setupWithAdapter(adapter)
                adapter.setData(list)
                clDispatchRequestSent.setVisibility(list.isValidList())
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMapReset(event: NSOnMapResetEvent) {
        if (viewModel.isMapReset) {
            viewModel.isMapReset = false
            binding.mapFragmentEmployee.removeAllViews()
            mapBoxView?.clearMap()
            mapBoxView?.initMapView(requireContext(), binding.mapFragmentEmployee,
                viewModel.currentMapFleetData
            )
        }
        if (event.isReset) {
            viewModel.isMapReset = event.isReset
            binding.mapFragmentEmployee.removeAllViews()
            mapBoxView?.clearMap()
        }
    }
}