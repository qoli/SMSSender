package com.qoli.smssender.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class JobEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo val jobMode: Int,
    @ColumnInfo val jobTitle: String,
    @ColumnInfo val jobInterval: Int = 5000,
    @ColumnInfo val jobBackNumber: String? = null,
    @ColumnInfo val basePhoneNumbers: String? = null,
    @ColumnInfo val baseMessage: String? = null,
    @ColumnInfo val csvFileByte: Byte? = null,
    @ColumnInfo val notionURL: String? = null,
) {
    constructor(jobMode: Int, jobTitle: String) : this(0, jobMode, jobTitle)
}
