package com.florinstroe.toiletlocator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.florinstroe.toiletlocator.databinding.FragmentWelcomeBinding
import com.google.firebase.auth.FirebaseAuth

class WelcomeFragment : Fragment() {
    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // if user is already logged in, navigate to main fragment
        if (FirebaseAuth.getInstance().currentUser != null) {
            findNavController().navigate(R.id.action_welcomeFragment_to_mainFragment)
        }

        binding.loginWithGoogleButton.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_googleAuthFragment)
        }

        binding.loginAsGuestButton.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_mainFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}