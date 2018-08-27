package com.xbyg.beacon.fragment

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.xbyg.beacon.R
import com.xbyg.beacon.activity.TutorActivity
import com.xbyg.beacon.data.Tutor
import com.xbyg.beacon.adapter.TutorsAdapter
import com.xbyg.beacon.service.TutorService
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_tutors.*

class TutorsFragment : NetworkRequiredFragment(R.layout.fragment_tutors), TutorsAdapter.OnItemClickListener {
    private val tutors: ArrayList<Tutor> = ArrayList()

    override fun onNetworkAvailable() {
        with(tutorsRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = TutorsAdapter(tutors, this@TutorsFragment)
        }

        TutorService.instances.getTutors()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { retrievedTutors ->
                    tutors.clear()
                    tutors.addAll(retrievedTutors)

                    tutorsRecyclerView.adapter.notifyDataSetChanged()
                }
    }

    override fun onItemClicked(item: View, position: Int) {
        val intent = Intent(context, TutorActivity::class.java)
        intent.putExtra("tutor", tutors[position])
        startActivity(intent)
    }
}