package com.qoli.smssender.entity

import androidx.room.*


@Dao
interface JobsDAO {
    @Query("SELECT * FROM JobsEntity")
    fun getAll(): List<JobsEntity>

    @Query("SELECT * FROM JobsEntity WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<JobsEntity>

//    @Query(
//        "SELECT * FROM user WHERE first_name LIKE :first AND " +
//                "last_name LIKE :last LIMIT 1"
//    )
//    fun findByName(first: String, last: String): JobsEntity

    @Insert
    fun insertAll(vararg users: JobsEntity)

    @Delete
    fun delete(user: JobsEntity)

    @Update
    fun updateTodo(vararg todos: JobsEntity)
}
