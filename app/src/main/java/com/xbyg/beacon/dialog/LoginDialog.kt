package com.xbyg.beacon.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.xbyg.beacon.R
import kotlinx.android.synthetic.main.dialog_login.*

class LoginDialog(private val c: Context, private val listener: Listener) : Dialog(c) {

    interface Listener {
        fun onSubmit(dialog: LoginDialog, username: String, pwd: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_login)

        loginBtn.setOnClickListener { _ ->
            listener.onSubmit(this, username.text.toString(), pwd.text.toString())
        }
    }
}