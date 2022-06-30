package com.florinstroe.toiletlocator.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.florinstroe.toiletlocator.R
import com.florinstroe.toiletlocator.adapters.ReviewAdapter
import com.florinstroe.toiletlocator.databinding.FragmentViewToiletBinding
import com.florinstroe.toiletlocator.viewmodels.AddEditToiletViewModel
import com.florinstroe.toiletlocator.viewmodels.ToiletViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                R.id.review -> {
                    findNavController().navigate(R.id.action_viewToiletFragment_to_reviewFragment)
                    true
                }
                else -> false
            }
        }
        binding.navigateFab.setOnClickListener {
            val latitude = toiletViewModel.selectedToilet!!.coordinates!!.latitude
            val longitude = toiletViewModel.selectedToilet!!.coordinates!!.longitude
            val gmmIntentUri =
                Uri.parse("google.navigation:mode=w&q=$latitude,$longitude")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            context!!.startActivity(mapIntent)
        }

        binding.shareFab.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                val message =
                    "${getString(R.string.share_toilet_message)}: ${toiletViewModel.selectedToilet!!.address} " +
                            "https://maps.google.com/?q=${toiletViewModel.selectedToilet!!.coordinates!!.latitude},${toiletViewModel.selectedToilet!!.coordinates!!.longitude}"
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
        binding.coordinatesTextView.text = toiletViewModel.selectedToilet!!.printCoordinates()
        binding.addressTextView.text = toiletViewModel.selectedToilet!!.address.toString()

        val description = toiletViewModel.selectedToilet!!.description
        if (description.isBlank()) {
            binding.descriptionFrame.visibility = View.GONE
        } else {
            binding.descriptionFrame.visibility = View.VISIBLE
            binding.descriptionTextView.text = description
        }


        binding.freeChip.visibility =
            if (toiletViewModel.selectedToilet!!.isFree) View.VISIBLE else View.GONE
        binding.accessibleChip.visibility =
            if (toiletViewModel.selectedToilet!!.isAccessible) View.VISIBLE else View.GONE
        binding.locationTypeChip.text = toiletViewModel.selectedToilet!!.locationType!!.name
        binding.locationTypeChip.chipIcon = ContextCompat.getDrawable(
            context!!,
            resources.getIdentifier(
                toiletViewModel.selectedToilet!!.locationType!!.icon,
                "drawable",
                context!!.packageName
            )
        )

        lifecycleScope.launch(Dispatchers.Main) {
            binding.reviewsProgressBar.visibility = View.VISIBLE

            toiletViewModel.getSelectedToiletReviews()

            if (toiletViewModel.selectedToiletReviews.value!!.isEmpty()) {
                binding.noReviewsTextView.visibility = View.VISIBLE
            } else {
                binding.noReviewsTextView.visibility = View.GONE

                val adapter = ReviewAdapter(toiletViewModel.selectedToiletReviews.value!!)
                binding.reviewsRecyclerView.layoutManager = LinearLayoutManager(context!!)
                binding.reviewsRecyclerView.adapter = adapter
            }

            binding.reviewsProgressBar.visibility = View.GONE
        }

        toiletViewModel.selectedToiletReviews.observe(viewLifecycleOwner) {
            var sum = 0
            for (review in it) {
                sum += review.stars!!
            }
            binding.ratingBar.rating = sum.toFloat() / it.size.toFloat()
            binding.numberOfReviewsTextView.text = "(${it.size} reviews)"
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