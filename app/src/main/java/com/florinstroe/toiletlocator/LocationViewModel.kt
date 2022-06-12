package com.florinstroe.toiletlocator

import android.location.Location
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {
    private lateinit var locationRepository: LocationRepository
    private val location: MutableLiveData<Location> by lazy {
        MutableLiveData<Location>().also {
            getDeviceLocation()
        }
    }

    fun getLocation(): LiveData<Location> {
        return location
    }

    fun getDeviceLocation() {
        viewModelScope.launch {
            while(location.value == null) {
                locationRepository.getDeviceLocation()
                location.value = locationRepository.getLocation()
                delay(3000L)
            }
        }
    }

    fun setFusedLocationProviderClient(locationProvider: FusedLocationProviderClient) {
        locationRepository = LocationRepository(locationProvider)
    }


}