//package com.makeevrserg.technicalremake.scheduler
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.makeevrserg.technicalremake.databinding.ListProfileBinding
//import com.makeevrserg.technicalremake.scheduler.JsonParseClasses.AdvancedDay
//
//
//class RecAdapter(private val clickListener: TimeZoneListener) :
//    ListAdapter<AdvancedDay, RecAdapter.ViewHolder>(
//        TimeZoneDiffCallback()
//    ) {
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val item = getItem(position)
//        holder.bind(item, clickListener)
//    }
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        return ViewHolder.from(parent)
//    }
//
//    class ViewHolder private constructor(private val binding: ListProfileBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun bind(item: AdvancedDay, clickListener: TimeZoneListener) {
//            binding.advancedDay = item
//            binding.clickListener = clickListener
//            binding.executePendingBindings()
//        }
//
//        companion object {
//            fun from(parent: ViewGroup): ViewHolder {
//                val layoutInflater = LayoutInflater.from(parent.context)
//                val binding = ListProfileBinding.inflate(layoutInflater, parent, false)
//                return ViewHolder(binding)
//            }
//        }
//    }
//
//    class TimeZoneDiffCallback : DiffUtil.ItemCallback<AdvancedDay>() {
//        override fun areItemsTheSame(
//            oldItem: AdvancedDay,
//            newItem: AdvancedDay
//        ): Boolean {
//            return oldItem.playlistId == newItem.playlistId
//        }
//
//        override fun areContentsTheSame(
//            oldItem: AdvancedDay,
//            newItem: AdvancedDay
//        ): Boolean {
//
//            return false
//        }
//    }
//
//}
//
//class TimeZoneListener(val clickListener: (timeZone: AdvancedDay, view: View) -> Unit) {
//    fun onClick(timeZone: AdvancedDay, view: View) = clickListener(timeZone, view)
//
//}