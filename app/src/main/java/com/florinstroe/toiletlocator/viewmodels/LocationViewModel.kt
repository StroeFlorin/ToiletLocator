package com.florinstroe.toiletlocator.viewmodels

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LocationViewModel : ViewModel() {
    private var location = MutableLiveData<Location>()

    fun getLocation(): MutableLiveData<Location> {
        return this.location
    }

    fun setLocation(location: Location) {
        this.location.value = location
    }
}