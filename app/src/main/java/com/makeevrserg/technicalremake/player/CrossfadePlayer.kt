package com.makeevrserg.technicalremake.player

import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.SimpleExoPlayer
import com.makeevrserg.technicalremake.scheduler.JsonParseClasses
import java.util.*


class CrossfadePlayer(
    val context: Context, val cacheDir: String
) : Player.Listener {
    var mainPlayer: SimpleExoPlayer
    var crossfadePlayer: SimpleExoPlayer
    val TAG = "CrossfadePlayer"

    var profileFiles: List<JsonParseClasses.ProfileFile>? = null

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
            manageSound()
            this.initListener()
        }
            .setPosition(this.currentPosition + 100)
            .setDeleteAfterDelivery(true)
            .setLooper(Looper.getMainLooper())
            .send()
    }

    fun manageSound() {
        val toEnd = mainPlayer.contentDuration.minus(mainPlayer.contentPosition)

        if (toEnd < 5000) {
            playCrossfade()
            setVolume(1.0f.minus(toEnd.div(5000)))
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
        Log.d(TAG, "onMediaItemTransition: ${reason}")
        setStrings(mainPlayer.currentMediaItem)
        if (reason == ExoPlayer.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED)
            return
        else if (reason == ExoPlayer.MEDIA_ITEM_TRANSITION_REASON_AUTO || reason == ExoPlayer.MEDIA_ITEM_TRANSITION_REASON_REPEAT)
            rotatePlayer()

    }

    fun update(musicInfos: List<JsonParseClasses.ProfileFile>) {
        stop()
        this.profileFiles = musicInfos
        generatePlayer(musicInfos)

    }

    private fun generatePlayer(musicInfos: List<JsonParseClasses.ProfileFile>) {
        val mediaItemList = mutableListOf<MediaItem>()
        for (profileFile in musicInfos) {
            val mediaItem: MediaItem =
                MediaItem.Builder().setUri(cacheDir + "/" + profileFile.name)
                    .setMediaId(profileFile.name)
                    .setMediaMetadata(
                        MediaMetadata.Builder().setTitle("Playlist").build()
                    )
                    .build()
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
        setVolume(0.0f)
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