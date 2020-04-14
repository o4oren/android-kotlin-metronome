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
    private var interval = 1500
    private var isPlaying = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Metronome service started")
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .build()
        soundPool.load(this, R.raw.click, 1);
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, " has unbound")
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

    fun setInterval(bpm: Int) {
        interval = 60000 / bpm
    }

    private suspend fun startTicking() {
        while (isPlaying) {
            Thread.sleep(interval.toLong())
            Log.i(TAG, "Tick")
            soundPool.play(1, 1f, 1f, 1, 0, 1f)
        }
    }

    inner class MetronomeBinder : Binder() {
        fun getService() : MetronomeService {
            return this@MetronomeService
        }
    }
}

