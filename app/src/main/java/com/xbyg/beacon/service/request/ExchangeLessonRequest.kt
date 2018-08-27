package com.xbyg.beacon.service.request

import com.xbyg.beacon.data.ExchangeForm
import com.xbyg.beacon.data.ExchangeLesson
import io.reactivex.Single
import okhttp3.Response

class ExchangeLessonRequest(private val exchangeLesson: ExchangeLesson, private val exchangeForm: ExchangeForm) : Request<Boolean>() {
    override fun make(): Single<Boolean> {
        val map = HashMap<String, String>().apply {
            put("id", exchangeLesson.postID)
            put("phone", exchangeForm.contactNumber)
            put("reason", exchangeForm.exchangeReason)
        }

        return post("https://www.beacon.com.hk/sitin_page/form1.php", map)
                .map { res -> parseResponse(res) }
                .doOnError { e -> e.printStackTrace() }
    }

    override fun parseResponse(response: Response): Boolean {
        TODO("not implemented")
    }
}