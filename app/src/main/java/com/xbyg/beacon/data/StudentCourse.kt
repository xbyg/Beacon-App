package com.xbyg.beacon.data

import java.io.Serializable

data class StudentCourse(val id: String, val name: String, var time: String, val topic: String?, val lessons: ArrayList<Lesson>) : Serializable