package com.xbyg.beacon.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.xbyg.beacon.data.Tutor
import com.xbyg.beacon.fragment.CourseFragment
import com.xbyg.beacon.fragment.IntroductionFragment
import com.xbyg.videoplayer.VideoPlayer
import android.view.WindowManager
import com.xbyg.beacon.R
import kotlinx.android.synthetic.main.activity_tutor.*

class TutorActivity : AppCompatActivity() {
    var tutor: Tutor? = null
    var player: VideoPlayer? = null
    var expanded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutor)

        tutor = intent.getSerializableExtra("tutor") as Tutor
        val fragments = ArrayList<Fragment>().apply {
            add(IntroductionFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("tutor", tutor)
                }
            })

            add(CourseFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("tutor", tutor)
                }
            })
        }

        val titles = ArrayList<String>().apply {
            add("簡介")
            add("課程")
        }

        viewPager.setFragments(supportFragmentManager, fragments, titles)

        tabs.shouldExpand = true
        tabs.indicatorHeight = 8
        tabs.indicatorColor = Color.parseColor("#7FFFD4")
        tabs.setViewPager(viewPager)

        tutorVideo.prepare(tutor!!.video).subscribe { tutorVideo.play() }
        tutorVideo.listener = object : VideoPlayer.VideoPlayerListener {
            override fun onEnterFullscreenMode() = this@TutorActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

            override fun onExitFullscreenMode() = this@TutorActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    override fun onResume() {
        super.onResume()
        player?.play()
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}