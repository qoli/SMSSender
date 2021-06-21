package com.qoli.smssender.activity.viewJobs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.qoli.smssender.R
import com.qoli.smssender.app.AppConstant
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViewJobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.AppBarLayout.topAppBar.setNavigationOnClickListener {
            this.finish()
        }
        binding.runButton.setOnClickListener {

            MaterialDialog(this).show {
                title(R.string.run_button_title)
                message(text = "執行本次發送計畫")
                positiveButton(R.string.agree) { dialog ->
                    runButtonAction(isRun = true)
                }
                negativeButton(R.string.cancal) { dialog ->
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

        smsTools = SmsTools(this)

        // Receiver
        registerReceiver(simStateReceiver, IntentFilter("android.intent.action.SIM_STATE_CHANGED"))

        fetchData()
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(simStateReceiver)
    }

    // BroadcastReceiver
    private val simStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals("android.intent.action.SIM_STATE_CHANGED")) {
                reloadSIMCardUI()
            }
        }
    }


    private fun reloadSIMCardUI() {
        binding.SIMCardText.text = smsTools.getSIMCardState()
        binding.PhoneNumber.text = smsTools.getPhoneNumber()
    }

    private fun fetchData() {
        val ctx = this
        val id = intent.getIntExtra(AppConstant.jobID, 0)
        JobsHelper(this.applicationContext).getOne(id) { data ->
            Log.d("Logs", id.toString())
            Log.d("Logs", data.toString())
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

                if (((i + 1) % backLoop) == 0) {
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
        MaterialDialog(this).show {
            title(text = "Result")
            message(text = resultText)
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
            positiveButton(R.string.agree) { dialog ->
                JobsHelper(ctx.applicationContext).del(ctx.data!!) {
                    ctx.finish()
                }
            }
            negativeButton(R.string.cancal) { dialog ->
                // Do something
            }
        }
    }
}
