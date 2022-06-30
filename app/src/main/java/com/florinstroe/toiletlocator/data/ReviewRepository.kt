package com.florinstroe.toiletlocator.data

import com.florinstroe.toiletlocator.data.models.Review
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ReviewRepository {
    private val db = Firebase.firestore

    fun saveReview(review: Review) {
        db.collection("review")
            .add(review)
    }

    suspend fun getReviews(toiletId: String)  = suspendCoroutine {
        db.collection("review")
            .orderBy("dateTime", Query.Direction.DESCENDING)
            .whereEqualTo("toiletId", toiletId)
            .get()
            .addOnSuccessListener { result ->
                val reviews = result.toObjects<Review>()
                it.resume(reviews)
            }
            .addOnFailureListener { _ ->
                it.resume(null)
            }
    }
}