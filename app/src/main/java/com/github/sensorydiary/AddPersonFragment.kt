package com.github.sensorydiary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AddPersonFragment : Fragment() {

    private fun nameAlreadyExistsDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.name_already_exists))
        builder.setMessage(getString(R.string.name_already_exists_instructions))
        builder.setPositiveButton(getString(R.string.ok)) { _, _ ->

        }
        val alertDialog: AlertDialog = builder.show()
        val buttonPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        buttonPositive.textSize = 20F
    }

    private fun nameIsEmptyDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.name_field_is_empty))
        builder.setMessage(getString(R.string.name_field_is_empty_instructions))
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
        return inflater.inflate(R.layout.fragment_add_person, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonAddPersonToDatabase =
            requireView().findViewById<Button>(R.id.buttonAddPersonToDatabase)
        val editTextAddPersonName = requireView().findViewById<EditText>(R.id.editTextAddPersonName)

        buttonAddPersonToDatabase.setOnClickListener {
            lifecycleScope.launch {
                val database = AppDatabase.getInstance(requireContext())
                val personName = editTextAddPersonName.text.toString().trim()
                if (personName.isNotEmpty()) {
                    if (database.personDao().checkIfPersonNameExists(personName) == 0) {
                        val person = Person(null, personName)
                        database.personDao().addPerson(person)
                        Toast.makeText(requireContext(), getString(R.string.person_successfully_added, personName),
                        Toast.LENGTH_LONG).show()
                    }
                    else {
                        nameAlreadyExistsDialog()
                    }
                }
                else {
                    nameIsEmptyDialog()
                }
            }
        }
    }
}