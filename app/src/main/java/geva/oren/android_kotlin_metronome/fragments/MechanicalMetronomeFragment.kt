package geva.oren.android_kotlin_metronome.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import geva.oren.android_kotlin_metronome.R

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
}
