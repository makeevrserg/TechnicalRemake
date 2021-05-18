package com.makeevrserg.technicalremake.scheduler

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorDestinationBuilder
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.makeevrserg.technicalremake.R
import com.makeevrserg.technicalremake.database.Database
import com.makeevrserg.technicalremake.databinding.FragmentSchedulerBinding

class SchedulerFragment : Fragment() {

    private val viewModel: SchedulerViewModel by lazy {
        ViewModelProvider(this).get(SchedulerViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSchedulerBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_scheduler,
            container,
            false
        )


        val application = requireNotNull(this.activity).application

        val dataSource = Database.getInstance(application).databaseDao

        val viewModelFactory = SchedulerViewModelFactory(dataSource, application)
        val schedulerViewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(SchedulerViewModel::class.java)

        binding.viewModel = schedulerViewModel
        binding.lifecycleOwner = this

        //RecyclerView
        val adapter = RecAdapter(TimeZoneListener { timeZone, view ->
            viewModel.onTimeZoneClicked(timeZone, view)
        })
        binding.recyclerView.adapter = adapter
        viewModel.timeZones.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it.toMutableList())
            }
        })
        //Toolbar
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_current_player -> {
                    this.findNavController()
                        .navigate(SchedulerFragmentDirections.actionSchedulerFragmentToPlayerFragment())
                    true
                }
                else -> false
            }
        }
        //Проверка на соединение
        viewModel.connected.observe(viewLifecycleOwner, Observer {
            if (it == false) {
                Toast.makeText(context, "Нет соединения", Toast.LENGTH_SHORT).show()
                activity?.finish()
            }
        })
        return binding.root
    }


}