package com.xbyg.beacon.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.xbyg.beacon.R
import com.xbyg.beacon.data.StudentCourse
import kotlinx.android.synthetic.main.item_student_course.view.*

class StudentCourseAdapter(private val courses: List<StudentCourse>) : RecyclerView.Adapter<StudentCourseAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_student_course, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /*val course = courses[position]
        val lessonsLayout = lessonsLayouts[position]

        holder.itemView.apply {
            courseName.text = course.name
            courseID.text = "ID: " + course.id
            courseTime.text = "Time: " + course.time
            courseTopic.text = "Topic: " + if (course.topic == null) "--" else course.topic

            contentLayout.removeView(lessonsLayoutHolder)
            if (lessonsLayout.parent == null) contentLayout.addView(lessonsLayout)
        }*/
    }

    override fun getItemCount() = courses.size
}