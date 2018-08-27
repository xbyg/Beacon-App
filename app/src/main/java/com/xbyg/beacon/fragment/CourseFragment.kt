package com.xbyg.beacon.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xbyg.beacon.R
import com.xbyg.beacon.data.CoursePdf
import com.xbyg.beacon.data.Tutor
import com.xbyg.beacon.adapter.CoursePdfAdapter
import kotlinx.android.synthetic.main.fragment_courses.*
import android.content.Intent
import android.net.Uri
import com.xbyg.beacon.service.TutorService
import io.reactivex.android.schedulers.AndroidSchedulers

class CourseFragment : Fragment(), CoursePdfAdapter.OnItemClickListener {
    val courses: ArrayList<CoursePdf> = ArrayList<CoursePdf>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_courses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(coursesRecyclerView) {
            adapter = CoursePdfAdapter(courses, this@CourseFragment)
            layoutManager = LinearLayoutManager(context)
        }

        TutorService.instances.getCourses(arguments?.get("tutor") as Tutor)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { courses ->
                    this@CourseFragment.courses.apply {
                        clear()
                        addAll(courses)
                    }
                    coursesRecyclerView.adapter.notifyDataSetChanged()
                }
    }


    override fun onItemClicked(item: View, position: Int) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(courses[item.tag as Int].url))
        startActivity(intent)
    }
}