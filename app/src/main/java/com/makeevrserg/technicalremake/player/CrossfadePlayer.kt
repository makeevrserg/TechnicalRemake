package com.makeevrserg.technicalremake.player

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.fixedRateTimer


class CrossfadePlayer(val coroutineScope: CoroutineScope,val context: Context, val cacheDir: String) : Player.EventListener {
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
        mainPlayer.setThrowsWhenUsingWrongThread(false)
        crossfadePlayer.setThrowsWhenUsingWrongThread(false)


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


    override fun onEvents(player: Player, events: Player.Events) {
        if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)) {
            Log.i(TAG, "onEvents: ${player} ${crossfadePlayer} ${mainPlayer}")
            val mediaItem = mainPlayer.currentMediaItem
            _mediaName.value = mediaItem?.mediaId
            _mediaPlaylist.value = mediaItem?.mediaMetadata?.title

        }
    }

//    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
//        if (mediaItem == null)
//            return
//        if (mediaItem.)
//        super.onMediaItemTransition(mediaItem, reason)
//    }

    fun update(musicInfos: List<PlayerViewModel.MusicInfo>) {
        for (musicInfo in musicInfos)
            Log.i(TAG, "update: ${musicInfo}")
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

            //mainHandler?.removeCallbacksAndMessages(null)

            Log.i(TAG, "Pause: ${mainPlayer.isPlaying}")
            return false
        } else {
            play()
            Log.i(TAG, "Play: ${mainPlayer.isPlaying}")
            return true
        }
    }

    //var mainHandler: Handler? = null
    private fun play() {
        mainPlayer.play()

        timer = fixedRateTimer("CrossfadeUpdateTimer", true, 0, 10) {
            val length = mainPlayer.contentDuration
            val toEnd = length - mainPlayer.contentPosition

                if (toEnd < 5000) {
                    if (!crossfadePlayer.isPlaying) {
                        crossfadePlayer.prepare()
                        crossfadePlayer.play()
                    }
                    val mSound: Float = (1.0f - (toEnd.toFloat() / (5000)))
                    crossfadePlayer.volume = mSound
                    mainPlayer.volume = 1.0f - mSound
                }

                if (toEnd < 500) {
                    val oldCrossfade = crossfadePlayer
                    crossfadePlayer = mainPlayer
                    mainPlayer = oldCrossfade
                    crossfadePlayer.next()
                    crossfadePlayer.next()
                    crossfadePlayer.pause()
                }


                //mainHandler?.postDelayed(this, 50)

        }


        //mainHandler = Handler(Looper.getMainLooper())

//        mainHandler?.post(object : Runnable {
//            override fun run() {
//
//
//            }
//        })


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
        timer?.purge()
        timer?.cancel()
        //mainHandler?.removeCallbacksAndMessages(null)
    }


}