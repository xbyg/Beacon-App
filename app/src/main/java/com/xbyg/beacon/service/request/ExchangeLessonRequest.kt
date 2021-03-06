package com.xbyg.beacon.service.request

import com.xbyg.beacon.data.Payment
import io.reactivex.Single
import okhttp3.Response

class ExchangeLessonRequest(private val payment: Payment) : Request<Boolean>() {
    override fun make(): Single<Boolean> {
        val map = HashMap<String, String>().apply {
            put("amount", payment.amount)
            put("enrolment_id", payment.enrolmentID)
            put("_token", payment.token)
        }

        return post("https://studentportal.beacon.com.hk/paypalChangeLesson", map)
                .map { res -> parseResponse(res) }
                .doOnError { e -> e.printStackTrace() }
    }

    override fun parseResponse(response: Response): Boolean = true //TODO: Awaiting for sample...
}