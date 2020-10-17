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

        private fun getInstance(context: Context): WeatherDatabase {
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

        fun startDb(context: Context) {
            val db = getInstance(context).currentDao()
            db.deleteAll()
            db.insert(WeatherEntity("name", "temp"))
        }

        fun getDbAll(context: Context): WeatherEntity {
            val db = getInstance(context).currentDao()
            return db.getAll()[0]
        }

        fun getDbDao(context: Context): WeatherDao {
            return getInstance(context).currentDao()
        }
    }

}
