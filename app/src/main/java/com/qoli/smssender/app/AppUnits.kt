package com.qoli.smssender.app

import android.content.Context
import android.content.Intent
import com.qoli.smssender.activity.viewJobs.ViewJob

object AppUnits {
    fun simStatedText(value: Int?): String {
        return when (value) {
            1 -> "Absent"
            2 -> "PinRequired"
            3 -> "PukRequired"
            4 -> "NetworkLocked"
            5 -> "Ready"
            6 -> "NotReady"
            7 -> "PermDisabled"
            8 -> "CardIoError"
            9 -> "CardRestricted"

            else -> {
                "unknown"
            }
        }
    }

    fun toJobViewByID(ctx: Context, jobID: Int) {
        val intent = Intent(ctx, ViewJob::class.java)
        intent.putExtra(AppConstant.jobID, jobID)
        ctx.startActivity(intent)
    }
}
