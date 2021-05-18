package com.makeevrserg.technicalremake.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.makeevrserg.technicalremake.R
import com.makeevrserg.technicalremake.database.Database
import com.makeevrserg.technicalremake.databinding.PlayerFragmentBinding

class PlayerFragment : Fragment() {

    private val viewModel: PlayerViewModel by lazy {
        ViewModelProvider(this).get(PlayerViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: PlayerFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.player_fragment,
            container,
            false
        )
        val application = requireNotNull(this.activity).application
        val dataSource = Database.getInstance(application).databaseDao
        val viewModelFactory = PlayerViewModelFactory(dataSource, application)
        val playerViewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(PlayerViewModel::class.java)

        //в ViewModel есть таймер, который проверят фиксированное время обновление времени
        viewModel.isUpdated.observe(viewLifecycleOwner, Observer {
            if (it==true){
                Snackbar.make(
                   requireActivity().findViewById(android.R.id.content),"Музыка обновлена",Snackbar.LENGTH_SHORT
                ).show()
                viewModel.doneShowingSnackBar()
            }
        })
        //Если нет доступной музыки
        viewModel.isEmpty.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                Toast.makeText(context, "Нет доступной музыки", Toast.LENGTH_SHORT).show()
                activity?.finish()

            }
        })

        binding.viewModel = playerViewModel

        binding.lifecycleOwner = this


        return binding.root
    }
}