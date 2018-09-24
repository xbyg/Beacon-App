package com.xbyg.beacon.service.request

import com.xbyg.beacon.data.Payment
import io.reactivex.Single
import okhttp3.Response

class PaypalUrlRequest(private val payment: Payment): Request<String>() {
    override fun make(): Single<String> {
        val map = HashMap<String, String>().apply {
            put("_token", payment.token)
            put("amount", payment.amount)
            put("enrolment_id", payment.enrolmentID)
        }

        return post("https://studentportal.beacon.com.hk/paypalChangeLesson", map)
                .map { res -> parseResponse(res) }
                .doOnError { e -> e.printStackTrace() }
    }

    override fun parseResponse(response: Response): String = response.header("Location")!!
}