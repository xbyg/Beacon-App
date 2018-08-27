package com.xbyg.beacon.service.request

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.SingleEmitter
import okhttp3.*
import okhttp3.Request
import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.CookieJar
import okhttp3.OkHttpClient

abstract class Request<T> {
    companion object {
        private val cookieStore = HashMap<String, ArrayList<Cookie>>()
        //Server issue: the 'PHPSESSID' cookie is cleared when logged in successfully and access 'https://www.beacon.com.hk/2011/index.php'
        private var sessionID: Cookie? = null

        private val client = OkHttpClient.Builder()
                .followRedirects(false)
                .followSslRedirects(false)
                .cookieJar(object : CookieJar {
                    override fun saveFromResponse(httpUrl: HttpUrl, list: List<Cookie>) {
                        //Server issue: the 'PHPSESSID' cookie is cleared when logged in successfully and access 'https://www.beacon.com.hk/2011/index.php'
                        for (cookie in list) {
                            if (cookie.name().equals("PHPSESSID")) {
                                sessionID = cookie
                                break
                            }
                        }
                        cookieStore.put(httpUrl.host(), ArrayList(list))
                    }

                    override fun loadForRequest(httpUrl: HttpUrl): List<Cookie> {
                        var cookies = cookieStore.get(httpUrl.host())
                        if (cookies != null) {
                            if (sessionID != null) {
                                cookies.add(sessionID!!)
                            }
                        } else {
                            cookies = ArrayList<Cookie>()
                        }
                        return cookies
                    }
                }).build()
    }

    abstract fun make(): Single<T>

    abstract fun parseResponse(response: Response): T

    fun get(url: String): Single<Response> {
        return Single.create { e: SingleEmitter<Response> ->
            try {
                val res = client.newCall(Request.Builder().url(url)
                        .removeHeader("Content-Type")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .removeHeader("User-Agent")
                        .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36")
                        .build()).execute()
                e.onSuccess(res)
            } catch (exception: Exception) {
                e.onError(exception)
            }
        }.subscribeOn(Schedulers.io())
    }

    fun post(url: String, postData: Map<String, String>): Single<Response> {
        return Single.create { e: SingleEmitter<Response> ->
            val requestBody = MultipartBody.Builder().apply {
                setType(MultipartBody.FORM)
                for ((key, value) in postData) {
                    addFormDataPart(key, value)
                }
            }.build()

            val request = Request.Builder()
                    .url(url)
                    .method("POST", RequestBody.create(null, ByteArray(0)))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36")
                    .post(requestBody)
                    .build()

            try {
                val res = client.newCall(request).execute()
                e.onSuccess(res)
            } catch (exception: Exception) {
                e.onError(exception)
            }
        }.subscribeOn(Schedulers.io())
    }
}