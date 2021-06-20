package com.qoli.smssender.entity

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [JobEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): JobsDAO?

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()
        private const val dbName = "app_db_v3"

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java, dbName
        ).build()

    }
}
