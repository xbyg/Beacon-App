package com.xbyg.beacon.service.request

import com.google.gson.JsonParser
import com.xbyg.beacon.data.CoursePdf
import com.xbyg.beacon.data.Tutor
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.Response
import kotlin.collections.ArrayList

class CoursesPdfRequest(val tutor: Tutor) : Request<List<CoursePdf>>() {

    override fun make(): Single<List<CoursePdf>> = get("https://www.beacon.com.hk/mobile/api/search.php?type=json&coursetype=1&tutor=${tutor.tutorId}")
            .observeOn(Schedulers.computation())
            .map { response -> parseResponse(response) }

    override fun parseResponse(response: Response): List<CoursePdf> {
        val courses = ArrayList<CoursePdf>()
        val json = JsonParser().parse(response.body()!!.string()).asJsonObject
        if (json.getAsJsonObject("course") == null) {
            return emptyList()
        }

        val coursesJson = json.getAsJsonObject("course").getAsJsonArray("1")
        for (i in coursesJson) {
            val courseJson = i.asJsonObject
            courses.add(CoursePdf(courseJson.get("course_name").asString, courseJson.get("filename").asString))
        }
        return courses
    }
}