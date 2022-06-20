package com.florinstroe.toiletlocator.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.geofire.GeoLocation
import com.florinstroe.toiletlocator.data.LocationTypeRepository
import com.florinstroe.toiletlocator.data.ToiletRepository
import com.florinstroe.toiletlocator.data.models.LocationType
import com.florinstroe.toiletlocator.data.models.Toilet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ToiletViewModel : ViewModel() {
    private val locationTypeRepository = LocationTypeRepository()
    private val toiletRepository = ToiletRepository()

    private val _toiletList: MutableLiveData<ArrayList<Toilet>> = MutableLiveData()
    val toiletList: LiveData<ArrayList<Toilet>> = _toiletList

    suspend fun getToilets(location: GeoLocation, radius: Double) {
        var listOfToilets: ArrayList<Toilet>
        withContext(Dispatchers.IO) {
            listOfToilets = toiletRepository.getToilets(location, radius)
        }
        _toiletList.value = listOfToilets
    }

    suspend fun setToiletLocationType(toilet: Toilet) {
        var locationType: LocationType?
        withContext(Dispatchers.IO) {
            locationType = locationTypeRepository.getLocationTypeFromId(toilet.locationTypeId)
        }
        toilet.locationType = locationType
    }
}