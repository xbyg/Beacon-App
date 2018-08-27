package com.xbyg.beacon.data

import java.io.Serializable

data class Tutor(val id: Int, val tutorId: String, val name: String, val subjects: String, val icon: String, val video: String) : Serializable