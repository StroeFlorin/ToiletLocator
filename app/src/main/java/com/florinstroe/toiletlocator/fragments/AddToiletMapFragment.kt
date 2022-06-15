package com.florinstroe.toiletlocator.fragments

import android.graphics.Color
import android.location.Geocoder
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.florinstroe.toiletlocator.R
import com.florinstroe.toiletlocator.databinding.FragmentAddToiletMapBinding
import com.florinstroe.toiletlocator.utilities.LocationUtil
import com.florinstroe.toiletlocator.viewmodels.LocationViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddToiletMapFragment : Fragment(), OnMapReadyCallback {
    private val locationVM: LocationViewModel by activityViewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var _binding: FragmentAddToiletMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddToiletMapBinding.inflate(inflater, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    locationVM.askForLastDeviceLocation()
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        binding.toolbar2.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24)
        binding.toolbar2.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        binding.nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_addToiletMapFragment_to_addToiletDetailsFragment)
        }
    }

    private fun setCameraSettings() {
        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
        map.uiSettings.isZoomControlsEnabled = true
    }

    private fun drawGreenCircle(coordinates: LatLng) {
        map.addCircle(
            CircleOptions()
                .center(LatLng(coordinates.latitude, coordinates.longitude))
                .radius(450.0)
                .strokeColor(Color.parseColor("#014421"))
                .fillColor(Color.parseColor("#809DCDA0"))
        )
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map

        setCameraSettings()

        // everytime the device moves redraw the green circle
        locationVM.getLocation().observe(viewLifecycleOwner) {
            map.clear()
            drawGreenCircle(LatLng(it.latitude, it.longitude))
        }

        // update address with every move
        map.setOnCameraIdleListener {
            lifecycleScope.launch(Dispatchers.Main) {
                val coordinates = map.cameraPosition.target
                var fullAddress: String?
                withContext(Dispatchers.IO) {
                    fullAddress = try {
                        LocationUtil.getFullAddressFromCoordinates(
                            coordinates,
                            Geocoder(context)
                        )
                    } catch (e: Exception) {
                        "Address not found..."
                    }
                }
                binding.addressTextView.text = fullAddress
            }
        }

        map.setOnCameraMoveStartedListener()
        {
            binding.addressTextView.text = "Loading address..."
        }
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onResume() {
        startLocationUpdates()
        super.onResume()
    }
}