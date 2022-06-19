package com.florinstroe.toiletlocator.data

import com.google.firebase.auth.FirebaseAuth

class UserRepository {
    fun getCurrentUserId() = FirebaseAuth.getInstance().currentUser!!.uid
}