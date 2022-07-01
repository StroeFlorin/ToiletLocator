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
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.geofire.GeoLocation
import com.florinstroe.toiletlocator.R
import com.florinstroe.toiletlocator.adapters.ToiletAdapter
import com.florinstroe.toiletlocator.databinding.FragmentListBinding
import com.florinstroe.toiletlocator.utilities.LocationUtil.defaultLocation
import com.florinstroe.toiletlocator.viewmodels.LocationViewModel
import com.florinstroe.toiletlocator.viewmodels.ToiletViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        lifecycleScope.launch(Dispatchers.Main) {
            binding.listProgressBar.visibility = View.VISIBLE

            val location = locationVM.getLocation().value ?: defaultLocation
            toiletViewModel.getToilets(GeoLocation(location.latitude, location.longitude), RADIUS)
            toiletViewModel.sortToiletsByDistance(location)

            binding.numberOfToiletsTextView.text = "${getString(R.string.number_of_toilets)} ${toiletViewModel.toiletList.value?.size.toString()} ${getString(R.string.toilets_nearby)}!"
            loadRecyclerView(location)

            binding.listProgressBar.visibility = View.GONE
        }
    }

    private fun loadRecyclerView(location: Location) {
        val adapter =
            ToiletAdapter(toiletViewModel.toiletList.value!!, location) {
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

    companion object {
        const val RADIUS = 10000.0
    }
}