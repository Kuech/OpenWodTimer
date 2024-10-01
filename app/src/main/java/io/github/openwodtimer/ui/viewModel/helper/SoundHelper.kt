package io.github.openwodtimer.ui.viewModel.helper

import android.content.Context
import android.media.MediaPlayer
import io.github.openwodtimer.R

class SoundHelper(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    fun playG5BeepSound(){
        release()
        playBeepSound(R.raw.beep_g5)
    }

    fun playA6BeepSound(){
        release()
        playBeepSound(R.raw.beep_a6)
    }

    private fun playBeepSound(resId: Int) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, resId)
        }
        mediaPlayer?.start()
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
