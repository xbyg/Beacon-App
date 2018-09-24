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
import com.xbyg.beacon.data.ExchangeForm
import com.xbyg.beacon.data.StudentCourse
import com.xbyg.beacon.dialog.ExchangeFormDialog
import com.xbyg.beacon.dialog.PaymentDialog
import com.xbyg.beacon.dialog.StudentCoursesDialog
import com.xbyg.beacon.service.UserService
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_std_courses.*

class StudentCoursesActivity : AppCompatActivity(), StudentCoursesDialog.Listener, ExchangeFormDialog.Listener, PaymentDialog.Listener {
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
                StudentCoursesDialog(this, date, courses[date]!!, this).show()
            } else {
                rootView.showSnackBar("No lessons on that day")
            }
        }
    }

    override fun onSelectExchangeCourse(dialog: Dialog, view: View, date: String, course: StudentCourse) {
        UserService.instances.getExchangeForm(course.id, date)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { exchangeForm: ExchangeForm? ->
                    if (exchangeForm == null) {
                        view.showSnackBar("No available lessons!")
                        return@subscribe
                    }
                    dialog.dismiss()
                    ExchangeFormDialog(this, "${course.tutor} ${course.subject}", exchangeForm, this).show()
                }
    }

    override fun onCompleteExchangeForm(dialog: Dialog, exchangeForm: ExchangeForm) {
        dialog.dismiss()
        UserService.instances.getPayment(exchangeForm)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { payment ->
                    dialog.dismiss()
                    PaymentDialog(this, payment, this).show()
                }
    }

    override fun onPaymentSuccess(dialog: Dialog) {
        dialog.dismiss()
    }

    private fun getDateFromCalendarDay(calendarDay: CalendarDay): String {
        val monthString = if (calendarDay.month < 9) "0${calendarDay.month + 1}" else (calendarDay.month + 1).toString() //Bug? if calendarDay.month = 8, the month displayed on the calendar view is 9
        val dayString = if (calendarDay.day < 10) "0${calendarDay.day}" else calendarDay.day.toString()
        return "${calendarDay.year}-$monthString-$dayString"
    }
}