package com.qoli.smssender.entity

import android.content.Context
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@DelicateCoroutinesApi
class JobsHelper(val context: Context) {

    fun newJob(obj: JobEntity, callback: (JobEntity?) -> Unit) {
        val ctx = this
        GlobalScope.launch {
            AppDatabase(context).dao()?.insertAll(obj)

            if (obj.timestamp == null) {
                callback.invoke(null)
            }

            ctx.getByTimestamp(obj.timestamp!!.toInt()) { data ->
                callback.invoke(data)
            }
        }
    }

    fun listAll(callback: (result: List<JobEntity>) -> Unit) {
        GlobalScope.launch {
            callback.invoke(AppDatabase(context).dao()?.getAll() ?: emptyList())
        }
    }

    fun getOne(id: Int, callback: (result: JobEntity?) -> Unit) {
        GlobalScope.launch {
            callback.invoke(AppDatabase(context).dao()?.getOne(id))
        }
    }

    private fun getByTimestamp(timestamp: Int, callback: (result: JobEntity?) -> Unit) {
        GlobalScope.launch {
            callback.invoke(AppDatabase(context).dao()?.getOnebyTimestamp(timestamp))
        }
    }

    fun del(obj: JobEntity, callback: () -> Unit) {
        GlobalScope.launch {
            AppDatabase(context).dao()?.delete(obj)
            callback.invoke()
        }

    }
}
