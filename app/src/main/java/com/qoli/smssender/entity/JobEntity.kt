package com.qoli.smssender.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class JobEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo var jobMode: Int,
    @ColumnInfo var jobTitle: String,
    @ColumnInfo var timestamp: Int? = null,
    @ColumnInfo var jobInterval: Int = 5,
    @ColumnInfo var jobBackNumber: String? = null,
    @ColumnInfo var jobBackNumberLoop: Int? = null,
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
            0 -> "Base Mode"
            1 -> "CSV Mode"
            2 -> "Notion Database Mode"
            else -> "unknown"
        }
    }
}

