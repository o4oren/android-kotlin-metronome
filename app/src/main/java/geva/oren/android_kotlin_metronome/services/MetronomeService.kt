package geva.oren.android_kotlin_metronome.services

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Binder
import android.os.IBinder
import android.util.Log
import geva.oren.android_kotlin_metronome.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * The Metronome service is responsible for playing, stoping and timing the ticks.
 * It is a started AND bound service, so it can persist and survive device rotation, and allow
 * The fragments to bind keep referencing it.
 */
class MetronomeService : Service() {
    private val binder = MetronomeBinder()
    private lateinit var soundPool: SoundPool
    private var tickJob: Job? = null
    private val TAG = "METRONOME_SERVICE"
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
        Log.i(TAG, "Metronome service created")
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .build()
        soundPool.load(this, R.raw.wood, 1);
        soundPool.load(this, R.raw.click, 1);
        soundPool.load(this, R.raw.ding, 1);
        soundPool.load(this, R.raw.beep, 1);
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "Metronome service destroyed")
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun play() {
        if (!isPlaying) {
            tickJob = coroutineScope.launch(Dispatchers.Default) {
                isPlaying = true
                tick()
            }
        }
    }

    fun pause() {
        isPlaying = false
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
    fun toggleEmphasis() {
        emphasis = !emphasis
    }

    /**
     * Rotates to the next rhythm
     */
    fun nextRhythm(): Rhythm {
        rhythm = rhythm.next()
        setInterval(bpm)
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

    private suspend fun tick() {
        var beat = 0

        while (isPlaying) {
            Thread.sleep(interval.toLong())
            if (beat % rhythm.value == 0) {
                for (t in tickListeners) t.onTick(interval)
            }
            val rate = if (emphasis && beat % beatsPerMeasure == 0)  1.4f else 1.0f
            if (isPlaying) soundPool.play(tone.value, 1f, 1f, 1, 0, rate)
            if (beat < beatsPerMeasure - 1)
                beat++
            else
                beat = 0
        }
    }

    fun addTickListener(tickListener: TickListener) {
        tickListeners.add(tickListener)
        Log.i(TAG, "number of listeners ${tickListeners.size}")
    }

    fun removeTickListener(tickListener: TickListener) {
        tickListeners.remove(tickListener)
        Log.i(TAG, "number of listeners ${tickListeners.size}")
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
            private val values = Tone.values()
        }

        fun next(): Tone {
            return Tone.values()[(this.ordinal + 1) % values.size]
        }
    }

    enum class Rhythm(val value: Int) {
        QUARTER(1),
        EIGHTH(2),
        SIXTEENTH(4);

        companion object {
            private val values = Rhythm.values()
        }

        fun next(): Rhythm {
            return Rhythm.values()[(this.ordinal + 1) % values.size]
        }
    }

    interface TickListener {
        fun onTick(interval: Int)
    }
}



