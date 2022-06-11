package com.florinstroe.toiletlocator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val message = MutableLiveData<String>()

    init {
        message.value = "Mesaj initial"
    }

    fun changeValue(value: String) {
        message.value = value
    }
}