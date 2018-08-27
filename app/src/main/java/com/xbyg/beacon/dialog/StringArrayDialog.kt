package com.xbyg.beacon.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.xbyg.beacon.R
import com.xbyg.beacon.ui.StringArrayRecyclerView
import kotlinx.android.synthetic.main.dialog_list.*

class StringArrayDialog(c: Context, private val strings: List<String>, private val textSize: Float, private val listener: Listener) : Dialog(c) {
    interface Listener {
        fun onSelect(dialog: Dialog, string: String, pos: Int)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_list)
        recyclerView.setStringArray(strings, textSize, object : StringArrayRecyclerView.Listener {
            override fun onItemClicked(string: String, pos: Int) {
                listener.onSelect(this@StringArrayDialog, string, pos)
            }
        })
    }
}