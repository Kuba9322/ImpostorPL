package pl.kuce.impostorpl.sound

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes

object SoundPlayer {

    fun play(context: Context, @RawRes resId: Int) {
        val player = MediaPlayer.create(context, resId)
        player?.setOnCompletionListener {
            it.release()
        }
        player?.start()
    }
}
