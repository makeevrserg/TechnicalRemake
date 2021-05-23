package com.makeevrserg.technicalremake.player

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.*
import java.util.*

class CrossfadePlayer(val context: Context, val cacheDir: String) : Player.EventListener {
    lateinit var mainPlayer: SimpleExoPlayer
    lateinit var crossfadePlayer: SimpleExoPlayer
    val TAG = "CrossfadePlayer"

    var musicInfos: List<PlayerViewModel.MusicInfo>? = null


    init {
        mainPlayer = SimpleExoPlayer.Builder(context).build()
        mainPlayer.addListener(this)
        crossfadePlayer =
            SimpleExoPlayer.Builder(context).build()
        mainPlayer.repeatMode = Player.REPEAT_MODE_ALL
        crossfadePlayer.repeatMode = Player.REPEAT_MODE_ALL
        crossfadePlayer.addListener(this)


    }

    private var _mediaName: MutableLiveData<String> = MutableLiveData("Загрузка...")
    public val mediaName: LiveData<String>
        get() = _mediaName

    private var _mediaPlaylist: MutableLiveData<String> = MutableLiveData("Загрузка...")
    public val mediaPlaylist: LiveData<String>
        get() = _mediaPlaylist

    var timer: Timer? = null


    private fun setStrings(mediaItem: MediaItem?) {
        _mediaName.value = mediaItem?.mediaId
        _mediaPlaylist.value = mediaItem?.mediaMetadata?.title
    }

    private fun rotatePlayer() {
        val oldCrossfade = crossfadePlayer
        crossfadePlayer = mainPlayer
        mainPlayer = oldCrossfade
        crossfadePlayer.next()
        crossfadePlayer.pause()
    }


    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        setStrings(mainPlayer.currentMediaItem)
        if (reason == ExoPlayer.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED)
            return
        if (reason==ExoPlayer.MEDIA_ITEM_TRANSITION_REASON_AUTO)
            rotatePlayer()
        super.onMediaItemTransition(mediaItem, reason)
    }

    fun update(musicInfos: List<PlayerViewModel.MusicInfo>) {
        stop()
        this.musicInfos = musicInfos
        generatePlayer(musicInfos)

    }

    private fun generatePlayer(musicInfos: List<PlayerViewModel.MusicInfo>) {
        val mediaItemList = mutableListOf<MediaItem>()
        for (musicInfo: PlayerViewModel.MusicInfo in musicInfos) {
            val mediaItem: MediaItem =
                MediaItem.Builder().setUri(cacheDir + "/" + musicInfo.fileName)
                    .setMediaId(musicInfo.fileName)
                    .setMediaMetadata(
                        MediaMetadata.Builder().setTitle(musicInfo.playlistName).build()
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
        if (mainPlayer.isPlaying) {
            mainPlayer.pause()
            mainPlayer.next()
            crossfadePlayer.pause()
            crossfadePlayer.next()

            mainHandler?.removeCallbacksAndMessages(null)
            Log.i(TAG, "Pause: ${mainPlayer.isPlaying}")
            return false
        } else {
            play()
            Log.i(TAG, "Play: ${mainPlayer.isPlaying}")
            return true
        }
    }

    var mainHandler: Handler? = null
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
        mainHandler = Handler(Looper.getMainLooper())
        mainHandler?.post(object : Runnable {
            override fun run() {
                val length = mainPlayer.contentDuration
                val toEnd = length - mainPlayer.contentPosition
                synchronized(this) {
                    if (toEnd < 5000) {
                        playCrossfade()
                        setVolume((1.0f - (toEnd.toFloat() / (5000))))
                    }
                }

                mainHandler?.postDelayed(this, 200)
            }
        })


    }

    fun stop() {
        mainHandler?.removeCallbacksAndMessages(null)
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