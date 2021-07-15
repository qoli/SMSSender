package com.qoli.smssender.receiver

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.klinker.android.send_message.SentReceiver
import com.qoli.smssender.app.Logs


class SmsSentReceiver : SentReceiver() {

//    override fun onReceive(context: Context?, intent: Intent) {
//        if (intent.extras != null && intent.extras!!["uri"] != null) {
//            Logs("SmsSentReceiver / onReceive / messageUri: ${intent.extras!!["message_uri"].toString()} # ${intent.extras!!["uri"].toString()} # errorCode: ${intent.extras!!["errorCode"].toString()}")
//            ToastUtils.show("${intent.extras!!["uri"].toString()} errorCode: ${intent.extras!!["errorCode"].toString()}")
//
//            val i = Intent("SmsSentReceiver.onReceive")
//            i.putExtra("errorCode", intent.extras!!["errorCode"].toString())
//            i.putExtra("uri", intent.extras!!["uri"].toString())
//            i.putExtra("messageUri", intent.extras!!["message_uri"].toString())
//
//            context!!.sendBroadcast(i)
//
//        }
//    }

    override fun onMessageStatusUpdated(context: Context, intent: Intent, receiverResultCode: Int) {

        try {
            val uri = Uri.parse(intent.getStringExtra("message_uri"))

            Logs("receiverResultCode: ${receiverResultCode.toString()} uri: ${uri.toString()}")
            Logs("intent ${intent.extras.toString()}")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        fun getIntent(context: Context?): Intent {
            return Intent(context, SmsSentReceiver::class.java)
        }
    }
}
