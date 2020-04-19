package geva.oren.android_kotlin_metronome.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
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
    var isEmphasis = true
    private var highlightedBeat = -1
    private val firstEmptyCircle =
        ContextCompat.getDrawable(context, R.drawable.first_beat_circle_empty_no_emphasis)
    private val firstFullCircle =
        ContextCompat.getDrawable(context, R.drawable.first_beat_circle_full)
    private val firstEmptyCircleNoEmphasis =
        ContextCompat.getDrawable(context, R.drawable.first_beat_circle_empty_no_emphasis)
    private val firstFullCircleNoEmphasis =
        ContextCompat.getDrawable(context, R.drawable.first_beat_circle_full_no_emphasis)
    private val emptyCircle = ContextCompat.getDrawable(context, R.drawable.beat_circle_empty)
    private val fullCircle = ContextCompat.getDrawable(context, R.drawable.beat_circle_full)
    private val marginParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

    init {
        orientation = HORIZONTAL

        gravity = Gravity.CENTER
        marginParams.setMargins(5, 5, 5, 5)
        createBeats()
    }

    private fun createBeats() {
        for (i in 0 until beats) {
            val imageView = ImageView(context)
            val drawable = if (i == 0)
                if (isEmphasis) this.firstEmptyCircle else this.firstEmptyCircleNoEmphasis
            else
                emptyCircle

            imageView.setImageDrawable(drawable)
            imageView.layoutParams = marginParams
            addView(imageView)
        }
    }

    private fun getDrawable(beat: Int): Drawable? {

        return this.emptyCircle
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
            if (highlightedBeat == 0)
                currentBeat.setImageDrawable(firstFullCircle)
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