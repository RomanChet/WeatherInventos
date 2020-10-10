package com.example.weatherappinventos.database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [CurrentEntity::class], version = 1)
abstract class CurrentDatabase : RoomDatabase() {
    abstract fun currentDao(): CurrentDao
}
