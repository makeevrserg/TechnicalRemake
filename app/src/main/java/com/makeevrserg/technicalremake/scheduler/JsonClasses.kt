package com.makeevrserg.technicalremake.scheduler

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.makeevrserg.technicalremake.Util.Companion.dict
import com.makeevrserg.technicalremake.Util.Companion.getCurrentDay
import java.util.*

class JsonParseClasses {

    @Entity(tableName = "files")
    data class ProfileFile(
        @PrimaryKey(autoGenerate = false)
        val id: Long,
        val file_name: String,
        val name: String,
        val size: Int,
        val md5_file: String,
        val duration: Long,
        val order: Int,
        var isBroken: Boolean
    )

    data class ProfilePlaylist(
        val id: Long,
        val name: String,
        val duration: Long,
        val random: Boolean,
        val files: MutableList<ProfileFile>
    ) {
        fun getFileIndexById(id: Long): Int? {
            for ((i, file) in files.withIndex())
                if (file.id == id)
                    return i
            return null
        }
    }

    data class TempPlaylistsTimezone(
        val playlist_id: Long,
        val proportion: Int
    )

    data class TempProfileTimezone(
        val from: String,
        val to: String,
        val playlists: List<TempPlaylistsTimezone>
        //val playlists: Map<Int,Int>
    )

    data class TempProfileDay(
        val day: String,
        val timeZones: List<TempProfileTimezone>
    )

    @Entity(tableName = "timings")
    data class AdvancedDay(
        @PrimaryKey(autoGenerate = true)
        val idDay: Long = 0,
        val day: String,
        val from: String = "00:00",
        val to: String = "00:00",
        val playlistId: Long = 0L,
        var playlistProportion: Int = -1,
        val playlistName: String = "",
        val isBroken: Boolean = true,
        val showDay: Boolean = true
    ) {
        fun getProportion(): String {
            return playlistProportion.toString()
        }

        constructor(
            d: String,
            t: TempProfileTimezone,
            p: TempPlaylistsTimezone,
            playlistName: String,
            showDay: Boolean
        ) : this(
            0L,
            d,
            t.from,
            t.to,
            p.playlist_id,
            p.proportion,
            playlistName,
            false,
            showDay
        )
    }

    data class ProfileSchedule(
        val playlists: List<ProfilePlaylist>,
        val days: List<TempProfileDay>,
        var advancedDays: List<AdvancedDay>
    )


    @Entity(tableName = "profile")
    data class Profile(
        @PrimaryKey(autoGenerate = false) val id: Int,
        val name: String,
        val schedule: ProfileSchedule
    ) {
        fun initAdvancedProfile() {
            fun showDay(days: List<AdvancedDay>, day: String): Boolean {
                if (days.isEmpty())
                    return true
                return days[days.size - 1].day != day

            }

            val days = mutableListOf<AdvancedDay>()
            for (d in schedule.days) {
                if (d.timeZones.isEmpty())
                    days.add(AdvancedDay(day = dict[d.day] ?: continue))
                for (t in d.timeZones)
                    for (p in t.playlists)
                        days.add(
                            AdvancedDay(
                                dict[d.day] ?: continue, t, p,
                                getPlaylistByPlaylistID(p.playlist_id)?.name ?: continue,
                                showDay(days, dict[d.day] ?: continue)
                            )
                        )

            }
            schedule.advancedDays = days
        }

        fun getPlaylistByMusicID(file: ProfileFile): MutableList<ProfilePlaylist> {
            val list = mutableListOf<ProfilePlaylist>()
            for (playlist in schedule.playlists)
                for (other in playlist.files)
                    if (file.id == other.id) {
                        list.add(playlist)
                        break
                    }
            return list
        }

        private fun getPlaylistByPlaylistID(id: Long): ProfilePlaylist? {
            for (p in schedule.playlists)
                if (p.id == id)
                    return p
            return null
        }

        fun setBrokenFile(file: ProfileFile): Profile {
            for (playlist in schedule.playlists) {
                val index = playlist.getFileIndexById(file.id) ?: continue
                playlist.files[index] = file
            }
            return this
        }

        fun getAllFiles(): List<ProfileFile> {
            val map = mutableMapOf<Long, ProfileFile>()
            for (profile in schedule.playlists)
                for (file in profile.files)
                    map[file.id] = file

            return map.values.toList()
        }

        //Время 00:00 и 0:00 не сравнивается друг с другом
        private fun String.fixTime(): String {
            if (this.length < 5)
                return "0$this"
            return this
        }

        private fun Map<Long,Int>.sortAndReverse(): Map<Long, Int> {
            this.toList().sortedBy { (_,v)->v }.toMap()
            this.entries.associateBy ({it.value}){it.key}
            return this
        }
        fun getProportionMapByTime(time: String): Map<Long, Int> {
            val proportions = mutableMapOf<Long, Int>()

            for (p in schedule.advancedDays)
                if (time > p.from.fixTime() && time < p.to.fixTime() && p.day == getCurrentDay())
                    if (!proportions.containsKey(p.playlistId))
                        proportions[p.playlistId] = p.playlistProportion
                    else
                        proportions[p.playlistId] = p.playlistProportion+ proportions[p.playlistId]!!

            return proportions.sortAndReverse()

        }

        fun getFilesByTime(time: String): Map<String, List<ProfileFile>> {
            val proportions = getProportionMapByTime(time)

            val filesByPlaylistName = mutableMapOf<String, List<ProfileFile>>()
            for (playlistID in proportions.keys) {
                val playlist = getPlaylistByPlaylistID(playlistID) ?: continue
                val fileList = mutableListOf<ProfileFile>()
                for (i in 0 until proportions[playlistID]!!) {
                    var file: ProfileFile
                    do {
                        file = playlist.files[Random().nextInt(playlist.files.size)]
                    } while (file.isBroken)
                    fileList.add(file)
                }
                filesByPlaylistName[getPlaylistByPlaylistID(playlistID)?.name ?: continue] =
                    fileList
            }
            return filesByPlaylistName

        }

    }
}