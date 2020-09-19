package com.example.weatherappinventos.API

import android.content.Context
import com.example.weatherappinventos.DataClass.DataWeather
import com.example.weatherappinventos.R
import com.example.weatherappinventos.DataClass.Weather
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherNetworkClient(val context: Context) {
    private val apiKey = context.resources.getString(R.string.api_key) // Api ключ с сайта openweathermap
    fun clientCall (city: String): Call<DataWeather> { // из clientCall передается параметр "название города" с типом данных стринг, Call - Синхронно отправить запрос и вернуть его ответ
        val network = Retrofit.Builder()
            .baseUrl("http://api.openweathermap.org/data/2.5/") //Базовая часть адреса
            .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
            .build()
        val weatherServices = network.create(WeatherApiInterface::class.java) //Создаем объект, при помощи которого будем выполнять запросы
        val weather = weatherServices.weatherCall(city, "metric", "ru", apiKey) //Создаем объект, из которого берем параметры в запрос и выполняем запрос с параментрами
        return weather //возвращаем результат запроса в переменную веазер
    }
}

class SecondNetworkClient(val context: Context) {
    private val apiKey = context.resources.getString(R.string.api_key) // Api ключ с сайта openweathermap
    fun clientCall (city: String): Call<DataWeather> { // из clientCall передается параметр "название города" с типом данных стринг, Call - Синхронно отправить запрос и вернуть его ответ
        val network = Retrofit.Builder()
            .baseUrl("http://api.openweathermap.org/data/2.5/") //Базовая часть адреса
            .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
            .build()
        val weatherServices = network.create(SecondApiInterface::class.java) //Создаем объект, при помощи которого будем выполнять запросы
        val weather = weatherServices.weatherCall(city, "metric", "ru", apiKey) //Создаем объект, из которого берем параметры в запрос и выполняем запрос с параментрами
        return weather //возвращаем результат запроса в переменную веазер
    }
}
