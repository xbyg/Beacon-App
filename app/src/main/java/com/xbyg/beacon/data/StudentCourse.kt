package com.xbyg.beacon.data

import java.io.Serializable

data class StudentCourse(val id: String, val tutor: String, val subject: String, val lessons: ArrayList<Lesson>) : Serializable