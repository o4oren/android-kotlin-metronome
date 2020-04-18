package geva.oren.android_kotlin_metronome.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import geva.oren.android_kotlin_metronome.R

class BeatsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var beats = 4
    val emptyCircle = ContextCompat.getDrawable(context, R.drawable.beat_circle_empty)
    val fullCircle = ContextCompat.getDrawable(context, R.drawable.beat_circle_full)
    var marginParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    init {
        orientation = HORIZONTAL
        marginParams.setMargins(5, 5, 5, 5)
        createBeats()
    }

    private fun createBeats() {
        for (i in 0 until beats) {
            var imageView = ImageView(context)
            val drawableToSet = if (i==0) fullCircle else emptyCircle
            imageView.setImageDrawable(drawableToSet)
            imageView.layoutParams = marginParams
            addView(imageView)
        }
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