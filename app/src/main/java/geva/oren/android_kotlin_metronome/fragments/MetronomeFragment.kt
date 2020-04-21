package geva.oren.android_kotlin_metronome.fragments

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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import geva.oren.android_kotlin_metronome.R
import geva.oren.android_kotlin_metronome.services.MetronomeService
import geva.oren.android_kotlin_metronome.views.RotaryKnobView
import geva.oren.android_kotlin_metronome.views.TonesView
import kotlinx.android.synthetic.main.metronome_fragment.*


/**
 * Main Metronome app fragment
 */
class MetronomeFragment : Fragment(),
    MetronomeService.TickListener, RotaryKnobView.RotaryKnobListener {

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
        bindService()

        playButton.setOnClickListener() { this.play() }
        pauseButton.setOnClickListener() { this.pause() }
        rhythmButton.setOnClickListener() { this.nextRhythm() }
        toneButton.setOnClickListener() { this.nextTone() }
        emphasisButton.setOnClickListener() {v ->
            val isEmphasis = metronomeService?.toggleEmphasis()
            beatsView.isEmphasis =  isEmphasis!!
        }


        val knob = digitalMetronomeLayout.getChildAt(digitalMetronomeLayout.childCount - 1) as RotaryKnobView
        knob.id = 12345
//
        val rotaryKnob = digitalMetronomeLayout.findViewById<RotaryKnobView>(12345)
        rotaryKnob.listener = this
        setBpmText(rotaryKnob.value)
    }

    private fun bindService() {
        activity?.bindService(
            Intent(
                activity,
                MetronomeService::class.java
            ), mConnection, Context.BIND_AUTO_CREATE
        )
        isBound = true
    }

    private fun setBpmText(bpm: Int) {
        bpmText.text = if (bpm >= 100) bpm.toString() else " ${bpm.toString()}"
    }

    private fun nextTone() {
        val tone = metronomeService?.nextTone()
        if (tone != null) {
            tonesView.selectTone(tone)
        }
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
        beatsView.resetBeats(true)
    }

    private fun play() {
        beatsView.resetBeats(true)
        metronomeService?.play()
    }

    private fun pause() {
        metronomeService?.pause()
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

    /**
     * RotaryListener interface implementation
     */
    override fun onRotate(value: Int) {
        val bpm = value
        setBpmText(bpm)
        metronomeService?.setInterval(bpm)
    }

    override fun onTick(interval: Int) {
        if (metronomeService?.isPlaying!!)
            activity?.runOnUiThread() {beatsView.nextBeat()}
    }
}
