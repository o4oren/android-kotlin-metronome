package geva.oren.android_kotlin_metronome.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import geva.oren.android_kotlin_metronome.R
import geva.oren.android_kotlin_metronome.services.MetronomeService
import kotlinx.android.synthetic.main.mechanical_metronome_fragment.*


/**
 * A simple [Fragment] subclass.
 * Use the [MechanicalMetronomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MechanicalMetronomeFragment : AbstractMetronomeFragment() {

    private var duration = 1000L
    private var position = Position.CENTER
    private lateinit var MID_TO_RIGHT_ANIMATION: Animation
    private lateinit var RIGHT_TO_LEFT_ANIMATION: Animation
    private lateinit var LEFT_TO_RIGHT_ANIMATION: Animation


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.mechanical_metronome_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MID_TO_RIGHT_ANIMATION = AnimationUtils.loadAnimation(context, R.anim.mid_to_right)
        LEFT_TO_RIGHT_ANIMATION = AnimationUtils.loadAnimation(context, R.anim.left_to_right)
        RIGHT_TO_LEFT_ANIMATION = AnimationUtils.loadAnimation(context, R.anim.right_to_left)
    }

    private fun animateArm(duration: Long) {
        Log.i("Mechanical", "anitate with $duration")

        val animation = when(position) {
            Position.LEFT -> {
                this.position = Position.RIGHT
                LEFT_TO_RIGHT_ANIMATION
            }
            Position.RIGHT -> {
                this.position = Position.LEFT
                RIGHT_TO_LEFT_ANIMATION
            }
            else -> {
                this.position = Position.RIGHT
                MID_TO_RIGHT_ANIMATION
            }
        }

        animation.duration = duration
        metronomeArmView.startAnimation(animation)
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

    override fun onTick(interval: Int) {
        duration = interval.toLong()
        activity?.runOnUiThread() {
            animateArm(duration)
        }
    }

    enum class Position(val value: Int) {
        CENTER(0),
        LEFT(1),
        RIGHT(2);
    }
}


