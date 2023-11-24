package com.nyotek.dot.admin.ui.dashboardTab

import android.content.pm.PackageManager
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapbox.maps.MapView
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.MapBoxView
import com.nyotek.dot.admin.common.NSAddress
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSDateTimeHelper
import com.nyotek.dot.admin.common.NSOnMapResetEvent
import com.nyotek.dot.admin.common.NSPermissionEvent
import com.nyotek.dot.admin.common.NSRequestCodes
import com.nyotek.dot.admin.common.callbacks.NSMapDriverClickCallback
import com.nyotek.dot.admin.common.utils.gone
import com.nyotek.dot.admin.common.utils.setupWithAdapterAndCustomLayoutManager
import com.nyotek.dot.admin.common.utils.visible
import com.nyotek.dot.admin.databinding.NsFragmentDashboardTabBinding
import com.nyotek.dot.admin.repository.network.responses.FleetDataItem
import com.nyotek.dot.admin.repository.network.responses.NSDispatchOrderListData
import com.nyotek.dot.admin.ui.fleets.employee.NSEmployeeFragment
import com.nyotek.dot.admin.ui.fleets.map.NSMapViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NSDashboardTabFragment :
    BaseViewModelFragment<NSMapViewModel, NsFragmentDashboardTabBinding>(),
    NSMapDriverClickCallback {

    private var isFragmentLoad: Boolean = false
    private var mapBoxView: MapBoxView? = null
    private var mapView: MapView? = null
    private var capabilitiesRecycleAdapter: NSDispatchOrderRecycleAdapter? = null

    override val viewModel: NSMapViewModel by lazy {
        ViewModelProvider(this)[NSMapViewModel::class.java]
    }
    
    companion object {
        fun newInstance() = NSDashboardTabFragment()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NsFragmentDashboardTabBinding {
        mapBoxView = MapBoxView(requireContext())
        mapView = MapView(requireContext())
        return NsFragmentDashboardTabBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        setFrameToMapView()
        baseObserveViewModel(viewModel)
        observeViewModel()
        initMapBox()
        initUI()
    }

    private fun setFrameToMapView() {
        binding.mapView.removeAllViews()
        binding.mapView.addView(mapView!!)
    }

    override fun loadFragment() {
        super.loadFragment()
        getCurrentLocation()
        isFragmentLoad = true
        viewModel.getFleetLocations("", true, isFromFleetDetail = false) {
            initMapView(it)
        }
    }

    private fun initMapView(fleetData: FleetDataItem?) {
        viewModel.tempFleetDataItem = fleetData
        mapBoxView?.initMapView(
            requireContext(), mapView!!,
            fleetData, mapCallback = this
        )
    }

    /***
     * Init MapBox
     */
    private fun initMapBox() {
        mapBoxView?.initMapView(
            requireContext(),
            mapView!!,
            FleetDataItem(), mapCallback = this
        )
    }

    /**
     * Init ui
     *
     */
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

    /**
     * Event register and deRegister for getting location
     *
     * @param isRegister
     */
    fun eventRegister(isRegister: Boolean) {
        if (isRegister) {
            if (isFragmentLoad) {
                getCurrentLocation()
            }
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this)
            }
        } else {
            NSApplication.getInstance().getLocationManager().removeLocation()
            EventBus.getDefault().unregister(this)
        }
    }

    /**
     * get current location of user
     */
    private fun getCurrentLocation() {
        if (NSApplication.getInstance().getPermissionHelper()
                .isLocationPermissionEnable(activity, NSRequestCodes.REQUEST_LOCATION_CODE)
        ) {
            NSApplication.getInstance().getLocationManager()
                .requestLocation(Looper.getMainLooper(), false)
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun getCurrentLocation(event: NSAddress) {
        viewModel.getFleetLocations("", false, isFromFleetDetail = false) {
            initMapView(it)
        }
        val address = event.addresses[0].getAddressLine(0).toString()
        if (address.isNotEmpty()) {
            event.locationResult.lastLocation?.apply {
                mapBoxView?.setCurrentLatLong(latitude, longitude)
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMapReset(event: NSOnMapResetEvent) {
        binding.mapView.removeAllViews()
        mapView = MapView(requireContext())
        binding.mapView.addView(mapView)
        mapBoxView?.clearMap()
        mapBoxView = MapBoxView(requireContext())
        mapBoxView?.initMapView(
            requireContext(),
            mapView!!,
            viewModel.tempFleetDataItem?:FleetDataItem()
        )
    }

    override fun onDriverMap(driverId: String, isDialogDismiss: Boolean) {
        viewModel.apply {
            binding.apply {
                if (!isDialogDismiss) {
                    srlRefresh.visible()

                    srlRefresh.setOnRefreshListener {
                        getDispatchDrivers(driverId, false) {
                            setAdapter(it)
                        }
                    }

                    getDispatchDrivers(driverId, true) {
                        setAdapter(it)
                    }
                } else {
                    srlRefresh.gone()
                    setAdapter(arrayListOf())
                }
            }
        }
    }

    private fun setAdapter(list: MutableList<NSDispatchOrderListData>) {
        viewModel.apply {
            with(binding) {
                with(rvAssignedList) {
                    list.sortByDescending { NSDateTimeHelper.getDateValue(it.status[0].statusCapturedTime)}
                    if (capabilitiesRecycleAdapter == null) {
                        capabilitiesRecycleAdapter = NSDispatchOrderRecycleAdapter(activity, { model, isDelete ->

                        })

                        setupWithAdapterAndCustomLayoutManager(
                            capabilitiesRecycleAdapter!!,
                            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                        )
                        isNestedScrollingEnabled = false
                    }
                    capabilitiesRecycleAdapter?.setData(list)
                }
            }
        }
    }
}