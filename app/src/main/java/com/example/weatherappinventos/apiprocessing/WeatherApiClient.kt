package com.example.weatherappinventos.apiprocessing

import android.content.Context
import com.example.weatherappinventos.dataclass.CurrentDataWeather
import com.example.weatherappinventos.dataclass.ForecastDataWeather
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherApiClient {

    // Три независимых API ключа
    val apiKey = "cef1ebe434addacc0ea0911feea6b570" // account: Dev577
    val apiKeySecond = "8eb64f08f572d28625a4f9e180a4e369" // account: Dev577
    val apiKeyThird = "19c2555631ff13e224f58e3fe06f4a86" // account: Dev5777

    private val api = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)

    suspend fun currentWeather(
        cityName: String,
        apiKey: String
    ): CurrentDataWeather {
        return api.currentWeatherCall(cityName, "metric", "ru", apiKey)
    }

    suspend fun weatherForecast(
        cityName: String,
        apiKey: String
    ): ForecastDataWeather {
        return api.forecastWeatherCall(cityName, "metric", "ru", apiKey)
    }
}