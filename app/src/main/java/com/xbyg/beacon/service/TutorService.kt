package com.xbyg.beacon.service

import com.xbyg.beacon.data.CoursePdf
import com.xbyg.beacon.data.Tutor
import com.xbyg.beacon.service.request.CoursesPdfRequest
import com.xbyg.beacon.service.request.IntroductionRequest
import com.xbyg.beacon.service.request.TutorsRequest
import io.reactivex.Single

class TutorService : Service {

    companion object {
        val instances = TutorService()
    }

    fun getTutors(): Single<List<Tutor>> = TutorsRequest().make()

    fun getIntro(tutor: Tutor): Single<String> = IntroductionRequest(tutor).make()

    fun getCourses(tutor: Tutor): Single<List<CoursePdf>> = CoursesPdfRequest(tutor).make()
}