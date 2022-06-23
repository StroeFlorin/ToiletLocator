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
import com.florinstroe.toiletlocator.databinding.FragmentAddEditToiletDetailsBinding
import com.florinstroe.toiletlocator.viewmodels.AddEditToiletViewModel

class AddEditToiletDetailsFragment : Fragment() {
    private var _binding: FragmentAddEditToiletDetailsBinding? = null
    private val binding get() = _binding!!
    private val addEditToiletViewModel: AddEditToiletViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditToiletDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(addEditToiletViewModel.locationTypesList.value.isNullOrEmpty()) {
            addEditToiletViewModel.loadLocationTypes()
        }

        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        if (addEditToiletViewModel.toilet.description != "") {
            binding.descriptionTextField.setText(addEditToiletViewModel.toilet.description)
        }
        binding.descriptionTextField.doOnTextChanged { text, _, _, _ ->
            addEditToiletViewModel.toilet.description = text.toString()
        }

        binding.isFreeSwitch.isChecked = addEditToiletViewModel.toilet.isFree
        binding.isFreeSwitch.setOnClickListener {
            addEditToiletViewModel.toilet.isFree = binding.isFreeSwitch.isChecked
        }

        binding.accessibleToiletSwitch.isChecked = addEditToiletViewModel.toilet.isAccessible
        binding.accessibleToiletSwitch.setOnClickListener {
            addEditToiletViewModel.toilet.isAccessible = binding.accessibleToiletSwitch.isChecked
        }

        addEditToiletViewModel.locationTypesList.observe(viewLifecycleOwner) {
            val items = it

            if (addEditToiletViewModel.toilet.locationTypeId != null) {
                binding.locationTypeMenu.setText(items.first { value -> value.id == addEditToiletViewModel.toilet.locationTypeId }.name)
                binding.submitButton.isEnabled = true
            }

            val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
            (binding.locationTypeMenu as? AutoCompleteTextView)?.setAdapter(adapter)

            binding.locationTypeMenu.setOnItemClickListener { parent, _, position, _ ->
                val item = parent.getItemAtPosition(position) as LocationType
                addEditToiletViewModel.toilet.locationTypeId = item.id
                binding.submitButton.isEnabled = true
            }
        }

        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        if (addEditToiletViewModel.toilet.locationTypeId == null) {
            binding.submitButton.isEnabled = false
        }
        binding.submitButton.setOnClickListener {
            addEditToiletViewModel.saveToilet()
            findNavController().navigate(R.id.action_addToiletDetailsFragment_to_addToiletSuccessFragment)
        }
    }
}
