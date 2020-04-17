package com.example.android_kotlin_metronome

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.metronome_fragment.*


/**
 * Main Metronome app fragment
 */
class MetronomeFragment : Fragment(), MetronomeService.TickListener {

    private var isBound = false
    private var metronomeService: MetronomeService? = null
    private val TAG = "METRONOME_FRAGMENT"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "on create view")
        return inflater.inflate(R.layout.metronome_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "View created")
        activity?.bindService(
            Intent(
                activity,
                MetronomeService::class.java
            ), mConnection, Context.BIND_AUTO_CREATE
        )
        isBound = true
        bpmText.text = "${bpmSeekbar?.progress}"

        bpmSeekbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                bpmText.text = "$progress"
                updateBpm(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        playButton.setOnClickListener() { play() }
        pauseButton.setOnClickListener() { pause() }
        rhythmButton.setOnClickListener() { nextRhythm() }
        soundButton.setOnClickListener() { nextSound() }
    }

    private fun nextSound() {
        val sound = metronomeService?.nextSound()
        Log.i("tet", sound?.name)
        soundText.text = sound?.name
    }


    private fun nextRhythm() {
        val rhythm = metronomeService?.nextRhythm()
        val drawable = when (rhythm) {
            MetronomeService.Rhythm.QUARTER -> R.drawable.ic_quarter_note
            MetronomeService.Rhythm.EIGHTH -> R.drawable.ic_eighth_note
            MetronomeService.Rhythm.SIXTEENTH -> R.drawable.ic_sixteenth_note
            null -> R.drawable.ic_quarter_note
        }

        rhythmImage.setImageDrawable(
            activity?.applicationContext?.let {
                ContextCompat.getDrawable(
                    it, drawable
                )
            })
    }

    private fun play() {
        metronomeService?.play()
        playButton.isEnabled = false
        pauseButton.isEnabled = true
    }

    private fun pause() {
        metronomeService?.pause()
        playButton.isEnabled = true
        pauseButton.isEnabled = false
    }

    private fun updateBpm(bpm: Int) {
        metronomeService?.setInterval(bpm)
    }

    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            metronomeService = (service as MetronomeService.MetronomeBinder).getService()
            metronomeService?.addTickListener(this@MetronomeFragment)
        }

        override fun onServiceDisconnected(className: ComponentName) {
            metronomeService = null
            isBound = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "On destroy")

        if (isBound) {
            metronomeService?.removeTickListener(this)
            // Detach our existing connection.
            activity!!.unbindService(mConnection)
            isBound = false
        }
    }

    override fun onTick(interval: Int) {
//        TODO("Not yet implemented")
    }
}
