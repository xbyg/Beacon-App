package com.xbyg.beacon.ui

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet

class FragmentViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    class FragmentAdapter(private val fm: FragmentManager, private val fragments: List<Fragment>, private val titles: List<String>? = null) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment = fragments.get(position)

        override fun getCount(): Int = fragments.size

        override fun getPageTitle(position: Int): CharSequence? = titles?.get(position)
    }

    fun setFragments(fm: FragmentManager, fragments: List<Fragment>, titles: List<String>? = null) {
        this.adapter = FragmentAdapter(fm, fragments, titles)
    }
}