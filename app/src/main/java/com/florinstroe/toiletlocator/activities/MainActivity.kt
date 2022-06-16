package com.florinstroe.toiletlocator.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.florinstroe.toiletlocator.ActivityFragmentCommunication
import com.florinstroe.toiletlocator.databinding.ActivityMainBinding
import com.florinstroe.toiletlocator.viewmodels.LocationViewModel
import com.google.android.gms.location.*


class MainActivity : AppCompatActivity(), ActivityFragmentCommunication {
    private lateinit var binding: ActivityMainBinding
    private val locationVM: LocationViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationVM.setLocation(locationResult.lastLocation)
            }
        }
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getLocationPermissionStatus(): Boolean {
        return (ContextCompat.checkSelfPermission(
            this.applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
                == PackageManager.PERMISSION_GRANTED)
    }

    override fun onResume() {
        if (getLocationPermissionStatus()) {
            startLocationUpdates()
        } else {
            openPermissionsActivity()
        }
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun openPermissionsActivity() {
        val intent = Intent(this, PermissionsActivity::class.java)
        startActivity(intent)
    }
}