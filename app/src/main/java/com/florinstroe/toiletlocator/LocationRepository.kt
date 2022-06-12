package com.florinstroe.toiletlocator

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient


class LocationRepository(var locationProvider: FusedLocationProviderClient) {
    private var location: Location? = null

    fun getLocation(): Location? {
        return location
    }

     fun getDeviceLocation() {
        val locationResult = locationProvider.lastLocation
        locationResult.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                location = task.result
            }
        }
    }
}