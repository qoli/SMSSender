package com.qoli.smssender.activity.viewJobs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.qoli.smssender.app.AppConstant
import com.qoli.smssender.databinding.ActivityViewJobBinding
import com.qoli.smssender.entity.JobEntity
import com.qoli.smssender.entity.JobsHelper
import com.qoli.smssender.module.SmsTools

class ViewJob : AppCompatActivity() {

    private lateinit var binding: ActivityViewJobBinding
    private lateinit var smsTools: SmsTools
    private var data: JobEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViewJobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.AppBarLayout.topAppBar.setNavigationOnClickListener {
            super.onBackPressed()
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
        }
    }
}
