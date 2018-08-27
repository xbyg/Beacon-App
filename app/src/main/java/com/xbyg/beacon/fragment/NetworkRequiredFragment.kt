package com.xbyg.beacon.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xbyg.beacon.R
import com.xbyg.beacon.activity.isNetworkAvailable
import com.xbyg.beacon.activity.showSnackBar
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_network_required.*
import java.util.*
import java.util.concurrent.TimeUnit

abstract class NetworkRequiredFragment(private val layoutResId: Int) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(if (context!!.isNetworkAvailable()) layoutResId else R.layout.fragment_network_required, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (context!!.isNetworkAvailable()) {
            onNetworkAvailable()
            return
        }

        refreshLayout.setOnClickListener {
            loadingBar.visibility = View.VISIBLE
            text1.visibility = View.GONE
            text2.visibility = View.GONE

            Completable.timer(Random().nextInt(1000) + 500L, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        loadingBar.visibility = View.GONE
                        text1.visibility = View.VISIBLE
                        text2.visibility = View.VISIBLE
                        if (context!!.isNetworkAvailable()) updateViewsAndNotify() else view.showSnackBar("No network available！")
                    }
        }
    }

    private fun updateViewsAndNotify() {
        (view as ViewGroup).apply {
            removeAllViews()
            val v = layoutInflater.inflate(layoutResId, this, false)
            addView(v)
        }
        onNetworkAvailable()
    }

    protected abstract fun onNetworkAvailable()
}