package com.xbyg.beacon.activity

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import com.xbyg.beacon.R
import com.xbyg.beacon.data.ExchangeLesson
import com.xbyg.beacon.data.StudentCourse
import com.xbyg.beacon.dialog.ExchangeListDialog
import com.xbyg.beacon.dialog.StudentCoursesDialog
import com.xbyg.beacon.service.UserService
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_std_courses.*

class StudentCoursesActivity : AppCompatActivity(), StudentCoursesDialog.Listener, ExchangeListDialog.Listener {
    private val courses = HashMap<String, HashMap<StudentCourse, Int>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_std_courses)

        UserService.instances.getCourses()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { attendedCourses: List<StudentCourse> ->
                    val lessonsDate = HashSet<String>()

                    attendedCourses.forEach { course ->
                        course.lessons.forEachIndexed { index, lesson ->
                            lessonsDate.add(lesson.date)
                            if (courses[lesson.date] == null) {
                                courses[lesson.date] = HashMap()
                            }
                            courses[lesson.date]!![course] = index
                        }
                    }

                    calendarView.addDecorator(object : DayViewDecorator {
                        override fun shouldDecorate(calendarDay: CalendarDay) = lessonsDate.contains(getDateFromCalendarDay(calendarDay))

                        override fun decorate(view: DayViewFacade) = view.addSpan(DotSpan(10f, Color.CYAN))
                    })
                }

        calendarView.setOnDateChangedListener { _, day, _ ->
            val date = getDateFromCalendarDay(day)
            if (courses.keys.contains(date)) {
                StudentCoursesDialog(this@StudentCoursesActivity, date, courses[date]!!, this).show()
            } else {
                rootView.showSnackBar("No lessons on that day")
            }
        }
    }

    override fun onSelect(dialog: Dialog, view: View, date: String, course: StudentCourse) {
        UserService.instances.getExchangeList(course.id, date)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { exchangeLessonList: List<ExchangeLesson> ->
                    if (exchangeLessonList.isEmpty()) {
                        view.showSnackBar("No available lessons!")
                        return@subscribe
                    }
                    dialog.dismiss()
                    ExchangeListDialog(this@StudentCoursesActivity, "${course.tutor} ${course.subject}", exchangeLessonList, this@StudentCoursesActivity).show()
                }
    }

    override fun onSubmit(dialog: Dialog, lesson: ExchangeLesson) {
        dialog.dismiss()
        //UserService.instances.exchangeLesson(lesson).subscribe { _ ->

        //}
    }

    private fun getDateFromCalendarDay(calendarDay: CalendarDay): String {
        val monthString = if (calendarDay.month < 9) "0${calendarDay.month + 1}" else (calendarDay.month + 1).toString() //Bug? if calendarDay.month = 8, the month displayed on the calendar view is 9
        val dayString = if (calendarDay.day < 10) "0${calendarDay.day}" else calendarDay.day.toString()
        return "${calendarDay.year}-$monthString-$dayString"
    }
}