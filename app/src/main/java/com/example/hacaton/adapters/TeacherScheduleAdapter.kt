package com.example.hacaton.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.hacaton.R
import com.example.hacaton.model.ScheduleItem

class TeacherScheduleAdapter(private val items: List<ScheduleItem>) :
    RecyclerView.Adapter<TeacherScheduleAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeBlock: TextView = itemView.findViewById(R.id.time_block)
        val groupName: TextView = itemView.findViewById(R.id.group_name)
        val subjectName: TextView = itemView.findViewById(R.id.subject_name)
        val room: TextView = itemView.findViewById(R.id.room)
        val detailsContainer: ConstraintLayout = itemView.findViewById(R.id.details_container)
        val detailsText: EditText = itemView.findViewById(R.id.details_text)
        val saveButton: Button = itemView.findViewById(R.id.save_button)

        init {
            detailsContainer.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.teacher_schedule_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.timeBlock.text = "${item.startTime} - ${item.endTime}"
        holder.groupName.text = item.groupName
        holder.subjectName.text = item.subjectName
        holder.room.text = item.room

        holder.itemView.setOnClickListener {
            if (holder.detailsContainer.visibility == View.GONE) {
                holder.detailsContainer.visibility = View.VISIBLE
                // Анимация появления
                holder.detailsContainer.animate().alpha(1f).setDuration(300).start()
            } else {
                holder.detailsContainer.visibility = View.GONE
                // Анимация скрытия
                holder.detailsContainer.animate().alpha(0f).setDuration(300).start()
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}