package com.florinstroe.toiletlocator.viewmodels

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.florinstroe.toiletlocator.AddressFormState
import com.florinstroe.toiletlocator.R
import com.florinstroe.toiletlocator.data.AddToiletRepository
import com.florinstroe.toiletlocator.data.models.LocationType
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddToiletViewModel : ViewModel() {
    private val repository = AddToiletRepository()

    private val _locationTypesList: MutableLiveData<ArrayList<LocationType>> = MutableLiveData()
    val locationTypesList: LiveData<ArrayList<LocationType>> = _locationTypesList

    private val _addressForm = MutableLiveData<AddressFormState>()
    val addressFormState: LiveData<AddressFormState> = _addressForm

    private var _location: LatLng? = null
    var location: LatLng? = _location

    private var _address: String? = null
    var address: String? = _address


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
                repository.getLocationTypesAsList()
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

    fun clear() {
        location = null
        address = null
    }
}