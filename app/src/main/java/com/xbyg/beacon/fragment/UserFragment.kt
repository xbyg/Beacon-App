package com.xbyg.beacon.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xbyg.beacon.*
import com.xbyg.beacon.activity.BranchSchoolActivity
import com.xbyg.beacon.activity.StudentCoursesActivity
import com.xbyg.beacon.activity.showSnackBar
import com.xbyg.beacon.dialog.StringArrayDialog
import com.xbyg.beacon.dialog.LoginDialog
import com.xbyg.beacon.service.SchoolService
import com.xbyg.beacon.service.UserService
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_user.*

class UserFragment : Fragment(), LoginDialog.Listener, StringArrayDialog.Listener {
    private val userService = UserService.instances
    private val branchSchoolsAbbr = HashMap<String, String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locations.setOnClickListener { _ -> showLocationsDialog() }

        if (userService.isLoggedIn()) {
            guestLayout.visibility = View.GONE
            userLayout.visibility = View.VISIBLE

            name.text = userService.user!!.name
            courses.setOnClickListener { _ -> startActivity(Intent(context, StudentCoursesActivity::class.java)) }
            logout.setOnClickListener { _ ->
                userService.logout().subscribe { (context as Activity).recreate() }
            }
        } else {
            loginBtn.setOnClickListener { _ -> showLoginDialog() }
            courses.setOnClickListener { _ -> showLoginDialog() }
        }
    }

    private fun showLocationsDialog() {
        SchoolService.instances.getBranchSchoolsName()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { retrievedBranchSchoolsName ->
                    branchSchoolsAbbr.clear()
                    branchSchoolsAbbr.putAll(retrievedBranchSchoolsName)
                    StringArrayDialog(this.context!!, retrievedBranchSchoolsName.keys.toList(), 22f, this).show()
                }
    }

    private fun showLoginDialog() = LoginDialog(this.context!!, this).show()

    override fun onSubmit(dialog: LoginDialog, username: String, pwd: String) {
        userService.login(username, pwd).observeOn(AndroidSchedulers.mainThread())
                .subscribe { success ->
                    if (success) {
                        dialog.dismiss()
                        (this@UserFragment.context as Activity).recreate()
                    } else {
                        dialog.findViewById<View>(android.R.id.content).showSnackBar("Incorrect password！")
                    }
                }
    }

    override fun onSelect(dialog: Dialog, location: String, pos: Int) {
        SchoolService.instances.getBranchSchool(location, branchSchoolsAbbr[location]!!)
                .subscribe { branchSchool ->
                    dialog.dismiss()
                    val intent = Intent(context, BranchSchoolActivity::class.java)
                    intent.putExtra("branchSchool", branchSchool)
                    startActivity(intent)
                }
    }
}