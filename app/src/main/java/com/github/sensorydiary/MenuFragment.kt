package com.github.sensorydiary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch


class MenuFragment : Fragment() {

    private fun deletePersonDialog(
        personName: String,
        spinnerAdapter: ArrayAdapter<String>
    ) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.delete_confirmation))
        builder.setMessage(getString(R.string.delete_confirmation_instructions, personName))
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            lifecycleScope.launch {
                val database = AppDatabase.getInstance(requireContext())
                if (database.personDao().checkIfPersonNameExists(personName) != 0) {
                    database.personDao().deletePerson(personName)
                    spinnerAdapter.clear()
                    val names = database.personDao().returnAllPersons()
                    spinnerAdapter.addAll(names)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.person_successfully_deleted, personName),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        builder.setNegativeButton(getString(R.string.no)) { _, _ ->

        }
        val alertDialog: AlertDialog = builder.show()
        val buttonPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val buttonNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        buttonPositive.textSize = 20F
        buttonNegative.textSize = 20F
    }

    private suspend fun getIdFromName(
        personName: String
    ): Int {
        val database = AppDatabase.getInstance(requireContext())
        return if (database.personDao().checkIfPersonNameExists(personName) != 0) {
            database.personDao().getPersonId(personName)
        } else {
            -1
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.sensory_diary)

        val spinnerPersons = requireView().findViewById<Spinner>(R.id.spinnerSelectPerson)

        lifecycleScope.launch {
            val database = AppDatabase.getInstance(requireContext())
            val names = database.personDao().returnAllPersons()

            val spinnerAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                names
            )
            spinnerPersons.adapter = spinnerAdapter
        }

        val settingsIcon = requireView().findViewById<ImageView>(R.id.settingsIcon)
        val buttonSeeDiary = requireView().findViewById<Button>(R.id.buttonSeeDiary)
        val buttonAddPerson = requireView().findViewById<Button>(R.id.buttonAddPerson)
        val buttonEditPerson = requireView().findViewById<Button>(R.id.buttonEditPerson)
        val buttonDeletePerson = requireView().findViewById<Button>(R.id.buttonDeletePerson)

        settingsIcon.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_settingsFragment)
        }

        buttonSeeDiary.setOnClickListener {
            lifecycleScope.launch {
                if (spinnerPersons.adapter.count > 0) {
                    val personName = spinnerPersons.selectedItem.toString()
                    val personId = getIdFromName(personName)
                    if (personId != -1) {
                        // Is there a better way to pass these variables to the tabs fragments?
                        MainActivity.TabsData.personId = personId
                        MainActivity.TabsData.personName = personName
                        findNavController().navigate(R.id.action_menuFragment_to_viewPagerAdapter)
                    }
                }
            }
        }

        buttonAddPerson.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_addPersonFragment)
        }

        buttonEditPerson.setOnClickListener {
            lifecycleScope.launch {
                if (spinnerPersons.adapter.count > 0) {
                    val personName = spinnerPersons.selectedItem.toString()
                    val personId = getIdFromName(personName)
                    if (personId != -1) {
                        val action =
                            MenuFragmentDirections.actionMenuFragmentToEditPersonFragment(
                                personId,
                                personName
                            )
                        findNavController().navigate(action)
                    }
                }
            }
        }

        buttonDeletePerson.setOnClickListener {
            if (spinnerPersons.adapter.count > 0) {
                val personName = spinnerPersons.selectedItem.toString()
                @Suppress("UNCHECKED_CAST")
                deletePersonDialog(
                    personName, spinnerPersons.adapter as ArrayAdapter<String>
                )
            }
        }
    }
}