package com.florinstroe.toiletlocator.utilities


import android.location.Geocoder
import android.location.Location
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.VisibleRegion
import com.google.firebase.firestore.GeoPoint
import java.text.DecimalFormat
import kotlin.math.pow
import kotlin.math.sqrt


object LocationUtil {
    val defaultLocation = Location("").apply {
        latitude = 90.0
        longitude = 90.0
    }

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

    fun generateGeohash(coordinates: GeoPoint) =
        GeoFireUtils.getGeoHashForLocation(GeoLocation(coordinates.latitude, coordinates.longitude))

    fun getMapVisibleRadius(visibleRegion: VisibleRegion): Double {
        val distanceWidth = FloatArray(1)
        val distanceHeight = FloatArray(1)

        val farRight = visibleRegion.farRight
        val farLeft = visibleRegion.farLeft
        val nearRight = visibleRegion.nearRight
        val nearLeft = visibleRegion.nearLeft

        Location.distanceBetween(
            (farLeft.latitude + nearLeft.latitude) / 2,
            farLeft.longitude,
            (farRight.latitude + nearRight.latitude) / 2,
            farRight.longitude,
            distanceWidth
        )

        Location.distanceBetween(
            farRight.latitude,
            (farRight.longitude + farLeft.longitude) / 2,
            nearRight.latitude,
            (nearRight.longitude + nearLeft.longitude) / 2,
            distanceHeight
        )

        return sqrt(
            distanceWidth[0]
                .toDouble().pow(2.0) + distanceHeight[0].toDouble().pow(2.0)
        ) / 2
    }
}