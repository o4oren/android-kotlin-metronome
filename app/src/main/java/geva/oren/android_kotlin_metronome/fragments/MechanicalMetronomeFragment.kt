package geva.oren.android_kotlin_metronome.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import androidx.fragment.app.Fragment
import geva.oren.android_kotlin_metronome.R
import kotlinx.android.synthetic.main.mechanical_metronome_fragment.*


/**
 * A simple [Fragment] subclass.
 * Use the [MechanicalMetronomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MechanicalMetronomeFragment : Fragment() {

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
        val an = RotateAnimation(-30.0f, 30.0f, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_SELF, 0.7f)
        an.duration = 1500 // duration in ms
        an.repeatCount = -1 // -1 = infinite repeated
        an.repeatMode = Animation.REVERSE // reverses each repeat
        an.fillAfter = true // keep rotation after animation

        metronomeArmView.animation = an
    }
}
