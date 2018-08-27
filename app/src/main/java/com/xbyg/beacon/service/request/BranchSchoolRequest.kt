package com.xbyg.beacon.service.request

import com.xbyg.beacon.data.BranchSchool
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.Response
import org.jsoup.Jsoup
import java.nio.charset.Charset

class BranchSchoolRequest(val name: String, val location: String) : GZIPHtmlRequest<BranchSchool>(Charset.forName("BIG5")) {
    override fun make(): Single<BranchSchool> = get("https://www.beacon.com.hk/2012/beacon_location.php?pl=$location")
            .observeOn(Schedulers.computation())
            .map { res -> parseResponse(res) }

    override fun parseResponse(response: Response): BranchSchool {
        val decompressedString = decompress(response.body()!!.bytes())
        val body = Jsoup.parse(decompressedString)
        val tdElements = body.select("td[align=left][valign=top]")
        val address = tdElements[0].text().replace("地址︰ ", "")
        val phoneNo = tdElements[2].text().replace("分校電話︰", "")
        val locationImageSrc = "https://www.beacon.com.hk/2012/" + body.select("img[width=450]").attr("src")

        val workingHours = Regex("\\([A-Z]+\\) \\([0-9_\\/]+\\) [a-z_0-9_:_\\-_\\u4e00-\\u9fa5]+")
                .findAll(tdElements[3].text())
                .toList()
                .map { matchResult -> matchResult.value }
        return BranchSchool(name, address, phoneNo, workingHours, locationImageSrc)
    }
}