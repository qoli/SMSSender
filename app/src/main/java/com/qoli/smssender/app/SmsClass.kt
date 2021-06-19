package com.qoli.smssender.app

import android.content.Context

class SmsClass(private val ctx: Context) {
    private var settings: com.qoli.smssender.Settings? = null

    fun getSMSSetting(): com.qoli.smssender.Settings? {
        return settings
    }

    private fun initSettings() {
        settings = com.qoli.smssender.Settings.get(ctx)
    }



//    private fun sendLoops(): Unit {
//        Prefs.putString(AppConstant.loopTimes, binding.loopTimes.text.toString())
//        Prefs.putString(AppConstant.interval, binding.interval.text.toString())
//
//        val loopTimes: Int = binding.loopTimes.text.toString().toIntOrNull() ?: 5
//        val interval: Long = binding.interval.text.toString().toLongOrNull() ?: 5000
//
//        GlobalScope.launch {
//            binding.helperText.text = "Ready, Loop Times: $loopTimes, interval: $interval, Waiting first time"
//            for (i in 1..loopTimes) {
//                delay(interval)
//                sendMessage(i)
//            }
//        }
//    }
//
//    private fun sendMessage(times: Int) {
//        Thread {
//            try {
//                val sendSettings = Settings()
//                sendSettings.mmsc = settings?.mmsc
//                sendSettings.proxy = settings?.mmsProxy
//                sendSettings.port = settings?.mmsPort
//                sendSettings.useSystemSending = true
//                val transaction = Transaction(this, sendSettings)
//                val message =
//                    Message("${binding.messageEdit.text.toString()} #$times", binding.to.text.toString())
//
//                transaction.sendNewMessage(message, Transaction.NO_THREAD_ID)
//
//                binding.helperText.text = "Send to ${binding.to.text} #$times"
//            } catch (e: Exception) {
//                binding.helperText.text = e.toString()
//                Log.w("sendMessage", e.toString())
//            }
//        }.start()
//    }
}
