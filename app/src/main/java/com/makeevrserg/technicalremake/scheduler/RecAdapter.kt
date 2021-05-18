package com.makeevrserg.technicalremake.scheduler

import android.R.attr.data
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.makeevrserg.technicalremake.database.TimeZoneScheduler
import com.makeevrserg.technicalremake.databinding.ListProfileBinding


class RecAdapter(val clickListener: TimeZoneListener) : ListAdapter<TimeZoneScheduler, RecAdapter.ViewHolder>(
    TimeZoneDiffCallback()
) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        //Костыль для отображения дней. Если новый день = старому значит не отображаем этот элемент
        if (position != 0 && getItem(position - 1).day == getItem(position).day)
            holder.binding.textViewDay.visibility = View.GONE
        else
            holder.binding.textViewDay.visibility = View.VISIBLE

        holder.bind(item, clickListener)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TimeZoneScheduler, clickListener: TimeZoneListener) {
            binding.timeZone = item
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
    class TimeZoneDiffCallback : DiffUtil.ItemCallback<TimeZoneScheduler>() {
        override fun areItemsTheSame(
            oldItem: TimeZoneScheduler,
            newItem: TimeZoneScheduler
        ): Boolean {
            return oldItem.timeZoneId == newItem.timeZoneId
        }

        override fun areContentsTheSame(
            oldItem: TimeZoneScheduler,
            newItem: TimeZoneScheduler
        ): Boolean {

            return false
            //return oldItem.proportion == newItem.proportion
        }
    }

}

class TimeZoneListener(val clickListener: (timeZone: TimeZoneScheduler, view: View) -> Unit){
    fun onClick(timeZone: TimeZoneScheduler, view: View) = clickListener(timeZone, view)

}