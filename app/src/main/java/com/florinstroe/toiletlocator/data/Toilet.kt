package com.florinstroe.toiletlocator.data

import android.location.Location

data class Toilet(
    val id: String,
    val description: String,
    val location: Location
)
