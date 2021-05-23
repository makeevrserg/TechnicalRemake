package com.makeevrserg.technicalremake.player

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.makeevrserg.technicalremake.Util
import com.makeevrserg.technicalremake.database.*
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
            CrossfadePlayer(viewModelScope,application.applicationContext, cacheDir.path)

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
            val playlistIds: List<TimeZoneScheduler> = getPlaylistsFromDatabase()
            val playlistMap = mutableMapOf<Long, Int>()


            for (timeZone: TimeZoneScheduler in playlistIds)
                playlistMap[timeZone.playlistId!!] = timeZone.proportion!!

            //Мы не должны быть в этом фрагменте если нет музыки
            if (playlistMap.isEmpty()) {
                _isEmpty.value = true
                timer.purge()
                timer.cancel()
                return@launch
            }
            if (oldPlaylistMap == playlistMap) {
                return@launch
            } else
                oldPlaylistMap = playlistMap

            val musicInfos: List<MusicInfo> = getMusicFiles(playlistMap)
            if (musicInfos.isEmpty()) {
                _isEmpty.postValue(true)
                return@launch
            }
            crossfadePlayer.update(musicInfos)
            _isLoading.postValue(false)
            _isUpdated.postValue(true)
            _isPlaying.postValue(false)
        }
    }


    data class MusicInfo(val fileName: String, val playlistName: String)

    private suspend fun getMusicFiles(playlistMap: Map<Long, Int>): List<MusicInfo> {
        return withContext(Dispatchers.IO) {
            val musicToPlay = mutableListOf<MusicInfo>()

            for (playlistId in playlistMap.keys) {
                val proportion: Int = playlistMap[playlistId]!!
                val musicIds: Array<Long> = database.getMusicIdsByPlaylistId(playlistId)
                val playlistName: String = database.getPlaylistNameByPlaylistId(playlistId)
                if (musicIds.isEmpty()) {

                    _isEmpty.postValue(true)
                    return@withContext musicToPlay
                }
                for (i in 0 until proportion) {
                    musicToPlay.add(
                            MusicInfo(
                                    database.getFileName(
                                            musicIds[Random.nextInt(0, musicIds.size)]
                                    ), playlistName
                            )
                    )

                }
            }
            musicToPlay
        }
    }

    private suspend fun getPlaylistsFromDatabase(): List<TimeZoneScheduler> {
        return withContext(Dispatchers.IO) {
            val db = database.getTimezonesByTime(Util.getCurrentDay(), Util.getCurrentTime())
            db
        }
    }

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