package com.florinstroe.toiletlocator.utilities

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat

object TimeUtil {
    fun getTimeAsString(timestamp: Timestamp): String {
        val pattern = "dd-MM-yyyy HH:mm"
        val simpleDateFormat = SimpleDateFormat(pattern)
        return simpleDateFormat.format(timestamp.toDate())
    }
}