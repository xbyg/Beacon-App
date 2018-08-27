package com.xbyg.beacon.service.request

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.Response
import org.jsoup.Jsoup

class NameRequest : Request<String>() {
    override fun make(): Single<String> = get("https://www.beacon.com.hk/2011/index.php")
            .observeOn(Schedulers.computation())
            .map { res -> parseResponse(res) }

    override fun parseResponse(response: Response): String {
        val text = Jsoup.parse(response.body()!!.string()).select("td[width=310]").text()
        return Regex("(?<= )\\w([\\w_ ])*\\w").find(text)!!.value
    }
}