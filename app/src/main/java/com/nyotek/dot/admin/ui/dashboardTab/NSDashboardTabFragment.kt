package com.nyotek.dot.admin.ui.dashboardTab

import android.content.pm.PackageManager
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.nyotek.dot.admin.base.fragment.BaseViewModelFragment
import com.nyotek.dot.admin.common.MapBoxView
import com.nyotek.dot.admin.common.NSAddress
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSPermissionEvent
import com.nyotek.dot.admin.common.NSRequestCodes
import com.nyotek.dot.admin.databinding.NsFragmentDashboardTabBinding
import com.nyotek.dot.admin.repository.network.responses.FleetDataItem
import com.nyotek.dot.admin.ui.fleets.map.NSMapViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NSDashboardTabFragment :
    BaseViewModelFragment<NSMapViewModel, NsFragmentDashboardTabBinding>() {

    private var isFragmentLoad: Boolean = false
    private var mapBoxView: MapBoxView? = null

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

    override fun observeViewModel() {
        super.observeViewModel()
        with(viewModel) {
            isFleetLocationListAvailable.observe(
                viewLifecycleOwner
            ) { fleetData ->
                mapBoxView?.initMapView(
                    requireContext(), binding.mapView,
                    fleetData
                )
            }
        }
    }

    override fun loadFragment() {
        super.loadFragment()
        getCurrentLocation()
        isFragmentLoad = true
        viewModel.getFleetLocations("", true)
    }

    /***
     * Init MapBox
     */
    private fun initMapBox() {
        mapBoxView?.initMapView(
            requireContext(),
            binding.mapView,
            FleetDataItem()
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
        viewModel.getFleetLocations("", false)
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
}