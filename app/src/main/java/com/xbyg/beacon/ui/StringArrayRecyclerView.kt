package com.xbyg.beacon.ui

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView

class StringArrayRecyclerView(c: Context, attrs: AttributeSet) : RecyclerView(c, attrs) {

    interface Listener {
        fun onItemClicked(string: String, pos: Int)
    }

    private class StringArrayAdapter(val strings: List<String>, val size: Float, val listener: Listener) : Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
                object : ViewHolder(TextView(parent.context).apply { textSize = size }) {}

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            (holder.itemView as TextView).apply {
                text = strings[position]
                setOnClickListener { listener.onItemClicked(strings[position], position) }
            }
        }

        override fun getItemCount(): Int = strings.size
    }

    init {
        layoutManager = LinearLayoutManager(c)
    }

    fun setStringArray(strings: List<String>, size: Float, listener: Listener) {
        adapter = StringArrayAdapter(strings, size, listener)
    }
}