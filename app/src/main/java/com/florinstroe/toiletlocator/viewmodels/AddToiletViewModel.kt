package com.florinstroe.toiletlocator.viewmodels

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.florinstroe.toiletlocator.AddressFormState
import com.florinstroe.toiletlocator.DetailsFormState
import com.florinstroe.toiletlocator.R
import com.florinstroe.toiletlocator.data.LocationTypeRepository
import com.florinstroe.toiletlocator.data.ToiletRepository
import com.florinstroe.toiletlocator.data.models.LocationType
import com.florinstroe.toiletlocator.data.models.Toilet
import com.florinstroe.toiletlocator.data.UserRepository
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*

class AddToiletViewModel : ViewModel() {
    private val locationTypeRepository = LocationTypeRepository()
    private val userRepository = UserRepository()
    private val toiletRepository = ToiletRepository()

    private val _locationTypesList: MutableLiveData<ArrayList<LocationType>> = MutableLiveData()
    val locationTypesList: LiveData<ArrayList<LocationType>> = _locationTypesList

    private val _addressForm = MutableLiveData<AddressFormState>()
    val addressFormState: LiveData<AddressFormState> = _addressForm

    private val _detailsForm = MutableLiveData<DetailsFormState>()
    val detailsFormState: LiveData<DetailsFormState> = _detailsForm

    var toilet: Toilet = Toilet()

    fun saveToilet() {
        if (toilet.uid == null) {
            toilet.uid = userRepository.getCurrentUserId()
            Log.d("AddToiletViewModel", "saveToilet: ${toilet}")
            viewModelScope.launch(Dispatchers.IO) {
                toiletRepository.addToilet(toilet)
                clearData()
            }
        } else {
           // toilet.uid = userRepository.getCurrentUserId()
            TODO("Not implemented")
        }
    }

    fun addressDataChanged(address: String, coordinates: LatLng, greenCircle: Circle) {
        if (address.isEmpty()) {
            _addressForm.value = AddressFormState(AddressError = R.string.address_is_empty)
        } else if (!isLocationInCircle(coordinates, greenCircle)) {
            _addressForm.value = AddressFormState(CircleError = R.string.not_in_circle)
        } else {
            _addressForm.value = AddressFormState(isDataValid = true)
        }
    }

    fun detailsDataChanged(locationType: LocationType) {
        if (locationType == null) {
            _detailsForm.value =
                DetailsFormState(LocationTypeError = R.string.location_type_is_empty)
        } else {
            _detailsForm.value = DetailsFormState(isDataValid = true)
        }
    }

    fun loadLocationTypes() {
        viewModelScope.launch(Dispatchers.Main) {
            _locationTypesList.value = withContext(Dispatchers.IO) {
                locationTypeRepository.getLocationTypesAsList()
            }
        }
    }

    private fun isLocationInCircle(location: LatLng, greenCircle: Circle): Boolean {
        try {
            val distance = FloatArray(2)

            Location.distanceBetween(
                location.latitude,
                location.longitude,
                greenCircle.center.latitude,
                greenCircle.center.longitude,
                distance
            )

            if (distance[0] > greenCircle.radius) {
                return false
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun clearData() {
        toilet = Toilet()
    }
}