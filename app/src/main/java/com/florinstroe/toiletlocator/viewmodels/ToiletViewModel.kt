package com.florinstroe.toiletlocator.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.geofire.GeoLocation
import com.florinstroe.toiletlocator.data.LocationTypeRepository
import com.florinstroe.toiletlocator.data.ReviewRepository
import com.florinstroe.toiletlocator.data.ToiletRepository
import com.florinstroe.toiletlocator.data.models.Review
import com.florinstroe.toiletlocator.data.models.Toilet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ToiletViewModel : ViewModel() {
    private val locationTypeRepository = LocationTypeRepository()
    private val toiletRepository = ToiletRepository()
    private val reviewRepository = ReviewRepository()

    private val _toiletList: MutableLiveData<ArrayList<Toilet>> = MutableLiveData()
    val toiletList: LiveData<ArrayList<Toilet>> = _toiletList

    var selectedToilet: Toilet? = null

    private val _selectedToiletReviews: MutableLiveData<List<Review>> = MutableLiveData()
    val selectedToiletReviews: LiveData<List<Review>> = _selectedToiletReviews

    suspend fun getToilets(location: GeoLocation, radius: Double) {
        var listOfToilets: ArrayList<Toilet>
        withContext(Dispatchers.IO) {
            listOfToilets = toiletRepository.getToilets(location, radius)

            for (toilet in listOfToilets) {
                toilet.locationType =
                    locationTypeRepository.getLocationTypeFromId(toilet.locationTypeId)
            }
        }
        _toiletList.value = listOfToilets
    }

    suspend fun getSelectedToiletReviews() {
        val reviews: List<Review>?

        withContext(Dispatchers.IO) {
            reviews = reviewRepository.getReviews(selectedToilet!!.id!!)
        }

        _selectedToiletReviews.value = reviews
    }
}