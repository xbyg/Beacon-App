package com.xbyg.beacon.service.request

import com.orhanobut.logger.Logger
import com.xbyg.beacon.data.StudentCourse
import com.xbyg.beacon.data.Lesson
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.nio.charset.Charset

class StudentsCoursesRequest : GZIPHtmlRequest<List<StudentCourse>>(Charset.forName("big5")) {

    override fun make(): Single<List<StudentCourse>> = get("https://www.beacon.com.hk/2009/stu_att_lists.php")
            .observeOn(Schedulers.computation())
            .map { response -> parseResponse(response) }

    //https://stackoverflow.com/questions/12340201/why-do-i-get-garbled-text-when-fetching-a-page-using-defaulthttpclient
    // it saved my life XD
    override fun parseResponse(response: Response): List<StudentCourse> {
        val decompressedString = decompress(response.body()!!.bytes())
        val elements: Elements = Jsoup.parse(decompressedString).select("table[border=2] tbody tr:not([bgcolor=#669966])")
        val courseElements: Elements = elements.not("[bgcolor=#FFFFFF]")

        val courses = ArrayList<StudentCourse>()
        for (courseElement in courseElements) {
            // strings are not wrapped by elements and they are just separated by <br>
            // so it is quite thorny to extract data from the HTML since jsoup cannot treat them as an element
            // sample :
            // <b>COURSE_ID </b><br>導師/級別/科目: TUTOR_NAME COURSE_NAME<br>課程名稱: COURSE_NAME<br>課程類型: --<br>上課地點: <br>
            val html = courseElement.select("font")[0].html()
            Logger.d(html)
            val id = courseElement.getElementsByTag("b")[0].text()

            val regexResults = Regex("(?<=: )([\\u4e00-\\u9fa5_A-Z_a-z_0-9_ _\\-_.])*(?=<br>)").findAll(html).toList()
            val courseName = regexResults[0].value
            val topic = if (regexResults[2].value.equals(" --")) null else regexResults[2].value
            courses.add(StudentCourse(id, courseName, "", topic, ArrayList<Lesson>())) // the value of the third parameter 'time' which represents the time of each lesson in that course is defined below
        }

        var i = -1
        for (element in elements) {
            if (!element.hasAttr("bgcolor")) {
                i++
                continue
            }
            val course = courses[i]

            val fonts = element.select("font")
            val classNo = fonts[0].text()
            val date = fonts[1].text()
            val time = fonts[2].text()
            val changedLesson = fonts[3].text()

            course.time = time
            course.lessons.add(Lesson(classNo, date, time, changedLesson))
        }
        return courses
    }
}