package com.florinstroe.toiletlocator.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.florinstroe.toiletlocator.ActivityFragmentCommunication
import com.florinstroe.toiletlocator.R
import com.florinstroe.toiletlocator.databinding.FragmentMapBinding
import com.florinstroe.toiletlocator.utilities.LocationUtil
import com.florinstroe.toiletlocator.viewmodels.LocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat


class MapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private var activityFragmentCommunication: ActivityFragmentCommunication? = null
    private val locationVM: LocationViewModel by activityViewModels()
    private lateinit var map: GoogleMap
    private var listOfLocations: List<LatLng> = listOf(
        LatLng(45.16501654474241, 26.78933279498917),
        LatLng(45.1519247081899, 26.818178408050027),
        LatLng(45.1630648122714, 26.81756477622222),
        LatLng(45.1636700618852, 26.814040848698717),
        LatLng(45.16208149233267, 26.821567213059684),
        LatLng(45.141215735403996, 26.80915897116156),
        LatLng(45.663567661811385, 25.560803366717515),
        LatLng(45.658284584053796, 25.561259123383742),
        LatLng(45.6428462077683, 25.58937456204003),
        LatLng(45.13825944636694, 26.7341331785445),
        LatLng(45.148727697530376, 26.640001232534154),
        LatLng(44.998089373633356, 26.440774127976304),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        locationVM.getLocation().observe(viewLifecycleOwner) {
            Log.d("LOCATION", it.toString())
        }

    }

    private fun setMapSettings() {
        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
    }

    private fun getLocationPermissionStatus(): Boolean {
        return (ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
                == PackageManager.PERMISSION_GRANTED)
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map

        if(!locationPermission())
            return

        setMapSettings()

        map.setOnCameraIdleListener {
            lifecycleScope.launch(Dispatchers.Main) {
                map.clear()
                binding.progressBar.visibility = View.VISIBLE

                val listOfVisibleMarkers: ArrayList<LatLng> = getListOfVisibleMarkers()
                for (location in listOfVisibleMarkers) {
                    map.addMarker(
                        MarkerOptions()
                            .position(location)
                            .title("TOILET")
                            .icon(bitmapDescriptorFromVector(R.drawable.icon_logo))
                    )
                }

                binding.progressBar.visibility = View.INVISIBLE
            }
        }

        map.setOnMarkerClickListener {
            val markerCoordinates = LatLng(it.position.latitude, it.position.longitude)

            map.animateCamera(CameraUpdateFactory.newLatLng(markerCoordinates))   // zoom on the marker

            binding.toiletDetailsCard.visibility = View.VISIBLE // show the details card

            printTheCoordinates(markerCoordinates)

            lifecycleScope.launch(Dispatchers.Main) {
                printTheAddress(markerCoordinates)
            }

            lifecycleScope.launch(Dispatchers.Main) {
                val startPoint = Location("Start Point").apply {
                    latitude = it.position.latitude
                    longitude = it.position.longitude
                }
                val endPoint = Location("End Point").apply {
                    latitude = locationVM.getLocation().value?.latitude ?: 0.0
                    longitude = locationVM.getLocation().value?.longitude ?: 0.0
                }
                printTheDistance(startPoint, endPoint)
            }
            true
        }

        // hide the marker details card when user taps on the map
        map.setOnMapClickListener {
            binding.toiletDetailsCard.visibility = View.INVISIBLE
        }
    }

    private fun locationPermission(): Boolean {
        if(!getLocationPermissionStatus()) {
            activityFragmentCommunication!!.openPermissionsActivity()
            return false
        }
        return true
    }

    private suspend fun getListOfVisibleMarkers(): ArrayList<LatLng> {
        val listOfVisibleMarkers: ArrayList<LatLng> = ArrayList()

        val mapBounds = map.projection.visibleRegion.latLngBounds

        withContext(Dispatchers.IO) {
            for (location in listOfLocations) {
                if (mapBounds.contains(location)) {
                    listOfVisibleMarkers.add(location)
                }
            }
        }

        return listOfVisibleMarkers
    }

    private suspend fun printTheAddress(markerCoordinates: LatLng) {
        var address = "Loading..."
        binding.addressTextView.text = address

        withContext(Dispatchers.IO) {
            address = try {
                LocationUtil.getFullAddressFromCoordinates(
                    markerCoordinates, Geocoder(context)
                )
            } catch (e: Exception) {
                "No address found"
            }
        }

        binding.addressTextView.text = address
    }

    private suspend fun printTheDistance(startPoint: Location, endPoint: Location) {
        var distance = "calculating distance..."
        binding.distanceTextView.text = distance

        withContext(Dispatchers.IO) {
            distance = LocationUtil.getDistanceBetween2LocationsAsString(startPoint, endPoint)
        }

        binding.distanceTextView.text = distance
    }

    private fun printTheCoordinates(markerCoordinates: LatLng) {
        val df = DecimalFormat("#.######")
        binding.coordinatesTextView.text =
            "${df.format(markerCoordinates.latitude)}, ${df.format(markerCoordinates.longitude)}"
    }

    private fun bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context!!, vectorResId)?.run {
            setBounds(0, 0, 70, 70)
            val bitmap = Bitmap.createBitmap(70, 70, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ActivityFragmentCommunication) {
            activityFragmentCommunication = context
        }
    }
}