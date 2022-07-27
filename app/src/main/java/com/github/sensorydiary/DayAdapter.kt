package com.github.sensorydiary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView


class DayAdapter(
    private val appContext: Context,
    private val dayList: ArrayList<DayViewModel>,
    private val personId: Int,
    private val navController: NavController
) : RecyclerView.Adapter<DayAdapter.ViewHolder>() {

    private lateinit var listener: DeleteItemListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_diary, parent, false)

        return ViewHolder(view)
    }

    interface DeleteItemListener {
        fun onItemDeleted(date: String)
    }

    fun setListener(listener: DeleteItemListener) {
        this.listener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dayViewModel = dayList[position]
        holder.textViewDay.text = dayViewModel.date
        holder.textViewStressScore.text = dayViewModel.stressScore
        holder.textViewStressors.text = dayViewModel.stressors
        holder.textViewNotes.text = dayViewModel.notes

        holder.textViewOptions.setOnClickListener {
            val popup = PopupMenu(appContext, holder.textViewOptions)
            popup.inflate(R.menu.day_options_menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.edit -> {
                        val date = holder.textViewDay.text.toString()
                        val action = ViewPagerAdapterDirections.actionViewPagerAdapterToEditDayFragment(personId, date)
                        navController.navigate(action)
                    }
                    R.id.delete -> {
                        val date = holder.textViewDay.text.toString()
                        for (day in dayList) {
                            if (day.date == date) {
                                val index = dayList.indexOf(day)
                                dayList.removeAt(index)
                                notifyItemRemoved(index)
                                listener.onItemDeleted(day.date)
                                break
                            }
                        }
                    }
                }
                false
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int {
        return dayList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textViewDay: TextView = itemView.findViewById(R.id.textViewDate)
        val textViewStressScore: TextView = itemView.findViewById(R.id.textViewStressScore)
        val textViewStressors: TextView = itemView.findViewById(R.id.textViewStressors)
        val textViewNotes: TextView = itemView.findViewById(R.id.textViewNotes)
        val textViewOptions: TextView = itemView.findViewById(R.id.textViewOptions)
    }
}
