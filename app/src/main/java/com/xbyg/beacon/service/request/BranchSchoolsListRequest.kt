package com.xbyg.beacon.service.request

import com.xbyg.beacon.data.BranchSchool
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.Response
import org.jsoup.Jsoup
import java.nio.charset.Charset

class BranchSchoolsListRequest : GZIPHtmlRequest<Map<String, String>>(Charset.forName("BIG5")) {
    override fun make(): Single<Map<String, String>> = get("https://www.beacon.com.hk/beacon_events/beacon_events.php")
            .observeOn(Schedulers.computation())
            .map { res -> parseResponse(res) }

    override fun parseResponse(response: Response): Map<String, String> {
        val decompressedString = decompress(response.body()!!.bytes())
        val map = HashMap<String, String>()
        Jsoup.parse(decompressedString).select("div[id=dropmenu4] a").forEach { aElement ->
            map.put(aElement.text(), aElement.attr("href").replace("/2012/beacon_location.php?pl=", ""))
        }
        return map
    }
}