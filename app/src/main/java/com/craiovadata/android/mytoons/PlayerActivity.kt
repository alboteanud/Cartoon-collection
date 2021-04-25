package com.craiovadata.android.mytoons

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.craiovadata.android.mytoons.data.MyYoutubeExtractor
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import timber.log.Timber


class PlayerActivity : AppCompatActivity() {

    private var playerView: PlayerView? = null
    private var player: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        val videoId = intent.getStringExtra(ARG_ITEM_ID)
        if (videoId== null){
            Toast.makeText(this, getString(R.string.error_invalid_video_id), Toast.LENGTH_LONG).show()
            return finish()
        }
        playerView = findViewById(R.id.video_view)
        findViewById<ImageButton>(R.id.exo_close).setOnClickListener {
            pausePlayer()
            releasePlayer()
            finish()
        }

        hideSystemUi()
        val videoUrl = getString(R.string.youtube_url, videoId)
        Timber.d(videoUrl)
        // extract & play
        MyYoutubeExtractor(this) { downloadUrl ->
            if (downloadUrl!=null){
                initializePlayer(downloadUrl)
                startPlayer()
            } else {
                Toast.makeText(this, getString(R.string.error_cannot_play), Toast.LENGTH_LONG).show()
                finish()
            }

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

    private fun startPlayer() {
        player?.apply {
            playWhenReady = true
            addListener(stateListener)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun releasePlayer() {
        player?.apply {
            removeListener(stateListener)
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

    private val stateListener = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            if (playbackState == PlaybackStateCompat.STATE_PLAYING && playWhenReady) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else{
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            super.onPlayWhenReadyChanged(playWhenReady, reason)
            Timber.d("play when ready changed:  %s", playWhenReady)
            if (playWhenReady) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
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