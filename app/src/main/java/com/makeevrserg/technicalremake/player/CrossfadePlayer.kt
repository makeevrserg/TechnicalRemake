package com.makeevrserg.technicalremake.player

import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.makeevrserg.technicalremake.database.entities.PlayerFile
import java.util.*


class CrossfadePlayer(
    private val context: Context, private val cacheDir: String
) : Player.Listener {
    private var mainPlayer: SimpleExoPlayer
    private var crossfadePlayer: SimpleExoPlayer
    private val TAG = "CrossfadePlayer"


    private var _mediaName: MutableLiveData<String> = MutableLiveData("Загрузка...")
    val mediaName: LiveData<String>
        get() = _mediaName

    private var _mediaPlaylist: MutableLiveData<String> = MutableLiveData("Загрузка...")
    val mediaPlaylist: LiveData<String>
        get() = _mediaPlaylist


    private fun buildPlayer(): SimpleExoPlayer {
        val player = SimpleExoPlayer.Builder(context).build()
        player.addListener(this)
        player.repeatMode = Player.REPEAT_MODE_ALL
        return player
    }

    init {
        mainPlayer = buildPlayer()
        crossfadePlayer = buildPlayer()
    }

    fun update(filesByPlaylistID: Map<String, List<PlayerFile>>) {
        stop()
        generatePlayer(filesByPlaylistID)
    }

    private fun ExoPlayer.initListener() {

        this.createMessage { _, _ ->
            manageSound()
            this.initListener()
        }
            .setPosition(this.currentPosition + 100)
            .setDeleteAfterDelivery(true)
            .setLooper(Looper.getMainLooper())
            .send()
    }

    private fun manageSound() {
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
        crossfadePlayer = mainPlayer
        mainPlayer = oldCrossfade
        crossfadePlayer.next()
        crossfadePlayer.pause()
        mainPlayer.initListener()
    }


    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        setStrings(mainPlayer.currentMediaItem)
        if (reason == ExoPlayer.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED)
            return
        else if (reason == ExoPlayer.MEDIA_ITEM_TRANSITION_REASON_AUTO || reason == ExoPlayer.MEDIA_ITEM_TRANSITION_REASON_REPEAT)
            rotatePlayer()

    }

    private fun getMediaItem(file: PlayerFile, playlistName: String): MediaItem {
        return MediaItem.Builder().setUri(cacheDir + "/" + file.name)
            .setMediaId(file.name)
            .setMediaMetadata(
                MediaMetadata.Builder().setTitle(playlistName).build()
            )
            .build()
    }

    private fun generatePlayer(filesByPlaylistID: Map<String, List<PlayerFile>>) {
        val mediaItemList = mutableListOf<MediaItem>()
        for (playlistName in filesByPlaylistID.keys)
            for (file in filesByPlaylistID[playlistName]!!) {
                val mediaItem: MediaItem = getMediaItem(file, playlistName)
                mediaItemList.add(mediaItem)
                mainPlayer.addMediaItem(mediaItem)
                mainPlayer.prepare()
            }

        Collections.rotate(mediaItemList, -1)
        for (mediaItem in mediaItemList)
            crossfadePlayer.addMediaItem(mediaItem)
        crossfadePlayer.prepare()
    }

    fun onPlayPressed(): Boolean {
        Log.i(TAG, "isPlaying: ${mainPlayer.isPlaying}")
        return if (mainPlayer.isPlaying) {
            mainPlayer.pause()
            mainPlayer.next()
            crossfadePlayer.pause()
            crossfadePlayer.next()
            Log.i(TAG, "Pause: ${mainPlayer.isPlaying}")
            false
        } else {
            play()
            Log.i(TAG, "Play: ${mainPlayer.isPlaying}")
            true
        }
    }

    private fun playCrossfade() {
        if (crossfadePlayer.isPlaying)
            return
        crossfadePlayer.prepare()
        crossfadePlayer.play()

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