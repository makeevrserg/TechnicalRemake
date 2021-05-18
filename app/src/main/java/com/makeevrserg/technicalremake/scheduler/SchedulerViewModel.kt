package com.makeevrserg.technicalremake.scheduler

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.makeevrserg.technicalremake.R
import com.makeevrserg.technicalremake.Util
import com.makeevrserg.technicalremake.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.ConnectException
import java.net.URL
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class SchedulerViewModel(
    val database: DatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private val TAG = "SchedulerViewModel"
    val _URL = "https://empireprojekt.ru/test.json"
    private var _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private var _profileName = MutableLiveData<String>("Загрузка..")
    val getProfile: LiveData<String>
        get() = _profileName

    private val _connected = MutableLiveData<Boolean>(true)
    val connected: LiveData<Boolean>
        get() = _connected

    private var _timeZones = database.getTimeZones()
    val timeZones: LiveData<List<TimeZoneScheduler>>
        get() = _timeZones

    private var _fileLoading = MutableLiveData<String>("")
    public val fileLoading: LiveData<String>
        get() = _fileLoading


    private val cacheDir: File = application.cacheDir

    init {
        initDatabase()
    }

    private fun downloadFiles(cacheDir: File) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val timeZones: List<TimeZoneScheduler> =
                    database.getTimeZones().value ?: mutableListOf()
                val playlists: List<PlayList> = database.getPlaylists()
                val filesDatabase: List<FileDatabase> = database.getFiles()

                for (file: FileDatabase in filesDatabase) {
                    try {
                        _fileLoading.postValue(file.name)
                        file.isBroken = Util.download(cacheDir, file)

                        Log.i(TAG, "cacheDir: ${cacheDir}")
                        database.fileUpdate(file)
                    } catch (e: FileNotFoundException) {
                        file.isBroken = true
                    } catch (e: ConnectException) {
                        file.isBroken = true
                    } catch (e: SSLHandshakeException) {
                        file.isBroken = true
                    } catch (e: IOException) {
                        file.isBroken = true
                    } catch (e: EOFException) {
                        file.isBroken = true
                    }

                    //По умолчанию все файлы в порядке
                    if (file.isBroken) {
                        database.updatePlaylistBrokentByMusicId(file.isBroken, file.id)
                        for (plId: Long in database.getPlaylistsByMusicId(file.id))
                            database.updateTimezoneBrokentByPlaylistId(file.isBroken, plId)
                    }
                }
                _timeZones = database.getTimeZones()

            }
            _isLoading.value = false
        }

    }


    private fun initDatabase() {
        viewModelScope.launch {
            CreateDatabase()
        }
    }

    //Парсинг json и создание БД
    private suspend fun CreateDatabase() {
        withContext(Dispatchers.IO) {
            database.clearFiles()
            database.clearPlayLists()
            database.clearTimeZone()

            try {
                val jsonStr: JSONObject = JSONObject(URL(_URL).readText())
                //Берем название профила и ставим его для отображения
                _profileName.postValue(jsonStr.getString("name"))

                createPlaylistAndFiles(jsonStr.getJSONObject("schedule").getJSONArray("playlists"))
                CreateTimeZones(jsonStr.getJSONObject("schedule").getJSONArray("days"))
                downloadFiles(cacheDir)
            } catch (e: UnknownHostException) {
                //Если нет соединения
                _connected.postValue(false)
            }


        }
    }


    private suspend fun CreateTimeZones(jsArray: JSONArray) {
        for (i in 0 until jsArray.length()) {
            val jsonObject: JSONObject = jsArray.getJSONObject(i)
            //Если учитывать что .json приходит верным,то continue никогда не случится
            val day: String = Util.dict[jsonObject.getString("day")] ?: continue

            for (j in 0 until jsonObject.getJSONArray("timeZones").length()) {
                val jsonTimeZone = jsonObject.getJSONArray("timeZones").getJSONObject(j)

                var from = jsonTimeZone.getString("from")
                //В SQL не получается сравнить 6:00 и 06:00 поэтому добавляем ноль
                if (from.length < 5)
                    from = "0$from"
                var to = jsonTimeZone.getString("to")
                if (to.length < 5)
                    to = "0$to"
                //Проходим по timezones.playlists
                for (k in 0 until jsonTimeZone.getJSONArray("playlists").length()) {
                    val jsonPlaylist: JSONObject =
                        jsonTimeZone.getJSONArray("playlists").getJSONObject(k)
                    val playlistId: Long = jsonPlaylist.getLong("playlist_id")
                    val timeZoneScheduler: TimeZoneScheduler =
                        TimeZoneScheduler(
                            0,
                            from,
                            to,
                            day,
                            database.getPlaylistNameByPlaylistId(playlistId),
                            playlistId,
                            jsonPlaylist.getInt("proportion")
                        )
                    database.timeZoneInsert(timeZoneScheduler)
                }
            }
            //У нас может быть пустая timeZone, так что сверху она на добавится. Поэтому добавляем сами.
            if (jsonObject.getJSONArray("timeZones").length() == 0)
                database.timeZoneInsert(
                    TimeZoneScheduler(
                        0,
                        "",
                        "",
                        day,
                        "",
                        null, null
                    )
                )


        }
    }

    //Парсинг и создание БД для playlists и всех files
    private suspend fun createPlaylistAndFiles(jsArray: JSONArray) {
        for (i in 0 until jsArray.length()) {
            val id = jsArray.getJSONObject(i).getLong("id")
            val name = jsArray.getJSONObject(i).getString("name")
            val filesArray = jsArray.getJSONObject(i).getJSONArray("files")

            for (j in 0 until filesArray.length()) {
                val jsonObject: JSONObject = filesArray.getJSONObject(j)
                val file: FileDatabase =
                    Gson().fromJson(jsonObject.toString(), FileDatabase::class.java)
                database.fileInsert(file)
                database.playlistInsert(PlayList(0, id, name, file.id))
            }
        }
    }

    //При нажатии на элемент recyclerView меняем пропорцию
    private suspend fun onProportionChanged(timeZone: TimeZoneScheduler, k: Int) {
        withContext(Dispatchers.IO) {
            timeZone.proportion = timeZone.proportion?.plus(k)
            if (timeZone.proportion ?: 0 < 1)
                timeZone.proportion = 1
            database.timeZoneUpdate(timeZone)

            _timeZones = database.getTimeZones()
        }
    }

    fun onTimeZoneClicked(timeZone: TimeZoneScheduler, view: View) {
        viewModelScope.launch {
            when (view.id) {
                R.id.imageViewAdd -> {
                    onProportionChanged(timeZone, 1)

                }
                R.id.imageViewSub -> {
                    onProportionChanged(timeZone, -1)

                }
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
    }
}