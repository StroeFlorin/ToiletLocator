package com.florinstroe.toiletlocator.fragments

import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.florinstroe.toiletlocator.R
import com.florinstroe.toiletlocator.adapters.ToiletAdapter
import com.florinstroe.toiletlocator.databinding.FragmentListBinding
import com.florinstroe.toiletlocator.utilities.LocationUtil
import com.florinstroe.toiletlocator.viewmodels.LocationViewModel
import com.florinstroe.toiletlocator.viewmodels.ToiletViewModel

class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val locationVM: LocationViewModel by activityViewModels()
    private val toiletViewModel: ToiletViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        toiletViewModel.toiletList.value!!.sortBy {
            LocationUtil.getDistanceBetween2LocationsAsString(
                locationVM.getLocation().value!!,
                Location("end point").apply {
                    latitude = it.coordinates!!.latitude
                    longitude = it.coordinates!!.longitude
                })
        }

        val adapter =
            ToiletAdapter(toiletViewModel.toiletList.value!!, locationVM.getLocation().value!!) {
                toiletViewModel.selectedToilet = it
                requireActivity().findNavController(R.id.nav_host_fragment)
                    .navigate(R.id.action_mainFragment_to_viewToiletFragment)
            }
        binding.toiletsRecyclerView.layoutManager = LinearLayoutManager(context!!)
        binding.toiletsRecyclerView.adapter = adapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}