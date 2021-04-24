package com.craiovadata.android.mytoons

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.craiovadata.android.mytoons.data.MyYoutubeExtractor
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import timber.log.Timber


class PlayerActivity : AppCompatActivity() {
    private var playbackStateListener: PlaybackStateListener? = null
    private var playerView: PlayerView? = null
    private var player: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        val videoId = intent.getStringExtra(ARG_ITEM_ID)
            ?: return finish()
        playerView = findViewById(R.id.video_view)

        hideSystemUi()
        val videoUrl = getString(R.string.youtube_url, videoId)
        Timber.d(videoUrl)
        // extract & play
        MyYoutubeExtractor(this) { downloadUrl ->
            initializePlayer(downloadUrl)
            playbackStateListener = PlaybackStateListener(player, window)
            play()
        }.extract(videoUrl, true, true)
    }

    override fun onStop() {
        super.onStop()
        pausePlayer()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        pausePlayer()
        releasePlayer()
    }

    private fun initializePlayer(downloadUrl: String) {
        val trackSelector = DefaultTrackSelector(this)
        trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSizeSd())
        player = SimpleExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()
        val mediaItem = MediaItem.fromUri(downloadUrl)
        player?.apply {
            setMediaItem(mediaItem)
            prepare()
        }
        playerView!!.player = player
    }

    private fun play() {
        player?.apply {
            playWhenReady = true
            addListener(playbackStateListener!!)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun releasePlayer() {
        player?.apply {
            removeListener(playbackStateListener!!)
            release()
            player = null
        }
    }

    private fun pausePlayer() {
        player?.apply {
            playWhenReady = false
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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

    private class PlaybackStateListener(val player: SimpleExoPlayer?, val window: Window) :
        Player.EventListener {
        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            super.onPlayWhenReadyChanged(playWhenReady, reason)
            Timber.d("play when ready changed:  %s", playWhenReady)
            if (playWhenReady) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }else{
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            if (playbackState == PlaybackStateCompat.STATE_PLAYING) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                Timber.d("player playing")
            } else{
                Timber.d("player stopped")
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            }
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Timber.d("changed state to $stateString")
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            super.onPlayerError(error)
            player?.stop()
        }
    }

    companion object {
        const val ARG_ITEM_ID = "video_id"
    }

}