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

    fun getExchangeForm(courseID: String, originalDate: String): Single<ExchangeForm?> = ExchangeFormRequest(courseID, originalDate).make()

    fun getPayment(exchangeForm: ExchangeForm) = PaymentRequest(exchangeForm).make()

    fun exchangeLesson(payment: Payment): Single<Boolean> = ExchangeLessonRequest(payment).make()
}