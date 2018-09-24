package com.xbyg.beacon.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.xbyg.beacon.R
import com.xbyg.beacon.data.Payment
import kotlinx.android.synthetic.main.dialog_payment.*
import android.content.Intent
import android.net.Uri
import com.xbyg.beacon.service.UserService
import com.xbyg.beacon.service.request.PaypalUrlRequest

class PaymentDialog(private val c: Context, private val payment: Payment, private val listener: Listener): Dialog(c) {

    interface Listener {
        fun onPaymentSuccess(dialog: Dialog)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_payment)

        amount.text = "HKD ${payment.amount}"
        help.setOnClickListener { showHelpMessage() }

        if (!payment.amount.equals("0")) {
            submitBtn.text = "Go to Paypal"
            submitBtn.setOnClickListener {
                PaypalUrlRequest(payment).make().subscribe { paypalUrl ->
                    val i = Intent(Intent.ACTION_VIEW, Uri.parse(paypalUrl))
                    context.startActivity(i)
                }
            }
        } else {
            submitBtn.setOnClickListener {
                UserService.instances.exchangeLesson(payment).subscribe { success -> listener.onPaymentSuccess(this) }
            }
        }
    }

    private fun showHelpMessage() {
        AlertDialog.Builder(context).setMessage("根據遵理新政策，上課前48小時至7天內方可調堂，而上課前48小時內調堂會被收取每堂行政費$20\n" +
                "如同學未能透過網上繳費，請到分校繳付行政費辦理調堂手續\n" +
                "每個獨立課程（4堂）有兩次免費網上調堂名額，不足4堂按比例計\n\n" +
                "P.S.: 本應用只提供跳轉至網上繳費頁面服務，但無法得知是否繳費成功，繳費後請在上課時間表確認是否調堂成功").create().show()
    }
}