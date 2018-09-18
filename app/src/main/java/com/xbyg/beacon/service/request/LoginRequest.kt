package com.xbyg.beacon.service.request

import io.reactivex.Single
import okhttp3.Response
import org.jsoup.Jsoup

class LoginRequest(val name: String, val pwd: String) : Request<Boolean>() {

    override fun make(): Single<Boolean> {
        return getToken().flatMap { token ->
            val map = HashMap<String, String>().apply {
                put("email", name)
                put("password", pwd)
                put("_token", token)
            }

            post("https://studentportal.beacon.com.hk/login", map)
                    .map { response -> parseResponse(response) }
        }
    }

    private fun getToken(): Single<String> = get("https://studentportal.beacon.com.hk/login").map { res ->
        Jsoup.parse(res.body()!!.string()).getElementsByAttributeValue("name", "_token")[0].attr("value")
    }

    override fun parseResponse(response: Response): Boolean = !response.body()!!.string().contains("https://studentportal.beacon.com.hk/login")
}