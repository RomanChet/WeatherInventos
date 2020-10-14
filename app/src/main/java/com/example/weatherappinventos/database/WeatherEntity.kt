package com.example.weatherappinventos.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class WeatherEntity(
        @PrimaryKey
        var name: String,
        var temp: String
)

