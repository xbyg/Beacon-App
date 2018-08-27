package com.xbyg.beacon.data

import java.io.Serializable

data class BranchSchool(val name: String, val address: String, val phoneNo: String, val workingHours: List<String>, val locationImageUrl: String) : Serializable