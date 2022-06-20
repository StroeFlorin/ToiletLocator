package com.florinstroe.toiletlocator.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
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

        if(addToiletViewModel.locationTypesList.value.isNullOrEmpty()) {
            addToiletViewModel.loadLocationTypes()
        }

        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        if (addToiletViewModel.toilet.description != "") {
            binding.descriptionTextField.setText(addToiletViewModel.toilet.description)
        }
        binding.descriptionTextField.doOnTextChanged { text, _, _, _ ->
            addToiletViewModel.toilet.description = text.toString()
        }

        binding.isFreeSwitch.isChecked = addToiletViewModel.toilet.isFree
        binding.isFreeSwitch.setOnClickListener {
            addToiletViewModel.toilet.isFree = binding.isFreeSwitch.isChecked
        }

        binding.accessibleToiletSwitch.isChecked = addToiletViewModel.toilet.isAccessible
        binding.accessibleToiletSwitch.setOnClickListener {
            addToiletViewModel.toilet.isAccessible = binding.accessibleToiletSwitch.isChecked
        }

        addToiletViewModel.locationTypesList.observe(viewLifecycleOwner) {
            val items = it

            if (addToiletViewModel.toilet.locationTypeId != null) {
                binding.locationTypeMenu.setText(items.first { value -> value.id == addToiletViewModel.toilet.locationTypeId }.name)
                binding.submitButton.isEnabled = true
            }

            val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
            (binding.locationTypeMenu as? AutoCompleteTextView)?.setAdapter(adapter)

            binding.locationTypeMenu.setOnItemClickListener { parent, _, position, _ ->
                val item = parent.getItemAtPosition(position) as LocationType
                addToiletViewModel.toilet.locationTypeId = item.id
                binding.submitButton.isEnabled = true
            }
        }

        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        if (addToiletViewModel.toilet.locationTypeId == null) {
            binding.submitButton.isEnabled = false
        }
        binding.submitButton.setOnClickListener {
            addToiletViewModel.saveToilet()
            findNavController().navigate(R.id.action_addToiletDetailsFragment_to_addToiletSuccessFragment)
        }
    }
}
