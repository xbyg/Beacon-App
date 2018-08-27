package com.xbyg.beacon.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.xbyg.beacon.R
import io.reactivex.Completable
import kotlinx.android.synthetic.main.activity_logo.*
import java.util.concurrent.TimeUnit

class LogoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo)
        Logger.addLogAdapter(AndroidLogAdapter())

        supportActionBar!!.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        name.post {
            //start animation after the width of TextView is measured
            logoAnimation().andThen(nameAnimation())
                    .delay(2500, TimeUnit.MILLISECONDS)
                    .subscribe {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
        }
    }

    private fun logoAnimation(): Completable = Completable.create { e ->
        logo.translationX = name.width.toFloat()
        logo.animate().translationX(0f)
                .alpha(1f)
                .setDuration(1200)
                .withEndAction { e.onComplete() }
    }

    private fun nameAnimation(): Completable = Completable.create { e ->
        name.translationX = -60f
        name.animate().translationX(0f)
                .alpha(1f)
                .setDuration(600)
                .withEndAction { e.onComplete() }
    }
}