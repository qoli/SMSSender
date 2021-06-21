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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ViewJob : AppCompatActivity() {

    private lateinit var binding: ActivityViewJobBinding
    private lateinit var smsTools: SmsTools
    private var data: JobEntity? = null

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

    private fun runButtonAction(isRun: Boolean = false) {

        val ctx = this
        val max = data?.basePhoneNumbers?.lines()?.size ?: return
        val phones = data?.basePhoneNumbers?.lines() ?: return
        val repeatTimes = data?.jobInterval ?: return
        val messageText = data?.baseMessage ?: return

        Log.d("Logs", max.toString())


        GlobalScope.launch {
            for (i in 0 until max) {

                val nowPhoneNumber = phones[i]

                binding.nowSendToNumber.text = nowPhoneNumber
                binding.numBeenSent.text = i.toString()
                binding.numWillSent.text = (max - i).toString()

                var ii = 1
                repeat(repeatTimes) {
                    binding.nowSendInterval.text = "${ii.toString()}(s)"
                    delay(1000)
                    ii += 1
                }

                smsTools.sendMessage(nowPhoneNumber, messageText, isRun) { bool ->
                    if (bool) {
                        binding.nowResult.text = "${nowPhoneNumber} 成功"
                    } else {
                        binding.nowResult.text = "${nowPhoneNumber} 失敗"
                    }
                }

            }

            binding.numBeenSent.text = max.toString()
            binding.numWillSent.text = "0"
            binding.nowSendInterval.text = "-"
            binding.nowSendToNumber.text = "-"

            runOnUiThread {
                MaterialDialog(ctx).show {
                    title(text = "All is Done")
                    message(text = "已全部發送完成")
                }
            }


        }
    }

    private fun viewPhones() {
        MaterialDialog(this).show {
            title(text = "All Phone Number")
            message(text = data?.basePhoneNumbers)
        }
    }
}
