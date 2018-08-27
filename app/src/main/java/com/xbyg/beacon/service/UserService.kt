package com.xbyg.beacon.service

import com.xbyg.beacon.data.*
import com.xbyg.beacon.service.request.*
import io.reactivex.Completable
import io.reactivex.Single

class UserService : Service {
    companion object {
        val instances = UserService()
    }

    var user: User? = null

    fun isLoggedIn(): Boolean = user != null

    fun login(username: String, pwd: String): Single<Boolean> = LoginRequest(username, pwd).make().doOnSuccess { success ->
        if (success) {
            user = User(NameRequest().make().blockingGet(), username, pwd)
        }
    }

    fun logout(): Completable = Completable.create { e ->
        user = null
        e.onComplete()
    }

    fun getCourses(): Single<List<StudentCourse>> = StudentsCoursesRequest().make()

    fun getExchangeList(courseID: String, originalDate: String): Single<List<ExchangeLesson>> = ExchangeListRequest(courseID, originalDate).make()

    fun exchangeLesson(lesson: ExchangeLesson, form: ExchangeForm): Single<Boolean> = ExchangeLessonRequest(lesson, form).make()
}