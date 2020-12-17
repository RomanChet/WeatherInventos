package com.example.weatherappinventos.apiprocessing

import com.example.weatherappinventos.dataclass.CurrentDataWeather
import com.example.weatherappinventos.dataclass.ForecastDataWeather
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class WeatherApiClient {

    private var apiKey = "cef1ebe434addacc0ea0911feea6b570" // account: Dev57
    private val apiKeySecond = "8eb64f08f572d28625a4f9e180a4e369" // account: Dev577
    private val apiKeyThird = "19c2555631ff13e224f58e3fe06f4a86" // account: Dev5777

    private val api = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)

    suspend fun currentWeather(
        cityName: String
    ): CurrentDataWeather {
        return try {
            api.currentWeatherCall(cityName, "metric", "ru", apiKey)
        } catch (e: HttpException) {
            changeApiKey(e.code())
            api.currentWeatherCall(cityName, "metric", "ru", apiKey)
        }
    }

    suspend fun weatherForecast(
        cityName: String
    ): ForecastDataWeather {
        return api.forecastWeatherCall(cityName, "metric", "ru", apiKey)
    }

    // смена ключа API при достижении лимита запросов
    private fun changeApiKey(code: Int) {
        while (code == 429) {
            if (apiKey == "cef1ebe434addacc0ea0911feea6b570") {
                apiKey = apiKeySecond
                break
            }
            if (apiKey == apiKeySecond) {
                apiKey = apiKeyThird
                break
            } else {
                break
            }
        }
    }
}