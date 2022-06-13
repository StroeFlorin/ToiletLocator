package com.florinstroe.toiletlocator

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient

class LocationViewModel : ViewModel() {
    private lateinit var locationRepository: LocationRepository

    fun setFusedLocationProviderClient(locationProvider: FusedLocationProviderClient) {
        locationRepository = LocationRepository(locationProvider)
    }

    fun getLocation(): MutableLiveData<Location> {
        return locationRepository.getLocation()
    }

    fun askForLastDeviceLocation() {
        locationRepository.lastDeviceLocation()
    }
}