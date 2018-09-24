package com.xbyg.beacon.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import com.xbyg.beacon.R
import com.xbyg.beacon.data.ExchangeForm
import com.xbyg.beacon.data.ExchangeLesson
import kotlinx.android.synthetic.main.dialog_exchange_form.*

class ExchangeFormDialog(private val c: Context, private val courseName: String, private val exchangeForm: ExchangeForm, val listener: Listener) : Dialog(c) {

    interface Listener {
        fun onCompleteExchangeForm(dialog: Dialog, exchangeForm: ExchangeForm)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_exchange_form)
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        course.text = courseName

        val index = HashMap<String, ExchangeLesson>()
        exchangeForm.lessons.forEach { lesson -> index["${lesson.location} ${lesson.date} ${lesson.time}"] = lesson }
        spinner.adapter = ArrayAdapter<String>(c, android.R.layout.simple_spinner_item, index.keys.toList())

        nextBtn.setOnClickListener { _ ->
            val selectedLesson = index[spinner.selectedItem.toString()]!!
            exchangeForm.lnewid = selectedLesson.postID
            listener.onCompleteExchangeForm(this, exchangeForm)
        }
    }
}