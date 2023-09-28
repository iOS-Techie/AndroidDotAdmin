package com.nyotek.dot.admin.ui.dashboardTab

import android.content.pm.PackageManager
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.MapBoxView
import com.nyotek.dot.admin.common.NSAddress
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSDateTimeHelper
import com.nyotek.dot.admin.common.NSPermissionEvent
import com.nyotek.dot.admin.common.NSRequestCodes
import com.nyotek.dot.admin.common.callbacks.NSMapDriverClickCallback
import com.nyotek.dot.admin.common.utils.gone
import com.nyotek.dot.admin.common.utils.setupWithAdapterAndCustomLayoutManager
import com.nyotek.dot.admin.common.utils.visible
import com.nyotek.dot.admin.databinding.NsFragmentDashboardTabBinding
import com.nyotek.dot.admin.repository.network.responses.FleetDataItem
import com.nyotek.dot.admin.repository.network.responses.NSDispatchOrderListData
import com.nyotek.dot.admin.ui.fleets.map.NSMapViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NSDashboardTabFragment :
    BaseViewModelFragment<NSMapViewModel, NsFragmentDashboardTabBinding>(),
    NSMapDriverClickCallback {

    private var isFragmentLoad: Boolean = false
    private var mapBoxView: MapBoxView? = null
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
        return NsFragmentDashboardTabBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        baseObserveViewModel(viewModel)
        observeViewModel()
        initMapBox()
        initUI()
    }

    override fun loadFragment() {
        super.loadFragment()
        getCurrentLocation()
        isFragmentLoad = true
        viewModel.getFleetLocations("", true) {
            initMapView(it)
        }
    }

    private fun initMapView(fleetData: FleetDataItem?) {
        mapBoxView?.initMapView(
            requireContext(), binding.mapView,
            fleetData, mapCallback = this
        )
    }

    /***
     * Init MapBox
     */
    private fun initMapBox() {
        mapBoxView?.initMapView(
            requireContext(),
            binding.mapView,
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
        viewModel.getFleetLocations("", false) {
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