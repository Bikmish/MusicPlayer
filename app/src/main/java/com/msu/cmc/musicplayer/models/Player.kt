package com.msu.cmc.musicplayer.models

import android.media.MediaPlayer

class Player {
    companion object {
        var inst: MediaPlayer? = null
        fun getInstance(): MediaPlayer? = inst ?: MediaPlayer()
        var currentIndex = -1
    }
}