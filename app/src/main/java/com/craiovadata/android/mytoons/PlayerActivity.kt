package com.craiovadata.android.mytoons

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import timber.log.Timber


class PlayerActivity : AppCompatActivity() {
    private var videoId: String? = null
    private var playbackStateListener: PlaybackStateListener? = null
    private var playerView: PlayerView? = null
    private var player: SimpleExoPlayer? = null
    private var mPlayWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        videoId = intent.getStringExtra(ARG_ITEM_VIDEO_ID)
            ?: return finish()
        playerView = findViewById(R.id.video_view)
        playbackStateListener = PlaybackStateListener()
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (player == null) {
            initializePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun initializePlayer() {
        if (player == null) {
            val trackSelector = DefaultTrackSelector(this)
            trackSelector.setParameters(
                trackSelector.buildUponParameters().setMaxVideoSizeSd()
            )
            player = SimpleExoPlayer.Builder(this)
                .setTrackSelector(trackSelector)
                .build()
        }
        playerView!!.player = player
        val videoUrl = getString(R.string.youtube_url, videoId)
        Timber.d(videoUrl)

        val myYoutubeExtractor = MyYoutubeExtractor(this) { url ->
            val mediaItem = MediaItem.fromUri(url)
            player?.apply {
                setMediaItem(mediaItem)
                playWhenReady = mPlayWhenReady
                seekTo(currentWindow, playbackPosition)
                addListener(playbackStateListener!!)
                prepare()
            }
        }
        myYoutubeExtractor.extract(videoUrl, true, true)

    }

    private fun releasePlayer() {
        player?.apply {
            playbackPosition = currentPosition
            currentWindow = currentWindowIndex
            mPlayWhenReady = playWhenReady
            removeListener(playbackStateListener!!)
            release()
            player = null
        }
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        playerView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    private class PlaybackStateListener : Player.EventListener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Timber.d("changed state to $stateString")
        }
    }

    private class MyYoutubeExtractor(con: Context, val listener: (url: String) -> Unit) :
        YouTubeExtractor(con) {

        override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, vMeta: VideoMeta) {
            if (ytFiles != null) {
                val itag = 22
                val downloadUrl = ytFiles[itag].url
                Timber.d(downloadUrl)
                listener(downloadUrl)
            }
        }
    }

    companion object {
        const val ARG_ITEM_VIDEO_ID = "video_id"
    }

}