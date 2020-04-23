package geva.oren.android_kotlin_metronome.views

import android.content.Context
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.ImageView.ScaleType
import android.widget.RelativeLayout
import androidx.core.view.GestureDetectorCompat
import geva.oren.android_kotlin_metronome.R
import kotlinx.android.synthetic.main.rotary_knob_view.view.*
import kotlin.math.atan2


class RotaryKnobView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), GestureDetector.OnGestureListener {
    private val gestureDetector: GestureDetectorCompat
    private var maxValue = 99
    private var minValue = 0
    var listener: RotaryKnobListener? = null
    var value = 130
    private var knobDrawable: Drawable? = null

    interface RotaryKnobListener {
        fun onRotate(value: Int)
    }

    init {
        this.maxValue = maxValue + 1 // To allow reaching last defined value

        LayoutInflater.from(context)
            .inflate(R.layout.rotary_knob_view, this, true)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.RotaryKnobView,
            0,
            0
        ).apply {
            try {
                minValue = getInt(R.styleable.RotaryKnobView_minValue, 40)
                maxValue = getInt(R.styleable.RotaryKnobView_maxValue, 220) + 1
                value = getInt(R.styleable.RotaryKnobView_initialValue, 130)
                knobDrawable = getDrawable(R.styleable.RotaryKnobView_knobDrawable)
                knobImageView.setImageDrawable(knobDrawable)
            } finally {
                recycle()
            }
        }
        gestureDetector = GestureDetectorCompat(context, this)
    }

    /**
     * Calculate the angle from x,y coordinates of the touch event
     * explanation - 0,0 in android is top left corner
     * divided by height and width we get 0 - 1 values (0,0) top left, (1,1) bottom right
     * while x's direction is correct - going up from left to right, y's isn't, as it's
     * smallest value is at the top, so we reverse it by subtracting y from 1
     * Now x is going from 0 (most left) to 1 (most right)
     * And Y is going from 0 (most downwards) to 1 (most upwards.
     * Lastly, we need to bring 0,0 to the middle - so subtract 0.5 from both.
     * now 0,0 is in the middle, 0, 0.5 is at 12 o'clock and 0.5, 0 is at 3 o'clock
     * Now that we have the coordinates in proper cartesian coordinate system, to calculate theta,
     * we should call atan2(y,x).
     * However, theta is the angle between the x axis and the point.
     * Which means it rises as we turn counter clockwise. And in addition, we want the "north"
     * to be at 12 o'clock. So we reverse the direction of the angle by prefixing it with a -
     * and add 90 to move the "zero degrees" point north (taking care to handling the range between
     * 180 and 270 degrees, bringing them to their proper values of -180 .. -90 by adding 360 to the
     * value.
     *
     * @param x - x coordinate of the touch event
     * @param y - y coordinate of the touch event
     * @return
     */
    private fun calculateAngle(x: Float, y: Float): Float {
        val x = (x / width.toFloat()) - 0.5
        val y = ( 1 - y / height.toFloat()) - 0.5
        var angle = -(Math.toDegrees(atan2(y, x)) )
            .toFloat() + 90
        if (angle > 180) angle -= 360;
        Log.d("KNOB", "x: $x y: $y")
        Log.i("KNOB", "angle: $angle")

        return angle
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(event))
            true
        else
            super.onTouchEvent(event)
    }

    private fun setKnobPosition(deg: Float) {
        val matrix = Matrix()
        knobImageView.scaleType = ScaleType.MATRIX
        matrix.postRotate(deg, width.toFloat() / 2 , height.toFloat() / 2)
        knobImageView.imageMatrix = matrix
    }

    /**
     *
     * We're only interested in e2 - the coordinates of the end movement.
     * We calculate the polar angle (Theta) from these coordinates and use these to animate the
     * knob movement and calculate the value
     */
    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float)
            : Boolean {

        val rotationDegrees = calculateAngle(e2.x, e2.y)
        Log.i("KNOB", "rotation degrees ${(rotationDegrees)}")

        // use only -150 to 150 range (knob min/max points
        if (rotationDegrees >= -150 && rotationDegrees <= 150) {
            setKnobPosition(rotationDegrees)

            // Calculate rotary value
            // The range is the 300 degrees between -150 and 150, so we'll add 150 to adjust the
            // range to 0 - 300
            val valueRangeDegrees = rotationDegrees + 150
                val divider = 300f / (maxValue - minValue) // scale the results to the passed range
                value = ((valueRangeDegrees / divider) + minValue).toInt()
                if (listener != null) listener!!.onRotate(value)
        }
        return true
    }

    // Unused. Needed for GestureDetector implementation
    override fun onDown(event: MotionEvent): Boolean {
        return true
    }
    // Unused. Needed for GestureDetector implementation
    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return true
    }

    // Unused. Needed for GestureDetector implementation
    override fun onFling(arg0: MotionEvent, arg1: MotionEvent, arg2: Float, arg3: Float)
            : Boolean {
        return false
    }

    // Unused. Needed for GestureDetector implementation
    override fun onLongPress(e: MotionEvent) {}

    // Unused. Needed for GestureDetector implementation
    override fun onShowPress(e: MotionEvent) {}
}
