package com.example.weatherappinventos.apiprocessing

import android.content.Context
import com.example.weatherappinventos.R
import com.example.weatherappinventos.dataclass.CurrentDataWeather
import com.example.weatherappinventos.dataclass.ForecastDataWeather
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CurrentCall(val context: Context) {
    private val apiKey = context.resources.getString(R.string.api_key) // Api ключ с сайта openweathermap
    fun clientCallCurrent (city: String): Call<CurrentDataWeather> { // из clientCall передается параметр "название города" с типом данных стринг, Call - Синхронно отправить запрос и вернуть его ответ
        val network = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/") //Базовая часть адреса
            .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
            .build()
        val weatherServices = network.create(WeatherApi::class.java) //Создаем объект, при помощи которого будем выполнять запросы
        val weather = weatherServices.currentWeatherCall(city, "metric", "ru", apiKey) //Создаем объект, из которого берем параметры в запрос и выполняем запрос с параментрами
        return weather //возвращаем результат запроса в переменную веазер
    }
}

class ForecastCall(val context: Context) {
    private val apiKey = context.resources.getString(R.string.api_key) // Api ключ с сайта openweathermap
    fun clientCallForecast (city: String): Call<ForecastDataWeather> { // из clientCall передается параметр "название города" с типом данных стринг, Call - Синхронно отправить запрос и вернуть его ответ
        val network = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/") //Базовая часть адреса
            .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
            .build()
        val weatherServices = network.create(WeatherApi::class.java) //Создаем объект, при помощи которого будем выполнять запросы
        val weather = weatherServices.forecastWeatherCall(city, "metric", "ru", apiKey) //Создаем объект, из которого берем параметры в запрос и выполняем запрос с параментрами
        return weather //возвращаем результат запроса в переменную веазер
    }
}
