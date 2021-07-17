package com.makeevrserg.technicalremake.player

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.makeevrserg.technicalremake.database.DatabaseDao
import java.io.File
import java.util.*
import kotlin.concurrent.fixedRateTimer

class PlayerViewModel(
    val database: DatabaseDao,
    application: Application
) : AndroidViewModel(application) {


    private var _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private var _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean>
        get() = _isPlaying

    private var _isUpdated = MutableLiveData(false)
    val isUpdated: LiveData<Boolean>
        get() = _isUpdated

    private var _isEmpty = MutableLiveData(false)
    val isEmpty: LiveData<Boolean>
        get() = _isEmpty

    val playlistName: LiveData<String>
        get() = crossfadePlayer.mediaPlaylist

    val musicName: LiveData<String>
        get() = crossfadePlayer.mediaName

    private val TAG = "PlayerViewModel"


    private val cacheDir: File = application.cacheDir
    private var crossfadePlayer: CrossfadePlayer =
        CrossfadePlayer(application.applicationContext, cacheDir.path)

    private var timer: Timer

    init {
        //Ставим таймер чтобы он чекал на обновление времени
        timer = fixedRateTimer("MusicUpdateTimer", true, 0, 5000) {
            Log.i(TAG, "Timer Checking: ")
//            loadData()
        }
    }

    private var oldPlaylistMap: Map<Long, Int>? = null


//    private fun loadData() {
//        viewModelScope.launch {
//            val profile = database.getProfile()
//            profile.schedule.advancedDays = database.getAdvancedDays()
//            val proportionMap = profile.getProportionMapByTime(Util.getCurrentTime())
//
//
//
//            //Мы не должны быть в этом фрагменте если нет музыки
//            if (proportionMap.isEmpty()) {
//                _isEmpty.value = true
//                timer.purge()
//                timer.cancel()
//                return@launch
//            }
//            if (oldPlaylistMap == proportionMap)
//                return@launch
//
//            oldPlaylistMap = proportionMap
//
//            val fileByPlaylistID = profile.getFilesByTime(Util.getCurrentTime())
//            if (fileByPlaylistID.isEmpty()) {
//                _isEmpty.postValue(true)
//                return@launch
//            }
//            crossfadePlayer.update(fileByPlaylistID)
//            _isLoading.postValue(false)
//            _isUpdated.postValue(true)
//            _isPlaying.postValue(false)
//        }
//    }

    fun playButtonOnClick() {
        _isPlaying.value = crossfadePlayer.onPlayPressed()
    }

    override fun onCleared() {
        crossfadePlayer.stop()
        timer.purge()
        timer.cancel()
        super.onCleared()
    }

    fun doneShowingSnackBar() {
        _isUpdated.value = false
    }

}