package com.florinstroe.toiletlocator.data.models

import com.google.firebase.Timestamp

data class Review(
    var stars: Int? = null,
    var message: String? = null,
    var dateTime: Timestamp? = null,
    var uid: String? = null,
) {
    var id: String? = null
}