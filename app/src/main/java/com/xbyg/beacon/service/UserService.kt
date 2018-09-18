package com.xbyg.beacon.service

import com.xbyg.beacon.data.*
import com.xbyg.beacon.service.request.*
import io.reactivex.Completable
import io.reactivex.Single

class UserService : Service {
    companion object {
        val instances = UserService()
    }

    var userProfile: UserProfile? = null

    fun isLoggedIn(): Boolean = userProfile != null

    fun login(username: String, pwd: String): Single<Boolean> = LoginRequest(username, pwd).make().doOnSuccess { success ->
        if (success) {
            userProfile = UserProfileRequest(username, pwd).make().blockingGet()
        }
    }

    fun logout(): Completable = Completable.create { e ->
        userProfile = null
        e.onComplete()
    }

    fun getCourses(): Single<List<StudentCourse>> = StudentsCoursesRequest().make()

    fun getExchangeList(courseID: String, originalDate: String): Single<List<ExchangeLesson>> = ExchangeListRequest(courseID, originalDate).make()

    fun exchangeLesson(lesson: ExchangeLesson): Single<Boolean> = ExchangeLessonRequest(lesson).make()
}