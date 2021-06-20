package com.qoli.smssender.entity

import android.content.Context
import android.util.Log
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class JobsHelper(val context: Context) {

    @DelicateCoroutinesApi
    fun newBaseJob(title: String) {
        GlobalScope.launch {
            AppDatabase(context).dao()?.insertAll(
                JobEntity(0, title)
            )
        }
    }

    fun listAll() {
        GlobalScope.launch {
            val all = AppDatabase(context).dao()?.getAll()
            Log.d("Logs", all.toString())
        }

    }
}
