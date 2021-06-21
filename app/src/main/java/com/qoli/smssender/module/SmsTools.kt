package com.qoli.smssender.module


import android.content.Context
import android.telephony.TelephonyManager
import android.util.Log
import com.klinker.android.send_message.*
import com.klinker.android.send_message.Settings
import com.qoli.smssender.app.AppUnits


class SmsTools(private val ctx: Context) {
    private var settings: com.qoli.smssender.module.Settings? = null

    init {
        initSettings()
        initApns()
    }

    private fun initApns() {
        ApnUtils.initDefaultApns(ctx) {
            settings = com.qoli.smssender.module.Settings.get(ctx, true)
        }
    }

    private fun initSettings() {
        settings = com.qoli.smssender.module.Settings.get(ctx)
    }


    fun getDefaultApp(): String {
        return if (Utils.isDefaultSmsApp(ctx)) {
            "YES"
        } else {
            "NO"
        }

    }

    fun getPhoneNumber(): String {
        return Utils.getMyPhoneNumber(ctx)
    }

    fun getSIMCardState(): String {
        val tm = ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val state = tm.simState
        return AppUnits.simStatedText(state)
    }

//    fun sendLoops(loopTimes: Int, interval: Long): Unit {
//        Prefs.putString(AppConstant.loopTimes, loopTimes.toString())
//        Prefs.putString(AppConstant.interval, interval.toString())
//
//        GlobalScope.launch {
//            for (i in 1..loopTimes) {
//                delay(interval)
//                sendMessage(i)
//            }
//        }
//    }

    fun sendMessage(
        numberText: String,
        messageText: String,
        isReal: Boolean = false,
        callback: (Boolean) -> Unit
    ) {

        Thread {
            try {
                val sendSettings = Settings()
                sendSettings.mmsc = settings?.mmsc
                sendSettings.proxy = settings?.mmsProxy
                sendSettings.port = settings?.mmsPort
                sendSettings.useSystemSending = true
                val transaction = Transaction(ctx, sendSettings)
                val message = Message(messageText, numberText)

                if (isReal) {
                    transaction.sendNewMessage(message, Transaction.NO_THREAD_ID)
                }
                callback.invoke(true)
            } catch (e: Exception) {
                callback.invoke(false)
                Log.w("sendMessage", e.toString())
            }
        }.start()
    }
}
