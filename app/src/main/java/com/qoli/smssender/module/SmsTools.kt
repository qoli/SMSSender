package com.qoli.smssender.module


import android.content.Context
import android.telephony.TelephonyManager
import android.util.Log
import com.hjq.toast.ToastUtils
import com.klinker.android.send_message.ApnUtils
import com.klinker.android.send_message.Settings
import com.klinker.android.send_message.Transaction
import com.klinker.android.send_message.Utils
import com.pixplicity.easyprefs.library.Prefs
import com.qoli.smssender.app.AppConstant
import com.qoli.smssender.app.AppUnits
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
        return AppUnits.simStatetoText(state)
    }

    fun sendLoops(loopTimes: Int = 5, interval: Long = 5000): Unit {
        Prefs.putString(AppConstant.loopTimes, loopTimes.toString())
        Prefs.putString(AppConstant.interval, interval.toString())

        GlobalScope.launch {
            for (i in 1..loopTimes) {
                delay(interval)
                sendMessage(i)
            }
        }
    }

    private fun sendMessage(times: Int) {
        Thread {
            try {
                val sendSettings = Settings()
                sendSettings.mmsc = settings?.mmsc
                sendSettings.proxy = settings?.mmsProxy
                sendSettings.port = settings?.mmsPort
                sendSettings.useSystemSending = true
                val transaction = Transaction(ctx, sendSettings)
//                val message =  Message("#$times", binding.to.text.toString())
//                transaction.sendNewMessage(message, Transaction.NO_THREAD_ID)
//                binding.helperText.text = "Send to ${binding.to.text} #$times"
                ToastUtils.show("Send to #$times")
            } catch (e: Exception) {
//                binding.helperText.text = e.toString()
                Log.w("sendMessage", e.toString())
            }
        }.start()
    }
}
