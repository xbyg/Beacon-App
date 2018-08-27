package com.xbyg.beacon.service

import com.xbyg.beacon.data.BranchSchool
import com.xbyg.beacon.service.request.BranchSchoolRequest
import com.xbyg.beacon.service.request.BranchSchoolsListRequest
import io.reactivex.Single

class SchoolService : Service {
    companion object {
        val instances = SchoolService()
    }

    fun getBranchSchoolsName(): Single<Map<String, String>> = BranchSchoolsListRequest().make()

    fun getBranchSchool(location: String, abbr: String): Single<BranchSchool> = BranchSchoolRequest(location, abbr).make()
}