package com.florinstroe.toiletlocator.data.models

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.GeoPoint

data class Toilet(
    var coordinates: GeoPoint? = null,
    var address: String? = null,
    var description: String = "",
    var isFree: Boolean = false,
    var isAccessible: Boolean = false,
    var locationTypeId: String? = null,
    var uid: String? = null,
) {
    var hash: String? = null

    @Exclude
    @JvmField
    var id: String? = null

    @Exclude
    @JvmField
    var locationType: LocationType? = null

    fun setCoordinatesFromLatLng(coordinates: LatLng) {
        this.coordinates = GeoPoint(coordinates.latitude, coordinates.longitude)
    }

    fun printCoordinates(): String {
        return "(${coordinates?.latitude} , ${coordinates?.longitude})"
    }
}
