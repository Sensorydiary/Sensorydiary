package com.github.sensorydiary

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val spinnerSelectDateFormat =
            requireView().findViewById<Spinner>(R.id.spinnerSelectDateFormat)
        val buttonSaveSettings = requireView().findViewById<Button>(R.id.buttonSaveSettings)

        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.date_formats_array,
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerSelectDateFormat.adapter = spinnerAdapter

        val dayMonthYear = "DD-MM-YYYY"
        val monthDayYear = "MM-DD-YYYY"

        val preferences = this.activity!!.getPreferences(Context.MODE_PRIVATE)
        val editor = preferences.edit()

        if (preferences.getString("savedDateFormat", "") == "") {
            editor.putString("savedDateFormat", dayMonthYear)
            editor.apply()
        } else {
            if (preferences.getString("savedDateFormat", "") == monthDayYear) {
                spinnerSelectDateFormat.setSelection(1)
            }
        }

        buttonSaveSettings.setOnClickListener {
            val dateFormat = spinnerSelectDateFormat.selectedItem.toString()
            editor.putString("savedDateFormat", dateFormat)
            editor.apply()
            Toast.makeText(
                requireContext(), getString(R.string.settings_saved),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}