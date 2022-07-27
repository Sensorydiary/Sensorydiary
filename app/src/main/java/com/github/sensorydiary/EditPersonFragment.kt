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
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch

class EditPersonFragment : Fragment() {

    private fun nameAlreadyExistsDialog(editTextEditPersonName: EditText, currentName: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.name_already_exists))
        builder.setMessage(getString(R.string.edit_name_already_exists_instructions))
        builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
            editTextEditPersonName.setText(currentName)
        }
        val alertDialog: AlertDialog = builder.show()
        val buttonPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        buttonPositive.textSize = 20F
    }

    private fun nameIsEmptyDialog(editTextEditPersonName: EditText, currentName: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.name_field_is_empty))
        builder.setMessage(getString(R.string.edit_name_field_is_empty_instructions))
        builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
            editTextEditPersonName.setText(currentName)
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
        return inflater.inflate(R.layout.fragment_edit_person, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: EditPersonFragmentArgs by navArgs()
        val personId = args.personId
        var currentName = args.personName

        val editTextEditPersonName =
            requireView().findViewById<EditText>(R.id.editTextEditPersonName)
        editTextEditPersonName.setText(currentName)

        val buttonEditPersonInDatabase =
            requireView().findViewById<Button>(R.id.buttonEditPersonInDatabase)

        buttonEditPersonInDatabase.setOnClickListener {
            lifecycleScope.launch {
                val database = AppDatabase.getInstance(requireContext())
                if (database.personDao().checkIfPersonIdExists(personId) != 0) {
                    val newName = editTextEditPersonName.text.toString().trim()
                    if (newName.isNotEmpty()) {
                        if(database.personDao().checkIfPersonNameExists(newName) == 0) {
                            database.personDao().editPersonName(personId, newName)
                            currentName = newName
                            Toast.makeText(
                                requireContext(), getString(R.string.name_successfully_edited, currentName),
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            nameAlreadyExistsDialog(editTextEditPersonName, currentName)
                        }
                    } else {
                        nameIsEmptyDialog(editTextEditPersonName, currentName)
                    }
                }
            }
        }
    }
}