package com.xbyg.beacon.adapter

import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.xbyg.beacon.R
import com.xbyg.beacon.data.Tutor
import kotlinx.android.synthetic.main.item_tutor.view.*
import com.squareup.picasso.Transformation

class TutorsAdapter(private val tutors: ArrayList<Tutor>, private val clickListener: OnItemClickListener) : RecyclerView.Adapter<TutorsAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnItemClickListener {
        fun onItemClicked(item: View, position: Int)
    }

    private val cropTransformation = object : Transformation {
        override fun key(): String = "Crop"

        //https://stackoverflow.com/questions/27753634/android-bitmap-save-without-transparent-areac
        override fun transform(source: Bitmap): Bitmap {
            var minX = source.width
            var minY = source.height
            var maxX = -1
            var maxY = -1
            for (y in 0 until source.height) {
                for (x in 0 until source.width) {
                    val alpha = source.getPixel(x, y) shr 24 and 255
                    if (alpha > 0) {
                        if (x < minX) minX = x
                        if (x > maxX) maxX = x
                        if (y < minY) minY = y
                        if (y > maxY) maxY = y
                    }
                }
            }
            val croppedBitmap = Bitmap.createBitmap(source, minX, minY, maxX - minX + 1, maxY - minY + 1)
            source.recycle()
            return croppedBitmap
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_tutor, parent, false)
        itemView.setOnClickListener { _ -> clickListener.onItemClicked(itemView, itemView.tag as Int) }
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tutor = tutors[position]
        with(holder.itemView) {
            tag = position

            Picasso.get().load(tutor.icon).transform(cropTransformation).into(tutorIcon)
            tutorName.text = tutor.name
            tutorSubject.text = tutor.subjects
        }
    }

    override fun getItemCount(): Int {
        return tutors.size
    }
}