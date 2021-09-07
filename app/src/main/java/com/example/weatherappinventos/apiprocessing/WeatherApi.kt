package com.example.weatherappinventos.apiprocessing

import com.example.weatherappinventos.dataclass.CurrentDataWeather
import com.example.weatherappinventos.dataclass.ForecastDataWeather
import retrofit2.http.GET
import retrofit2.http.Query

// Интерфейс для выполнения GET запросов с параметрами
interface WeatherApi {
    // http://api.weatherapi.com/v1/current.json?key=b5f6f391da774ad7a4a195525212406&q=ивня&lang=ru
    @GET("/v1/current.json")
    suspend fun currentWeatherCall(
        @Query("key") key: String, // апи ключ
        @Query("q") city: String, // место
        @Query("lang") lang: String = "ru" // язык вывода результатов, по умолчанию ру
    )
            : CurrentDataWeather

    //https://api.weatherapi.com/v1/forecast.json?key=b5f6f391da774ad7a4a195525212406&q=ивня&days=5
    @GET("/v1/forecast.json")
    suspend fun forecastWeatherCall(
        @Query("key") key: String, // апи ключ
        @Query("q") city: String, // место
        @Query("lang") lang: String = "ru", // язык вывода результатов, по умолчанию ру
        @Query("days") days: Int = 5 // кол-во дней для прогноза , по умолчанию 5
    )
            : ForecastDataWeather
}