package com.xbyg.beacon.service.request

import com.orhanobut.logger.Logger
import com.xbyg.beacon.data.ExchangeLesson
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.Response
import org.jsoup.Jsoup
import java.nio.charset.Charset

class ExchangeListRequest(val courseID: String, val originalDate: String) : GZIPHtmlRequest<List<ExchangeLesson>>(Charset.forName("BIG5")) {

    override fun make(): Single<List<ExchangeLesson>> = getRecordID(courseID).flatMap { recordID ->
        if (recordID.equals("empty")) Single.just(ArrayList()) else getExchangeList(recordID)
    }

    // RecordID, differs from courseID, as a data of the POST request for obtaining the available exchange lesson list of a course
    private fun getRecordID(courseID: String): Single<String> {
        val map = HashMap<String, String>().apply {
            put("s_courseid", courseID)
            put("showdetail", "submit")
        }
        return post("https://www.beacon.com.hk/sitin_page/index2.php", map)
                .subscribeOn(Schedulers.io())
                .map { res ->
                    val trElements = Jsoup.parse(res.body()!!.string()).select("tr[bgcolor=#E7e7E7][align=center]")
                    for (tr in trElements) {
                        if (tr.getElementsContainingText(originalDate).size > 0) return@map tr.select("input[name=record_id]").attr("value")
                    }
                    return@map "empty"
                }
    }

    private fun getExchangeList(recordID: String): Single<List<ExchangeLesson>> {
        val map = HashMap<String, String>().apply {
            put("record_id", recordID)
            put("show_sitin", "yes")
            put("edit", "")
        }
        return post("https://www.beacon.com.hk/sitin_page/record_detail.php", map)
                .subscribeOn(Schedulers.io())
                .map { res -> parseResponse(res) }
    }


    override fun parseResponse(response: Response): List<ExchangeLesson> {
        val decompressedString = decompress(response.body()!!.bytes())
        val optionsElements = Jsoup.parse(decompressedString).select("option")
        if (optionsElements.size == 1) {
            Logger.d(optionsElements[0].text())
        }
        optionsElements.remove(optionsElements[0]) //  <option value="">請選擇</option> or <option>沒有任何相關課程</option>

        if (optionsElements[0].text().equals("沒有任何相關課程") || optionsElements[0].text().equals("您所選之堂數的所有調堂名額已滿")) {
            return ArrayList()
        }

        val exchangeLessons = ArrayList<ExchangeLesson>()
        for (optionElement in optionsElements) {
            val postID = optionElement.attr("value")
            if (postID != null) {
                val regexResult = Regex("[A-Z_a-z_0-9_\\-_:]+").findAll(optionElement.text()).toList()
                val originalID = regexResult[0].value
                val date = regexResult[1].value
                val time = regexResult[2].value
                val location = Regex("[\\u4e00-\\u9fa5]+").find(optionElement.text())!!.value
                exchangeLessons.add(ExchangeLesson(originalID, postID, date, time, location))
            }
        }
        return exchangeLessons
    }
}