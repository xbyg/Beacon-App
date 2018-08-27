package com.xbyg.beacon.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.squareup.picasso.Picasso
import com.xbyg.beacon.R
import com.xbyg.beacon.data.BranchSchool
import kotlinx.android.synthetic.main.activity_branch_school.*

class BranchSchoolActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_branch_school)

        val branchSchool: BranchSchool = intent.getSerializableExtra("branchSchool") as BranchSchool
        Picasso.get().load(branchSchool.locationImageUrl).into(locationImage)
        branchSchoolName.text = branchSchool.name
        address.text = "地址: " + branchSchool.address
        contactNo.text = "分校電話: " + branchSchool.phoneNo
        if (branchSchool.workingHours.isNotEmpty()) {
            workingDates.text = branchSchool.workingHours.reduce { acc, s -> acc + "\n" + s }
        }
    }
}