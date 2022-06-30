package com.florinstroe.toiletlocator.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.firebase.geofire.GeoLocation
import com.florinstroe.toiletlocator.ActivityFragmentCommunication
import com.florinstroe.toiletlocator.R
import com.florinstroe.toiletlocator.data.models.Toilet
import com.florinstroe.toiletlocator.databinding.FragmentMapBinding
import com.florinstroe.toiletlocator.utilities.LocationUtil
import com.florinstroe.toiletlocator.viewmodels.LocationViewModel
import com.florinstroe.toiletlocator.viewmodels.ToiletViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat


class MapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var activityFragmentCommunication: ActivityFragmentCommunication? = null

    private val locationViewModel: LocationViewModel by activityViewModels()
    private val toiletViewModel: ToiletViewModel by activityViewModels()

    private lateinit var map: GoogleMap
    private var mapOfLocations: HashMap<Marker, Toilet> = HashMap()

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

        locationViewModel.getLocation().observe(viewLifecycleOwner) {
            println("location: $it")
        }
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map

        if (!locationPermission())
            return

        activityFragmentCommunication!!.checkLocationSettingStatus()

        setMapSettings()

        zoomOnMyLocation()

        map.setOnCameraIdleListener {
            lifecycleScope.launch(Dispatchers.Main) {
                binding.progressBar.visibility = View.VISIBLE

                toiletViewModel.getToilets(
                    GeoLocation(
                        map.cameraPosition.target.latitude,
                        map.cameraPosition.target.longitude
                    ), LocationUtil.getMapVisibleRadius(map.projection.visibleRegion)
                )

                for (location in toiletViewModel.toiletList.value!!) {
                    if (mapOfLocations.containsValue(location)) {
                        continue
                    }

                    val marker: Marker? = map.addMarker(
                        MarkerOptions()
                            .position(
                                LatLng(
                                    location.coordinates!!.latitude,
                                    location.coordinates!!.longitude
                                )
                            )
                            .icon(bitmapDescriptorFromVector(R.drawable.icon_logo))
                    )

                    mapOfLocations[marker!!] = location
                }

                binding.progressBar.visibility = View.INVISIBLE
            }
        }

        map.setOnMarkerClickListener {
            val toilet = mapOfLocations[it]
            toiletViewModel.selectedToilet = toilet
            val markerCoordinates = LatLng(it.position.latitude, it.position.longitude)

            binding.toiletDetailsCard.visibility = View.VISIBLE // show the details card

            map.animateCamera(
                CameraUpdateFactory.newLatLng(
                    LatLng(
                        toilet!!.coordinates!!.latitude,
                        toilet.coordinates!!.longitude
                    )
                )
            )

            printTheCoordinates(markerCoordinates)

            printTheAddress(toilet)

            lifecycleScope.launch(Dispatchers.Main) {
                val startPoint = Location("Start Point").apply {
                    latitude = it.position.latitude
                    longitude = it.position.longitude
                }
                val endPoint = Location("End Point").apply {
                    latitude = locationViewModel.getLocation().value?.latitude ?: 0.0
                    longitude = locationViewModel.getLocation().value?.longitude ?: 0.0
                }
                printTheDistance(startPoint, endPoint)
            }

            printLocationType(toilet)

            binding.freeChip.visibility = if (toilet.isFree) View.VISIBLE else View.GONE

            binding.accessibleChip.visibility = if (toilet.isAccessible) View.VISIBLE else View.GONE

            binding.toiletDetailsCard.setOnClickListener {
                requireActivity().findNavController(R.id.nav_host_fragment)
                    .navigate(R.id.action_mainFragment_to_viewToiletFragment)
            }
            true
        }

        map.setOnMapClickListener {
            binding.toiletDetailsCard.visibility = View.INVISIBLE
            toiletViewModel.selectedToilet = null
        }
    }

    private fun setMapSettings() {
        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
    }

    private fun zoomOnMyLocation() {
        val location: LatLng = if (toiletViewModel.selectedToilet == null) {
            LatLng(
                locationViewModel.getLocation().value?.latitude ?: 0.0,
                locationViewModel.getLocation().value?.longitude ?: 0.0
            )

        } else {
            LatLng(
                toiletViewModel.selectedToilet!!.coordinates!!.latitude,
                toiletViewModel.selectedToilet!!.coordinates!!.longitude
            )
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    private fun printLocationType(toilet: Toilet) {
        binding.locationTypeChip.text = toilet.locationType!!.name
        binding.locationTypeChip.chipIcon = ContextCompat.getDrawable(
            context!!,
            resources.getIdentifier(
                toilet.locationType!!.icon,
                "drawable",
                context!!.packageName
            )
        )
    }

    private fun locationPermission(): Boolean {
        if (!activityFragmentCommunication!!.getLocationPermissionStatus()) {
            activityFragmentCommunication!!.openPermissionsActivity()
            return false
        }
        return true
    }

    private fun printTheAddress(toilet: Toilet) {
        binding.addressTextView.text = toilet.address
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ActivityFragmentCommunication) {
            activityFragmentCommunication = context
        }
    }
}