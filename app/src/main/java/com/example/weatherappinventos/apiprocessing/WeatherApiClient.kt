package com.example.weatherappinventos.apiprocessing

import android.content.Context
import com.example.weatherappinventos.R
import com.example.weatherappinventos.dataclass.CurrentDataWeather
import com.example.weatherappinventos.dataclass.ForecastDataWeather
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherApiClient(context: Context) {
    private val apiKey = context.resources.getString(R.string.api_key) // Api ключ с сайта openweathermap
    private val api = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)

    fun currentWeather(cityName: String): Call<CurrentDataWeather> {
        return api.currentWeatherCall(cityName, "metric", "ru", apiKey)
    }

    fun weatherForecast(cityName: String): Call<ForecastDataWeather> {
        return api.forecastWeatherCall(cityName, "metric", "ru", apiKey)
    }
}