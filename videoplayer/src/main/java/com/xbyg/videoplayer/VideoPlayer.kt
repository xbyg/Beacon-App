package com.xbyg.videoplayer

import android.content.pm.ActivityInfo
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.widget.SeekBar
import android.os.Looper
import android.media.MediaPlayer
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.*
import android.widget.RelativeLayout
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.video_player.view.*

class VideoPlayer(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs), SurfaceHolder.Callback {
    private val mediaPlayer = MediaPlayer()

    private var isFullscreenMode = false
    //always check whether has started updating the progress before sending UPDATE_PROGRESS, it avoids updating the progress with multiple times in one period
    private var isUpdatingProgress = false

    var listener: VideoPlayerListener? = null

    private val progressHandler = object : Handler(Looper.myLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                UPDATE_PROGRESS -> {
                    val ms = mediaPlayer.currentPosition
                    progressBar.progress = ms
                    progressText.text = formatMs(ms)
                    this.sendEmptyMessageDelayed(UPDATE_PROGRESS, 1000)
                }
            }
        }
    }

    init {
        //setting focusable to true, we can handle the key event
        this.isFocusableInTouchMode = true

        View.inflate(context, R.layout.video_player, this)
        this.visibility = View.GONE
        videoSurface.holder.addCallback(this)

        playBtn.setOnClickListener{ _ -> if (!mediaPlayer.isPlaying) play() else pause() }
        fullscreen.setOnClickListener{ _ -> setFullscreenMode(!isFullscreenMode) }

        progressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) = progressText.setText(formatMs(progress))

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                //if video is playing, then keep playing it, don't need to pause
                progressHandler.removeMessages(UPDATE_PROGRESS)
                isUpdatingProgress = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mediaPlayer.seekTo(progressBar.progress)
                if (!mediaPlayer.isPlaying) {
                    //if video is paused by user before tracking touch, then play it when tracking is done
                    mediaPlayer.start()
                }
                progressHandler.sendEmptyMessage(UPDATE_PROGRESS)
                isUpdatingProgress = true
            }
        })
    }

    private fun formatMs(ms : Int): String {
        val second = ms / 1000
        val hh = second / 3600
        val mm = second % 3600 / 60
        val ss = second % 60
        return if (hh == 0) String.format("%02d:%02d", mm, ss) else String.format("%02d:%02d:%02d", hh, mm, ss)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mediaPlayer.setDisplay(holder)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        this.pause()
        mediaPlayer.setDisplay(null)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && isFullscreenMode) {
            //consume this event and exit full screen mode
            setFullscreenMode(false)
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> parent.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_UP -> {
                parent.requestDisallowInterceptTouchEvent(false)
                if (controllerLayout.visibility == View.GONE) {
                    if (!isUpdatingProgress) {
                        progressHandler.sendEmptyMessage(UPDATE_PROGRESS)
                        isUpdatingProgress = true
                    }
                    controllerLayout.visibility = View.VISIBLE
                } else {
                    controllerLayout.visibility = View.GONE
                    progressHandler.removeMessages(UPDATE_PROGRESS)
                    isUpdatingProgress = false
                }
            }
        }
        return true
    }

    interface VideoPlayerListener {
        fun onEnterFullscreenMode()

        fun onExitFullscreenMode()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutParams = layoutParams.apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }
            isFullscreenMode = true
            listener?.onEnterFullscreenMode()
        } else {
            layoutParams = layoutParams.apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            isFullscreenMode = false
            listener?.onExitFullscreenMode()
        }
        resizeSurfaceSize()
    }

    fun prepare(videoUrl: String) : Completable {
        this.visibility = View.VISIBLE
        return Completable.concatArray(prepareMediaPlayer(videoUrl), prepareViews())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete{ this.resizeSurfaceSize() }
    }

    private fun prepareMediaPlayer(videoUrl: String) = Completable.create{ e: CompletableEmitter ->
        mediaPlayer.reset()
        mediaPlayer.setDataSource(videoUrl)
        //mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
        mediaPlayer.prepareAsync() //some devices have to prepare asynchronously?
        mediaPlayer.setOnPreparedListener { _ -> e.onComplete() }
    }.subscribeOn(Schedulers.io())


    private fun prepareViews() = Completable.create{ e: CompletableEmitter ->
        try {
            playBtn.setImageResource(R.drawable.pause)
            progressBar.progress = 0
            progressBar.max = mediaPlayer.duration
            duration.text = formatMs(mediaPlayer.duration)
            e.onComplete()
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }.subscribeOn(AndroidSchedulers.mainThread())

    private fun resizeSurfaceSize() {
        val videoWidth = mediaPlayer.videoWidth
        val videoHeight = mediaPlayer.videoHeight
        val screenWidth = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.width

        val surfaceLayoutParams = videoSurface.layoutParams
        surfaceLayoutParams.width = screenWidth
        surfaceLayoutParams.height = screenWidth * videoHeight / videoWidth
        videoSurface.layoutParams = surfaceLayoutParams
    }

    fun play() {
        if (!mediaPlayer.isPlaying) {
            if (controllerLayout.visibility == View.VISIBLE && !isUpdatingProgress) {
                progressHandler.sendEmptyMessage(UPDATE_PROGRESS)
                isUpdatingProgress = true
            } else {
                progressHandler.removeMessages(UPDATE_PROGRESS)
                isUpdatingProgress = false
            }
            mediaPlayer.start()
            playBtn.setImageResource(R.drawable.pause)
        }
    }

    fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            playBtn.setImageResource(R.drawable.play)
            progressHandler.removeMessages(UPDATE_PROGRESS)
            isUpdatingProgress = false
        }
    }

    fun release() {
        progressHandler.removeMessages(UPDATE_PROGRESS)
        isUpdatingProgress = false
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    fun setFullscreenMode(enable: Boolean) {
        if (enable) {
            (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            fullscreen.setImageResource(R.drawable.full_screen_off)
        } else {
            (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            fullscreen.setImageResource(R.drawable.full_screen_on)
        }
    }

    companion object {
        private val UPDATE_PROGRESS = 1
    }
}