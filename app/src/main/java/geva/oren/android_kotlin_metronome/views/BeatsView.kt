package geva.oren.android_kotlin_metronome.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import geva.oren.android_kotlin_metronome.R

const val MAX_BEAT = 9

/**
 * Displays the beats and active beat dynamically
 */
class BeatsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var beatsPerMeasure = 4
        set(beats) {
            field = beats
            resetBeats(true)
        }
    var isEmphasis = true
    set(isEmphasis) {
        field = isEmphasis
        resetBeats(false)
    }
    private var highlightedBeat = -1
    private val firstEmptyCircle =
        ContextCompat.getDrawable(context, R.drawable.first_beat_circle_empty)
    private val firstFullCircle =
        ContextCompat.getDrawable(context, R.drawable.first_beat_circle_full)
    private val firstEmptyCircleNoEmphasis =
        ContextCompat.getDrawable(context, R.drawable.first_beat_circle_empty_no_emphasis)
    private val firstFullCircleNoEmphasis =
        ContextCompat.getDrawable(context, R.drawable.first_beat_circle_full_no_emphasis)
    private val offCircle =
        ContextCompat.getDrawable(context, R.drawable.beat_circle_off)
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
        for (i in 0 until MAX_BEAT) {
            val imageView = ImageView(context)
            val drawable = getCircleDrawable(i, false)
            imageView.setImageDrawable(drawable)
            imageView.layoutParams = marginParams
            addView(imageView)
        }
    }

    fun nextBeat() {
        if (highlightedBeat != -1) {
            val prevBeat = getChildAt(highlightedBeat) as ImageView
            val pervBeatIndex = highlightedBeat
            if (highlightedBeat == beatsPerMeasure - 1)
                highlightedBeat = 0
            else
                highlightedBeat++
            val currentBeat = getChildAt(highlightedBeat) as ImageView
            prevBeat.setImageDrawable(getCircleDrawable(pervBeatIndex , false))
            currentBeat.setImageDrawable(getCircleDrawable(highlightedBeat, true))
        } else {
            highlightedBeat++
            val currentBeat = getChildAt(highlightedBeat) as ImageView
            currentBeat.setImageDrawable(getCircleDrawable(highlightedBeat, true))
        }
    }

    private fun getCircleDrawable(beatIndex: Int, isFull: Boolean): Drawable? {
        Log.d("BEATS", "i: $beatIndex, full: $isFull")
        return when (beatIndex) {
            0 -> when (isEmphasis) {
                true -> if (isFull) firstFullCircle else firstEmptyCircle
                false -> if (isFull) firstFullCircleNoEmphasis else firstEmptyCircleNoEmphasis
            }
            in 1 until beatsPerMeasure -> if (isFull) fullCircle else emptyCircle
            else -> offCircle
        }
    }

    /**
     * Resets the beats view
     * @param resetHighlightedBeat - indicates if the highlighted beat should be reset to zero
     */
    fun resetBeats(resetHighlightedBeat: Boolean) {
        if (resetHighlightedBeat) highlightedBeat = -1
        removeAllViews()
        createBeats()
    }
}