package com.example.weatherappinventos.database

import androidx.room.*


@Dao
public interface CurrentDao {
    @Query("SELECT * FROM current_table")
    fun getAll(): List<CurrentEntity>

    @Query("SELECT * FROM current_table WHERE name = :name")
    fun getById(name: String): CurrentEntity

    @Insert
    fun insert(currententity: CurrentEntity)

    @Update
    fun update(currententity: CurrentEntity)

    @Delete
    fun delete(currententity: CurrentEntity)

}