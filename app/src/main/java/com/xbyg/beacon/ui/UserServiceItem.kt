package com.xbyg.beacon.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.LayoutInflater
import com.xbyg.beacon.R
import kotlinx.android.synthetic.main.item_user_service.view.*

class UserServiceItem(context: Context, attrs: AttributeSet) : CardView(context, attrs) {
    private var icon: Drawable? = null
    private var name: String = "Service"

    init {
        val ta = context.theme.obtainStyledAttributes(attrs, R.styleable.UserServiceItem, 0, 0)
        name = ta.getString(R.styleable.UserServiceItem_serviceName)
        icon = ta.getDrawable(R.styleable.UserServiceItem_serviceIcon)
        ta.recycle()

        LayoutInflater.from(getContext()).inflate(R.layout.item_user_service, this)
        serviceName.text = name
        serviceIcon.setImageDrawable(icon)
    }
}