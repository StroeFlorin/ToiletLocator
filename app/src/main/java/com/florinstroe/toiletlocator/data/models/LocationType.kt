package com.florinstroe.toiletlocator.data.models

data class LocationType(
    var icon: String? = null,
    var name: String? = null
) {
    var id: String ?= null

    override fun toString(): String {
        return name.toString()
    }
}
