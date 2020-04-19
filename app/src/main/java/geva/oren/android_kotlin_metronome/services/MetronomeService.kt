package geva.oren.android_kotlin_metronome.services

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Binder
import android.os.IBinder
import android.util.Log
import geva.oren.android_kotlin_metronome.R
import kotlinx.coroutines.*

/**
 * The Metronome service is responsible for playing, stoping and timing the ticks.
 * It is a started AND bound service, so it can persist and survive device rotation, and allow
 * The fragments to bind keep referencing it.
 */
class MetronomeService : Service() {
    private val binder = MetronomeBinder()
    private lateinit var soundPool: SoundPool
    private var tickJob: Job? = null
    private val tag = "METRONOME_SERVICE"
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var bpm = 100
    private var beatsPerMeasure = 4
    private var interval = 600
    var isPlaying = false
        private set
    private val tickListeners = arrayListOf<TickListener>()
    private var tone =
        Tone.WOOD
    private var rhythm =
        Rhythm.QUARTER
    var emphasis = true

    override fun onCreate() {
        super.onCreate()
        Log.i(tag, "Metronome service created")
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .build()
        soundPool.load(this, R.raw.wood, 1)
        soundPool.load(this, R.raw.click, 1)
        soundPool.load(this, R.raw.ding, 1)
        soundPool.load(this, R.raw.beep, 1)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(tag, "Metronome service destroyed")
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun play() {
        tickJob = coroutineScope.launch(Dispatchers.Default) {
            isPlaying = true
            var tick = 0
            while (isPlaying && isActive) {
                var rate = 1f
                delay(interval.toLong())
                if (tick % rhythm.value == 0) {
                    for (t in tickListeners)
                        t.onTick(interval)
                    if (emphasis && tick == 0)
                        rate = 1.4f
                }
                if (isPlaying) soundPool.play(tone.value, 1f, 1f, 1, 0, rate)
                if (tick < beatsPerMeasure * rhythm.value - 1)
                    tick++
                else
                    tick = 0
            }
        }
    }

    fun pause() {
//        isPlaying = false
        tickJob?.cancel()
    }

    /**
     * Accepts bpm value an sets the interval in ms
     * @param bpm - the bpm value
     */
    fun setInterval(bpm: Int) {
        this.bpm = bpm
        interval = 60000 / (bpm * rhythm.value)
    }

    /**
     * Toggle emphasis on/off
     */
    fun toggleEmphasis(): Boolean {
        emphasis = !emphasis
        return emphasis
    }

    /**
     * Rotates to the next rhythm
     */
    fun nextRhythm(): Rhythm {
        pause()
        Log.i(tag, "is not active anymore")
        rhythm = rhythm.next()
        setInterval(bpm)
        play()
        return rhythm
    }

    /**
     * Rotates to the next sound
     */
    fun nextTone(): Tone {
        tone = tone.next()
        setInterval(bpm)
        return tone
    }

    fun addTickListener(tickListener: TickListener) {
        tickListeners.add(tickListener)
        Log.i(tag, "number of listeners ${tickListeners.size}")
    }

    fun removeTickListener(tickListener: TickListener) {
        tickListeners.remove(tickListener)
        Log.i(tag, "number of listeners ${tickListeners.size}")
    }

    inner class MetronomeBinder : Binder() {
        fun getService(): MetronomeService {
            return this@MetronomeService
        }
    }

    enum class Tone(val value: Int) {
        WOOD(1),
        CLICK(2),
        DING(3),
        BEEP(4);

        companion object {
            private val values = values()
        }

        fun next(): Tone {
            return values()[(this.ordinal + 1) % values.size]
        }
    }

    enum class Rhythm(val value: Int) {
        QUARTER(1),
        EIGHTH(2),
        SIXTEENTH(4);

        companion object {
            private val values = values()
        }

        fun next(): Rhythm {
            return values()[(this.ordinal + 1) % values.size]
        }
    }

    interface TickListener {
        fun onTick(interval: Int)
    }
}



