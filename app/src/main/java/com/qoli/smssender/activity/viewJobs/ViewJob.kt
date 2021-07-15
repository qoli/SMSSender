package com.qoli.smssender.activity.viewJobs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.hjq.toast.ToastUtils
import com.qoli.smssender.R
import com.qoli.smssender.app.AppConstant
import com.qoli.smssender.app.Logs
import com.qoli.smssender.databinding.ActivityViewJobBinding
import com.qoli.smssender.entity.JobEntity
import com.qoli.smssender.entity.JobsHelper
import com.qoli.smssender.module.SmsTools
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ViewJob : AppCompatActivity() {

    private lateinit var binding: ActivityViewJobBinding
    private lateinit var smsTools: SmsTools
    private var data: JobEntity? = null
    private var resultText = "尚未執行"

    private var isEnable = false
    private var isSending = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        smsTools = SmsTools(this)

        binding = ActivityViewJobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.AppBarLayout.topAppBar.setNavigationOnClickListener {
            this.finish()
        }
        binding.runButton.setOnClickListener {

            MaterialDialog(this).show {
                title(R.string.run_button_title)
                message(text = "執行本次發送計畫")
                positiveButton(R.string.ok) { dialog ->
                    runButtonAction(isRun = true)
                }
                negativeButton(R.string.cancel) { dialog ->
                    // Do something
                }
            }

        }
        binding.testButton.setOnClickListener {
            runButtonAction(isRun = false)
        }

        binding.viewPhones.setOnClickListener {
            viewPhones()
        }

        binding.exportButton.setOnClickListener {
            export()
        }

        binding.delButton.setOnClickListener {
            delJob()
        }

        fetchData()
    }

    override fun onResume() {
        super.onResume()

        // Receiver
        try {
            registerReceiver(
                simStateReceiver,
                IntentFilter("android.intent.action.SIM_STATE_CHANGED")
            )
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtils.show("註冊 simStateReceiver 錯誤")
        }

        try {
            registerReceiver(
                smsSentReceiver,
                IntentFilter("SmsSentReceiver.onReceive")
            )
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtils.show("註冊 SmsSentReceiver 錯誤")
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(simStateReceiver)
        unregisterReceiver(smsSentReceiver)
    }

    override fun onBackPressed() {
        exit {
            super.onBackPressed()
        }
    }

    override fun finish() {
        exit {
            super.finish()
        }
    }

    fun exit(callback: () -> Unit) {
        if (isSending) {
            ToastUtils.show("無法在發送中退出界面")
        } else {
            callback.invoke()
        }
    }

    // BroadcastReceiver
    private val simStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals("android.intent.action.SIM_STATE_CHANGED")) {
                reloadSIMCardUI()
            }
        }
    }

    private val smsSentReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Logs(
                "ViewJob / smsSentReceiver / ${
                    intent.extras?.get("errorCode")?.toString()
                } / ${intent.extras?.get("uri")?.toString()}"
            )
        }
    }


    private fun reloadSIMCardUI() {

        val ctx = this

        if (smsTools.getSIMCardState() == "Ready") {
            binding.PhoneNumber.text = smsTools.getPhoneNumber() ?: "Error"

            if (smsTools.getPhoneNumber() != null) {
                isEnable = true
            } else {
                MaterialDialog(this).show {
                    title(text = "遇到錯誤")
                    message(text = "無法找到有效的電話號碼\n可能會無法成功發送")
                    positiveButton(text = "繼續") { _ ->
                        isEnable = true
                    }

                    negativeButton(R.string.cancel) { _ ->
                        ctx.finish()
                    }
                }
            }

        } else {
            binding.PhoneNumber.text = "SIM 未就緒"
        }

        binding.SIMCardText.text = smsTools.getSIMCardState()
        binding.runButton.isEnabled = isEnable

    }

    private fun fetchData() {
        val ctx = this
        val id = intent.getIntExtra(AppConstant.jobID, 0)
        JobsHelper(this.applicationContext).getOne(id) { data ->
            ctx.data = data
            binding.AppBarLayout.topAppBar.title = data?.jobTitle ?: "get data error"
            runOnUiThread {
                binding.modeText.text = data?.getJobModeText()
                binding.sendInterval.text = "${data?.jobInterval.toString()}(s)"
                binding.backNumber.text = data?.jobBackNumber
                binding.numTotal.text = data?.basePhoneNumbers?.lines()?.size.toString()
                binding.nowMessageContent.text = data?.baseMessage.toString()
                binding.nowSendToNumber.text = data?.basePhoneNumbers?.lines()?.first().toString()
                //
                binding.numBeenSent.text = "0"
                binding.numWillSent.text = data?.basePhoneNumbers?.lines()?.size.toString()
                //
                binding.backNumberLoop.text = data?.jobBackNumberLoop.toString()
                binding.nowSendInterval.text = "-"
                binding.nowSendToNumber.text = "-"
            }
        }
    }

    @DelicateCoroutinesApi
    private fun runButtonAction(isRun: Boolean = false) {

        val ctx = this
        val max = data?.basePhoneNumbers?.lines()?.size ?: return
        val phones = data?.basePhoneNumbers?.lines() ?: return
        val repeatTimes = data?.jobInterval ?: return
        val messageText = data?.baseMessage ?: return
        val backLoop = data?.jobBackNumberLoop ?: return
        val backPhone = data?.jobBackNumber ?: return

        Log.d("Logs", "max: $max")

        resultText = "執行結果"
        isSending = true

        binding.runButton.isEnabled = false
        binding.testButton.isEnabled = false


        val run = GlobalScope.launch {
            for (i in 0 until max) {

                val nowPhoneNumber = phones[i]

                runOnUiThread {
                    binding.nowSendToNumber.text = nowPhoneNumber
                    binding.numBeenSent.text = i.toString()
                    binding.numWillSent.text = (max - i).toString()
                }

                var ii = 1
                repeat(repeatTimes) {
                    runOnUiThread {
                        binding.nowSendInterval.text = "${ii.toString()}(s)"
                    }
                    delay(1000)
                    ii += 1
                }

                runOnUiThread {
                    binding.nowSendInterval.text = "正在發送"
                }


                delay(2000)
                smsTools.sendMessage(nowPhoneNumber, messageText, isRun) { bool ->
                    if (bool) {
                        runOnUiThread {
                            binding.nowResult.text = "$nowPhoneNumber 成功"
                        }
                        resultText += "\n\r ${i + 1} $nowPhoneNumber 成功"
                    } else {
                        runOnUiThread {
                            binding.nowResult.text = "$nowPhoneNumber 失敗"
                        }
                        resultText += "\n\r ${i + 1} $nowPhoneNumber 失敗"
                    }
                }

                if (backLoop != 0 && ((i + 1) % backLoop) == 0) {
                    runOnUiThread {
                        binding.nowSendInterval.text = "回測信息"
                        binding.nowSendToNumber.text = "回測號碼"
                    }
                    delay(2000)
                    smsTools.sendMessage(backPhone, "[回測信息] $messageText", isRun) { bool ->
                        if (bool) {
                            runOnUiThread {
                                binding.nowResult.text = "[回測信息] $nowPhoneNumber 成功"
                            }
                            resultText += "\n\r ${i + 1} 回測信息 成功"
                        } else {
                            runOnUiThread {
                                binding.nowResult.text = "[回測信息] $nowPhoneNumber 失敗"
                            }
                            resultText += "\n\r ${i + 1} 回測信息 失敗"
                        }
                    }
                }
                delay(2000)
            }

        }

        run.invokeOnCompletion { h ->
            runOnUiThread {
                binding.numBeenSent.text = max.toString()
                binding.numWillSent.text = "0"
                binding.nowSendInterval.text = "-"
                binding.nowSendToNumber.text = "-"

                binding.runButton.isEnabled = true
                binding.testButton.isEnabled = true

                isSending = false

                ctx.export()
            }
        }
    }

    private fun viewPhones() {
        MaterialDialog(this).show {
            title(text = "All Phone Number")
            message(text = data?.basePhoneNumbers)
        }
    }


    private fun export() {
        try {
            MaterialDialog(this).show {
                title(text = "Result")
                message(text = resultText)
            }
        } catch (e: Exception) {
            ToastUtils.show("發送完畢")
        }

    }

    private fun delJob() {

        val ctx = this
        if (ctx.data == null) {
            return
        }

        MaterialDialog(this).show {
            title(text = "刪除")
            message(text = "刪除任務")
            positiveButton(R.string.ok) { dialog ->
                JobsHelper(ctx.applicationContext).del(ctx.data!!) {
                    ctx.finish()
                }
            }
            negativeButton(R.string.cancel) { dialog ->
                // Do something
            }
        }
    }
}
