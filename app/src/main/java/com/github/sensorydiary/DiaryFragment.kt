package com.github.sensorydiary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class DiaryFragment : Fragment(), DayAdapter.DeleteItemListener {

    private val personId: Int = MainActivity.TabsData.personId
    private val personName: String = MainActivity.TabsData.personName

    override fun onItemDeleted(date: String) {
        val displayFormat = DateTimeFormatter.ofPattern("dd MMM yyyy")
        val databaseFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val databaseDateFormat = databaseFormat.format(displayFormat.parse(date))

        if (personId != -1) {
            lifecycleScope.launch {
                val database = AppDatabase.getInstance(requireContext())
                val dayId = database.dayDao().getDayId(personId, databaseDateFormat)
                // This delete will also cascade to the dayStressor table's dayId foreign keys
                database.dayDao().deleteDay(dayId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_diary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = personName

        val recyclerViewDay = requireView().findViewById<RecyclerView>(R.id.recyclerViewDay)

        recyclerViewDay.layoutManager = LinearLayoutManager(requireContext())
        val recyclerViewData = ArrayList<DayViewModel>()
        val diaryFragment = this

        lifecycleScope.launch {
            val database = AppDatabase.getInstance(requireContext())

            if (personId != -1) {
                // Is there a way to combine the two below database queries into one query?
                val allDays = database.dayDao().getAllDays(personId)
                val allStressors = database.dayStressorDao().getAllStressors(personId)

                for (day in allDays) {
                    val databaseFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val displayFormat = DateTimeFormatter.ofPattern("dd MMM yyyy")
                    val displayDateFormat = displayFormat.format(databaseFormat.parse(day.date))

                    if (day.notes != "") {
                        day.notes += "\n"
                    }

                    val dayViewModel = DayViewModel(
                        displayDateFormat,
                        getString(R.string.stress_score, day.stressScore),
                        getString(R.string.no_sensory_stressors),
                        day.notes
                    )

                    for (stressor in allStressors) {
                        if (day.dayId == stressor.dayId) {
                            dayViewModel.stressors = stressor.stressors
                            break
                        }
                    }
                    recyclerViewData.add(dayViewModel)
                }
                val adapter =
                    DayAdapter(requireContext(), recyclerViewData, personId, findNavController())
                recyclerViewDay.adapter = adapter
                adapter.setListener(diaryFragment)
            }
        }

        val buttonAddDay = requireView().findViewById<Button>(R.id.buttonAddDay)

        buttonAddDay.setOnClickListener {
            if (personId != -1) {
                val action =
                    ViewPagerAdapterDirections.actionViewPagerAdapterToAddDayFragment(
                        personId
                    )
                findNavController().navigate(action)
            }
        }
    }
}