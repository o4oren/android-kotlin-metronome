package geva.oren.android_kotlin_metronome.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import geva.oren.android_kotlin_metronome.R

/**
 * Displays the beats and active beat dynamically
 */
class BeatsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var beats = 4
    set(beats) {
        field = beats
        createBeats()
    }
    private var highlightedBeat = -1
    private val emptyCircle = ContextCompat.getDrawable(context, R.drawable.beat_circle_empty)
    private val fullCircle = ContextCompat.getDrawable(context, R.drawable.beat_circle_full)
    private val marginParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

    init {
        orientation = HORIZONTAL
        marginParams.setMargins(5, 5, 5, 5)
        createBeats()
    }

    private fun createBeats() {
        for (i in 0 until beats) {
            val imageView = ImageView(context)
            imageView.setImageDrawable(emptyCircle)
            imageView.layoutParams = marginParams
            addView(imageView)
        }
    }

    fun nextBeat() {
        if (highlightedBeat != -1) {
            val prevBeat = getChildAt(highlightedBeat) as ImageView
            if (highlightedBeat == beats - 1)
                highlightedBeat = 0
            else highlightedBeat++
            val currentBeat = getChildAt(highlightedBeat) as ImageView
            prevBeat.setImageDrawable(emptyCircle)
            currentBeat.setImageDrawable(fullCircle)
        } else {
            highlightedBeat++
            val currentBeat = getChildAt(highlightedBeat) as ImageView
            currentBeat.setImageDrawable(fullCircle)
        }
    }

    fun resetBeats() {
        highlightedBeat = -1
        removeAllViews()
        createBeats()
    }

    fun inc(): Int {
        if (beats < 10)
            beats++
        return beats
    }

    fun dec(): Int {
        if (beats > 1)
            beats--
        return beats
    }
}