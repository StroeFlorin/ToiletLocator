package com.florinstroe.toiletlocator.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.florinstroe.toiletlocator.databinding.FragmentAddToiletSuccessBinding

class AddToiletSuccessFragment : Fragment() {
    private var _binding: FragmentAddToiletSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddToiletSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.returnToHomePageButton.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}