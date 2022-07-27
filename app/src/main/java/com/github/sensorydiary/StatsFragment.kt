package com.github.sensorydiary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class StatsFragment : Fragment() {

    private fun setStressScoreIfNotNull(
        textViewList: List<TextView>,
        stressScoreList: List<String?>
    ) {
        for (textView in textViewList) {
            val stressScoreIndex = textViewList.indexOf(textView)
            val stressScore: String? = stressScoreList[stressScoreIndex]
            if (stressScore != null) {
                textView.text = stressScore
            }
        }
    }

    private val personId: Int = MainActivity.TabsData.personId

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textViewSight = requireView().findViewById<TextView>(R.id.textViewSight)
        val textViewSmell = requireView().findViewById<TextView>(R.id.textViewSmell)
        val textViewSound = requireView().findViewById<TextView>(R.id.textViewSound)
        val textViewTaste = requireView().findViewById<TextView>(R.id.textViewTaste)
        val textViewTouch = requireView().findViewById<TextView>(R.id.textViewTouch)

        if (personId != -1) {
            lifecycleScope.launch {
                val database = AppDatabase.getInstance(requireContext())
                val sightStressScore = database.dayDao().getAverageStressScore(personId, 1)
                val smellStressScore = database.dayDao().getAverageStressScore(personId, 2)
                val soundStressScore = database.dayDao().getAverageStressScore(personId, 3)
                val tasteStressScore = database.dayDao().getAverageStressScore(personId, 4)
                val touchStressScore = database.dayDao().getAverageStressScore(personId, 5)

                val textViewList = listOf(
                    textViewSight,
                    textViewSmell,
                    textViewSound,
                    textViewTaste,
                    textViewTouch
                )

                val stressScoreList = listOf(
                    sightStressScore,
                    smellStressScore,
                    soundStressScore,
                    tasteStressScore,
                    touchStressScore
                )

                setStressScoreIfNotNull(textViewList, stressScoreList)
            }
        }
    }
}