package com.xbyg.beacon.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import com.xbyg.beacon.R
import com.xbyg.beacon.data.ExchangeLesson
import kotlinx.android.synthetic.main.dialog_exchange_list.*

class ExchangeListDialog(private val c: Context, private val courseName: String, private val availableLessons: List<ExchangeLesson>, val listener: Listener) : Dialog(c) {

    interface Listener {
        fun onSubmit(dialog: Dialog, lesson: ExchangeLesson)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_exchange_list)
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        course.text = courseName

        val index = HashMap<String, ExchangeLesson>()
        availableLessons.forEach { lesson -> index["${lesson.location} ${lesson.date} ${lesson.time}"] = lesson }
        val adapter = ArrayAdapter<String>(c, android.R.layout.simple_spinner_item, index.keys.toList())
        spinner.adapter = adapter

        submitBtn.setOnClickListener { _ ->
            val selectedLessonString = spinner.selectedItem.toString()
            listener.onSubmit(this, index[selectedLessonString]!!)
        }
    }
}