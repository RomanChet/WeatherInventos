package com.example.weatherappinventos

import android.os.Bundle
import android.os.Handler
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import com.example.weatherappinventos.apiprocessing.WeatherApiClient
import com.example.weatherappinventos.dataclass.CurrentDataWeather
import com.example.weatherappinventos.dataclass.ForecastDataWeather
import kotlinx.android.synthetic.main.activity_second.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SecondActivity : AppCompatActivity() {

    // переменные для обновления по свайпу вниз
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var apiClient: WeatherApiClient

    companion object {
        const val PLACE_NAME = "place_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        apiClient = WeatherApiClient(this)

        currentApiProcessing()
        forecastApiProcessing()
        swipeRefreshSecond()
    }

    // обновление активити по свайпу
    private fun swipeRefreshSecond(){
        handler = Handler()
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.go_refresh)
        swipeRefresh.setOnRefreshListener {
            runnable = Runnable {
                currentApiProcessing()
                forecastApiProcessing()
                swipeRefresh.isRefreshing = false
            }
            handler.postDelayed(
                runnable, 800.toLong()
            )
        }

        go_refresh.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
    }

    private fun currentApiProcessing() {
        val count : String? = intent.getStringExtra(PLACE_NAME)
        val call = count?.let { apiClient.currentWeather(it) }
        if (call != null) {
            call.enqueue(object : Callback<CurrentDataWeather> { // объект для получения ответа
                override fun onFailure(call: Call<CurrentDataWeather>?, t: Throwable?) {
                    t?.printStackTrace()
                }
                override fun onResponse(call: Call<CurrentDataWeather>?, response: Response<CurrentDataWeather>?) {
                    if (response != null) {
                        val weather: CurrentDataWeather? = response.body()
                        weather?.main
                        weather?.let {
                            presentData(it)
                        }
                    }
                }
            })
        }
    }

    private fun forecastApiProcessing() {
        val countSecond : String? = intent.getStringExtra(PLACE_NAME)
        val call = countSecond?.let { apiClient.weatherForecast(it) }
        if (call != null) {
            call.enqueue(object : Callback<ForecastDataWeather> { // объект для получения ответа
                override fun onFailure(call: Call<ForecastDataWeather>?, t: Throwable?) {
                    t?.printStackTrace()
                }
                override fun onResponse(call: Call<ForecastDataWeather>?, response: Response<ForecastDataWeather>?) {
                    if (response != null) {
                        val weatherSec: ForecastDataWeather? = response.body()
                        weatherSec?.list?.get(0)?.main
                        weatherSec?.let {
                            weekDaysInstaller(it)
                            iconsInstaller(it)
                            downPresentData(it)
                            progressBarSecond.visibility = View.INVISIBLE
                        }
                    }
                }
            })
        }
    }

    private fun reduceFun (viewIcon: ImageView, nameIcon: String) {
        viewIcon.setImageResource(weatherConditionIconResId(nameIcon))
    }

    // инцилизация и установка иконок погодных условий
    fun iconsInstaller(main: ForecastDataWeather){
        reduceFun(mainDescrImage, main.list[0].weather[0].icon)
        reduceFun(firstDescrImage, main.list[7].weather[0].icon)
        reduceFun(secondDescrImage, main.list[15].weather[0].icon)
        reduceFun(thirdDescrImage, main.list[23].weather[0].icon)
        reduceFun(fourthDescrImage, main.list[31].weather[0].icon)
        reduceFun(fifthDescrImage, main.list[39].weather[0].icon)
    }

    // возвращает значение аргумента в зависимости от имени иконки
    private fun weatherConditionIconResId(iconName: String): Int {
       return when (iconName) {
            "01d" -> R.drawable.r01d
            "01n" -> R.drawable.r01n
            "02d" -> R.drawable.r02d
            "02n" -> R.drawable.r02n
            "03d" -> R.drawable.r03d
            "03n" -> R.drawable.r03n
            "04d" -> R.drawable.r04d
            "04n" -> R.drawable.r04n
            "09d" -> R.drawable.r09d
            "09n" -> R.drawable.r09n
            "10d" -> R.drawable.r10d
            "10n" -> R.drawable.r10n
            "11d" -> R.drawable.r11d
            "11n" -> R.drawable.r11n
            "13d" -> R.drawable.r13d
            "13n" -> R.drawable.r13n
            "50d" -> R.drawable.r50d
            "50n" -> R.drawable.r50n
            else -> R.drawable.r02d
        }
    }

    // перевод и установка представляемых данных текущей температуры
    private fun presentData(main: CurrentDataWeather) {
        val constPrDt =weekDayValue(main.dt)
        var st = constPrDt[0]
        var mounthName = constPrDt[1]
        val numberDay = constPrDt[2]

        mounthName = when (mounthName) {
            "Jan" -> "Января"
            "Feb" -> "Февраля"
            "Mar" -> "Марта"
            "Apr" -> "Апреля"
            "May" -> "Мая"
            "Jun" -> "Июня"
            "Jul" -> "Июля"
            "Aug" -> "Августа"
            "Sep" -> "Сентября"
            "Oct" -> "Октября"
            "Nov" -> "Ноября"
            "Dec" -> "Декабря"
            else -> ""
        }

        st = when (st) {
            "Mon" -> "Понедельник"
            "Tue" -> "Вторник"
            "Wed" -> "Среда"
            "Thu" -> "Четверг"
            "Fri" -> "Пятница"
            "Sat" -> "Суббота"
            "Sun" -> "Воскресенье"
            else -> ""
        }
        with(main) {
            cityNameSecond.text = main.name
            cityTempSecond.text = "${main.main.temp} °C"
            cityDescrSecond.text = main.weather[0].description
            weekDate.text = "${st},                  ${numberDay} ${mounthName}"
        }
    }

    // извлекатель дня недели из UNIX даты
    private fun weekDayValue(unixTime: Long): List<String> {
        val toUsualDate = Date(unixTime*1000).toString()
        return toUsualDate.split(" ")
    }

    // переводчик дней недели
    private fun translatingWeekDays(weekDay: String): String {
        return when (weekDay) {
            "Mon" ->  "Понедельник"
            "Tue" ->  "Вторник"
            "Wed" ->  "Среда"
            "Thu" ->  "Четверг"
            "Fri" ->  "Пятница"
            "Sat" ->  "Суббота"
            "Sun" ->  "Воскресенье"
            else -> ""
        }
    }

    private fun reduceDayWeek(dt: Long): String {
        return translatingWeekDays(weekDayValue(dt)[0])
    }

    // извлечение и перевод дней недели из приходящей в UNIX формате даты
    fun weekDaysInstaller(main: ForecastDataWeather) {
        nameDay1.text = "${reduceDayWeek(main.list[7].dt)}:"
        nameDay2.text = "${reduceDayWeek(main.list[15].dt)}:"
        nameDay3.text = "${reduceDayWeek(main.list[23].dt)}:"
        nameDay4.text = "${reduceDayWeek(main.list[31].dt)}:"
        nameDay5.text = "${reduceDayWeek(main.list[39].dt)}:"
}
    // представление данных, прогнозируеммой погоды
    fun downPresentData(main: ForecastDataWeather){
        // прогноз температуры на 5 дней (1 колонка)
        dayOneTemp.text = "${main.list[7].main.temp} °C"
        dayTwoTemp.text = "${main.list[15].main.temp} °C"
        dayThreeTemp.text = "${main.list[23].main.temp} °C"
        dayFourTemp.text = "${main.list[31].main.temp} °C"
        dayFiveTemp.text = "${main.list[39].main.temp} °C"

        // прогноз ощущаемой температуры на 5 дней (2 колонка)
        dayOneLikeTemp.text = "${main.list[7].main.feels_like} °C"
        dayTwoLikeTemp.text = "${main.list[15].main.feels_like} °C"
        dayThreeLikeTemp.text = "${main.list[23].main.feels_like} °C"
        dayFourLikeTemp.text = "${main.list[31].main.feels_like} °C"
        dayFiveLikeTemp.text = "${main.list[39].main.feels_like} °C"

        // погодные условния (прогноз)
        descrText1.text = main.list[7].weather[0].description
        descrText2.text = main.list[15].weather[0].description
        descrText3.text = main.list[23].weather[0].description
        descrText4.text = main.list[31].weather[0].description
        descrText5.text = main.list[39].weather[0].description
    }
}
