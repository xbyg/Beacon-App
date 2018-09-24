package com.xbyg.beacon.service.request

import com.orhanobut.logger.Logger
import com.xbyg.beacon.data.ExchangeForm
import com.xbyg.beacon.data.ExchangeLesson
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.Response
import org.jsoup.Jsoup
import java.nio.charset.Charset

class ExchangeFormRequest(val courseID: String, val originalDate: String) : GZIPHtmlRequest<ExchangeForm?>(Charset.forName("utf-8")) {

    override fun make(): Single<ExchangeForm?> {
        return getOriginalLessonListURL().flatMap { url ->
            if (url.equals("empty")) Single.just("empty") else getAvailableLessonListURL(url)
        }.map { url ->
            if (url.equals("empty")) null else parseResponse(get(url).blockingGet())
        }
    }

    private fun getOriginalLessonListURL(): Single<String> {
        return get("https://studentportal.beacon.com.hk/changelesson")
                .subscribeOn(Schedulers.io())
                .map { res ->
                    val divElements = Jsoup.parse(res.body()!!.string()).select("div[class=panel-body]")[1].select("div[class=col-sm-12] > div[class=table-content row col-sm-12]")
                    for (div in divElements) {
                        if (div.getElementsByClass("col-sm-4").text().contains(courseID)) return@map div.getElementsByTag("a").attr("href")
                    }
                    return@map "empty"
                }
    }

    private fun getAvailableLessonListURL(lessonsListUrl: String): Single<String> {
        return get(lessonsListUrl)
                .subscribeOn(Schedulers.io())
                .map { res ->
                    val divElements = Jsoup.parse(res.body()!!.string()).select("div[class=panel-body]")[2].select("div[class=row]")
                    for (div in divElements) {
                        Logger.d(originalDate)
                        Logger.d(div.getElementsByClass("col-lg-3")[2].text())
                        Logger.d(div.getElementsByClass("col-lg-2 > a").attr("href"))
                        if (div.getElementsByClass("col-lg-3")[2].text().contains(originalDate)) {
                            val url = div.getElementsByTag("a").attr("href")
                            if (!url.isNullOrEmpty()) return@map url
                        }
                    }
                    return@map "empty"
                }
    }

    override fun parseResponse(response: Response): ExchangeForm? {
        val decompressedString = decompress(response.body()!!.bytes())
        val form = Jsoup.parse(decompressedString).getElementById("changelesson-form")
        if (form == null) return null

        val optionsElements = form.select("option")

        val exchangeLessons = ArrayList<ExchangeLesson>()
        for (optionElement in optionsElements) {
            val postID = optionElement.attr("value")
            if (postID != null) {
                val regexResult = Regex("[A-Z_a-z_0-9_\\-_:]+").findAll(optionElement.text()).toList()
                val id = regexResult[0].value
                val date = regexResult[1].value
                val time = regexResult[2].value
                val location = Regex("[\\u4e00-\\u9fa5]+").find(optionElement.text())!!.value
                exchangeLessons.add(ExchangeLesson(id, postID, date, time, location))
            }
        }

        val token = form.select("input[name=_token]").attr("value")
        val lid = form.select("input[name=lid]").attr("value")
        val edid = form.select("input[name=edid]").attr("value")
        val classID = form.select("input[name=class_id]").attr("value")
        val sectionID = form.select("input[name=section_id]").attr("value")
        return ExchangeForm("", exchangeLessons, token, lid, edid, classID, sectionID)
    }
}