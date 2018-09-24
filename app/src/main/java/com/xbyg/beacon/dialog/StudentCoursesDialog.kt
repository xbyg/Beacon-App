package com.xbyg.beacon.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import com.xbyg.beacon.R
import com.xbyg.beacon.data.StudentCourse
import kotlinx.android.synthetic.main.dialog_std_course_selector.*
import kotlinx.android.synthetic.main.item_student_course.view.*

class StudentCoursesDialog(c: Context, val date: String, val courses: Map<StudentCourse, Int>, val listener: Listener) : Dialog(c) {

    interface Listener {
        fun onSelectExchangeCourse(dialog: Dialog, view: View, date: String, course: StudentCourse)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_std_course_selector)
        window.setBackgroundDrawableResource(android.R.color.transparent)

        for (course in courses) {
            rootView.addView(generateCourseLayout(course.key, course.value))
        }
    }

    private fun generateCourseLayout(course: StudentCourse, lessonIndex: Int): CardView {
        val courseLayout = LayoutInflater.from(context).inflate(R.layout.item_student_course, rootView, false).apply {
            courseSubject.text = course.subject
            courseTutor.text = "Tutor: ${course.tutor}"
            courseID.text = "ID: ${course.id}"
            location.text = "Location: ${course.lessons[lessonIndex].location}"
            time.text = "Time: ${course.lessons[lessonIndex].time}"

            setOnClickListener {
                listener.onSelectExchangeCourse(this@StudentCoursesDialog, this, date, course)
            }
        }
        return courseLayout as CardView
    }
}