package com.example.android_kotlin_metronome

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Binder
import android.os.IBinder
import android.util.Log
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
    private lateinit var tickJob: Job
    private val TAG = "METRONOME_SERVICE"
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var interval = 600
    private var isPlaying = false
    private val tickListeners = arrayListOf<TickListener>()
    private var sound = Sound.WOOD

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

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    fun play() {
        if(!isPlaying) {
            tickJob = coroutineScope.launch(Dispatchers.Default) {
                isPlaying = true
                startTicking()
            }
        }
    }

    fun pause() {
        tickJob.cancel()
        isPlaying = false
    }

    /**
     * Accepts bpm value an sets the interval in ms
     * @param bpm - the bpm value
     */
    fun setInterval(bpm: Int) {
        interval = 60000 / bpm
    }

    private suspend fun startTicking() {
        while (isPlaying) {
            Thread.sleep(interval.toLong())
            Log.i(TAG, "Tick")
            for (t in tickListeners) t.onTick(interval)
            soundPool.play(sound.value, 1f, 1f, 1, 0, 1f)
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
        fun getService() : MetronomeService {
            return this@MetronomeService
        }
    }

    enum class Sound(val value: Int) {
        WOOD(1),
        CLICK(2),
        DING(3),
        BEEP(4)
    }

    interface TickListener {
        fun onTick(interval: Int)
    }
}



