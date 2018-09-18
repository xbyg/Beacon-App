package com.xbyg.beacon.activity

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import com.xbyg.beacon.fragment.TutorsFragment
import com.xbyg.beacon.fragment.UserFragment
import android.support.design.widget.Snackbar
import com.xbyg.beacon.R

//Extended functions
fun Context.isNetworkAvailable(): Boolean {
    val conMgr: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = conMgr.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnected
}

fun View.showSnackBar(message: String) = Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()

class MainActivity : AppCompatActivity() {
    private val itemClickedListener = BottomNavigationView.OnNavigationItemSelectedListener {
        when (it.itemId) {
            R.id.navigationCourses -> viewPager.setCurrentItem(0, true)
            R.id.navigationUser -> viewPager.setCurrentItem(1, true)
        }
        true
    }

    private val pageChangedListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
            navigation.menu.getItem(position).isChecked = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.itemIconTintList = null
        navigation.setOnNavigationItemSelectedListener(itemClickedListener)

        val fragments = ArrayList<Fragment>().apply {
            add(TutorsFragment())
            add(UserFragment())
        }

        viewPager.setFragments(supportFragmentManager, fragments)
        viewPager.addOnPageChangeListener(pageChangedListener)
    }
}
