package com.xbyg.beacon.service.request

import com.xbyg.beacon.data.Tutor
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.Response
import org.jsoup.Jsoup

class IntroductionRequest(val tutor: Tutor) : Request<String>() {

    override fun make(): Single<String> = get("https://www.beacon.com.hk/2009/teacher_profile.php?id=${tutor.id}&tutor_id=${tutor.tutorId}")
            .observeOn(Schedulers.computation())
            .map { response -> parseResponse(response) }

    override fun parseResponse(response: Response): String = Jsoup.parse(response.body()!!.string()).select(".contentsty").html()
}