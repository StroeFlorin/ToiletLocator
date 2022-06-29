package com.florinstroe.toiletlocator.adapters

import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.florinstroe.toiletlocator.data.models.Toilet
import com.florinstroe.toiletlocator.databinding.ItemToiletBinding
import com.florinstroe.toiletlocator.utilities.LocationUtil

class ToiletAdapter(
    private val toilets: ArrayList<Toilet>,
    private val currentLocation: Location,
    private val onToiletClicked: (toilet: Toilet) -> Unit
) :
    RecyclerView.Adapter<ToiletAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemToiletBinding) : RecyclerView.ViewHolder(binding.root) {
        val address = binding.addressTextView
        val distance = binding.distanceTextView
        val locationTypeChip = binding.locationTypeChip
        val freeChip = binding.freeChip
        val accessibleChip = binding.accessibleChip
        private val frame = binding.toiletDetailsCard

        init {
            frame.setOnClickListener {
                onToiletClicked(toilets[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToiletAdapter.ViewHolder {
        val binding = ItemToiletBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.address.text = toilets[position].address

        val startPoint = Location("Start Point").apply {
            latitude = toilets[position].coordinates!!.latitude
            longitude = toilets[position].coordinates!!.longitude
        }
        holder.distance.text =
            LocationUtil.getDistanceBetween2LocationsAsString(startPoint, currentLocation)

        holder.accessibleChip.visibility =
            if (toilets[position].isAccessible) View.VISIBLE else View.GONE

        holder.locationTypeChip.text = toilets[position].locationType!!.name

        holder.freeChip.visibility =
            if (toilets[position].isFree) View.VISIBLE else View.GONE

        holder.locationTypeChip.chipIcon = ContextCompat.getDrawable(
            holder.locationTypeChip.context!!,
            holder.locationTypeChip.context.resources.getIdentifier(
                toilets[position].locationType!!.icon,
                "drawable",
                holder.locationTypeChip.context!!.packageName
            )
        )
    }

    override fun getItemCount() = toilets.size
}