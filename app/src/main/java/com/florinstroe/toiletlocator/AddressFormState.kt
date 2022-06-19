package com.florinstroe.toiletlocator

data class AddressFormState(
    val AddressError: Int? = null,
    val CircleError: Int? = null,
    val isDataValid: Boolean = false
)