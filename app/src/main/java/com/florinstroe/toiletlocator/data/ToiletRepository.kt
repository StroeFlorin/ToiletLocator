package com.florinstroe.toiletlocator.data

import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.florinstroe.toiletlocator.data.models.Toilet
import com.florinstroe.toiletlocator.utilities.LocationUtil
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class ToiletRepository {
    private val db = Firebase.firestore

    fun addToilet(toilet: Toilet) {
        toilet.hash = LocationUtil.generateGeohash(toilet.coordinates!!)
        db.collection("toilets")
            .add(toilet)
    }

    fun updateToilet(toilet: Toilet) {
        toilet.hash = LocationUtil.generateGeohash(toilet.coordinates!!)
        db.collection("toilets")
            .document(toilet.id!!)
            .set(toilet)
    }

    suspend fun getToilets(center: GeoLocation, radius: Double) =
        suspendCoroutine { cont ->
            val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radius)
            val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
            for (b in bounds) {
                val q: Query = db.collection("toilets")
                    .orderBy("hash")
                    .startAt(b.startHash)
                    .endAt(b.endHash)
                tasks.add(q.get())
            }

            Tasks.whenAllComplete(tasks)
                .addOnCompleteListener {
                    val matchingDocs: MutableList<DocumentSnapshot> = ArrayList()
                    for (task in tasks) {
                        val snap = task.result
                        for (doc in snap.documents) {
                            val lat = doc.getGeoPoint("coordinates")!!.latitude
                            val lng = doc.getGeoPoint("coordinates")!!.longitude
                            val docLocation = GeoLocation(lat, lng)
                            val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)
                            if (distanceInM <= radius) {
                                matchingDocs.add(doc)
                            }
                        }
                    }
                    val listOfToilets = ArrayList<Toilet>()
                    for (doc in matchingDocs) {
                        val toilet = doc.toObject<Toilet>()
                        toilet!!.id = doc.id
                        listOfToilets.add(toilet)
                    }
                    cont.resume(listOfToilets)
                }
        }
}