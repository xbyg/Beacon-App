package com.xbyg.beacon.data

import java.io.Serializable

data class Lesson(val classNo: String, val date: String, val time: String, val changedLesson: String?) : Serializable