package com.florinstroe.toiletlocator.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

data class Review(
    var stars: Int? = null,
    var message: String? = null,
    var dateTime: Timestamp? = null,
    var uid: String? = null,
) {
    @Exclude
    @JvmField
    var id: String? = null
}







