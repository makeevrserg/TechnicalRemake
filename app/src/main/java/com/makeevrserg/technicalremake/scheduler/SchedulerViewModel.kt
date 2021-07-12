package com.makeevrserg.technicalremake.scheduler

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
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
import com.makeevrserg.technicalremake.scheduler.JsonParseClasses.*

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

    private var _timeZones = database.getAdvancedDayLiveData()
    val timeZones: LiveData<List<AdvancedDay>>
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
                for (file: ProfileFile in database.getAllFiles()) {
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
                    if (file.isBroken) {
                        profile.setBrokenFile(file)
                        database.updateProfile(profile)
                        for (pl in profile.getPlaylistByMusicID(file))
                            database.updateBrokenAdvancedDayByPlaylistID(file.isBroken, pl.id)
                    }
                }
                _timeZones = database.getAdvancedDayLiveData()
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
                    Gson().fromJson(jsonStr.toString(), Profile::class.java)
                profile.initAdvancedProfile()
                //Берем название профила и ставим его для отображения
                _profileName.postValue(profile.name)
                //Таблица файлов
                database.insertProfile(profile.getAllFiles())
                //Таблица профиля
                database.insertProfile(profile)
                //Таблица времени
                database.insertAdvancedDay(profile.schedule.advancedDays)
                //Скачиваем
                downloadFiles(cacheDir)
            } catch (e: UnknownHostException) {
                //Если нет соединения
                _connected.postValue(false)
            }


        }
    }




    //При нажатии на элемент recyclerView меняем пропорцию
    private suspend fun onProportionChanged(advancedDay: AdvancedDay, k: Int) {
        withContext(Dispatchers.IO) {
            advancedDay.playlistProportion += k
            if (advancedDay.playlistProportion < 1)
                advancedDay.playlistProportion = 1
            database.updateAdvancedDay(advancedDay)
            _timeZones.value?:return@withContext
            _timeZones = database.getAdvancedDayLiveData()

        }
    }

    fun callOnProportionChanged(advancedDay: AdvancedDay, k: Int) {
        viewModelScope.launch {
            onProportionChanged(advancedDay, k)
        }
    }

}