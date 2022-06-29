package com.florinstroe.toiletlocator.data.models

import com.google.firebase.firestore.Exclude

data class LocationType(
    var icon: String? = null,
    var name: String? = null
) {
    @Exclude
    @JvmField
    var id: String ?= null

    override fun toString(): String {
        return name.toString()
    }
}



