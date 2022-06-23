package com.florinstroe.toiletlocator.fragments


import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.florinstroe.toiletlocator.R
import com.florinstroe.toiletlocator.databinding.FragmentMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController =
            childFragmentManager.findFragmentById(R.id.nav_host_fragment_bottom_menu)
                ?.findNavController()
        setupWithNavController(binding.bottomNavigationView, navController!!)

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_settings -> {
                    findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
                    true
                }

                R.id.action_logout -> {
                    logout()
                    true
                }
                else -> {
                    true
                }
            }
        }

        setToolbarTitleAndSubtitle()

        if (FirebaseAuth.getInstance().currentUser != null) {
            binding.addToiletFab.show()
        } else {
            binding.addToiletFab.hide()
        }

        binding.addToiletFab.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_addToiletMapFragment)
        }
    }

    private fun setToolbarTitleAndSubtitle() {
        val loggedInUser = if (FirebaseAuth.getInstance().currentUser != null) {
            FirebaseAuth.getInstance().currentUser?.displayName
        } else {
            getString(R.string.guest)
        }
        binding.toolbar.title = getString(R.string.welcome_message)
        binding.toolbar.subtitle = "${getString(R.string.logged_in_as)} $loggedInUser"
    }

    private fun logout() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            AuthUI.getInstance()
                .signOut(context!!)
                .addOnCompleteListener {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}