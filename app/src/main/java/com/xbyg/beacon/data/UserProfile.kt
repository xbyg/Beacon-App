package com.xbyg.beacon.data

import java.io.Serializable

data class UserProfile(var chiName: String, val engName: String, val email: String, val pwd: String, val form: String, val electives: String, val userID: String, val phoneNumber: String, val birthday: String, val school: String) : Serializable