package com.xbyg.beacon.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.xbyg.beacon.R
import kotlinx.android.synthetic.main.dialog_login.*

class LoginDialog(private val c: Context, private val listener: Listener) : Dialog(c) {

    interface Listener {
        fun onSubmit(dialog: LoginDialog, email: String, pwd: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_login)

        loginBtn.setOnClickListener { _ ->
            listener.onSubmit(this, email.text.toString(), pwd.text.toString())
        }

        help.setOnClickListener { _ -> showHelpMessage() }
    }

    private fun showHelpMessage() {
        AlertDialog.Builder(context).setTitle("由於遵理內部系統更新，登入格式因此有所更改，新格式如下：")
                .setMessage("登入名稱： 註冊時登記的電郵地址\n" +
                "預設密碼： 英文姓氏 + 身份證英文字 ＋ 身份證首4位數字").create().show()
    }
}