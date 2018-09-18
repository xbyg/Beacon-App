package com.xbyg.beacon.service.request

import com.xbyg.beacon.data.UserProfile
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.Response
import org.jsoup.Jsoup

class UserProfileRequest(private val email: String, private val password: String) : Request<UserProfile>() {
    override fun make(): Single<UserProfile> = get("https://studentportal.beacon.com.hk/profile")
            .observeOn(Schedulers.computation())
            .map { res -> parseResponse(res) }

    override fun parseResponse(response: Response): UserProfile {
        val divElements = Jsoup.parse(response.body()!!.string()).select("div[class=col-md-4]")
        val name = divElements[0].text().split(": ")[1]
        val chiName = name.split(" ")[0]
        val engName = Regex("[A-Z_ ]+").find(name)!!.value
        val form = divElements[1].text().split(": ")[1]
        val electives = divElements[2].text().split(": ")[1]
        val userID = divElements[3].text().split(": ")[1]
        val phoneNumber = divElements[4].text().split(": ")[1]
        val birthday = divElements[5].text().split(": ")[1]
        val school = divElements[6].text().split(": ")[1]
        return UserProfile(chiName, engName, email, password, form, electives, userID, phoneNumber, birthday, school)
    }
}