package com.florinstroe.toiletlocator.fragments

import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.firebase.geofire.GeoLocation
import com.florinstroe.toiletlocator.R
import com.florinstroe.toiletlocator.databinding.FragmentUrgentBinding
import com.florinstroe.toiletlocator.utilities.LocationUtil
import com.florinstroe.toiletlocator.viewmodels.LocationViewModel
import com.florinstroe.toiletlocator.viewmodels.ToiletViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UrgentFragment : Fragment() {
    private var _binding: FragmentUrgentBinding? = null
    private val binding get() = _binding!!

    private val locationVM: LocationViewModel by activityViewModels()
    private val toiletViewModel: ToiletViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUrgentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch(Dispatchers.Main) {
            binding.urgentProgressBar.visibility = View.VISIBLE

            val location = locationVM.getLocation().value
            toiletViewModel.getToilets(GeoLocation(location!!.latitude, location.longitude), RADIUS)

            if (toiletViewModel.toiletList.value.isNullOrEmpty()) {
                binding.notToiletFoundLinearLayout.visibility = View.VISIBLE
                binding.toiletFoundLinearLayout.visibility = View.GONE
            } else {
                binding.notToiletFoundLinearLayout.visibility = View.GONE
                binding.toiletFoundLinearLayout.visibility = View.VISIBLE

                toiletViewModel.sortToiletsByDistance(locationVM.getLocation().value!!)
                toiletViewModel.selectedToilet = toiletViewModel.toiletList.value!![0]

                printDistance()
                binding.detailsButton.setOnClickListener {
                    requireActivity().findNavController(R.id.nav_host_fragment)
                        .navigate(R.id.action_mainFragment_to_viewToiletFragment)
                }
            }

            binding.urgentProgressBar.visibility = View.GONE
        }
    }

    private fun printDistance() {
        val startPoint = Location("Start Point").apply {
            latitude = toiletViewModel.selectedToilet!!.coordinates!!.latitude
            longitude = toiletViewModel.selectedToilet!!.coordinates!!.longitude
        }
        binding.distanceTextView.text =
            LocationUtil.getDistanceBetween2LocationsAsString(startPoint, locationVM.getLocation().value!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val RADIUS = 10000.0
    }
}