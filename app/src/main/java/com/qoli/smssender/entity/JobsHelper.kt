package com.qoli.smssender.entity

import android.content.Context
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

    @DelicateCoroutinesApi
    fun listAll(callback: (result: List<JobEntity>) -> Unit) {
        GlobalScope.launch {
            callback.invoke(AppDatabase(context).dao()?.getAll() ?: emptyList())
        }
    }
}
