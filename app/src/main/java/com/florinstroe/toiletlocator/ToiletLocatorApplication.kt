package com.florinstroe.toiletlocator

import android.app.Application
import com.google.android.material.color.DynamicColors

class ToiletLocatorApplication: Application() {
    override fun onCreate() {
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}