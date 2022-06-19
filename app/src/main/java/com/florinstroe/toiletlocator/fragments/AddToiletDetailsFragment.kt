package com.florinstroe.toiletlocator.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.activityViewModels
import com.florinstroe.toiletlocator.R
import com.florinstroe.toiletlocator.data.models.LocationType
import com.florinstroe.toiletlocator.databinding.FragmentAddToiletDetailsBinding
import com.florinstroe.toiletlocator.viewmodels.AddToiletViewModel

class AddToiletDetailsFragment : Fragment() {
    private var _binding: FragmentAddToiletDetailsBinding? = null
    private val binding get() = _binding!!
    private val addToiletViewModel: AddToiletViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddToiletDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        Log.d(
            "AddToiletDetailsFragment",
            "${addToiletViewModel.address.toString()} ${addToiletViewModel.location.toString()}"
        )

        addToiletViewModel.loadLocationTypes()

        addToiletViewModel.locationTypesList.observe(viewLifecycleOwner) {
            Log.d("list", "locationTypesList: $it")

            val items = it
            val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
            (binding.locationTypeMenu as? AutoCompleteTextView)?.setAdapter(adapter)

            binding.locationTypeMenu.setOnItemClickListener { parent, view, position, id ->
                val item = parent.getItemAtPosition(position) as LocationType
                val itemId = item.id
                Log.d("list", "itemId: $itemId")
            }
        }
    }


}
