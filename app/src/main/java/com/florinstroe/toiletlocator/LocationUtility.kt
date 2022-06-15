package com.florinstroe.toiletlocator

import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng

object LocationUtility {
    fun getFullAddressFromCoordinates(coordinates: LatLng, geocoder: Geocoder): String = geocoder.getFromLocation(
        coordinates.latitude,
        coordinates.longitude,
        1
    )[0].getAddressLine(0)
}