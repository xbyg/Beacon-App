package com.xbyg.beacon.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xbyg.beacon.R
import com.xbyg.beacon.data.CoursePdf
import kotlinx.android.synthetic.main.item_course_pdf.view.*

class CoursePdfAdapter(private val courses: List<CoursePdf>, protected val clickListener: CoursePdfAdapter.OnItemClickListener) : RecyclerView.Adapter<CoursePdfAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnItemClickListener {
        fun onItemClicked(item: View, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoursePdfAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_course_pdf, parent, false)
        itemView.setOnClickListener { _ -> clickListener.onItemClicked(itemView, itemView.tag as Int) }
        return CoursePdfAdapter.ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CoursePdfAdapter.ViewHolder, position: Int) {
        val course = courses[position]
        with(holder.itemView) {
            tag = position

            courseName.text = course.name
        }
    }

    override fun getItemCount() = courses.size
}