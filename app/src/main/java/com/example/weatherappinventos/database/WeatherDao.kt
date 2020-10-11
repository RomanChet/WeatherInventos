package com.example.weatherappinventos.database

import androidx.room.*


@Dao
interface WeatherDao {
    @Query("SELECT * FROM weatherentity")
    fun getAll(): List<WeatherEntity>

    @Query("DELETE FROM weatherentity")
    fun deleteAll()

    @Insert
    fun insert(currententity: WeatherEntity)

    @Update
    fun update(currententity: WeatherEntity)

    @Delete
    fun delete(currententity: WeatherEntity)

}