package com.github.sensorydiary

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddDayFragment : Fragment() {

    private suspend fun addDayStressors(checkBoxList: List<CheckBox>, dayId: Int) {
        val database = AppDatabase.getInstance(requireContext())
        for (checkBox in checkBoxList) {
            if (checkBox.isChecked) {
                // With zero-based indexing, the id of the stressor needs to be increased by 1 to be accurate
                val stressorId = checkBoxList.indexOf(checkBox) + 1
                val dayStressor = DayStressor(dayId, stressorId)
                database.dayStressorDao().addDayStressor(dayStressor)
            }
        }
    }

    private fun dateAlreadyExistsDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.date_already_exists))
        builder.setMessage(getString(R.string.date_already_exists_instructions))
        builder.setPositiveButton(getString(R.string.ok)) { _, _ ->

        }
        val alertDialog: AlertDialog = builder.show()
        val buttonPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        buttonPositive.textSize = 20F
    }

    private fun futureDateEnteredDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.future_date_entered))
        builder.setMessage(getString(R.string.future_date_entered_instructions))
        builder.setPositiveButton(getString(R.string.ok)) { _, _ ->

        }
        val alertDialog: AlertDialog = builder.show()
        val buttonPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        buttonPositive.textSize = 20F
    }

    private fun incorrectDateFormatDialog(dateFormat: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.incorrect_date_format))
        builder.setMessage(getString(R.string.incorrect_date_format_instructions, dateFormat))
        builder.setPositiveButton(getString(R.string.ok)) { _, _ ->

        }
        val alertDialog: AlertDialog = builder.show()
        val buttonPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        buttonPositive.textSize = 20F
    }

    private fun incorrectStressScoreDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.incorrect_stress_score_format))
        builder.setMessage(getString(R.string.incorrect_stress_score_format_instructions))
        builder.setPositiveButton(getString(R.string.ok)) { _, _ ->

        }
        val alertDialog: AlertDialog = builder.show()
        val buttonPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        buttonPositive.textSize = 20F
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_day, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: AddDayFragmentArgs by navArgs()
        val personId = args.personId

        val editTextEnterDate = requireView().findViewById<EditText>(R.id.editTextEnterDate)
        val editTextStressScore = requireView().findViewById<EditText>(R.id.editTextAddStressScore)
        val editTextAddNotes = requireView().findViewById<EditText>(R.id.editTextAddNotes)
        val buttonAddNewDay = requireView().findViewById<Button>(R.id.buttonAddNewDay)

        val dayMonthYear = "DD-MM-YYYY"
        val monthDayYear = "MM-DD-YYYY"

        val preferences = this.activity!!.getPreferences(Context.MODE_PRIVATE)
        lateinit var enteredFormat: DateTimeFormatter

        if (preferences.getString("savedDateFormat", "") == monthDayYear) {
            editTextEnterDate.hint = getString(R.string.month_day_year_format)
            enteredFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy")
        } else if(preferences.getString("savedDateFormat", "") == dayMonthYear) {
            editTextEnterDate.hint = getString(R.string.day_month_year_format)
            enteredFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        } else {
            val editor = preferences.edit()
            editor.putString("savedDateFormat", dayMonthYear)
            editTextEnterDate.hint = getString(R.string.day_month_year_format)
            enteredFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            editor.apply()
        }

        // Make the EditText scrollable within the ScrollView
        editTextAddNotes.setOnTouchListener(View.OnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_SCROLL -> {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                    return@OnTouchListener true
                }
            }
            false
        })

        val checkBoxAddSight = requireView().findViewById<CheckBox>(R.id.checkBoxAddSight)
        val checkBoxAddSmell = requireView().findViewById<CheckBox>(R.id.checkBoxAddSmell)
        val checkBoxAddSound = requireView().findViewById<CheckBox>(R.id.checkBoxAddSound)
        val checkBoxAddTaste = requireView().findViewById<CheckBox>(R.id.checkBoxAddTaste)
        val checkBoxAddTouch = requireView().findViewById<CheckBox>(R.id.checkBoxAddTouch)

        val checkBoxList = listOf(
            checkBoxAddSight,
            checkBoxAddSmell,
            checkBoxAddSound,
            checkBoxAddTaste,
            checkBoxAddTouch
        )

        buttonAddNewDay.setOnClickListener {
            val databaseFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val enteredString = editTextEnterDate.text.toString()
            try {
                val date = LocalDate.parse(databaseFormat.format(enteredFormat.parse(enteredString)))
                if (!date.isAfter(LocalDate.now())) {
                    try {
                        val stressScore = editTextStressScore.text.toString().toInt()
                        if (stressScore in 1..10) {
                            if (personId != -1) {
                                lifecycleScope.launch {
                                    val database = AppDatabase.getInstance(requireContext())
                                    val dateString = date.toString()
                                    val countDate: Int =
                                        database.dayDao().checkIfDateExists(personId, dateString)
                                    if (countDate != 0) {
                                        dateAlreadyExistsDialog()
                                    } else {
                                        val notes = editTextAddNotes.text.toString()
                                        val day =
                                            Day(null, personId, dateString, stressScore, notes)
                                        database.dayDao().addDay(day)
                                        val dayId = database.dayDao().getDayId(personId, dateString)
                                        addDayStressors(checkBoxList, dayId)
                                        Toast.makeText(
                                            requireContext(),
                                            getString(R.string.day_successfully_added),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                        } else {
                            incorrectStressScoreDialog()
                        }
                    } catch (e: NumberFormatException) {
                        incorrectStressScoreDialog()
                    }

                } else {
                    futureDateEnteredDialog()
                }
            } catch (e: Exception) {
                incorrectDateFormatDialog(editTextEnterDate.hint.toString())
            }
        }
    }
}