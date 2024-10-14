package com.nyotek.dot.admin.ui.tabs.dashboard

import android.content.pm.PackageManager
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapbox.maps.MapView
import com.nyotek.dot.admin.base.BaseFragment
import com.nyotek.dot.admin.common.NSAddress
import com.nyotek.dot.admin.common.NSDateTimeHelper
import com.nyotek.dot.admin.common.NSPermissionEvent
import com.nyotek.dot.admin.common.NSPermissionHelper
import com.nyotek.dot.admin.common.NSRequestCodes
import com.nyotek.dot.admin.common.callbacks.NSMapDriverClickCallback
import com.nyotek.dot.admin.common.extension.getMapValue
import com.nyotek.dot.admin.common.extension.gone
import com.nyotek.dot.admin.common.extension.isValidList
import com.nyotek.dot.admin.common.extension.setCoil
import com.nyotek.dot.admin.common.extension.setupWithAdapterAndCustomLayoutManager
import com.nyotek.dot.admin.common.extension.visible
import com.nyotek.dot.admin.common.map.MapBoxView
import com.nyotek.dot.admin.databinding.NsFragmentDashboardTabBinding
import com.nyotek.dot.admin.location.NSLocationManager
import com.nyotek.dot.admin.models.responses.FleetDataItem
import com.nyotek.dot.admin.models.responses.NSDispatchOrderListData
import com.nyotek.dot.admin.ui.tabs.fleets.map.NSMapViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : BaseFragment<NsFragmentDashboardTabBinding>(), NSMapDriverClickCallback {

    private var mapBoxView: MapBoxView? = null
    private var mapView: MapView? = null
    private var dispatchRecycleAdapter: NSDispatchOrderRecycleAdapter? = null
    private var isApiCalled: Boolean = false

    private val viewModel by viewModels<NSMapViewModel>()

    @Inject
    lateinit var locationManager: NSLocationManager

    @Inject
    lateinit var permissionHelper: NSPermissionHelper
    private lateinit var themeUI: DashboardUI

    companion object {
        fun newInstance() = DashboardFragment()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentDashboardTabBinding {
        return NsFragmentDashboardTabBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        initializeFragment()
    }

    private fun initializeFragment() {
        mapBoxView = MapBoxView(requireContext(), viewModel.colorResources, viewModel.languageConfig)
        mapView = MapView(requireContext())
        themeUI = DashboardUI(binding, viewModel.colorResources)
        if (isAdded) {
            loadFleetMap()
            setFrameToMapView()
            initMapView(FleetDataItem())
            initUI()
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        observeBaseViewModel(viewModel)
        observe()
    }

    private fun observe() {
        viewModel.apply {
            fleetDataItemObserve.observe(
                viewLifecycleOwner
            ) {
                initMapView(it)
            }

            dispatchOrderItemObserve.observe(
                viewLifecycleOwner
            ) {
                setAdapter(it)
            }

            refresh.observe(
                viewLifecycleOwner
            ) {
                binding.srlRefresh.isRefreshing = it
            }
        }
    }

    private fun loadFleetMap() {
        viewModel.apply {
            getCurrentLocation()
            viewModel.getFleetLocations(true, isFromFleetDetail = false)
        }
    }

    private fun setFrameToMapView() {
        binding.mapView.apply {
            removeAllViews()
            addView(mapView!!)
        }
    }

    private fun initMapView(fleetData: FleetDataItem?) {
        viewModel.tempFleetDataItem = fleetData
        if (mapView != null) {
            mapBoxView?.initMapView(
                requireContext(), mapView!!,
                fleetData, mapCallback = this, key = 0
            )
        }
    }

    private fun initUI() {
        binding.apply {
            stringResource.apply {
                setLayoutHeader(
                    layoutHomeHeader,
                    dashboard
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (mapBoxView?.getMapView(0) != null) {
            binding.mapView.removeAllViews()
            mapView = mapBoxView?.getMapView(0)!!
            binding.mapView.addView(mapView!!)
        }
        eventRegister(true)
    }

    override fun onPause() {
        super.onPause()
        eventRegister(false)
    }

    private fun eventRegister(isRegister: Boolean) {
        if (isRegister) {
            //if (isFragmentLoad) {
                getCurrentLocation()
            //}
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this)
            }
        } else {
            locationManager.removeLocation()
            EventBus.getDefault().unregister(this)
        }
    }

    private fun getCurrentLocation() {
        if (permissionHelper.isLocationPermissionEnable(activity, NSRequestCodes.REQUEST_LOCATION_CODE)) {
            locationManager.requestLocation(Looper.getMainLooper(), false)
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun getCurrentLocation(event: NSAddress) {
        if (!isApiCalled) {
            isApiCalled = true
            viewModel.getFleetLocations(false, isFromFleetDetail = false)
        } else {
            isApiCalled = false
        }
        
        if (event.addresses.isValidList()) {
            val address = event.addresses[0].getAddressLine(0).toString()
            if (address.isNotEmpty()) {
                event.locationResult.lastLocation?.apply {
                    mapBoxView?.setCurrentLatLong(latitude, longitude)
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPermissionEvent(event: NSPermissionEvent) {
        when (event.requestCode) {
            NSRequestCodes.REQUEST_LOCATION_CODE -> {
                if (event.grantResults.isNotEmpty() && event.grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation()
                } else {
                    Toast.makeText(requireActivity(), "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    /*@Subscribe(threadMode = ThreadMode.MAIN)
    fun onMapReset(@Suppress("UNUSED_PARAMETER") event: NSOnMapResetEvent) {
        binding.mapView.removeAllViews()
        mapView = MapView(requireContext())
        binding.mapView.addView(mapView)
        mapBoxView?.clearMap()
        mapBoxView = MapBoxView(requireContext(), viewModel.colorResources, viewModel.languageConfig)
        mapBoxView?.initMapView(
            requireContext(),
            mapView!!,
            viewModel.tempFleetDataItem?:FleetDataItem()
        )
    }*/

    override fun onDriverMap(driverId: String, isDialogDismiss: Boolean) {
        viewModel.apply {
            binding.apply {
                if (!isDialogDismiss) {
                    srlRefresh.visible()

                    srlRefresh.setOnRefreshListener {
                        getDispatchDrivers(driverId, false)
                    }

                    getDispatchDrivers(driverId, true)
                } else {
                    srlRefresh.gone()
                    setAdapter(arrayListOf())
                }
            }
        }
    }

    private fun setAdapter(list: MutableList<NSDispatchOrderListData>?) {
        viewModel.apply {
            with(binding) {
                with(rvAssignedList) {
                    list?.sortByDescending { NSDateTimeHelper.getDateValue(it.status[0].statusCapturedTime)}
                    if (dispatchRecycleAdapter == null) {
                        dispatchRecycleAdapter = NSDispatchOrderRecycleAdapter(themeUI) { vendorId, vendorIcon, vendorName ->
                            getVendorInfo(vendorId) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    vendorIcon.setCoil(it?.logo, it?.logoScale)
                                    vendorName.getMapValue(it?.name)
                                }
                            }
                        }

                        setupWithAdapterAndCustomLayoutManager(
                            dispatchRecycleAdapter!!,
                            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                        )
                        isNestedScrollingEnabled = false
                    }
                    dispatchRecycleAdapter?.setData(list?: arrayListOf())
                }
            }
        }
    }
}