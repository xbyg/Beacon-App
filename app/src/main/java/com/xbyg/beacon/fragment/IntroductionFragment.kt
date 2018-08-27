package com.xbyg.beacon.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xbyg.beacon.R
import com.xbyg.beacon.data.Tutor
import com.xbyg.beacon.service.TutorService
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_introduction.*

class IntroductionFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_introduction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        TutorService.instances.getIntro(arguments?.getSerializable("tutor") as Tutor)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { intro -> introduction.setHtml(intro) }
    }
}