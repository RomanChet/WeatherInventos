package com.example.weatherappinventos.apiprocessing

import android.content.Context
import com.example.weatherappinventos.R
import com.example.weatherappinventos.dataclass.CurrentDataWeather
import com.example.weatherappinventos.dataclass.ForecastDataWeather
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherApiClient(context: Context) {
    private val apiKey = context.resources.getString(R.string.api_key) // Api ключ
    private val api = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)

    suspend fun currentWeather(cityName: String): Response<CurrentDataWeather> {
        return api.currentWeatherCall(cityName, "metric", "ru", apiKey)
    }

    suspend fun weatherForecast(cityName: String): Response<ForecastDataWeather> {
        return api.forecastWeatherCall(cityName, "metric", "ru", apiKey)
    }
}