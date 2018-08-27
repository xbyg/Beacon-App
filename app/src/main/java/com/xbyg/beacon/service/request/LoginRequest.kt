package com.xbyg.beacon.service.request

import io.reactivex.Single
import okhttp3.Response

class LoginRequest(val name: String, val pwd: String) : Request<Boolean>() {

    override fun make(): Single<Boolean> {
        val map = HashMap<String, String>().apply {
            put("user", name)
            put("pwd", pwd)
            put("mode", "login")
            put("school", "pass")
            put("login.x", "0")
            put("login.y", "0")
        }

        return post("https://www.beacon.com.hk/2009/login_w.php", map)
                .map { response -> parseResponse(response) }
    }

    //return true if location is redirected
    override fun parseResponse(response: Response): Boolean = response.code() == 302
}