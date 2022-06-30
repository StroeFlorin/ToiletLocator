package com.florinstroe.toiletlocator.viewmodels

import androidx.lifecycle.ViewModel
import com.florinstroe.toiletlocator.data.ReviewRepository
import com.florinstroe.toiletlocator.data.UserRepository
import com.florinstroe.toiletlocator.data.models.Review
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReviewViewModel : ViewModel() {
    private val reviewRepository = ReviewRepository()
    private val userRepository = UserRepository()

    lateinit var review: Review

    suspend fun addReview() {
        withContext(Dispatchers.IO) {
            review.uid = userRepository.getCurrentUserId()
            reviewRepository.saveReview(review)
        }
    }
}