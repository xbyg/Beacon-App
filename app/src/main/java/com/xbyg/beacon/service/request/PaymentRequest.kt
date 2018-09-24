package com.xbyg.beacon.service.request

import com.orhanobut.logger.Logger
import com.xbyg.beacon.data.ExchangeForm
import com.xbyg.beacon.data.Payment
import io.reactivex.Single
import okhttp3.Response
import org.jsoup.Jsoup

class PaymentRequest(private val exchangeForm: ExchangeForm): Request<Payment>() {
    override fun make(): Single<Payment> {
        val map = HashMap<String, String>().apply {
            put("_token", exchangeForm.token)
            put("lnewid", exchangeForm.lnewid)
            put("lid", exchangeForm.lid)
            put("edid", exchangeForm.edid)
            put("class_id", exchangeForm.classID)
            put("section_id", exchangeForm.sectionID)
        }

        return post("https://studentportal.beacon.com.hk/changelesson/confirm", map)
                .map { res -> parseResponse(res) }
                .doOnError { e -> e.printStackTrace() }
    }

    override fun parseResponse(response: Response): Payment {
        val a = Jsoup.parse(response.body()!!.string())
        Logger.d(a.text())
        val form = a.getElementById("payment-form")
        val token = form.select("input[name=_token]").attr("value")
        val amount = form.select("input[name=amount]").attr("value")
        val enrolmentID = form.select("input[name=enrolment_id]").attr("value")
        return Payment(amount, enrolmentID, token)
    }
}