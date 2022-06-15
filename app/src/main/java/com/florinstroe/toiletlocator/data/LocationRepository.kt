package com.florinstroe.toiletlocator.data

import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient

class LocationRepository(private val locationProvider: FusedLocationProviderClient) {
     private val deviceLocation: MutableLiveData<Location> by lazy {
         MutableLiveData<Location>().also {
             lastDeviceLocation()
         }
    }

    fun getLocation(): MutableLiveData<Location> {
        return deviceLocation
    }

    fun lastDeviceLocation() {
        val locationResult = locationProvider.lastLocation
        locationResult.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                deviceLocation.value = task.result
            }
        }
    }
}