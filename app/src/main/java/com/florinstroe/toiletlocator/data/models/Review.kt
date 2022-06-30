package com.florinstroe.toiletlocator.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp

data class Review(
    var stars: Int? = null,
    var message: String? = null,
    var toiletId: String? = null
) {
    var uid: String? = null

    @ServerTimestamp
    var dateTime: Timestamp? = null

    @Exclude
    @JvmField
    var id: String? = null
}







