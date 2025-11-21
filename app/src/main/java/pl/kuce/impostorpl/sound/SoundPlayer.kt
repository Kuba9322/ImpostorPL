package pl.kuce.impostorpl.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.annotation.RawRes

object SoundPlayer {

    var enabled: Boolean = true

    private var soundPool: SoundPool? = null
    private val loadedSounds = mutableMapOf<Int, Int>()

    fun init(context: Context, @RawRes vararg soundIds: Int) {
        if (soundPool != null) return  // już zainicjalizowany

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(audioAttributes)
            .build()

        soundIds.forEach { resId ->
            val soundId = soundPool!!.load(context, resId, 1)
            loadedSounds[resId] = soundId
        }
    }

    fun play(@RawRes resId: Int, force: Boolean = false) {
        if (!enabled && !force) return

        val pool = soundPool ?: return
        val soundId = loadedSounds[resId] ?: return
        pool.play(soundId, 1f, 1f, 1, 0, 1f)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        loadedSounds.clear()
    }
}
