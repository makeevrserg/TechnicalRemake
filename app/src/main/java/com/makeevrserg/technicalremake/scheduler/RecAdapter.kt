package com.makeevrserg.technicalremake.scheduler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.makeevrserg.technicalremake.database.entities.relation.TimeZoneAndPlaylistProportion
import com.makeevrserg.technicalremake.databinding.ListProfileBinding


class RecAdapter(private val clickListener: TimeZoneListener) :
    ListAdapter<TimeZoneAndPlaylistProportion, RecAdapter.ViewHolder>(
        TimeZoneDiffCallback()
    ) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(private val binding: ListProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TimeZoneAndPlaylistProportion, clickListener: TimeZoneListener) {
            binding.item = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListProfileBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class TimeZoneDiffCallback : DiffUtil.ItemCallback<TimeZoneAndPlaylistProportion>() {
        override fun areItemsTheSame(
            oldItem: TimeZoneAndPlaylistProportion,
            newItem: TimeZoneAndPlaylistProportion
        ): Boolean {
            return oldItem.timeZone.id == newItem.timeZone.id
        }

        override fun areContentsTheSame(
            oldItem: TimeZoneAndPlaylistProportion,
            newItem: TimeZoneAndPlaylistProportion
        ): Boolean {

            return false
        }
    }

}

class TimeZoneListener(val clickListener: (timeZone: TimeZoneAndPlaylistProportion, view: View) -> Unit) {
    fun onClick(timeZone: TimeZoneAndPlaylistProportion, view: View) = clickListener(timeZone, view)

}