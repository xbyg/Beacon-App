package com.xbyg.beacon.service.request

import com.xbyg.beacon.data.StudentCourse
import com.xbyg.beacon.data.Lesson
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.nio.charset.Charset

class StudentsCoursesRequest : GZIPHtmlRequest<List<StudentCourse>>(Charset.forName("utf-8")) {

    override fun make(): Single<List<StudentCourse>> = get("https://studentportal.beacon.com.hk/history")
            .observeOn(Schedulers.computation())
            .map { response -> parseResponse(response) }

    //https://stackoverflow.com/questions/12340201/why-do-i-get-garbled-text-when-fetching-a-page-using-defaulthttpclient
    // it saved my life XD
    override fun parseResponse(response: Response): List<StudentCourse> {
        val decompressedString = decompress(response.body()!!.bytes())
        val tbody = Jsoup.parse(decompressedString).select("div[class=panel-body] > table > tbody")[0]
        val courseElements: Elements = tbody.getElementsByClass("student_history")
        val courses = ArrayList<StudentCourse>()

        for ( courseElement in courseElements ) {
            val tds = courseElement.getElementsByTag("td")
            val id = tds[2].text()
            val tutor = tds[4].text()
            val subject = tds[5].text()
            courses.add(StudentCourse(id, tutor, subject, ArrayList()))
        }

        for ( i in courses.indices ) {
            val course = courses[i]
            val lessonsElements = tbody.select("tr[id=collapse_$i] > td > table > tbody > tr")
            for ( lessonElement in lessonsElements ) {
                val tds = lessonElement.getElementsByTag("td")
                val date = tds[1].text().split(" ")[0]
                val day = tds[1].text().split(" ")[1]
                val time = tds[2].text()
                val location = tds[3].text()
                course.lessons.add(Lesson(date, day, time, location))
            }
        }
        return courses
    }
}