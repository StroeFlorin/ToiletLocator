package com.florinstroe.toiletlocator.viewmodels

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.florinstroe.toiletlocator.AddressFormState
import com.florinstroe.toiletlocator.R
import com.florinstroe.toiletlocator.data.LocationTypeRepository
import com.florinstroe.toiletlocator.data.ToiletRepository
import com.florinstroe.toiletlocator.data.models.LocationType
import com.florinstroe.toiletlocator.data.models.Toilet
import com.florinstroe.toiletlocator.data.UserRepository
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*

class AddEditToiletViewModel : ViewModel() {
    private val locationTypeRepository = LocationTypeRepository()
    private val userRepository = UserRepository()
    private val toiletRepository = ToiletRepository()

    private val _locationTypesList: MutableLiveData<ArrayList<LocationType>> = MutableLiveData()
    val locationTypesList: LiveData<ArrayList<LocationType>> = _locationTypesList

    private val _addressForm = MutableLiveData<AddressFormState>()
    val addressFormState: LiveData<AddressFormState> = _addressForm

    var toilet = Toilet()

    fun saveToilet() {
        toilet.uid = userRepository.getCurrentUserId()
        if (toilet.id == null) {
            viewModelScope.launch(Dispatchers.IO) {
                toiletRepository.addToilet(toilet)
                clearData()
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                toiletRepository.updateToilet(toilet)
                clearData()
            }
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

    fun loadLocationTypes() {
        viewModelScope.launch(Dispatchers.Main) {
            _locationTypesList.value = withContext(Dispatchers.IO) {
                locationTypeRepository.getLocationTypesAsList()
            }
        }
    }

    private fun isLocationInCircle(location: LatLng, greenCircle: Circle): Boolean {
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
    }

    fun clearData() {
        toilet = Toilet()
    }
}