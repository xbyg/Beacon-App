package com.xbyg.beacon.ui

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_tutor.view.*

class TutorItem(context: Context, attrs: AttributeSet) : ViewGroup(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //MeasureSpec.EXACTLY -> match_parent / designated size    MeasureSpec.UNSPECIFIED / MeasureSpec.AT_MOST(if the parent view is bound in size)-> wrap_content
        //https://stackoverflow.com/questions/16022841/when-will-measurespec-unspecified-and-measurespec-at-most-be-applied

        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = w * 3 / 4
        val childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY)
        measureChild(tutorIcon, widthMeasureSpec, childHeightMeasureSpec) //ImageView ignores the heightMeasureSpec if layout_height is set to be wrap_content?

        measureChild(tutorInfo, MeasureSpec.makeMeasureSpec(w / 2, MeasureSpec.EXACTLY), childHeightMeasureSpec) //since only about 50% of the width of the icon image is tutor's appearance
        measureChild(tutorBackground, widthMeasureSpec, MeasureSpec.makeMeasureSpec(h * 8 / 10, MeasureSpec.EXACTLY))
        setMeasuredDimension(w, h)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val bgHeight = tutorBackground.measuredHeight
        val bgTop = (measuredHeight - bgHeight) / 2

        tutorBackground.layout(0, bgTop, measuredWidth, bgTop + bgHeight)

        val iconHeight = measuredHeight * 9 / 10
        tutorIcon.layout(0, this.measuredHeight - iconHeight, tutorIcon.measuredWidth, this.measuredHeight)

        val textLeft = measuredWidth / 2 //since only about 50% of the width of the icon image is tutor's appearance
        tutorInfo.layout(textLeft, 0, textLeft + tutorInfo.measuredWidth, tutorInfo.measuredHeight)
    }
}