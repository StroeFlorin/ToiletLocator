package com.florinstroe.toiletlocator

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.florinstroe.toiletlocator.databinding.FragmentMainBinding
import com.google.firebase.auth.FirebaseAuth


class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val model: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var loggedInUser = if (FirebaseAuth.getInstance().currentUser != null) {
            FirebaseAuth.getInstance().currentUser?.email
        } else {
            "Guest"
        }

        binding.textViewWelcomeMessage.text = "Welcome $loggedInUser!"

        binding.buttonLogout.setOnClickListener {
            if (FirebaseAuth.getInstance().currentUser != null) {
                AuthUI.getInstance()
                    .signOut(context!!)
                    .addOnCompleteListener {
                        Toast.makeText(context, "Signed out successfully!", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_mainFragment_to_welcomeFragment)
                    }
            } else {
                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
                with(sharedPref!!.edit()) {
                    putBoolean("logged_as_guest", false)
                    apply()
                }
                findNavController().navigate(R.id.action_mainFragment_to_welcomeFragment)
            }

        }

        model.message.observe(viewLifecycleOwner) {
            binding.textViewWelcomeMessage.text = it;
        }


        val navController =
            childFragmentManager.findFragmentById(R.id.nav_host_fragment1)?.findNavController()
        //val navController = activity?.findNavController(R.id.nav_host_fragment1)
        setupWithNavController(binding.bnv, navController!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}