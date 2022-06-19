package com.florinstroe.toiletlocator.data.models

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.GeoPoint

data class Toilet(
    var coordinates: GeoPoint? = null,
    var address: String? = null,
    var description: String? = null,
    var isFree: Boolean? = true,
    var isAccessible: Boolean? = null,
    var locationTypeId: String? = null,
    var uid: String? = null,
) {

    @Exclude
    @JvmField
    var id: String? = null
}
