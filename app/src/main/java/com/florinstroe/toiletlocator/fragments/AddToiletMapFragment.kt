package com.florinstroe.toiletlocator.fragments

import android.content.Context
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.florinstroe.toiletlocator.ActivityFragmentCommunication
import com.florinstroe.toiletlocator.R
import com.florinstroe.toiletlocator.databinding.FragmentAddToiletMapBinding
import com.florinstroe.toiletlocator.utilities.LocationUtil
import com.florinstroe.toiletlocator.viewmodels.AddToiletViewModel
import com.florinstroe.toiletlocator.viewmodels.LocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddToiletMapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentAddToiletMapBinding? = null
    private val binding get() = _binding!!
    private var activityFragmentCommunication: ActivityFragmentCommunication? = null

    private val locationVM: LocationViewModel by activityViewModels()
    private val addToiletViewModel: AddToiletViewModel by activityViewModels()

    private lateinit var map: GoogleMap
    private lateinit var greenCircle: Circle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddToiletMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        binding.toolbar.setNavigationOnClickListener {
            backButtonAction()
        }

        binding.cancelButton.setOnClickListener {
            backButtonAction()
        }

        addToiletViewModel.addressFormState.observe(viewLifecycleOwner, Observer {
            val addressState = it ?: return@Observer

            binding.continueButton.isEnabled = addressState.isDataValid

            if (addressState.AddressError != null) {
                binding.addressTextField.error = getString(addressState.AddressError)
            } else {
                binding.addressTextField.error = null
            }
        })
    }

    private fun backButtonAction() {
        MaterialAlertDialogBuilder(context!!)
            .setTitle(resources.getString(R.string.app_name))
            .setMessage(resources.getString(R.string.cancel_adding_toilet_dialog_message))
            .setNeutralButton(resources.getString(R.string.cancel_adding_toilet_dialog_stay_button)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.cancel_adding_toilet_dialog_yes_button)) { _, _ ->
                addToiletViewModel.clear()
                activity?.onBackPressed()
            }
            .show()
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map

        if (!locationPermission())
            return
        activityFragmentCommunication!!.checkLocationSettingStatus()

        setMapSettings()

        if (addToiletViewModel.location == null) {
            zoomOnMyLocation()
        }

        // everytime the device moves redraw the green circle
        locationVM.getLocation().observe(viewLifecycleOwner) {
            map.clear()
            drawGreenCircle(LatLng(it.latitude, it.longitude))
        }

        // update address with every move and check if point is in circle
        map.setOnCameraIdleListener {
            printAddress()
            addToiletViewModel.addressDataChanged(
                binding.addressTextField.text.toString(),
                map.cameraPosition.target,
                greenCircle
            )
            addToiletViewModel.location = map.cameraPosition.target
        }

        map.setOnCameraMoveStartedListener()
        {
            binding.addressTextField.setText(getString(R.string.loading))
        }

        binding.addressTextField.doAfterTextChanged {
            addToiletViewModel.addressDataChanged(
                binding.addressTextField.text.toString(),
                map.cameraPosition.target,
                greenCircle
            )
            addToiletViewModel.address = binding.addressTextField.text.toString()
        }

        binding.continueButton.setOnClickListener {
            findNavController().navigate(R.id.action_addToiletMapFragment_to_addToiletDetailsFragment)
        }
    }

    private fun printAddress() {
        lifecycleScope.launch(Dispatchers.Main) {
            loadAddress()
            binding.addressTextField.setText(addToiletViewModel.address)
        }
    }

    private suspend fun loadAddress() {
        val coordinates = map.cameraPosition.target
        var fullAddress: String
        withContext(Dispatchers.IO) {
            fullAddress = try {
                LocationUtil.getFullAddressFromCoordinates(
                    coordinates,
                    Geocoder(context)
                )
            } catch (e: Exception) {
                getString(R.string.address_not_found)
            }
        }
        addToiletViewModel.address = fullAddress
    }


    private fun setMapSettings() {
        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
        map.uiSettings.isZoomControlsEnabled = true
    }

    private fun drawGreenCircle(coordinates: LatLng) {
        greenCircle = map.addCircle(
            CircleOptions()
                .center(LatLng(coordinates.latitude, coordinates.longitude))
                .radius(CIRCLE_RADIUS)
                .strokeWidth(5f)
                .strokeColor(Color.parseColor(STROKE_COLOR))
                .fillColor(Color.parseColor(FILL_COLOR))
        )
    }

    private fun locationPermission(): Boolean {
        if (!activityFragmentCommunication!!.getLocationPermissionStatus()) {
            activityFragmentCommunication!!.openPermissionsActivity()
            return false
        }
        return true
    }

    private fun zoomOnMyLocation() {
        val location = LatLng(
            locationVM.getLocation().value?.latitude ?: 0.0,
            locationVM.getLocation().value?.longitude ?: 0.0
        )
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ActivityFragmentCommunication) {
            activityFragmentCommunication = context
        }
    }

    companion object {
        const val STROKE_COLOR = "#014421"
        const val FILL_COLOR = "#809DCDA0"
        const val CIRCLE_RADIUS = 4000.0
    }
}