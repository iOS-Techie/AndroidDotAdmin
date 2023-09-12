package com.nyotek.dot.admin.ui.fleets.map

import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.NSAddress
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSPermissionEvent
import com.nyotek.dot.admin.common.NSRequestCodes
import com.nyotek.dot.admin.common.utils.*
import com.nyotek.dot.admin.databinding.LayoutSelectAddressBinding
import com.nyotek.dot.admin.repository.network.responses.AddressData
import com.nyotek.dot.admin.repository.network.responses.StringResourceResponse
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class LocationPickerDialog : DialogFragment(), OnMapReadyCallback {

    private var _binding: LayoutSelectAddressBinding? = null
    private val binding get() = _binding!!
    private var googleMap: GoogleMap? = null
    private var currentLatLng: LatLng? = null
    var stringResource = StringResourceResponse()
    private val pref = NSApplication.getInstance().getPrefs()
    var isCurrentLocationSelected = false
    var isFirstTimeLocationCall = true

    companion object {
        private var mapViewModel: NSMapViewModel? = null
        private var addressSelectCallback: ((AddressData?) -> Unit)? = null
        private var isFromEdit: Boolean = false
        private lateinit var selectedPositions: IntArray
        private var height: Int = 0

        fun newInstance(isFromEditBranch: Boolean, positions: IntArray, viewHeight: Int, addressData: AddressData?, mapModel: NSMapViewModel, vendorId: String, serviceIdList: MutableList<String>, addressId: String, addressCallback: ((AddressData?) -> Unit)) = LocationPickerDialog().apply {
            height = viewHeight
            selectedPositions = positions
            isFromEdit = isFromEditBranch
            mapViewModel = mapModel
            addressSelectCallback = addressCallback
            mapViewModel?.selectedVendorId = vendorId
            mapViewModel?.selectedServiceIdList = serviceIdList
            mapViewModel?.selectedAddressId = addressId
            mapViewModel?.selectedAddressModel = addressData
            mapViewModel?.currentAddressData = null
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        _binding = LayoutSelectAddressBinding.inflate(inflater, container, false)
        NSUtilities.setupSelectAddressView(binding)
        setBackAndNextAddress(binding, true)
        NSUtilities.setMapButtonUI(0, binding)
        isFirstTimeLocationCall = mapViewModel?.selectedAddressModel?.addr1?.isEmpty()?:false
        binding.tvSearch.text = mapViewModel?.selectedAddressModel?.addr1
        binding.rlEnd.setVisibility(!isFromEdit)
        binding.rlStart.setVisibility(isFromEdit)
        binding.ivRightArrow.rotation = if (pref.isLanguageRTL) 270f else 90f
        binding.ivTopArrow.rotation = if (pref.isLanguageRTL) -270f else -90f
        binding.ivBackAddress.rotation = if (pref.isLanguageRTL) 180f else 0f

        binding.rlEnd.setPadding(0, selectedPositions[1]/4, 0,0)

        binding.tvStandard.setOnClickListener {
            NSUtilities.setMapButtonUI(0, binding)
            googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
        }

        binding.tvSatellite.setOnClickListener {
            NSUtilities.setMapButtonUI(1, binding)
            googleMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
        }

        binding.tvHybrid.setOnClickListener {
            NSUtilities.setMapButtonUI(2, binding)
            googleMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
        }

        binding.ivBackAddress.setOnClickListener {
            setBackAndNextAddress(binding, true)
        }

        binding.tvAddressDone.setOnClickListener {
            dismiss()
            setBackAndNextAddress(binding, true)
            if (!isFromEdit) {
                mapViewModel?.createOrEditAddress(addressSelectCallback!!)
            } else {
                mapViewModel?.branchEditAddress(addressSelectCallback!!)
            }
        }

        binding.ivAdd.setOnClickListener {
            setBackAndNextAddress(binding, false)
            addAddressForm(binding)
        }

        binding.ivCurrentLocation.setOnClickListener {
            if (currentLatLng != null) {
                isCurrentLocationSelected = true
                getCurrentLocation()
            } else {
                getCurrentLocationAddress()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fm = childFragmentManager
        var mapFragment = fm.findFragmentByTag("mapFragment") as SupportMapFragment?
        if (mapFragment == null) {
            mapFragment = SupportMapFragment()
            val ft = fm.beginTransaction()
            ft.add(R.id.map_fragment, mapFragment, "mapFragment")
            ft.commit()
            fm.executePendingTransactions()
        }
        mapFragment.getMapAsync(this)
        val windowManager: WindowManager.LayoutParams? = dialog?.window?.attributes
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        windowManager?.gravity = if (isFromEdit) Gravity.TOP or if (pref.isLanguageRTL) Gravity.START else Gravity.END else Gravity.TOP or if (pref.isLanguageRTL) Gravity.END else Gravity.START
        windowManager?.x = selectedPositions[0]/2 //x position
        windowManager?.y = selectedPositions[1]/2 + if (isFromEdit) 10 else height/2
    }

    private fun loadMapPosition(latitude: Double, longitude: Double) {
        if (latitude != 0.0 && longitude != 0.0) {
            val latLng = LatLng(latitude, longitude)
            setMapPosition(latLng)
        } else {
            if (currentLatLng != null) {
                isCurrentLocationSelected = true
                getCurrentLocation()
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        this.googleMap = p0
        googleMap!!.setPadding(0, 0, 0, 30)
        p0.uiSettings.isMyLocationButtonEnabled = true
        p0.uiSettings.isCompassEnabled = false
        loadMapPosition(mapViewModel?.selectedAddressModel?.lat?:0.0, mapViewModel?.selectedAddressModel?.lng?:0.0)
        binding.tvSearch.text = mapViewModel?.selectedAddressModel?.addr1
        if (mapViewModel?.selectedAddressModel?.addr1?.isEmpty() != false) {
            getCurrentLocation()
        }

        googleMap?.setOnCameraIdleListener {
            if (!isFirstTimeLocationCall) {
                isFirstTimeLocationCall = true
            } else {
                getCurrentLocationAddress()
            }
        }
    }

    private fun addAddressForm(bind: LayoutSelectAddressBinding) {

        if (mapViewModel?.currentAddressData?.addr1.isNullOrEmpty()) {
            mapViewModel?.selectedAddressModel?.apply {
                setAddressForm(bind, lat.toString(), lng.toString(), addr1?:"", city?:"", state?:"", country?:"", zip?:"")
            }
        } else {
            mapViewModel?.currentAddressData?.apply {
                setAddressForm(bind, lat.toString(), lng.toString(), addr1?:"", city?:"", state?:"", country?:"", zip?:"")
            }
        }

        bind.btnSaveAddress.setOnClickListener {
            dismiss()
            setBackAndNextAddress(bind, true)
            if (mapViewModel?.currentAddressData == null) {
                mapViewModel?.currentAddressData = AddressData()
                mapViewModel?.apply {
                    currentAddressData?.apply {
                        lat = selectedAddressModel?.lat?:0.0
                        lng = selectedAddressModel?.lng?:0.0
                    }
                }
            }
            mapViewModel?.currentAddressData?.apply {
                addr1 = bind.etAddress.text.toString()
                city = bind.etCity.text.toString()
                zip = bind.etPostalCode.text.toString()
                country = bind.etCountry.text.toString()
                state = bind.etState.text.toString()
            }

            if (!isFromEdit) {
                mapViewModel?.createOrEditAddress(addressSelectCallback!!)
            } else {
                mapViewModel?.branchEditAddress(addressSelectCallback!!)
            }
        }
    }

    private fun setAddressForm(bind: LayoutSelectAddressBinding, lat: String, lng: String, address: String, city: String, state: String, country: String, zip: String) {
        bind.apply {
            tvAddressLat.addTextCol(stringResource.latShort, lat)
            tvAddressLong.addTextCol(stringResource.longShort, lng)
            etAddress.setText(address)
            etCity.setText(city)
            etPostalCode.setText(zip)
            etCountry.setText(country)
            etState.setText(state)
        }
    }

    private fun setBackAndNextAddress(bind: LayoutSelectAddressBinding, isFirst: Boolean) {
        bind.tvAddressDone.setVisibility(isFirst)
        bind.ivBackAddress.setVisibility(!isFirst)
        bind.nsvAddAddress.setVisibility(!isFirst)
        bind.clAddress.setVisibility(isFirst)
        bind.tvHeaderTitle.text =
            if (isFirst) stringResource.selectAddress else stringResource.addAddress
    }

    private fun setMapPosition(latLong: LatLng?) {
        if (latLong != null) {
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 10f))
        }
    }

    private fun getCurrentLocationAddress() {
        val latLng: LatLng = googleMap!!.cameraPosition.target
        Handler(Looper.getMainLooper()).post {
            try {
                val geocoder = Geocoder(requireContext(), Locale.ENGLISH)
                val addresses: List<Address>?
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(
                            latLng.latitude, latLng.longitude, 1
                        ) { locations ->
                            getLocationAddress(
                                locations,
                                latLng.latitude,
                                latLng.longitude
                            )
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                        getLocationAddress(addresses, latLng.latitude, latLng.longitude)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getLocationAddress(addresses: List<Address>?, latitude: Double, longitude: Double) {
        mapViewModel?.apply {
            if (addresses != null) {
                if (addresses.isValidList()) {
                    setAddressDetail(addresses[0])
                } else {
                    if (currentAddressData == null) {
                        currentAddressData = AddressData()
                        with(currentAddressData!!) {
                            lat = latitude
                            lng = longitude
                        }
                    }
                }
            }
        }
    }

    private fun setAddressDetail(addressDetail: Address) {
        mapViewModel?.getSelectedAddress(addressDetail)
        val address = addressDetail.getAddressLine(0).toString()
        binding.tvSearch.text = address
    }

    private fun getCurrentLocation() {
        if (NSApplication.getInstance().getPermissionHelper()
                .isLocationPermissionEnable(requireActivity(), 101)
        ) {
            NSApplication.getInstance().getLocationManager()
                .requestLocation(Looper.getMainLooper(), true)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getCurrentLocation(event: NSAddress) {
        val address = event.addresses[0].getAddressLine(0).toString()
        if (address.isNotEmpty()) {
            if (pref.selectedAddress.isNullOrEmpty() || isCurrentLocationSelected || currentLatLng == null) {
                isCurrentLocationSelected = false
                val current = LatLng(event.addresses[0].latitude, event.addresses[0].longitude)
                currentLatLng = current
                setAddressDetail(event.addresses[0])
                if (googleMap != null) {
                    googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 10f))
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
}