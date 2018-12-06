package com.softllc.freeze

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.softllc.freeze.data.Photo
import com.softllc.freeze.databinding.ListItemPhotoBinding


class PhotoDiffCallback : DiffUtil.ItemCallback<Photo>() {

    override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem.photoId == newItem.photoId
    }

    override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem == newItem
    }
}
class PhotoAdapter : ListAdapter<Photo, PhotoAdapter.ViewHolder>(PhotoDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo = getItem(position)
        holder.apply {
            bind(createOnClickListener(photo.photoId), photo)
            itemView.tag = photo
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemPhotoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    private fun createOnClickListener(photoId: String): View.OnClickListener {
        return View.OnClickListener {
            val direction = FreezeFragmentDirections.actionFreezeFragmentToPhotoFragment(photoId)
            it.findNavController().navigate(direction)
        }
    }

    class ViewHolder(
        private val binding: ListItemPhotoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(listener: View.OnClickListener, item: Photo) {
            binding.apply {
                clickListener = listener
                photo = item
                executePendingBindings()
            }
        }
    }
}