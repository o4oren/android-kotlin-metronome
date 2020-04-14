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
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.metronome_fragment.*


/**
 * Main Metronome app fragment
 */
class MetronomeFragment : Fragment() {

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

        bpmText.text = bpmSeekbar.progress.toString()

        bpmSeekbar?.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                bpmText.text = "BPM: $progress"
                updateBpm(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        playButton.setOnClickListener() { play() }
        pauseButton.setOnClickListener() { pause() }

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
            // Detach our existing connection.
            activity!!.unbindService(mConnection)
            isBound = false
        }
    }
}
