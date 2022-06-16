package com.florinstroe.toiletlocator.utilities


import android.location.Geocoder
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import java.text.DecimalFormat

object LocationUtil {
    fun getFullAddressFromCoordinates(coordinates: LatLng, geocoder: Geocoder): String {
        return geocoder.getFromLocation(
            coordinates.latitude,
            coordinates.longitude,
            1
        )[0].getAddressLine(0)
    }

    fun getDistanceBetween2LocationsAsString(location1: Location, location2: Location): String {
        val distanceInMeters = location1.distanceTo(location2)
        val distanceInKm = distanceInMeters / 1000
        val df = DecimalFormat("#.##")
        return df.format(distanceInKm) + " km"
    }
}