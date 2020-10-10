package com.example.weatherappinventos.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_table")
data class CurrentEntity(
    @PrimaryKey
    var name: String,
    var temp: String
)

