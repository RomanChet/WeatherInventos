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

        private fun getDao(context: Context): WeatherDao {
            return getInstance(context).currentDao()
        }

        private fun insert(context: Context, counterName: String = "", counterTemp: String = "") {
            getDao(context).insert(WeatherEntity(counterName, counterTemp))
        }

        private fun deleteAll(context: Context) {
            getDao(context).deleteAll()
        }

        fun start(context: Context) {
            val db = getInstance(context).currentDao()
            db.deleteAll()
            db.insert(WeatherEntity("name", "temp"))
        }

        fun getAll(context: Context): WeatherEntity {
            val db = getInstance(context).currentDao()
            return db.getAll()[0]
        }

        fun insertFun(context: Context, cityName: String, cityTemp: String) {
            deleteAll(context)
            insert(context, cityName, cityTemp)
        }
    }

}
