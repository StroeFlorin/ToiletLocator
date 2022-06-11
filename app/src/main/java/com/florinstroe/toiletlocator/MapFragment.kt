package com.florinstroe.toiletlocator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.florinstroe.toiletlocator.databinding.FragmentMainBinding
import com.florinstroe.toiletlocator.databinding.FragmentMapBinding
import com.google.firebase.auth.FirebaseAuth

class MapFragment : Fragment() {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private val model: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.message.observe(viewLifecycleOwner) {
            binding.textViewHarta.text = it;
        }

      binding.button2.setOnClickListener {
          requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.action_mainFragment_to_welcomeFragment)
          model.changeValue("New value");
      }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}