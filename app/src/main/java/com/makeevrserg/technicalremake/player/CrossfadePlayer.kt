package com.makeevrserg.technicalremake.player

import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.SimpleExoPlayer
import com.makeevrserg.technicalremake.scheduler.JsonParseClasses.*
import java.util.*


class CrossfadePlayer(
    val context: Context, val cacheDir: String
) : Player.Listener {
    var mainPlayer: SimpleExoPlayer
    var crossfadePlayer: SimpleExoPlayer
    val TAG = "CrossfadePlayer"


    private var _mediaName: MutableLiveData<String> = MutableLiveData("Загрузка...")
    public val mediaName: LiveData<String>
        get() = _mediaName

    private var _mediaPlaylist: MutableLiveData<String> = MutableLiveData("Загрузка...")
    public val mediaPlaylist: LiveData<String>
        get() = _mediaPlaylist


    init {
        mainPlayer = SimpleExoPlayer.Builder(context).build()
        mainPlayer.addListener(this)
        crossfadePlayer =
            SimpleExoPlayer.Builder(context).build()
        mainPlayer.repeatMode = Player.REPEAT_MODE_ALL
        crossfadePlayer.repeatMode = Player.REPEAT_MODE_ALL
        crossfadePlayer.addListener(this)


    }

    private fun ExoPlayer.initListener() {

        this.createMessage { _, _ ->
            Log.d(TAG, "initListener: ${this} ${mainPlayer} ${crossfadePlayer}")
            manageSound()
            this.initListener()
        }
            .setPosition(this.currentPosition+100)
            .setDeleteAfterDelivery(true)
            .setLooper(Looper.getMainLooper())
            .send()
    }

    fun manageSound() {
        val length = mainPlayer.contentDuration
        val toEnd = length - mainPlayer.contentPosition

        if (toEnd < 5000) {
            playCrossfade()
            setVolume((1.0f - (toEnd.toFloat() / (5000))))
        }
    }

    private fun setStrings(mediaItem: MediaItem?) {
        _mediaName.value = mediaItem?.mediaId
        _mediaPlaylist.value = mediaItem?.mediaMetadata?.title as String?
    }

    private fun rotatePlayer() {
        val oldCrossfade = crossfadePlayer
        oldCrossfade.initListener()
        crossfadePlayer = mainPlayer
        mainPlayer = oldCrossfade
        crossfadePlayer.next()
        crossfadePlayer.pause()
    }


    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        Log.d(TAG, "onMediaItemTransition: ${reason}")
        setStrings(mainPlayer.currentMediaItem)
        if (reason == ExoPlayer.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED)
            return
        else if (reason == ExoPlayer.MEDIA_ITEM_TRANSITION_REASON_AUTO || reason==ExoPlayer.MEDIA_ITEM_TRANSITION_REASON_REPEAT)
            rotatePlayer()

    }
    fun update(filesByPlaylistID: Map<String, List<ProfileFile>>) {
        stop()
        generatePlayer(filesByPlaylistID)

    }
    private fun getMediaItem(file:ProfileFile,playlistName:String): MediaItem {
        return MediaItem.Builder().setUri(cacheDir + "/" + file.name)
            .setMediaId(file.name)
            .setMediaMetadata(
                MediaMetadata.Builder().setTitle(playlistName).build()
            )
            .build()
    }

    private fun generatePlayer(filesByPlaylistID: Map<String,List<ProfileFile>>) {
        val mediaItemList = mutableListOf<MediaItem>()
        for (playlistName in filesByPlaylistID.keys) {
            for (file in filesByPlaylistID[playlistName]!!) {
                val mediaItem: MediaItem = getMediaItem(file,playlistName)
                mediaItemList.add(mediaItem)
                mainPlayer.addMediaItem(mediaItem)
                mainPlayer.prepare()
            }
        }
        Collections.rotate(mediaItemList, -1)
        for (mediaItem in mediaItemList)
            crossfadePlayer.addMediaItem(mediaItem)
        crossfadePlayer.prepare()
    }

    fun onPlayPressed(): Boolean {
        Log.i(TAG, "isPlaying: ${mainPlayer.isPlaying}")
        if (mainPlayer.isPlaying) {
            mainPlayer.pause()
            mainPlayer.next()
            crossfadePlayer.pause()
            crossfadePlayer.next()

            Log.i(TAG, "Pause: ${mainPlayer.isPlaying}")
            return false
        } else {
            play()
            Log.i(TAG, "Play: ${mainPlayer.isPlaying}")
            return true
        }
    }

    private fun playCrossfade() {
        if (!crossfadePlayer.isPlaying) {
            crossfadePlayer.prepare()
            crossfadePlayer.play()
        }
    }

    private fun setVolume(mSound: Float) {

        crossfadePlayer.volume = mSound
        mainPlayer.volume = 1.0f - mSound
    }

    private fun play() {
        mainPlayer.play()
        setVolume(0.0f)
        mainPlayer.initListener()
    }

    fun stop() {
        mainPlayer.pause()
        mainPlayer.stop()
        crossfadePlayer.pause()
        crossfadePlayer.stop()
        crossfadePlayer.volume = 1f
        mainPlayer.volume = 1f
        mainPlayer.clearMediaItems()
        crossfadePlayer.clearMediaItems()
    }


}