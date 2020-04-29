package geva.oren.android_kotlin_metronome.fragments

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import geva.oren.android_kotlin_metronome.R
import kotlinx.android.synthetic.main.info_fragment.*
import kotlinx.android.synthetic.main.digital_metronome_fragment.*

/**
 * A fragment containing about information
 */
class InfoFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.info_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        aboutText.movementMethod = LinkMovementMethod.getInstance()
    }
}
