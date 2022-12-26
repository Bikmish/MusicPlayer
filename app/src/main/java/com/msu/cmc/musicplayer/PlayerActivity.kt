package com.msu.cmc.musicplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.TimeUnit

class PlayerActivity : AppCompatActivity() {
    var titleTv: TextView? = null
    var currentTimeTv:TextView? = null
    var totalTimeTv:TextView? = null
    var seekBar: SeekBar? = null
    var pausePlay: ImageView? = null
    var nextBtn: ImageView? = null
    var previousBtn: ImageView? = null
    var musicIcon: ImageView? = null
    var trackList: ArrayList<TrackItem> = ArrayList()
    var currentTrack: TrackItem? = null;
    var mediaPlayer: MediaPlayer? = Player.getInstance()
    var angle = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        titleTv = findViewById(R.id.song_title);
        currentTimeTv = findViewById(R.id.current_time);
        totalTimeTv = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seek_bar);
        pausePlay = findViewById(R.id.pause_play);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);
        musicIcon = findViewById(R.id.music_icon_big);
        trackList = intent.getSerializableExtra("LIST") as ArrayList<TrackItem>
        titleTv!!.setSelected(true);

        initPlayer()

        this@PlayerActivity.runOnUiThread(object : Runnable {
            override fun run() {
                if (mediaPlayer != null) {
                    seekBar!!.setProgress(mediaPlayer!!.currentPosition)
                    currentTimeTv!!.setText(toMilliseconds(mediaPlayer!!.currentPosition.toString() + ""))
                    if (mediaPlayer!!.isPlaying) {
                        pausePlay!!.setImageResource(R.drawable.ic_pause)
                        musicIcon!!.setRotation(angle++.toFloat())
                    } else {
                        pausePlay!!.setImageResource(R.drawable.ic_play)
                        musicIcon!!.setRotation(angle.toFloat())
                    }
                }
                Handler().postDelayed(this, 100)
            }
        })

        seekBar!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer!!.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

    }

    fun initPlayer() {
        currentTrack = trackList.get(Player.currentIndex)
        titleTv!!.setText(currentTrack!!.title)
        totalTimeTv!!.setText(toMilliseconds(currentTrack!!.duration))

        pausePlay!!.setOnClickListener { v: View? -> pausePlay() }
        nextBtn!!.setOnClickListener { v: View? -> playNextSong() }
        previousBtn!!.setOnClickListener { v: View? -> playPreviousSong() }
        playMusic()
    }

    fun toMilliseconds(duration: String?): String? {
        val millis = duration!!.toLong()
        return java.lang.String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)
        )
    }

    private fun playMusic() {
        mediaPlayer!!.reset()
        try {
            mediaPlayer!!.setDataSource(currentTrack!!.path)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
            seekBar!!.progress = 0
            seekBar!!.max = mediaPlayer!!.duration
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playNextSong() {
        if (Player.currentIndex == trackList.size - 1) return
        Player.currentIndex += 1
        mediaPlayer!!.reset()
        initPlayer()
    }

    private fun playPreviousSong() {
        if (Player.currentIndex == 0) return
        Player.currentIndex -= 1
        mediaPlayer!!.reset()
        initPlayer()
    }

    private fun pausePlay() {
        if (mediaPlayer!!.isPlaying) mediaPlayer!!.pause() else mediaPlayer!!.start()
    }

}