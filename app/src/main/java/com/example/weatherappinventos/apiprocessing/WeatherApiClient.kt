package com.example.weatherappinventos.apiprocessing

import com.example.weatherappinventos.MainActivity
import com.example.weatherappinventos.dataclass.CurrentDataWeather
import com.example.weatherappinventos.dataclass.ForecastDataWeather
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class WeatherApiClient {

    private val apiKeys = arrayOf(
        "b5f6f391da774ad7a4a195525212406", //ucoe22
        "caf63b13e1a9457a95c205158212706", //chet444
        "43539c2cd74b489282a205518212706", //ucoe
    )

    private var apiKey = apiKeys[0]

    // ПРОФАЙЛЕР
//    val builder = OkHttpClient.Builder()
//        .addInterceptor(OkHttpProfilerInterceptor())
//    val client: OkHttpClient = builder.build()

    private val api = Retrofit.Builder()
        .baseUrl("https://api.weatherapi.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        //.client(client) // for OkHttp Profiler
        .build()
        .create(WeatherApi::class.java)


    suspend fun currentWeather(
        cityName: String
    ): CurrentDataWeather {
        return try {
            api.currentWeatherCall(apiKey, cityName)
        } catch (e: HttpException) {
            changeApiKey(e.code())
            currentWeather(cityName)
        }
    }

    suspend fun weatherForecast(
        cityName: String
    ): ForecastDataWeather {
        return try {
            api.forecastWeatherCall(apiKey, cityName)
        } catch (e: HttpException) {
            changeApiKey(e.code())
            weatherForecast(cityName)
        }
    }

    // смена ключа API при достижении лимита запросов
    private fun changeApiKey(code: Int) {
        val sizeApiKeys = apiKeys.size
        var index = apiKeys.indexOf(apiKey)
        if (index + 1 <= sizeApiKeys && code == 403) {
            index++
            apiKey = apiKeys[index]
        } else {
            index -= 2
            MainActivity().toHandleHttpErrors(0, false)
            //сброс ключа в нулевое состояние и вывод пользователю тоста о том, что превышено количество попыток
        }

    }
}