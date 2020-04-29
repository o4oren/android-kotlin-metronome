package geva.oren.android_kotlin_metronome.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import geva.oren.android_kotlin_metronome.R
import geva.oren.android_kotlin_metronome.services.MetronomeService
import geva.oren.android_kotlin_metronome.views.RotaryKnobView
import kotlinx.android.synthetic.main.digital_metronome_fragment.*

/**
 * Main Metronome app fragment
 */
class DigitalMetronomeFragment : AbstractMetronomeFragment(), RotaryKnobView.RotaryKnobListener {

    private var lastTapMilis: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.digital_metronome_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playButton.setOnClickListener { this.play() }
        pauseButton.setOnClickListener { this.pause() }
        rhythmButton.setOnClickListener { this.nextRhythm() }
        toneButton.setOnClickListener { this.nextTone() }
        tapTempoButton.setOnClickListener { this.tapTempAction() }
        emphasisButton.setOnClickListener {
            val isEmphasis = metronomeService?.toggleEmphasis()
            beatsView.isEmphasis =  isEmphasis!!
        }
        beatsUpButton.setOnClickListener { this.updateBeatsUp() }
        beatsDownButton.setOnClickListener { this.updateBeatsDown() }
        rotaryKnob.listener = this
        rotaryKnob.setKnobPositionByValue(100)
        setBpmText(rotaryKnob.value)
    }

    private fun updateBeatsUp() {
        val beats = metronomeService?.setBeatsUp()
        beatsView.beatsPerMeasure = beats!!
    }

    private fun updateBeatsDown() {
        val beats = metronomeService?.setBeatsDown()
        beatsView.beatsPerMeasure = beats!!
    }

    private fun tapTempAction() {
        val currentMilis = System.currentTimeMillis()
        val difference = currentMilis - lastTapMilis
        val calculatedBpm = (60000 / difference).toInt()
        val bpm = metronomeService?.setInterval(calculatedBpm)
        bpmText.text = bpm.toString()
        lastTapMilis = currentMilis
    }

    private fun setBpmText(bpm: Int) {
        bpmText.text = if (bpm >= 100) "$bpm" else " $bpm"
    }

    private fun nextTone() {
        val tone = metronomeService?.nextTone()
        if (tone != null) {
            tonesView.selectTone(tone)
        }
    }

    private fun nextRhythm() {
        val drawable = when (metronomeService?.nextRhythm()) {
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

    /**
     * RotaryListener interface implementation
     */
    override fun onRotate(value: Int) {
        setBpmText(value)
        metronomeService?.setInterval(value)
    }

    override fun onTick(interval: Int) {
        if (this.isVisible  && metronomeService?.isPlaying!!)
            activity?.runOnUiThread {beatsView.nextBeat()}
    }
}
