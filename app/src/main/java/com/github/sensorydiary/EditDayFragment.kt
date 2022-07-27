package com.github.sensorydiary

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EditDayFragment : Fragment() {

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
        builder.setMessage(getString(R.string.edit_date_already_exists_instructions))
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

    // These are declared here because they are used in more than one method
    private lateinit var checkBoxEditSight: CheckBox
    private lateinit var checkBoxEditSmell: CheckBox
    private lateinit var checkBoxEditSound: CheckBox
    private lateinit var checkBoxEditTaste: CheckBox
    private lateinit var checkBoxEditTouch: CheckBox
    private lateinit var editTextEditDate: EditText
    private lateinit var editTextEditStressScore: EditText
    private lateinit var editTextEditNotes: EditText

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean("checkBoxEditSight", checkBoxEditSight.isChecked)
        outState.putBoolean("checkBoxEditSmell", checkBoxEditSmell.isChecked)
        outState.putBoolean("checkBoxEditSound", checkBoxEditSound.isChecked)
        outState.putBoolean("checkBoxEditTaste", checkBoxEditTaste.isChecked)
        outState.putBoolean("checkBoxEditTouch", checkBoxEditTouch.isChecked)
        outState.putString("editTextEditDate", editTextEditDate.text.toString())
        outState.putString("editTextEditStressScore", editTextEditStressScore.text.toString())
        outState.putString("editTextEditNotes", editTextEditNotes.text.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_day, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: EditDayFragmentArgs by navArgs()
        val personId = args.personId
        val date = args.date

        val passedFormat = DateTimeFormatter.ofPattern("dd MMM yyyy")
        val databaseFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        editTextEditDate = requireView().findViewById(R.id.editTextEditDate)

        val dayMonthYear = "DD-MM-YYYY"
        val monthDayYear = "MM-DD-YYYY"

        val preferences = this.activity!!.getPreferences(Context.MODE_PRIVATE)
        lateinit var enteredFormat: DateTimeFormatter

        if (preferences.getString("savedDateFormat", "") == monthDayYear) {
            editTextEditDate.hint = getString(R.string.month_day_year_format)
            enteredFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy")
        } else if(preferences.getString("savedDateFormat", "") == dayMonthYear) {
            editTextEditDate.hint = getString(R.string.day_month_year_format)
            enteredFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        } else {
            val editor = preferences.edit()
            editor.putString("savedDateFormat", dayMonthYear)
            editTextEditDate.hint = getString(R.string.day_month_year_format)
            enteredFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            editor.apply()
        }

        var currentDateOfEntry = enteredFormat.format(passedFormat.parse(date))
        editTextEditDate.setText(currentDateOfEntry)

        editTextEditStressScore =
            requireView().findViewById(R.id.editTextEditStressScore)
        editTextEditNotes = requireView().findViewById(R.id.editTextEditNotes)
        val buttonEditDay = requireView().findViewById<Button>(R.id.buttonEditDay)

        // Make the EditText scrollable within the ScrollView
        editTextEditNotes.setOnTouchListener(View.OnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_SCROLL -> {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                    return@OnTouchListener true
                }
            }
            false
        })

        checkBoxEditSight = requireView().findViewById(R.id.checkBoxEditSight)
        checkBoxEditSmell = requireView().findViewById(R.id.checkBoxEditSmell)
        checkBoxEditSound = requireView().findViewById(R.id.checkBoxEditSound)
        checkBoxEditTaste = requireView().findViewById(R.id.checkBoxEditTaste)
        checkBoxEditTouch = requireView().findViewById(R.id.checkBoxEditTouch)

        val checkBoxList = listOf(
            checkBoxEditSight,
            checkBoxEditSmell,
            checkBoxEditSound,
            checkBoxEditTaste,
            checkBoxEditTouch
        )

        if (savedInstanceState != null) {
            checkBoxEditSight.isChecked = savedInstanceState.getBoolean("checkBoxEditSight")
            checkBoxEditSmell.isChecked = savedInstanceState.getBoolean("checkBoxEditSmell")
            checkBoxEditSound.isChecked = savedInstanceState.getBoolean("checkBoxEditSound")
            checkBoxEditTaste.isChecked = savedInstanceState.getBoolean("checkBoxEditTaste")
            checkBoxEditTouch.isChecked = savedInstanceState.getBoolean("checkBoxEditTouch")
            editTextEditDate.setText(savedInstanceState.getString("editTextEditDate"))
            editTextEditStressScore.setText(savedInstanceState.getString("editTextEditStressScore"))
            editTextEditNotes.setText(savedInstanceState.getString("editTextEditNotes"))
        } else if (personId != -1) {
            lifecycleScope.launch {
                val database = AppDatabase.getInstance(requireContext())
                val databaseDate = databaseFormat.format(enteredFormat.parse(currentDateOfEntry))
                val dayId = database.dayDao().getDayId(personId, databaseDate)
                val stressors = database.dayStressorDao().getStressors(dayId)
                val stressScore = database.dayDao().getStressScore(dayId)
                val notes = database.dayDao().getNotes(personId, databaseDate)

                editTextEditStressScore.setText(stressScore.toString())

                if (stressors.isNotEmpty()) {
                    if (stressors.contains(1)) {
                        checkBoxEditSight.isChecked = true
                    }
                    if (stressors.contains(2)) {
                        checkBoxEditSmell.isChecked = true
                    }
                    if (stressors.contains(3)) {
                        checkBoxEditSound.isChecked = true
                    }
                    if (stressors.contains(4)) {
                        checkBoxEditTaste.isChecked = true
                    }
                    if (stressors.contains(5)) {
                        checkBoxEditTouch.isChecked = true
                    }
                }

                if (notes != "") {
                    editTextEditNotes.setText(notes)
                }
            }
        }

        buttonEditDay.setOnClickListener {
            if (personId != -1) {
                lifecycleScope.launch {
                    val database = AppDatabase.getInstance(requireContext())
                    val newDate = editTextEditDate.text.toString()
                    var dateExists = false

                    try {
                        val newDatabaseDate =
                            databaseFormat.format(enteredFormat.parse(newDate)).toString()
                        if (currentDateOfEntry != newDate) {
                            val countDate: Int =
                                database.dayDao().checkIfDateExists(personId, newDatabaseDate)
                            if (countDate != 0) {
                                dateAlreadyExistsDialog()
                                editTextEditDate.setText(currentDateOfEntry)
                                dateExists = true
                            }
                        }
                        if (!dateExists) {
                            val newLocalDate = LocalDate.parse(newDatabaseDate)
                            if (!newLocalDate.isAfter(LocalDate.now())) {
                                try {
                                    val stressScore =
                                        editTextEditStressScore.text.toString().toInt()

                                    if (stressScore in 1..10) {
                                        val currentDatabaseDateOfEntry =
                                            databaseFormat.format(enteredFormat.parse(currentDateOfEntry)).toString()
                                        val dayId = database.dayDao()
                                            .getDayId(personId, currentDatabaseDateOfEntry)
                                        val notes = editTextEditNotes.text.toString()

                                        database.dayStressorDao().deleteOldStressors(dayId)

                                        database.dayDao().updateDay(
                                            dayId,
                                            newDatabaseDate,
                                            stressScore,
                                            notes
                                        )

                                        currentDateOfEntry = newDate
                                        addDayStressors(checkBoxList, dayId)

                                        Toast.makeText(
                                            requireContext(),
                                            getString(R.string.day_successfully_edited),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        incorrectStressScoreDialog()
                                    }
                                } catch (e: NumberFormatException) {
                                    incorrectStressScoreDialog()
                                }
                            } else {
                                futureDateEnteredDialog()
                            }
                        }
                    } catch (e: Exception) {
                        incorrectDateFormatDialog(editTextEditDate.hint.toString())
                    }
                }
            }
        }
    }
}