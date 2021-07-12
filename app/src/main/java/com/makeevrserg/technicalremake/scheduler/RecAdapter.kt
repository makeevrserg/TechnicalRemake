package com.makeevrserg.technicalremake.scheduler

import android.R.attr.data
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.makeevrserg.technicalremake.databinding.ListProfileBinding


class RecAdapter(private val clickListener: TimeZoneListener) : ListAdapter<JsonParseClasses.AdvancedDay, RecAdapter.ViewHolder>(
    TimeZoneDiffCallback()
) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: JsonParseClasses.AdvancedDay, clickListener: TimeZoneListener) {
            binding.advancedDay = item
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
    //Возможно не стоило это использовать потому что в areContentsTheSame он возвращает корректно только на каждый второй вызов функции
    class TimeZoneDiffCallback : DiffUtil.ItemCallback<JsonParseClasses.AdvancedDay>() {
        override fun areItemsTheSame(
            oldItem: JsonParseClasses.AdvancedDay,
            newItem: JsonParseClasses.AdvancedDay
        ): Boolean {
            return oldItem.playlistId == newItem.playlistId
        }

        override fun areContentsTheSame(
            oldItem: JsonParseClasses.AdvancedDay,
            newItem: JsonParseClasses.AdvancedDay
        ): Boolean {

            return false
            //return oldItem.proportion == newItem.proportion
        }
    }

}

class TimeZoneListener(val clickListener: (timeZone: JsonParseClasses.AdvancedDay, view: View) -> Unit){
    fun onClick(timeZone: JsonParseClasses.AdvancedDay, view: View) = clickListener(timeZone, view)

}