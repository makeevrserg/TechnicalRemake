package com.makeevrserg.technicalremake.scheduler

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.makeevrserg.technicalremake.Util
import com.makeevrserg.technicalremake.database.*
import com.makeevrserg.technicalremake.database.entities.PlayerProfile
import com.makeevrserg.technicalremake.database.entities.relation.TimeZoneAndPlaylistProportion
import com.makeevrserg.technicalremake.database.entities.relation.crossrefs.FilePlaylistCrossRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.*
import java.net.ConnectException
import java.net.URL
import java.net.UnknownHostException
import java.util.*
import javax.net.ssl.SSLHandshakeException

class SchedulerViewModel(
    val database: DatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private val TAG = "SchedulerViewModel"
    private val _URL = "https://empireprojekt.ru/test.json"
    private var _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private var _profileName = MutableLiveData("Загрузка..")
    val getProfile: LiveData<String>
        get() = _profileName

    private val _connected = MutableLiveData(true)
    val connected: LiveData<Boolean>
        get() = _connected

    private var _timeZones:LiveData<List<TimeZoneAndPlaylistProportion>> = MutableLiveData()
    val timeZones: LiveData<List<TimeZoneAndPlaylistProportion>>
        get() = _timeZones

    private var _fileLoading = MutableLiveData("Получение списка файлов...")
    val fileLoading: LiveData<String>
        get() = _fileLoading

    private val cacheDir: File = application.cacheDir


    init {
        initDatabase()
    }

    private fun downloadFiles(cacheDir: File) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val profile = database.getProfile()
                for (file in database.getAllFiles()) {
                    try {
                        _fileLoading.postValue(file.name)
                        file.isBroken = Util.download(cacheDir, file)
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
//                    //По умолчанию все файлы в порядке
//                    if (file.isBroken) {
//                        profile.setBrokenFile(file)
//                        database.updateProfile(profile)
//                        for (pl in profile.getPlaylistByMusicID(file))
//                            database.updateBrokenAdvancedDayByPlaylistID(file.isBroken, pl.id)
//                    }
                }
                _timeZones = MutableLiveData(database.getAllTimeZoneAndPlaylistProportion())
            }
            _isLoading.value = false
        }

    }


    private fun initDatabase() {
        viewModelScope.launch {
            createDatabase()
        }
    }


    //Парсинг json и создание БД
    private suspend fun createDatabase() {
        withContext(Dispatchers.IO) {
            try {
                val jsonStr = JSONObject(URL(_URL).readText())
                val profile =
                    Gson().fromJson(jsonStr.toString(), PlayerProfile::class.java)
                //Берем название профила и ставим его для отображения
                _profileName.postValue(profile.name)
                //Добавляем профиль
                database.insertProfile(profile)
                //Добавляем плейлисты
                database.insertPlayerPlaylist(profile.schedule?.playlists ?: return@withContext)
                //Добавляем файлы
                val filePlaylistCrossRef: MutableList<FilePlaylistCrossRef> = mutableListOf()
                profile.schedule.playlists.forEach { playlist ->
                    database.insertPlayerFile(playlist.files)
                    playlist.files.forEach { file ->
                        filePlaylistCrossRef.add(FilePlaylistCrossRef(playlist.id, file.id))
                    }
                }

                database.insertFilePlaylistCrossRefs(filePlaylistCrossRef)

                //Добавляем дни
                database.insertPlayerDay(profile.schedule.days)
                //Добавляем таймзоны
                profile.schedule.days.forEach { day ->
                    day.timeZones.forEach { timeZone -> timeZone.day = day.day }
                    database.insertPlayerTimeZones(day.timeZones)
                }
                //Добавляем пропорции
                profile.schedule.days.forEach { day ->
                    day.timeZones.forEach { timeZone ->
                        timeZone.playlists.forEach { it->it.day = timeZone.day }
                        database.insertPlayerPlaylistProportion(timeZone.playlists)
                    }
                }

//                val filesOfPl = database.getFilesOfPlaylist(288)
//                Log.d(TAG, "createDatabase: ${filesOfPl.playlist}")
//
//                val datAndTimezone = database.getDayAndTimezones("monday")
//                Log.d(TAG, "createDatabase: ${datAndTimezone.day}")
//                val timeZOneAndPlaylistProportion = database.getTimeZoneAndPlaylistProportion("monday")
//                for (tz in timeZOneAndPlaylistProportion)
//                Log.d(TAG, "createDatabase: ${tz.timeZone} ${tz.playlistProp}")
                //Скачиваем
                downloadFiles(cacheDir)
            } catch (e: UnknownHostException) {
                //Если нет соединения
                _connected.postValue(false)
            }


        }
    }


    //При нажатии на элемент recyclerView меняем пропорцию
    private suspend fun onProportionChanged(advancedDay: TimeZoneAndPlaylistProportion, k: Int) {
        withContext(Dispatchers.IO) {
//            advancedDay.playlistProportion += k
//            if (advancedDay.playlistProportion < 1)
//                advancedDay.playlistProportion = 1
//            database.updateAdvancedDay(advancedDay)
            _timeZones.value?:return@withContext
//            _timeZones = database.getAdvancedDayLiveData()

        }
    }

    fun callOnProportionChanged(advancedDay: TimeZoneAndPlaylistProportion, k: Int) {
        viewModelScope.launch {
            onProportionChanged(advancedDay, k)
        }
    }

}