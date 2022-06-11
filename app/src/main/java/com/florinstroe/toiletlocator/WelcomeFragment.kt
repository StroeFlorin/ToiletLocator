package com.florinstroe.toiletlocator

import android.content.Context
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

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val isLoggedAsGuest = sharedPref.getBoolean("logged_as_guest", false)

        // if user is already logged in, navigate to main fragment as soon as the view is created
        if (FirebaseAuth.getInstance().currentUser != null || isLoggedAsGuest) {
            findNavController().navigate(R.id.action_welcomeFragment_to_mainFragment)
        }

        binding.loginWithGoogleButton.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_googleAuthFragment)
        }

        binding.loginAsGuestButton.setOnClickListener {
            loginAsGuest()
        }
    }

    private fun loginAsGuest() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putBoolean("logged_as_guest", true)
            apply()
        }

        findNavController().navigate(R.id.action_welcomeFragment_to_mainFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}