package com.example.weatherappinventos.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [WeatherEntity::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun currentDao(): WeatherDao

    companion object {

        private var instance: WeatherDatabase? = null

        fun getInstance(context: Context): WeatherDatabase {
            if (instance == null) {
                instance = Room
                        .databaseBuilder(
                                context.applicationContext,
                                WeatherDatabase::class.java,
                                "example"
                        )
                        .allowMainThreadQueries()
                        .build()
            }
            return instance as WeatherDatabase
        }
    }

}
