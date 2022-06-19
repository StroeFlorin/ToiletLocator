package com.florinstroe.toiletlocator

interface ActivityFragmentCommunication {
    fun openPermissionsActivity()
    fun checkLocationSettingStatus()
    fun getLocationPermissionStatus(): Boolean
}