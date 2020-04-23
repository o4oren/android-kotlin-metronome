package geva.oren.android_kotlin_metronome.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import geva.oren.android_kotlin_metronome.services.MetronomeService
import geva.oren.android_kotlin_metronome.R

class TonesView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val offColor = ContextCompat.getColor(context,
        R.color.faintDigitalText
    )
    private val onColor = ContextCompat.getColor(context,
        R.color.digitalText
    )

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.tones_view, this, true)

        orientation = VERTICAL
    }

    fun selectTone(tone: MetronomeService.Tone) {
        for (child in children) {
            if (child is TextView) {
                if (child.text == tone.name)
                    child.setTextColor(onColor)
                else
                    child.setTextColor(offColor)
            }
        }
    }
}