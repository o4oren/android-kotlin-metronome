package geva.oren.android_kotlin_metronome.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class BeatsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var beats = 4

    init {
        orientation = HORIZONTAL
        TODO("add children")
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