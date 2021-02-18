package com.example.weatherappinventos.apiprocessing

import com.example.weatherappinventos.MainActivity
import com.example.weatherappinventos.dataclass.CurrentDataWeather
import com.example.weatherappinventos.dataclass.ForecastDataWeather
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class WeatherApiClient {

    private val apiKeys = arrayOf(
        "cef1ebe434addacc0ea0911feea6b570",
        "8eb64f08f572d28625a4f9e180a4e369",
        "19c2555631ff13e224f58e3fe06f4a86"
    )

    private var apiKey = apiKeys[0]

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
            currentWeather(cityName)
        }
    }

    suspend fun weatherForecast(
        cityName: String
    ): ForecastDataWeather {
        return try {
            api.forecastWeatherCall(cityName, "metric", "ru", apiKey)
        } catch (e: HttpException) {
            changeApiKey(e.code())
            weatherForecast(cityName)
        }
    }

    // смена ключа API при достижении лимита запросов
    private fun changeApiKey(code: Int) {
        val sizeApiKeys = apiKeys.size
        var index = apiKeys.indexOf(apiKey)
        if (index + 1 <= sizeApiKeys && code == 429) {
            index++
            apiKey = apiKeys[index]
        } else {
            index -= 2
            MainActivity().toHandleHttpErrors(0, false)
            //сброс ключа в нулевое состояние и вывод пользователю тоста о том, что превышено количество попыток
        }

    }
}