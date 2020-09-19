package com.example.weatherappinventos.API


import com.example.weatherappinventos.DataClass.DataWeather
import com.example.weatherappinventos.DataClass.Weather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// Интерфейс для выполнения GET запроса с параметрами
// http://api.openweathermap.org/data/2.5/weather?q=moscow&units=metric&lang=ru&appid=cef1ebe434addacc0ea0911feea6b570
interface WeatherApiInterface {
    @GET("/data/2.5/weather")
    fun weatherCall(@Query("q") city: String, // параметр города
                    @Query("units") units: String, // единицы измерения
                    @Query("lang") lang: String, // язык вывода результатов
                    @Query("APPID") apiKey: String) // ключ апи
            : Call<DataWeather>
}

// http://api.openweathermap.org/data/2.5/forecast?q=%D0%BC%D0%BE%D1%81&units=metric&lang=ru&cnt=5&appid=cef1ebe434addacc0ea0911feea6b570
interface SecondApiInterface {
    @GET("/data/2.5/forecast")
    fun weatherCall(@Query("q") city: String, // параметр города
                    @Query("units") units: String, // единицы измерения
                    @Query("lang") lang: String, // язык вывода результатов
                 //   @Query("cnt") cnt: Int, // количество прогнозируемых дней
                    @Query("APPID") apiKey: String) // ключ апи
            : Call<DataWeather>
}

