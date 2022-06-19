package com.florinstroe.toiletlocator.data

import android.util.Log
import com.florinstroe.toiletlocator.data.models.Toilet
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ToiletRepository {
    private val db = Firebase.firestore

    fun addToilet(toilet: Toilet) {
        db.collection("toilets")
            .add(toilet)
            .addOnSuccessListener { documentReference ->
                Log.d("AddToilet", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("AddToilet", "Error adding document", e)
            }
    }
}