package com.makeevrserg.technicalremake.scheduler

import android.app.Application
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
    val timeZones: LiveData<List<JsonParseClasses.AdvancedDay>>
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
                for (file: JsonParseClasses.ProfileFile in database.getFiles()) {
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
                    //По умолчанию все файлы в порядке
//                    if (file.isBroken) {
//                        database.updateBrokenPlaylisByMusicID(file.isBroken, file.id)
//                        for (plId: Long in database.getPlaylistsByMusicId(file.id))
//                            database.updateTimezoneBrokentByPlaylistId(file.isBroken, plId)
//                    }
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
            try {
                val jsonStr: JSONObject = JSONObject(URL(_URL).readText())

                val profile =
                    Gson().fromJson(jsonStr.toString(), JsonParseClasses.Profile::class.java)

                profile.initAdvancedProfile()
                //Берем название профила и ставим его для отображения
                _profileName.postValue(profile.name)
                //Таблица файлов
                database.fileInsert(profile.getAllFiles())
                //Таблица профиля
                database.insertProfile(profile)
                //Таблица времени
                database.insertTimezone(profile.schedule.advancedDays)


                //downloadFiles(cacheDir)
                _isLoading.postValue(false)
            } catch (e: UnknownHostException) {
                //Если нет соединения
                _connected.postValue(false)
            }


        }
    }


    //При нажатии на элемент recyclerView меняем пропорцию
    private suspend fun onProportionChanged(advancedDay: JsonParseClasses.AdvancedDay, k: Int) {
        withContext(Dispatchers.IO) {
            advancedDay.playlistProportion += k
            if (advancedDay.playlistProportion < 1)
                advancedDay.playlistProportion = 1
            database.timeZoneUpdate(advancedDay)

            _timeZones = database.getTimeZones()
        }
    }

    fun onTimeZoneClicked(timeZone: JsonParseClasses.AdvancedDay, view: View) {
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