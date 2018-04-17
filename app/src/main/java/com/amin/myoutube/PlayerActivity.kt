package com.amin.myoutube

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView

class PlayerActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener{

    companion object {
        const val RECOVERY_DIALOG_REQUEST = 1
        const val DEVELOPER_KEY = "AIzaSyCb1XOPX4u550TeFYZk6bWkdH3_fnJfVe8"

        lateinit var youTubePlayer: YouTubePlayer
    }

    private lateinit var youTubePlayerView: YouTubePlayerView
    //private lateinit var youTubePlayer: YouTubePlayer
    private lateinit var videoId: String

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider, player: YouTubePlayer, wasRestored: Boolean) {
        println("onInitializationSuccess $wasRestored  $player")
        youTubePlayer = player
        youTubePlayer.cueVideo(videoId)
        youTubePlayer.play()
        youTubePlayer.setPlaylistEventListener(OnPlaylistEventListener())
        youTubePlayer.setPlayerStateChangeListener(OnPlayerStateChangeListener())
        youTubePlayer.setPlaybackEventListener(OnPlaybackEventListener())
    }

    override fun onInitializationFailure(provider: YouTubePlayer.Provider, errorReason: YouTubeInitializationResult) {
        println("onInitializationFailure $errorReason")
        if (errorReason.isUserRecoverableError) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show()
        } else {
            val errorMessage = String.format(getString(R.string.error_player), errorReason.toString())
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_controls)

        videoId = intent.getStringExtra("id")
        println("onCreate $videoId")
        youTubePlayerView = findViewById(R.id.youtube_view)
        youTubePlayerView.initialize(DEVELOPER_KEY, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("onActivityResult $requestCode")
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            youTubePlayerView.initialize(DEVELOPER_KEY, this)
        }
    }

    class OnPlaylistEventListener: YouTubePlayer.PlaylistEventListener {
        override fun onPlaylistEnded() {
            println("onPlaylistEnded $youTubePlayer")
            youTubePlayer.play()
        }

        override fun onPrevious() {
            println("onPrevious")
        }

        override fun onNext() {
            println("onNext")
        }
    }

    class OnPlayerStateChangeListener: YouTubePlayer.PlayerStateChangeListener {
        override fun onAdStarted() {
            println("onAdStarted")
        }

        override fun onLoading() {
            println("onLoading")
        }

        override fun onVideoStarted() {
            println("onVideoStarted")
        }

        override fun onLoaded(videoId: String) {
            println("onLoaded  $videoId  $youTubePlayer")
            youTubePlayer.play()
        }

        override fun onVideoEnded() {
            println("onVideoEnded")
        }

        override fun onError(reason: YouTubePlayer.ErrorReason) {
            println("onError  $reason")
        }
    }

    class OnPlaybackEventListener: YouTubePlayer.PlaybackEventListener {
        override fun onSeekTo(endPositionMillis: Int) {
            println("onSeekTo  $endPositionMillis")
        }

        override fun onBuffering(isBuffering: Boolean) {
            println("onBuffering  $isBuffering")
        }

        override fun onPlaying() {
            println("onPlaying")
        }

        override fun onStopped() {
            println("onStopped")
        }

        override fun onPaused() {
            println("onPaused")
        }

    }
}