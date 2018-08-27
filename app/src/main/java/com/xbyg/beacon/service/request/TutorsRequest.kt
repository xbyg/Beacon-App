package com.xbyg.beacon.service.request

import com.google.gson.JsonParser
import com.xbyg.beacon.data.Tutor
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.Response

class TutorsRequest() : Request<List<Tutor>>() {

    override fun make(): Single<List<Tutor>> = get("https://www.beacon.com.hk/mobile/api/tutors.php?type=json&")
            .observeOn(Schedulers.computation())
            .map { response -> parseResponse(response) }

    override fun parseResponse(response: Response): List<Tutor> {
        val tutors = ArrayList<Tutor>()
        val tutorsJson = JsonParser().parse(response.body()!!.string()).asJsonObject.getAsJsonObject("queryresult").getAsJsonArray("tutor")

        for (i in tutorsJson) {
            val tutorJson = i.asJsonObject

            val id = tutorJson.get("id").asInt
            val tutorId = tutorJson.get("tutor_id").asString
            val name = tutorJson.get("name").asString
            val subjects = tutorJson.get("subjects").asString
            val tutor = Tutor(id, tutorId, name, subjects, "https://www.beacon.com.hk/2009/img/tutor/$tutorId.png", "https://www.beacon.com.hk/2010/teachervideo/video/$tutorId.mp4")
            tutors.add(tutor)
        }
        return tutors
    }
}