package com.qoli.smssender.module


import android.content.Context
import android.telephony.TelephonyManager
import com.klinker.android.send_message.*
import com.klinker.android.send_message.Settings
import com.qoli.smssender.app.AppUnits
import com.qoli.smssender.app.Logs
import com.qoli.smssender.receiver.SmsSentReceiver


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

    fun getPhoneNumber(): String? {
        return try {
            Utils.getMyPhoneNumber(ctx)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getSIMCardState(): String {
        val tm = ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val state = tm.simState
        return AppUnits.simStatedText(state)
    }

    private fun initSmsTransaction(): Transaction {
        val sendSettings = Settings()
        sendSettings.mmsc = settings?.mmsc
        sendSettings.proxy = settings?.mmsProxy
        sendSettings.port = settings?.mmsPort
        sendSettings.useSystemSending = true
        val transaction = Transaction(ctx, sendSettings)

        transaction.setExplicitBroadcastForSentSms(
            SmsSentReceiver.getIntent(this.ctx)
        )

        return transaction
    }

    fun sendMessage(
        numberText: String,
        messageText: String,
        isReal: Boolean = false,
        callback: (Boolean) -> Unit
    ) {

        Thread {
            try {
                val transaction = initSmsTransaction()
                val message = Message(messageText, numberText)

                if (isReal) {
                    transaction.sendNewMessage(message, Transaction.NO_THREAD_ID)
                }
                
                callback.invoke(true)

            } catch (e: Exception) {
                callback.invoke(false)
                Logs(e.toString())
            }
        }.start()
    }
}
