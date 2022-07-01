package com.florinstroe.toiletlocator.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.florinstroe.toiletlocator.R
import com.florinstroe.toiletlocator.data.models.Review
import com.florinstroe.toiletlocator.databinding.ItemReviewBinding
import com.florinstroe.toiletlocator.utilities.TimeUtil

class ReviewAdapter(
    private val reviews: List<Review>,
) :
    RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        val rating = binding.ratingBar
        val message = binding.messageTextView
        val date = binding.dateTextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewAdapter.ViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.rating.rating = reviews[position].stars!!.toFloat()

        if (!reviews[position].message.isNullOrBlank()) {
            holder.message.text = reviews[position].message
        } else {
            holder.message.text = holder.message.context.getString(R.string.no_message)
        }

        try {
            holder.date.text = TimeUtil.getTimeAsString(reviews[position].dateTime!!)
        } catch(exception: Exception) {
            holder.date.text = holder.date.context.getString(R.string.date_not_available)
        }
    }

    override fun getItemCount() = reviews.size
}