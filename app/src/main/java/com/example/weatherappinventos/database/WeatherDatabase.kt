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

        private fun startDb(context: Context) {
            val db = getInstance(context).currentDao()
            db.deleteAll()
            db.insert(WeatherEntity("name", "temp"))
        }

        private fun getDao(context: Context): WeatherDao {
            return getInstance(context).currentDao()
        }

        fun getAll(context: Context): WeatherEntity {
            val db = getInstance(context).currentDao()
            return db.getAll()[0]
        }

        fun action(
            context: Context,
            action: String,
            counterName: String = "",
            counterTemp: String = ""
        ) {
            when (action) {
                "start" -> startDb(context)
                "insert" -> getDao(context).insert(WeatherEntity(counterName, counterTemp))
                "deleteAll" -> getDao(context).deleteAll()
            }
        }
    }

}
