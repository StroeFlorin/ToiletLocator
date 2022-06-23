package com.florinstroe.toiletlocator.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.florinstroe.toiletlocator.R
import com.florinstroe.toiletlocator.databinding.FragmentViewToiletBinding
import com.florinstroe.toiletlocator.viewmodels.AddEditToiletViewModel
import com.florinstroe.toiletlocator.viewmodels.ToiletViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class ViewToiletFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentViewToiletBinding? = null
    private val binding get() = _binding!!

    private val toiletViewModel: ToiletViewModel by activityViewModels()
    private val updateToiletViewModel: AddEditToiletViewModel by activityViewModels()

    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewToiletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit -> {
                    updateToiletViewModel.toilet = toiletViewModel.selectedToilet!!.copy()
                    updateToiletViewModel.toilet.id = toiletViewModel.selectedToilet!!.id
                    findNavController().navigate(R.id.action_viewToiletFragment_to_addToiletMapFragment)
                    true
                }
                else -> false
            }
        }
        binding.navigateFab.setOnClickListener {
            val gmmIntentUri = Uri.parse("google.navigation:mode=w&q=${toiletViewModel.selectedToilet!!.coordinates!!.latitude},${toiletViewModel.selectedToilet!!.coordinates!!.longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            context!!.startActivity(mapIntent)
        }
    }


    override fun onMapReady(map: GoogleMap) {
        this.map = map
        val coordinates = LatLng(
            toiletViewModel.selectedToilet!!.coordinates!!.latitude,
            toiletViewModel.selectedToilet!!.coordinates!!.longitude
        )

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f))

        map.addMarker(
            MarkerOptions()
                .position(coordinates)
        )
    }
}