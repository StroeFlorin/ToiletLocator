package com.florinstroe.toiletlocator.data

import com.florinstroe.toiletlocator.data.models.LocationType
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationTypeRepository {
    private val db = Firebase.firestore

    suspend fun getLocationTypesAsList() = suspendCoroutine {
        db.collection("locationType").orderBy("order").get()
            .addOnSuccessListener { result ->
                val locationTypesList = ArrayList<LocationType>()
                for (document in result) {
                    val locationType = document.toObject<LocationType>()
                    locationType.id = document.id
                    locationTypesList.add(locationType)
                }
                it.resume(locationTypesList)
            }
            .addOnFailureListener { _ ->
                it.resume(ArrayList<LocationType>())
            }
    }
}