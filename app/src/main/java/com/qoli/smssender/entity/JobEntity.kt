package com.qoli.smssender.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.qoli.smssender.R
import com.qoli.smssender.app.AppApplication

@Entity
data class JobEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo var jobMode: Int,
    @ColumnInfo var jobTitle: String,
    @ColumnInfo var timestamp: Int? = null,
    @ColumnInfo var jobInterval: Int = 5,
    @ColumnInfo var jobBackNumber: String? = null,
    @ColumnInfo var jobBackNumberLoop: Int = 0,
    @ColumnInfo var basePhoneNumbers: String? = null,
    @ColumnInfo var baseMessage: String? = null,
    @ColumnInfo var csvFileByte: Byte? = null,
    @ColumnInfo var notionURL: String? = null,
) {
    constructor(jobMode: Int, jobTitle: String, timestamp: Int) : this(
        0,
        jobMode,
        jobTitle,
        timestamp
    )

    fun getJobModeText(): String {

        return when (this.jobMode) {
            0 -> AppApplication.getContext()?.getString(R.string.send_mode_base) ?: "Base Mode"
            1 -> AppApplication.getContext()?.getString(R.string.send_mode_csv) ?: "CSV Mode"
            2 -> AppApplication.getContext()?.getString(R.string.send_mode_notion)
                ?: "Notion Database Mode"
            else -> "unknown"
        }
    }
}

