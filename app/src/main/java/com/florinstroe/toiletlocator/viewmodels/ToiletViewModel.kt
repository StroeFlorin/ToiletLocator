package com.florinstroe.toiletlocator.viewmodels

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.geofire.GeoLocation
import com.florinstroe.toiletlocator.data.LocationTypeRepository
import com.florinstroe.toiletlocator.data.ReviewRepository
import com.florinstroe.toiletlocator.data.ToiletRepository
import com.florinstroe.toiletlocator.data.models.LocationType
import com.florinstroe.toiletlocator.data.models.Review
import com.florinstroe.toiletlocator.data.models.Toilet
import com.florinstroe.toiletlocator.utilities.LocationUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ToiletViewModel : ViewModel() {
    private val locationTypeRepository = LocationTypeRepository()
    private val toiletRepository = ToiletRepository()
    private val reviewRepository = ReviewRepository()

    private var locationTypes: List<LocationType>? = null

    private val _toiletList: MutableLiveData<ArrayList<Toilet>> = MutableLiveData()
    val toiletList: LiveData<ArrayList<Toilet>> = _toiletList

    var selectedToilet: Toilet? = null
    private val _selectedToiletReviews: MutableLiveData<List<Review>> = MutableLiveData()
    val selectedToiletReviews: LiveData<List<Review>> = _selectedToiletReviews

    suspend fun getToilets(location: GeoLocation, radius: Double) {
        if (locationTypes.isNullOrEmpty()) {
            withContext(Dispatchers.IO) {
                locationTypes = locationTypeRepository.getLocationTypesAsList()
            }
        }

        var listOfToilets: ArrayList<Toilet>
        withContext(Dispatchers.IO) {
            listOfToilets = toiletRepository.getToilets(location, radius)

            for(toilet in listOfToilets) {
                val locationType = locationTypes!!.find {
                    it.id == toilet.locationTypeId
                }
                toilet.locationType = locationType
            }
        }
        _toiletList.value = listOfToilets
    }

    fun sortToiletsByDistance(currentLocation: Location) {
        toiletList.value!!.sortBy {
            LocationUtil.getDistanceBetween2LocationsAsString(
                currentLocation,
                Location("end point").apply {
                    latitude = it.coordinates!!.latitude
                    longitude = it.coordinates!!.longitude
                })
        }
    }

    suspend fun getSelectedToiletReviews() {
        val reviews: List<Review>?

        withContext(Dispatchers.IO) {
            reviews = reviewRepository.getReviews(selectedToilet!!.id!!)
        }

        _selectedToiletReviews.value = reviews
    }
}