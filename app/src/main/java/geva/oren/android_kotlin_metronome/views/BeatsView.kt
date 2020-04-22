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
            resetBeats(true)
        }
    var isEmphasis = true
    set(isEmphasis: Boolean) {
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
            val drawable = getCircleDrawable(i, false)
            imageView.setImageDrawable(drawable)
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
            if (highlightedBeat == 1) {
                prevBeat.setImageDrawable(getCircleDrawable(highlightedBeat -1 , false))
            } else {
                prevBeat.setImageDrawable(getCircleDrawable(highlightedBeat -1 , false))
            }
            currentBeat.setImageDrawable(getCircleDrawable(highlightedBeat, true))
        } else {
            highlightedBeat++
            val currentBeat = getChildAt(highlightedBeat) as ImageView
            currentBeat.setImageDrawable(getCircleDrawable(highlightedBeat, true))
        }
    }

    private fun getCircleDrawable(beatIndex: Int, isFull: Boolean): Drawable? {
        return when (beatIndex) {
            0 -> when (isEmphasis) {
                true -> if (isFull) firstFullCircle else firstEmptyCircle
                false -> if (isFull) firstFullCircleNoEmphasis else firstEmptyCircleNoEmphasis
            }
            else -> if (isFull) fullCircle else emptyCircle
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