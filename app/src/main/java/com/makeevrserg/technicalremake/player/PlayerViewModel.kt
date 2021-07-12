package com.makeevrserg.technicalremake.player

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.makeevrserg.technicalremake.Util
import com.makeevrserg.technicalremake.database.*
import com.makeevrserg.technicalremake.scheduler.JsonParseClasses
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.random.Random

class PlayerViewModel(
        val database: DatabaseDao,
        application: Application
) : AndroidViewModel(application) {


    private var _isLoading = MutableLiveData<Boolean>(true)
    public val isLoading: LiveData<Boolean>
        get() = _isLoading

    private var _isPlaying = MutableLiveData<Boolean>(false)
    public val isPlaying: LiveData<Boolean>
        get() = _isPlaying

    private var _isUpdated = MutableLiveData<Boolean>(false)
    public val isUpdated: LiveData<Boolean>
        get() = _isUpdated

    private var _isEmpty = MutableLiveData<Boolean>(false)
    public val isEmpty: LiveData<Boolean>
        get() = _isEmpty

    public val playlistName: LiveData<String>
        get() = crossfadePlayer.mediaPlaylist

    public val musicName: LiveData<String>
        get() = crossfadePlayer.mediaName

    val TAG = "PlayerViewModel"


    private val cacheDir: File = application.cacheDir
    private var crossfadePlayer: CrossfadePlayer =
            CrossfadePlayer(application.applicationContext, cacheDir.path)

    lateinit var timer: Timer

    init {
        //Ставим таймер чтобы он чекал на обновление времени
        timer = fixedRateTimer("MusicUpdateTimer", true, 0, 5000) {
            Log.i(TAG, "Timer Checking: ")
           loadData()
        }

    }

    var oldPlaylistMap: MutableMap<Long, Int>? = null


    private fun loadData() {
        viewModelScope.launch {
            val profile = database.getProfile()


            val proportionMap = profile.getProportionMapByTime(Util.getCurrentTime())


            //Мы не должны быть в этом фрагменте если нет музыки
            if (proportionMap.isEmpty()) {
                _isEmpty.value = true
                timer.purge()
                timer.cancel()
                return@launch
            }
            if (oldPlaylistMap == proportionMap) {
                return@launch
            } else
                oldPlaylistMap = proportionMap

            val files = profile.getFilesByTime(Util.getCurrentTime())
            if (files.isEmpty()) {
                _isEmpty.postValue(true)
                return@launch
            }
            crossfadePlayer.update(files)
            _isLoading.postValue(false)
            _isUpdated.postValue(true)
            _isPlaying.postValue(false)
        }
    }


    data class MusicInfo(val fileName: String, val playlistName: String)



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