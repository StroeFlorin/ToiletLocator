package com.florinstroe.toiletlocator

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.florinstroe.toiletlocator.databinding.FragmentMapBinding
import com.google.android.gms.location.LocationServices

class MapFragment : Fragment() {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private val model: LocationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val locationProvider = LocationServices.getFusedLocationProviderClient(context!!)
        model.setFusedLocationProviderClient(locationProvider)
        model.getLocation().observe(viewLifecycleOwner) {
            Log.d("GeoLocation", it.latitude.toString() + "," + it.longitude.toString())
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}