package com.florinstroe.toiletlocator

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.florinstroe.toiletlocator.databinding.FragmentMapBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Math.abs
import java.text.DecimalFormat


class MapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private val locationVM: LocationViewModel by activityViewModels()

    private var map: GoogleMap? = null
    private lateinit var geocoder: Geocoder

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

        geocoder = Geocoder(context)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        val locationProvider = LocationServices.getFusedLocationProviderClient(context!!)
        locationVM.setFusedLocationProviderClient(locationProvider)
        locationVM.getLocation().observe(viewLifecycleOwner) {
            Log.d("GeoLocation", it.latitude.toString() + "," + it.longitude.toString())
        }



    }

    private fun bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context!!, vectorResId)?.run {
            setBounds(0, 0, 70, 70)
            val bitmap = Bitmap.createBitmap(70 , 70, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        this.map!!.isMyLocationEnabled = true
        this.map!!.uiSettings.isMyLocationButtonEnabled = true
        map.moveCamera(CameraUpdateFactory.zoomTo(10f))








        locationVM.getLocation().observe(viewLifecycleOwner) {
            Log.d("GeoLocation", it.latitude.toString() + "," + it.longitude.toString())
            this.map!!.moveCamera(CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude)))
            this.map!!.animateCamera(CameraUpdateFactory.zoomTo(15f))
        }



        this.map!!.setOnCameraIdleListener {
            Log.d("Zoom", map.cameraPosition.zoom.toString())
            viewLifecycleOwner.lifecycleScope.launch {
                //map.clear()
                binding.progressBar.visibility = View.VISIBLE
                for (location in listOfLocations) {
                    if (map.projection.visibleRegion.latLngBounds.contains(location)) {
                        map.addMarker(
                            MarkerOptions()
                                .position(location)
                                .title("WC")
                                .icon(bitmapDescriptorFromVector(R.drawable.icon_logo))
                            )
                    }
                }
                //delay(1000L)
                binding.progressBar.visibility = View.INVISIBLE
            }
        }


        this.map!!.setOnMapClickListener {
            binding.toiletDetailsCard.visibility = View.INVISIBLE
        }

        this.map!!.setOnMarkerClickListener {
            Log.d("Marker", it.title.toString())

            CoroutineScope(Dispatchers.Main).launch {
                map.animateCamera(CameraUpdateFactory.newLatLng(it.position))

                binding.toiletDetailsCard.visibility = View.VISIBLE
                try {
                    binding.addressTextView.text = geocoder.getFromLocation(
                        it.position.latitude,
                        it.position.longitude,
                        1
                    )[0].getAddressLine(0)
                } catch (e: Exception) {
                    binding.addressTextView.text = "No address found"
                }
                val startPoint = Location("Start Point")
                startPoint.latitude = it.position.latitude
                startPoint.longitude = it.position.longitude
                val endPoint = Location("End Point")
                endPoint.latitude = locationVM.getLocation().value?.latitude ?: 0.0
                endPoint.longitude = locationVM.getLocation().value?.longitude ?: 0.0
                val distanceInMeters = startPoint.distanceTo(endPoint)
                val distanceInKm = distanceInMeters / 1000
                val df = DecimalFormat("#.##")
                binding.distanceTextView.text = "${df.format(distanceInKm)} km away"
                val df_coordinates = DecimalFormat("#.######")
                binding.coordinatesTextView.text =
                    "${df_coordinates.format(it.position.latitude)}, ${df_coordinates.format(it.position.longitude)}"
            }
            true
        }

    }
}