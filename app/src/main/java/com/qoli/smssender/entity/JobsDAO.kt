package com.qoli.smssender.entity

import androidx.room.*


@Dao
interface JobsDAO {
    @Query("SELECT * FROM JobEntity")
    fun getAll(): List<JobEntity>

    @Query("SELECT * FROM JobEntity WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<JobEntity>

    @Query("SELECT * FROM JobEntity WHERE id = :id")
    fun getOne(id: Int): JobEntity

    @Query("SELECT * FROM JobEntity WHERE timestamp = :timestamp")
    fun getOnebyTimestamp(timestamp: Int): JobEntity

    @Insert
    fun insertAll(vararg users: JobEntity)

    @Delete
    fun delete(user: JobEntity)

    @Update
    fun updateTodo(vararg todos: JobEntity)
}
